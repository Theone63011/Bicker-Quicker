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