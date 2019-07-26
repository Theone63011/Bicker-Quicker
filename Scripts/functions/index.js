'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Cut off time. Child nodes older than this will be deleted.
const CUT_OFF_TIME = 5 * 60 * 1000; // 5 min Hours in milliseconds.

exports.deleteOldItems = functions.database.ref('/Bicker/{pushId}').onWrite(async (change) => {
  console.log("INSIDE deleteOldItems");
  const ref = change.after.ref.parent; // reference to the parent
  const now = Date.now();
  const cutoff = now - CUT_OFF_TIME;
  const oldItemsQuery = ref.orderByChild('create_date/time').endAt(cutoff);
  const snapshot = await oldItemsQuery.once('value');
  // create a map with all children that need to be removed
  const updates = {};

  snapshot.forEach((childSnapshot) => {
    var value = childSnapshot.val();
    if (value.receiverID === 'Unknown') {
      updates[childSnapshot.key] = null;
    }
  });

  // execute all updates in one go and return the result to end the function
  return ref.update(updates);
});

/* exports.moveExpiredBickers = functions.https.onRequest(async (req, res) => {
  console.log("INSIDE moveExpiredBickers");
  const bickers_ref = admin.database().ref('/Bicker');
  const expired_ref = admin.database().ref('/Expired');
}); */

exports.deleteNotification = functions.database.ref('/Bicker/{pushId}').onDelete(async (snapshot, context) => {
  console.log("INSIDE deleteNotification");
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

  try {
    var promise1 = await admin.messaging().send(message);
    console.log("Message sent! Response: " + promise1);
    return promise1;
  }
  catch (err) {
    console.log("Error in sending message: " + err);
    return "Error: " + err;
  }
});


exports.deleteCategory = functions.database.ref('/Bicker/{pushId}').onDelete(async (snapshot, context) => {
  var userRef = admin.database().ref("User/");

  userRef.once("value").then((snapshot) => {
     snapshot.forEach((child) => {
        console.log(child.key, child.val());
      });

      return;

  }).catch((error) => {
    console.log(TAG + 'Error sending message:', error);
  });


  console.log("INSIDE deleteCategory");
  var id = context.params.pushId;
  console.log("Delete pushid: " + id);
  console.log("Title: " + snapshot.val().title);
  var category = snapshot.val().category;
  var category_to_delete = null;
  console.log("Category: " + category);

  var count = null;
  var category_number = null;
  const updates = {};
  const updates2 = {};
  var updates2_position = 1;
  const updates2_toRemove = {};
  const updates3 = {};

  var ref = admin.database().ref('/Bicker');
  var ref2 = admin.database().ref("/Category");
  var ref3 = admin.database().ref("Category/" + category);
  var ref4 = admin.database().ref("/Category/" + category + "/count");
  var ref5 = admin.database().ref("/Category/" + category + "/Active_IDs");

  try {
    var snapshot4 = await ref4.once('value');
    var snapshot5 = await ref5.once('value');

    count = snapshot4.val();
    count--;

    if (count < 0) {
      count = 0;
    }

    updates3['count'] = count;
    console.log("new count: " + count);

    snapshot5.forEach((child) => {
      var key = child.key;
      var data = child.val();

      if (id === data) {
        console.log("match to delete found: " + data);
        category_number = key;
        updates[category_number] = null;

        // Remove the bicker Id from the Category
        ref5.update(updates)
      }
      else {
        updates2_toRemove[key] = null;
        updates2[updates2_position] = data;
        updates2_position++;
      }
    });

    // Remove all bicker IDs to be added again with correct number
    ref5.update(updates2_toRemove);

    // Decrement all bicker ID numbers by 1 and add back
    ref5.update(updates2);

    return ref3.update(updates3);
  }
  catch (err) {
    console.log("ERROR: deleteCategory caught an error: " + err);
    return "ERROR: " + err;
  }
});

exports.deleteCategory_Expired = functions.database.ref('/ExpiredBicker/{pushId}').onDelete(async (snapshot, context) => {
  console.log("INSIDE deleteCategory_Expired");
  var id = context.params.pushId;
  console.log("Delete pushid: " + id);
  console.log("Title: " + snapshot.val().title);
  var category = snapshot.val().category;
  var category_to_delete = null;
  console.log("Category: " + category);

  var count = null;
  var category_number = null;
  const updates = {};
  const updates2 = {};
  var updates2_position = 1;
  const updates2_toRemove = {};
  const updates3 = {};

  var ref = admin.database().ref('/Bicker');
  var ref2 = admin.database().ref("/Category");
  var ref3 = admin.database().ref("Category/" + category);
  var ref4 = admin.database().ref("/Category/" + category + "/count");
  var ref5 = admin.database().ref("/Category/" + category + "/Expired_IDs");

  try {
    var snapshot4 = await ref4.once('value');
    var snapshot5 = await ref5.once('value');

    count = snapshot4.val();
    count--;

    if (count < 0) {
      count = 0;
    }

    updates3['count'] = count;
    console.log("new count: " + count);

    snapshot5.forEach((child) => {
      var key = child.key;
      var data = child.val();

      if (id === data) {
        console.log("match to delete found: " + data);
        category_number = key;
        updates[category_number] = null;

        // Remove the bicker Id from the Category
        ref5.update(updates)
      }
      else {
        updates2_toRemove[key] = null;
        updates2[updates2_position] = data;
        updates2_position++;
      }
    });

    // Remove all bicker IDs to be added again with correct number
    ref5.update(updates2_toRemove);

    // Decrement all bicker ID numbers by 1 and add back
    ref5.update(updates2);

    return ref3.update(updates3);
  }
  catch (err) {
    console.log("ERROR: deleteCategory caught an error: " + err);
    return "ERROR: " + err;
  }
});

