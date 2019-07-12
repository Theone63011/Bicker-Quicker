package purdue.edu.bicker_quicker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

import static com.facebook.AccessTokenManager.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/User");
        ref.child(FirebaseAuth.getInstance().getUid() + "/notificationSettings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {

                String bin = Long.toBinaryString((long)dataSnapshot.getValue());
                Log.d(TAG, "notificationSettings: " + bin.charAt(6));
                Log.d(TAG, "binary: " + bin);
                if(bin.charAt(0) == '0'){
                    Log.d(TAG, "All notifications blocked.");
                }
                else if(bin.charAt(6) == '0' && remoteMessage.getData().get("type").equals("voter")){
                   Log.d(TAG, "Voter notif should be blocked.");
                   return;
                }else{
                    Log.d(TAG, "~From: " + remoteMessage.getFrom());

                    // Check if message contains a data payload.
                    if (remoteMessage.getData().size() > 0) {
                        Log.d(TAG, "~Message data payload: " + remoteMessage.getData());

                    }

                    // Check if message contains a notification payload.
                    if (remoteMessage.getNotification() != null) {
                        Log.d(TAG, "~Message Notification Body: " + remoteMessage.getNotification().getBody());
                    }

                    Log.d(TAG, "type: " + remoteMessage.getData().get("type"));
                    notification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
                }
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void notification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "expired";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_blue_lightpurple)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setGroup("Notifications");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify( (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }


}
