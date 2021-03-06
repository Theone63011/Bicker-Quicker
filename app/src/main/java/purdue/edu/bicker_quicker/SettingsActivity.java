package purdue.edu.bicker_quicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public Switch allNotifications;
    public Switch beenResponded;
    public Switch votingEnded;
    public Switch bickerCanceled;
    public Switch closeRequest;
    public Switch closeConfirm;
    public Switch voteOnEnd;
    public int bitString;
    public String dbID;
    public Button deleteAccount;

    public Switch matureContent;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SW_ALLNOT = "swAllNot";
    public static final String SW_BEENRESP = "swBeenResp";
    public static final String SW_VOTEEND = "swVoteEnd";
    public static final String SW_BICKCAN = "swBickCan";
    public static final String SW_CLOSEREQ = "swCloseReq";
    public static final String SW_CLOSECON = "swCloseCon";
    public static final String SW_VOTEONEND = "swVoteOnEnd";
    public static final int INT_ALLNOT = (int)    0b10000000;
    public static final int INT_BEENRESP = (int)  0b01000000;
    public static final int INT_VOTEEND = (int)   0b00100000;
    public static final int INT_BICKCAN = (int)   0b00010000;
    public static final int INT_CLOSEREQ = (int)  0b00001000;
    public static final int INT_CLOSECON = (int)  0b00000100;
    public static final int INT_VOTEONEND = (int) 0b00000010;

    public String userId;

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SW_ALLNOT, allNotifications.isChecked());
        editor.putBoolean(SW_BEENRESP, beenResponded.isChecked());
        editor.putBoolean(SW_VOTEEND, votingEnded.isChecked());
        editor.putBoolean(SW_BICKCAN, bickerCanceled.isChecked());
        editor.putBoolean(SW_CLOSEREQ, closeRequest.isChecked());
        editor.putBoolean(SW_CLOSECON, closeConfirm.isChecked());
        editor.putBoolean(SW_VOTEONEND, voteOnEnd.isChecked());
        editor.commit();
    }

    // Use this during AllNotifications switch being changed to false
    public void saveAllNot() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SW_ALLNOT, allNotifications.isChecked());
        editor.commit();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        allNotifications.setChecked(sharedPreferences.getBoolean(SW_ALLNOT, true));
        if (allNotifications.isChecked()) {
            beenResponded.setChecked(sharedPreferences.getBoolean(SW_BEENRESP, true));
            votingEnded.setChecked(sharedPreferences.getBoolean(SW_VOTEEND, true));
            bickerCanceled.setChecked(sharedPreferences.getBoolean(SW_BICKCAN, true));
            closeRequest.setChecked(sharedPreferences.getBoolean(SW_CLOSEREQ, true));
            closeConfirm.setChecked(sharedPreferences.getBoolean(SW_CLOSECON, true));
            voteOnEnd.setChecked(sharedPreferences.getBoolean(SW_VOTEONEND, false));
        } else {
            beenResponded.setEnabled(false);
            votingEnded.setEnabled(false);
            bickerCanceled.setEnabled(false);
            closeRequest.setEnabled(false);
            closeConfirm.setEnabled(false);
            voteOnEnd.setEnabled(false);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User/" + userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("matureContent").exists()){
                    matureContent.setChecked(Boolean.parseBoolean(dataSnapshot.child("matureContent").getValue().toString()));
                }
                else {
                    matureContent.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setBitString() {
        int n = 0b00000000;
        if (allNotifications.isChecked()) {
            n = (n | INT_ALLNOT);
        }
        if (beenResponded.isChecked()) {
            n = (n | INT_BEENRESP);
        }
        if (bickerCanceled.isChecked()) {
            n = (n | INT_BICKCAN);
        }
        if (closeConfirm.isChecked()) {
            n = (n | INT_CLOSECON);
        }
        if (closeRequest.isChecked()) {
            n = (n | INT_CLOSEREQ);
        }
        if (votingEnded.isChecked()) {
            n = (n | INT_VOTEEND);
        }
        if (voteOnEnd.isChecked()) {
            n = (n | INT_VOTEONEND);
        }

        bitString = n;
        sendSettingsUpdateToDatabase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get notification settings
        allNotifications = findViewById(R.id.allNot);
        beenResponded = findViewById(R.id.beenResponded);
        votingEnded = findViewById(R.id.votingEndMine);
        bickerCanceled = findViewById(R.id.cancelBicker);
        closeRequest = findViewById(R.id.closeRequest);
        closeConfirm = findViewById(R.id.closeConfirm);
        voteOnEnd = findViewById(R.id.voteFinished);
        deleteAccount = findViewById(R.id.statistics);
        matureContent = findViewById(R.id.matureContent);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadData();
        setBitString();
        dbID = null; // Will get set to null
        retrieveDBID(); // Set dbID whenever callback occurs

        allNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAllNotifications(isChecked);
                setBitString();
            }
        });

        beenResponded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_BEENRESP, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        votingEnded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_VOTEEND, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        bickerCanceled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_BICKCAN, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        closeRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_CLOSEREQ, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        closeConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_CLOSECON, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        voteOnEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allNotifications.isChecked()) {
                    SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(SW_VOTEONEND, isChecked);
                    editor.commit();
                    setBitString();
                }
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder bob = new AlertDialog.Builder(SettingsActivity.this);
                bob.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAccount();
                    }
                });
                bob.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //hide dialog
                    }
                });
                bob.setMessage("Are you sure you want to delete your account? This action is permanent.");
                bob.create();
                bob.show();
            }

        });

        matureContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                FirebaseDatabase.getInstance().getReference().child("User/" + userId + "/matureContent").setValue(isChecked);

            }
        });

        toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Profile");
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });
    }

    public void retrieveDBID() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("User");

        Query getID = ref.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        getID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> ds = dataSnapshot.getChildren();

                // Iterate to get the child of the DataSnapshot
                for (DataSnapshot userSnap : ds) {
                    dbID = userSnap.getKey();
                    sendSettingsUpdateToDatabase(); // Update settings incase things have changed since call happened
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean setAllNotifications(boolean isChecked) {
        if (!isChecked) {
            beenResponded.setChecked(false);
            votingEnded.setChecked(false);
            bickerCanceled.setChecked(false);
            closeRequest.setChecked(false);
            closeConfirm.setChecked(false);
            voteOnEnd.setChecked(false);
            beenResponded.setEnabled(false);
            votingEnded.setEnabled(false);
            bickerCanceled.setEnabled(false);
            closeRequest.setEnabled(false);
            closeConfirm.setEnabled(false);
            voteOnEnd.setEnabled(false);
            SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(SW_ALLNOT, isChecked);
            editor.commit();
            return false;
        } else {
            loadData();
            allNotifications.setChecked(true);
            beenResponded.setEnabled(true);
            votingEnded.setEnabled(true);
            bickerCanceled.setEnabled(true);
            closeRequest.setEnabled(true);
            closeConfirm.setEnabled(true);
            voteOnEnd.setEnabled(true);
            SharedPreferences pref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            beenResponded.setChecked(pref.getBoolean(SW_BEENRESP, true));
            votingEnded.setChecked(pref.getBoolean(SW_VOTEEND, true));
            bickerCanceled.setChecked(pref.getBoolean(SW_BICKCAN, true));
            closeRequest.setChecked(pref.getBoolean(SW_CLOSEREQ, true));
            closeConfirm.setChecked(pref.getBoolean(SW_CLOSECON, true));
            voteOnEnd.setChecked(pref.getBoolean(SW_VOTEONEND, false));

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(SW_ALLNOT, isChecked);
            editor.commit();
        }
        return true;
    }


    public void sendSettingsUpdateToDatabase() {

        if (dbID == null) // Don't send update if not ready
            return;

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("User/"+dbID+"/notificationSettings");
        ref.setValue(bitString);
    }

    public void leave() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
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
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        signOut();
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
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    }
                });
    }
}
