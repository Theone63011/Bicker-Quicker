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
  const bickerRef = admin.database().ref("Bicker/" + id);
  const userRef = admin.database().ref('User/');

  // create a map with all children that need to be removed
  const updates = {};
  const exp_updates = {};

  //const original = change.after.val();
  var id = context.params.pushId

  var time = change.after.val().seconds_until_expired;

  var message = {
    data: {
      title: 'Voting ended for: ',
      body: change.after.val().title,
      type: 'voter'
    },
    topic: id
  };


  var message2 = {
    data: {
      title: 'Voting ended for your bicker: ',
      body: change.after.val().title,
      type: 'creator'
    },
    topic: id + 'creatorNotification'
  };

  if (!(change.before.val().code === "code_used")) {
    var deadline = time * 1000;
    var delay = setTimeout((deadline) => {


      /* try {
        var promise1 = await admin.messaging().send(message);
        var promise2 = await admin.messaging().send(message2);
        console.log("Message sent! Response: " + promise1);
        console.log("Message sent! Response: " + promise2);
      }
      catch (err) {
        console.log("Error in sending message: " + err);
        return "Error: " + err;
      } */

      admin.messaging().send(message)
      .then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return;
      })
      .catch((error) => {
        console.log('Error sending message:', error);
      });


      var ref2 = admin.database().ref("Bicker/" + id);

      try {
        var snapshot = await ref2.once('value');
        var snapshot2 = await userRef.once('value');
        var snapshot3 = await bickerRef.once('value');
        var senderID = snapshot3.child("senderID").val();
        var receiverID = snapshot3.child("receiverID").val();
        var senderRef = null;
        var receiverRef = null;
        var senderVotedRef = null;
        var senderCreatedRef = null;
        var receiverVotedRef = null;
        var receiverCreatedRef = null;
        var senderSnapshot = null;
        var receiverSnapshot = null;
        var senderVotedSnapshot = null;
        var senderCreatedSnapshot = null;
        var receiverVotedSnapshot = null;
        var receiverCreatedSnapshot = null;
        var userSnapshot = null;

        userSnapshot = await userRef.once('value');

        if (senderID === null || receiverID === null) {
          console.log("senderID or receiverID is null");
        }
        else {
          senderRef = admin.database().ref("User/" + senderID);
          receiverRef = admin.database().ref("User/" + receiverID);
          senderVotedRef = admin.database().ref("User/" + senderID + "/votedOnBickers/");
          senderCreatedRef = admin.database().ref("User/" + senderID + "/CreatedBickers/");
          receiverVotedRef = admin.database().ref("User/" + receiverID + "/votedOnBickers/");
          receiverCreatedRef = admin.database().ref("User/" + receiverID + "/CreatedBickers/");
          senderSnapshot = await senderRef.once('value');
          receiverSnapshot = await receiverRef.once('value');
          senderVotedSnapshot = await senderVotedRef.once('value');
          senderCreatedSnapshot = await senderCreatedRef.once('value');
          receiverVotedSnapshot = await receiverVotedRef.once('value');
          receiverCreatedSnapshot = await receiverCreatedRef.once('value');
        }

        var left = snapshot.val().left_votes;
        var right = snapshot.val().right_votes;
        var winner = null;

        if (left < 0 || right < 0) {
          console.log("ERROR: left/right vote count is < 0");
        }
        else {
          if (left > right) {
            winner = "left";
          }
          else if (right > left) {
            winner = "right";
          }
          else {
            winner = "tie";
          }
        }

        var value = snapshot.val();
        //console.log("LEFT VOTES: " + left);

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

        // Set sender's 'CreatedBickers' winning side
        if (senderCreatedSnapshot === null) {
          console.log("ERROR: senderCreatedSnapshot === null");
        }
        else {
          var createdIDFound = false;
          senderCreatedSnapshot.forEach((child) => {
            var createdID = child.key;
            if (createdID === id) {
              child.val().child("Winning_Side").set(winner);
              createdIDFound = true;

              console.log("Updated user [" + senderID + "] 'Winning_Side' attribute");
            }
          });

          if (createdIDFound === false) {
            console.log("ERROR: could not find ID in user's CreatedBickers");
          }
        }

        // Set receiver's 'CreatedBickers' winning side
        if (receiverCreatedSnapshot === null) {
          console.log("ERROR: receiverCreatedSnapshot === null");
        }
        else {
          var createdIDFound = false;
          receiverCreatedSnapshot.forEach((child) => {
            var createdID = child.key;
            if (createdID === id) {
              child.val().child("Winning_Side").set(winner);
              createdIDFound = true;

              console.log("Updated user [" + receiverID + "] 'Winning_Side' attribute");
            }
          });

          if (createdIDFound === false) {
            console.log("ERROR: could not find ID in user's CreatedBickers");
          }
        }

        // Loop through all users' 'votedOnBickers' and udpate the 'Status' and 'Winning Side'
        userSnapshot.forEach((child) => {
          var userID = child.key;
          var voted_ref = child.ref().child("votedOnBickers");
          if (voted_ref === null) {
            console.log("voted_ref is null");
          }
          else {
            var voted_snapshot = await voted_ref.once('value');
            voted_snapshot.forEach((child2) => {
              if (child2.key === id) {
                child2.val().Winning_Side.set(winner);
                child2.val().Status.set("Expired");

                console.log("Updated user [" + userID + "] 'Winning_Side' and 'Status' attributes");
              }
            });
          }
        });

        ref.update(updates);
        return expBickRef.update(exp_updates);
      }
      catch (err) {
        console.log("newBicker ERROR: " + err);
        return "newBicker ERROR: " + err;
      }
    }, deadline);
  }

  return;
});