exports.addExpiredBackToCategory = functions.database.ref('/ExpiredBicker/{pushId}').onCreate(async (snapshot, context) => {
  console.log("INSIDE addExpiredBackToCategory");
  var id = context.params.pushId;
  console.log("To add pushid: " + id);
  console.log("Title: " + snapshot.val().title);
  var category = snapshot.val().category;
  console.log("Category: " + category);

  var count = null;
  const updates = {};
  const updates3 = {};

  var ref3 = admin.database().ref("Category/" + category);
  var ref4 = admin.database().ref("/Category/" + category + "/count");
  var ref5 = admin.database().ref("/Category/" + category + "/Expired_IDs");

  try {
    var snapshot4 = await ref4.once('value');
    var snapshot5 = await ref5.once('value');

    count = snapshot4.val();
    count++;
    updates[count] = id;
    updates3['count'] = count;
    console.log("new count: " + count);

    ref5.update(updates);

    return ref3.update(updates3);
  }
  catch (err) {
    console.log("ERROR: deleteCategory caught an error: " + err);
    return "ERROR: " + err;
  }
});

exports.newBicker = functions.database.ref('/Bicker/{pushId}').onUpdate(async (change, context) => {
  // Grab the current value of what was written to the Realtime Database.
  const ref = change.after.ref.parent; // reference to the parent
  const expBickRef = ref.parent.child('ExpiredBicker/');
  //const expBickRef = ref.child('ExpiredBicker'); //reference to parent then expired bicker document


  //const oldItemsQuery = ref.orderByChild('create_date/time');
  //const snapshot = await oldItemsQuery.once('value');

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
      title: 'Voting ended for: ',
      body: original.title,
      type: 'voter'
    },
    topic: id
  };


  var message2 = {
    data: {
      title: 'Voting ended for your bicker: ',
      body: original.title,
      type: 'creator'
    },
    topic: id + 'creatorNotification'
  };

  if (!(change.before.val().code === "code_used")) {
    var deadline = time * 1000;
    var delay = setTimeout((deadline) => {

      admin.messaging().send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
          return;
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });



      admin.messaging().send(message2).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return;
      })
        .catch((error) => {
          console.log('Error sending message:', error);
        });

      var ref2 = admin.database().ref("Bicker/" + id);
      ref2.once("value")
        .then((snapshot) => {
          var left = snapshot.val().left_votes;
          var value = snapshot.val();
          console.log("LEFT VOTES: " + left);


          const now = Date.now();
          if ((now - value.create_date.time) > (value.seconds_until_expired * 1000)) {
            //bicker has expired. Move it to expiredBicker section of DB
            console.log("Left votes: " + snapshot.val().left_votes)
            exp_updates[snapshot.key] = value;
            updates[snapshot.key] = null;
            console.log('Bicker has expired:' + value.title);
          } else {
            console.log("Error: " + (now - value.create_date.time) + " " + (value.seconds_until_expired * 1000));
          }


          // execute all updates in one go and return the result to end the function
          ref.update(updates);
          return expBickRef.update(exp_updates);

        }).catch((error) => {
          console.log(TAG + 'Error sending message:', error);
        });

    }, deadline);
  }

  return;
});










// BELOW IS OLD CODE THAT WE MAY WANT TO KEEP FOR REFERENCE:


/*const ref2 = admin.database.ref();
      ref2.child("Bickers/" + id).once("value",snapshot => {

       if(snapshot.exists()){
                    console.log("BICKER EXISTSBICKER EXISTSBICKER EXISTSBICKER EXISTS: " + id);
                    var value = snapshot.val();

                    //value.create_date.time gives time bicker was created
                    //now is the current time
                    //value.expiry is the total time, in seconds, the bicker was set to expire after
                    const now = Date.now();
                    if ((now - value.create_date.time) > (value.seconds_until_expired * 1000)) {
                        //bicker has expired. Move it to expiredBicker section of DB
                         console.log("Left votes: " + snapshot.val().left_votes)
                        exp_updates[snapshot.key] = value;
                        updates[snapshot.key] = null;
                        console.log('Bicker has expired:' + value.title);
                    }else{
                        console.log("Error: " + (now - value.create_date.time) + " " + (value.seconds_until_expired * 1000));
                    }


                // execute all updates in one go and return the result to end the function
                expBickRef.update(exp_updates);
                return ref.update(updates);




        }

      });*/



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


/*exports.notifyCreatorsOnExpire = functions.database.ref('/Bicker/{pushId}/approved_date').onUpdate((snapshot, context) => {
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




      //console.log(Date.now());
    }, deadline);
    return 0;

  }).catch((error) => {
    console.log(TAG + 'Error getting bicker snapshot:', error);
  });

  return 0;

});*/