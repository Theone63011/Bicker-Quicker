'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Cut off time. Child nodes older than this will be deleted.
const CUT_OFF_TIME = 5 * 60 * 1000; // 5 min Hours in milliseconds.

exports.deleteOldItems = functions.database.ref('/Bicker/{pushId}').onWrite(async (change) => {
  const ref = change.after.ref.parent; // reference to the parent
  const now = Date.now();
  const cutoff = now - CUT_OFF_TIME;
  const oldItemsQuery = ref.orderByChild('create_date/time').endAt(cutoff);
  const snapshot = await oldItemsQuery.once('value');
  // create a map with all children that need to be removed
  const updates = {};
  
  snapshot.forEach(function (childSnapshot) {
      var value = childSnapshot.val();
      if (value.receiverID === 'Unknown') {
          updates[childSnapshot.key] = null;
      }
    });

  // execute all updates in one go and return the result to end the function
  return ref.update(updates);
});

/*exports.moveOldItems = functions.database.ref('/Bicker/{pushId}').onUpdate(async (change) => {
  const ref = change.after.ref.parent; // reference to the parent
  const expBickRef =  ref.parent.child('ExpiredBicker');
  //const expBickRef = ref.child('ExpiredBicker'); //reference to parent then expired bicker document
  const now = Date.now();

  const oldItemsQuery = ref.orderByChild('create_date/time');
  const snapshot = await oldItemsQuery.once('value');

  // create a map with all children that need to be removed
  const updates = {};
  const exp_updates = {};

  snapshot.forEach(function (childSnapshot) {
      var value = childSnapshot.val();
      //value.create_date.time gives time bicker was created
      //now is the current time
      //value.expiry is the total time, in seconds, the bicker was set to expire after
      var receiver = "Unknown";
      if (((now - value.create_date.time) > (value.seconds_until_expired * 1000))
            && (receiver.localeCompare(value.receiverID) !== 0)) {
          //bicker has expired. Move it to expiredBicker section of DB
          exp_updates[childSnapshot.key] = value;
          updates[childSnapshot.key] = null;
          console.log('Bicker has expired:' + value.title);
      }
    });

  // execute all updates in one go and return the result to end the function
  expBickRef.set(exp_updates);
  return ref.update(updates);
  //return expBickRef.set(exp_updates);
  // return expBickRef.update(exp_updates);
});*/
exports.deleteNotification = functions.database.ref('/Bicker/{pushId}').onDelete((snapshot, context) => {
    var id = context.params.pushId;
    console.log("Delete pushid: " + id);
    console.log("Title: " + snapshot.val().title);
     var message = {
          data: {
            title: 'Bicker deleted: ',
            body: snapshot.val().title,
            type: 'delete'
          },
          topic: id + 'delete'
        };
         admin.messaging().send(message)
            .then((response) => {
               // Response is a message ID string.
               console.log('Successfully sent message:', response);
               return;
             })
             .catch((error) => {
               console.log('Error sending message:', error);
             });
});

exports.newBicker = functions.database.ref('/Bicker/{pushId}').onUpdate(async (change, context) => {
    // Grab the current value of what was written to the Realtime Database.
    const ref = change.after.ref.parent; // reference to the parent
      const expBickRef =  ref.parent.child('ExpiredBicker');
      //const expBickRef = ref.child('ExpiredBicker'); //reference to parent then expired bicker document


      const oldItemsQuery = ref.orderByChild('create_date/time');
      const snapshot = await oldItemsQuery.once('value');

      // create a map with all children that need to be removed
      const updates = {};
      const exp_updates = {};

    const original = change.after.val();
    var id = context.params.pushId;

    console.log("before code: ", change.before.val().code);
    console.log('ID: ', id);
    console.log('BICKER UPDATE: ', original);

   var time = original.seconds_until_expired;

     var message = {
      data: {
        title: 'Voting period ended for: ',
        body: original.title,
        type: 'voter'
      },
      topic: id
    };


    if(!(change.before.val().code === "code_used") ){
    var deadline = time * 1000;
    var delay = setTimeout((deadline)=> {

     admin.messaging().send(message)
     .then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return;
      })
      .catch((error) => {
        console.log('Error sending message:', error);
      });


       snapshot.forEach(function (childSnapshot) {
            var value = childSnapshot.val();
            //value.create_date.time gives time bicker was created
            //now is the current time
            //value.expiry is the total time, in seconds, the bicker was set to expire after
            const now = Date.now();
            if ((now - value.create_date.time) > (value.seconds_until_expired * 1000)) {
                //bicker has expired. Move it to expiredBicker section of DB
                exp_updates[childSnapshot.key] = value;
                updates[childSnapshot.key] = null;
                console.log('Bicker has expired:' + value.title);
            }else{
                console.log("Error: " + (now - value.create_date.time) + " " + (value.seconds_until_expired * 1000));
            }
          });

        // execute all updates in one go and return the result to end the function
        expBickRef.set(exp_updates);
        return ref.update(updates);

    }, deadline);
    }

    return;
});
