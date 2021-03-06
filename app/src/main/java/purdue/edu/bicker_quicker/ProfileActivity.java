package purdue.edu.bicker_quicker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    Button respondToBicker;
    Button signOut;
    Button toSettings;
    Button pastBickers;
    Button statisticsButton;
    Button reportedButton;
    Switch modToggle;
    Toolbar toolbar;
    FirebaseUser user;
    boolean toggle;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ArrayAdapter<DeletionRequest> deletionListAdapter;
    private static FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
       editor = pref.edit();

        toggle = pref.getBoolean("toggle", false);

        setTheme(R.style.AppTheme_NoActionBar); // Disable action bar (should be by default but this is precautionary)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        toSettings = findViewById(R.id.settingsButton);
        respondToBicker = findViewById(R.id.bickerRespond);
        pastBickers = findViewById(R.id.pastBickers);
        statisticsButton = findViewById(R.id.statistics);
        reportedButton = findViewById(R.id.reports);
        toolbar = findViewById(R.id.toolbarBicker);
        modToggle = findViewById(R.id.mod);

        if(toggle){
            modToggle.setChecked(true);
        }
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Profile");
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setTitle("Profile");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });

        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        respondToBicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRespond();
            }
        });

        signOut = findViewById(R.id.signOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        reportedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reports();
            }
        });

        reportedButton.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList deletionRequests = new ArrayList();
                String userId = user.getUid();

                // This loop adds the user's voted on bickers to the votedBickerIds list and bickers_votes map
                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    if (userSnapshot.child("userId").getValue().toString().equals(userId)) {
                        for (DataSnapshot deletionRequest : userSnapshot.child("receivedDeletionRequests").getChildren()){
                            try {

                                String key = deletionRequest.getKey();
                                String title = dataSnapshot.child("Bicker/" + key + "/title").getValue().toString();

                                deletionRequests.add(new DeletionRequest(title, key));
                            }
                            catch(Exception e){
                                Log.w("WARN", "Invalid receivedDeletionRequest for user" + userId);
                            }
                        }
                    }
                }

                if(deletionRequests.isEmpty()){
                    findViewById(R.id.deletionRequestHeader).setVisibility(View.GONE);
                }
                else {
                    deletionListAdapter = new ProfileActivity.deletionRequestArrayAdapter(ProfileActivity.this, 0, deletionRequests);

                    ListView listView = findViewById(R.id.deletionRequests);
                    listView.setAdapter(deletionListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pastBickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastBickers();
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics();
            }
        });

        modToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                String msg = "";
                if (checked) {
                    //is checked, activate mod mode
                    reportedButton.setVisibility(View.VISIBLE);
                    msg = "Moderator mode activated.";
                    editor.putBoolean("toggle", true);
                } else {
                    //unchecked, deactivate mod mode
                    reportedButton.setVisibility(View.GONE);
                    msg = "Moderator mode deactivated.";
                    editor.putBoolean("toggle", false);
                }
                editor.commit();
                System.out.println(msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            }

        });



        //get user info from DB, check if mod. If so, make mod toggle visible
        //create listener for PastBickers button in user info retrieval; pass User to pastBickers()
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    if (userSnapshot.child("userId").getValue().toString().equals(currUser.getUid())) {
                        //if the userId is that of the current user, check mod status
                        if (userSnapshot.child("moderator").getValue().toString().equals("true")) {
                            System.out.println("User is mod");
                            modToggle.setVisibility(View.VISIBLE);
                        } else {
                            System.out.println("User is not mod");
                        }
                    }
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    protected void onResume(){
        toggle = pref.getBoolean("toggle", false);
        if(toggle){
            modToggle.setChecked(true);
            reportedButton.setVisibility(View.VISIBLE);
        }else{
            reportedButton.setVisibility(View.GONE);
        }
        super.onResume();
    }

    public void pastBickers() {
        //pass uID from FirebaseAuth for bicker retrieval where child.equals(uId)
        /*
        Intent intent = new Intent(this, BasicBickerView.class);
        Bundle b = new Bundle();
        b.putBoolean("expBick", false);
        intent.putExtras(b);
        startActivity(intent);
        */
        Intent intent = new Intent(this, PastBickersActivity.class);
        startActivity(intent);
    }

    public void leave() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void goToRespond() {
        Intent intent = new Intent(this, RespondActivity.class);
        startActivity(intent);
    }

    public void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void statistics() {
        Intent intent = new Intent(this, StatisticsActivity_home.class);
        startActivity(intent);
    }

    public void reports(){
        Intent intent = new Intent(this, ReportedBickersActivity.class);
        startActivity(intent);
    }
    public void signOut(){

        // To Sign Out of Facebook, do this:
        MainActivity.signOut();

        //sign out of google and take back to MainActivity on success
        FirebaseAuth.getInstance().signOut();
        MainActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    }
                });
    }

    class deletionRequestArrayAdapter extends ArrayAdapter<DeletionRequest> {
        private Context context;
        private List<DeletionRequest> deletionRequests;

        //constructor
        public deletionRequestArrayAdapter(Context context, int resource, ArrayList<DeletionRequest> deletionRequests) {
            super(context, resource, deletionRequests);

            this.context = context;
            this.deletionRequests = deletionRequests;
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {
            DeletionRequest deletionRequest = deletionRequests.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_deletion_request, null);

            TextView title = view.findViewById(R.id.title);
            TextView key = view.findViewById(R.id.bickerKey);
            Button deleteButton = view.findViewById(R.id.deleteButton);

            title.setText(deletionRequest.title);
            key.setText(deletionRequest.key);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    delete(deletionRequest.key);

                    deletionListAdapter.remove(deletionRequest);
                    deletionListAdapter.notifyDataSetChanged();

                    ((View) v.getParent()).findViewById(R.id.deletionRequestLayout).setVisibility(View.GONE);
                }
            });

            return view;
        }
    }

    public void delete(final String key){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        FirebaseMessaging.getInstance().subscribeToTopic(key + "delete")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Notification success";
                        if (!task.isSuccessful()) {
                            msg = "Notification failure";
                        }
                        Log.d("Tag", msg);

                    }
                });

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO: Send a notification to both of these users that the bicker has been deleted
                String User1ToNotifyId = dataSnapshot.child("Bicker/" + key + "/receiverID").getValue().toString();
                String User2ToNotifyId = dataSnapshot.child("Bicker/" + key + "/senderID").getValue().toString();


                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    userSnapshot.child("votedBickerIds").child(key).getRef().setValue(null);
                    userSnapshot.child("sentDeletionRequests").child(key).getRef().setValue(null);
                    userSnapshot.child("receivedDeletionRequests").child(key).getRef().setValue(null);

                }

                dataSnapshot.child("Bicker/" + key).getRef().setValue(null);
                dataSnapshot.child("ExpiredBicker/" + key).getRef().setValue(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void deleteAccount() {
        //delete entry in auth and db
        final FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    if (userSnapshot.child("userId").getValue().toString().equals(currUser.getUid())) {
                        //if the userId is that of the current user, check mod status
                        System.out.println("Attempting delete calls");
                        //dataSnapshot.getRef().child("User").orderByChild("userId").equalTo(currUser.getUid());
                        userSnapshot.getRef().removeValue();
                        currUser.delete();
                        //take user back to starting page
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private class DeletionRequest {
        String key;
        String title;

        DeletionRequest(String title, String key) {
            this.title = title;
            this.key = key;
        }
    }
}
