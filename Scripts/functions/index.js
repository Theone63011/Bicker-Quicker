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


exports.notifyCreatorsOnExpire = functions.database.ref('/Bicker/{pushId}/approved_date').onUpdate((snapshot, context) => {
  var TAG = "notifyCreatorsOnExpire: ";
  console.log(TAG + "Inside notifyCreatorsOnExpire");
  var now_date = Date.now();
  var bickerID = context.params.pushId;
  var senderID;
  var receiverID;
  var deadline;
  var database = admin.database();
  var time_until_expired;
  var approved_time;
  var timer = null;
  var title;
  var message;
  ref = database.ref();

  ref.child("/Bicker/" + bickerID).once("value",snapshot => {
      if (snapshot.exists()){
        
        // Read variables from database
        time_until_expired = snapshot.child("seconds_until_expired").val();
        approved_time = snapshot.child("approved_date").child("time").val();
        title = snapshot.child("title").val();
        senderID = snapshot.child("senderID").val();
        receiverID = snapshot.child("receiverID").val();

        deadline = time_until_expired * 1000;

        var message = {
            data: {
              title: 'Voting period ended for your created bicker: ',
              body: title,
              type: 'creator'
            },
          topic: bickerID + 'creatorNotification'
        };

        console.log(TAG + "time_until_expired = " + time_until_expired);
        console.log(TAG + "approved_time = " + apprv_time);
        console.log(TAG + "deadline = " + deadline);

        if(apprv_time === 0) {
          console.log(TAG + "apprv_time === 0");
          return 0;
        }
      }
  }).then(() =>{

      console.log(TAG + "Inside .then()");

      if(apprv_time === 0) {
          console.log(TAG + "apprv_time in .then() === 0");
          return 0;
        }

      timer = setTimeout((deadline) => {
      database = admin.database();
      ref = database.ref();


      admin.messaging().send(message).then((response) => {
        // Response is a message ID string.
        console.log(TAG + 'Successfully sent message:', response);
        return;
      })
      .catch((error) => {
        console.log(TAG + 'Error sending message:', error);
      });



      /*ref.child("joinableLobby/normal").once("value",snapshot => {
          if (snapshot.exists()){
            var data = snapshot.val();
            var key = Object.keys(data)[0];
            var num = data[key].numParticipants;
            var keys = Object.keys(data['participants']);
            console.log(data);
            var playerData = new Array(keys.length);
            if (num >= 2) {
            console.log("Start Game");
            
            var data3 = {
              numParticipants : num,
              difficulty : "normal"
            }

            var lobbyData = {
              roomInfo : data3,
              Participants : 0
            };

            ref.child('gameLobby/' + key).set(lobbyData);
            
            for(var i = 0; i < playerData.length; i++){ 
              var k = keys[i];
              var data2 = {
                playerName: data['participants'][k].playerName
              }
              ref.child('gameLobby/' + key + '/Participants/' + data['participants'][k].playerId).set(data2);
              ref.child('Players/' + data['participants'][k].playerId + '/inGame').set(true);
            }
            ref.child("joinableLobby/normal").remove();
          }else{
            for(i = 0; i < playerData.length; i++){
              k = keys[i];
              ref.child('Players/' + data['participants'][k].playerId + '/currentRoom').set(0);
            }
            ref.child("joinableLobby/normal").remove();
          }
          }
      });
      */



      //console.log(Date.now());
    }, deadline);
    return 0;

  }).catch((error) => {
    console.log(TAG + 'Error getting bicker snapshot:', error);
  });
  
  return 0;

});