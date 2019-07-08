package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public Switch allNotifications;
    public Switch beenResponded;
    public Switch votingEnded;
    public Switch bickerCanceled;
    public Switch closeRequest;
    public Switch closeConfirm;
    public Switch voteOnEnd;


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SW_ALLNOT = "swAllNot";
    public static final String SW_BEENRESP = "swBeenResp";
    public static final String SW_VOTEEND = "swVoteEnd";
    public static final String SW_BICKCAN = "swBickCan";
    public static final String SW_CLOSEREQ = "swCloseReq";
    public static final String SW_CLOSECON = "swCloseCon";
    public static final String SW_VOTEONEND = "swVoteOnEnd";

    public static final String SHARED_TEST = "sharedTest";
    public static final String TEST_STRING = "testString";

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

        allNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAllNotifications(isChecked);
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
                }
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

        loadData();
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



    public void leave() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
