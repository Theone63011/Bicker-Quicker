package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class CreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CodeDialog.CodeDialogListener {
    // Declare scene items
    Toolbar toolbar;
    Spinner catSpin;
    TextView bickerTitle;       // Non-editable Text Fields
    TextView bickerCategory;
    TextView bickerDescription;
    TextView descToolTip;
    TextView yourSide;
    TextView tag1;
    TextView tag2;
    TextView tag3;
    int numTags = 0;
    String tag_string1;
    String tag_string2;
    String tag_string3;

    // TextViews below are for Censoring
    private TextView bicker_censor;

    EditText title;             // Editable Fields
    EditText description;
    EditText side;
    EditText tag;

    Button submitBicker;
    FloatingActionButton addTag;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static Censor censor;

    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar); // This needs to be done to create customizable tool bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Instantiate Scene Items
        bickerTitle = findViewById(R.id.textViewTitle);
        bickerCategory = findViewById(R.id.textViewCategory);
        bickerDescription = findViewById(R.id.textViewDesc);
        descToolTip = findViewById(R.id.textViewHint);
        yourSide = findViewById(R.id.textViewSide);
        title = findViewById(R.id.editTitle);
        description = findViewById(R.id.editTextDesc);
        side = findViewById(R.id.editTextSide);
        submitBicker = findViewById(R.id.submitBicker);
        tag = findViewById(R.id.tagField);
        addTag = findViewById(R.id.fabAddTag);
        tag1 = findViewById(R.id.liveTag1);
        tag2 = findViewById(R.id.liveTag2);
        tag3 = findViewById(R.id.liveTag3);
        numTags = 0;
        tag_string1 = null;
        tag_string2 = null;
        tag_string3 = null;

        censor = new Censor();
        bicker_censor = findViewById(R.id.bicker_censor);
        bicker_censor.setVisibility(View.GONE);
        title.addTextChangedListener(titleWatcher);
        description.addTextChangedListener(descWatcher);
        side.addTextChangedListener(sideWatcher);
        tag.addTextChangedListener(tagWatcher);

        TextView timer_title = (TextView) findViewById(R.id.timer_title);
        radioGroup = (RadioGroup) findViewById(R.id.timer_radio_group);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);

        // If you want to modify the timer values, please change below
        // If changing timer options, please use 'seconds', 'minutes' or 'hours' as the 2nd word
        // **************************************************************************************
        String option1 = "45 seconds";
        String option2 = "24 hours";
        String option3 = "48 hours";
        radioButton1.setText(option1);
        radioButton2.setText(option2);
        radioButton3.setText(option3);
        // **************************************************************************************

        tag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTag(1);
            }
        });

        tag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTag(2);
            }
        });

        tag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTag(3);
            }
        });

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numTags >= 3)
                    return;
                if (addNewTag(tag.getText().toString())) { // Tag worked
                    tag.setText("");
                }
            }
        });

        submitBicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewBicker();
            }
        });

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                resetColor(bickerTitle, "#777777");
            }
        });

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                resetColor(bickerDescription, "#777777");
            }
        });

        side.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                resetColor(yourSide, "#777777");
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(hasWindowFocus()) {
                    resetColor(timer_title, "#777777");
                }
            }
        });


        // Change toolbar to a new toolbar (
        toolbar = (Toolbar) findViewById(R.id.toolbarBicker);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Bicker");
        toolbar.setSubtitle("Enter Your Bicker Details Below");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));

        // Scale and draw back button icon in top left of toolbar
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });
        // Set up category spinner
        catSpin = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.simple_spinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        catSpin.setAdapter(adapter);
        catSpin.setSelection(0);
        catSpin.setOnItemSelectedListener(this);
        catSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetColor(bickerCategory, "#777777");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean deleteTag(int tagID) {
        if (tagID == 1) {
            if (numTags == 1) {
                numTags = 0;
                tag_string1 = null;
                tag1.setText("");
            } else if (numTags == 2) {
                numTags = 1;
                tag_string1 = tag_string2;
                tag_string2 = null;
                tag2.setText("");
                tag1.setText(tag_string1+" x");
            } else if (numTags == 3) {
                numTags = 2;
                tag_string1 = tag_string2;
                tag_string2 = tag_string3;
                tag3.setText("");
                tag2.setText(tag_string2+" x");
                tag1.setText(tag_string1+" x");
                tag3.setText("");
            }
        } else if (tagID == 2) {
            if (numTags == 2) {
                numTags = 1;
                tag_string2 = null;
                tag2.setText("");
            } else if (numTags == 3) {
                numTags = 2;
                tag_string2 = tag_string3;
                tag_string3 = null;
                tag3.setText("");
                tag2.setText(tag_string2+" x");
            }
        } else if (tagID == 3) {
            numTags = 2;
            tag_string3 = null;
            tag3.setText("");
        }

        return false;
    }

    public boolean addNewTag(String s) {

        if (s.length() < 2) {
            bicker_censor.setText("Tag Length Minimum: 2 chars");
            bicker_censor.setVisibility(View.VISIBLE);
            return false;
        }

        s = s.toLowerCase();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1); // Cap first letter

        // Ensure no duplicate tags from same user
        if (tag_string1 != null && s.toLowerCase().compareTo(tag_string1.toLowerCase()) == 0) {
            return false;
        }

        if (tag_string2 != null && s.toLowerCase().compareTo(tag_string2.toLowerCase()) == 0) {
            return false;
        }

        /*
        if (censor.check_chars(s) == false || censor.check_words(s) == false || censor.check_tag_length(s))
            return false;
        */

        if (bicker_censor.getVisibility() == View.VISIBLE) {
            return false;
        }

        //Toast.makeText(this, "NumTags "+(numTags), Toast.LENGTH_LONG).show();

        if (numTags >= 3)
            return false;

        switch (numTags) {
            case 0: {
                tag1.setText(s+" x");
                tag_string1 = s;
                numTags = 1;
                break;
            }
            case 1: {
                tag2.setText(s+" x");
                tag_string2 = s;
                numTags = 2;
                break;
            }
            case 2: {
                tag3.setText(s+" x");
                tag_string3 = s;
                numTags = 3;
                break;
            }
            default: {
                return false;
            }
        }

        return true;
    }


    private final TextWatcher titleWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_title_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_censor.setText("Length Limit: 18 chars");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_title_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_censor.setText("Length Limit: 18 chars");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }
    };

    private final TextWatcher descWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_desc_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_censor.setText("Length Limit: 50 chars");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_desc_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_censor.setText("Length Limit: 50 chars");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }
    };

    private final TextWatcher tagWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int valid = 0;
            if (censor.check_chars(s.toString()) == false) valid = 1;
            if (censor.check_words(s.toString()) == false) valid = 2;
            if (censor.check_tag_length(s.toString()) == false) valid = 3;

            if (valid > 0) {
                if (valid == 3) {
                    bicker_censor.setText("Length Limit: 12 chars");
                } else if (valid == 1) {
                    bicker_censor.setText("Invalid Character");
                } else if (valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }

                bicker_censor.setVisibility(View.VISIBLE);
            } else {
                bicker_censor.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            int valid = 0;
            if (censor.check_chars(s.toString()) == false) valid = 1;
            if (censor.check_words(s.toString()) == false) valid = 2;
            if (censor.check_tag_length(s.toString()) == false) valid = 3;

            if (valid > 0) {
                if (valid == 3) {
                    bicker_censor.setText("Tags Must Be Shorter than 12 Characters");
                } else if (valid == 1) {
                    bicker_censor.setText("Invalid Character");
                } else if (valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }

                bicker_censor.setVisibility(View.VISIBLE);
            } else {
                bicker_censor.setVisibility(View.GONE);
            }
        }
    };

    private final TextWatcher sideWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(valid > 0) {
                bicker_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_censor.setText("Inappropriate Input");
                }
            }
            else {
                bicker_censor.setVisibility(View.GONE);
            }
        }
    };


    public  void resetColor(TextView textView, String color) {
        textView.setTextColor(Color.parseColor(color));
    }

    public ArrayList<String> getKeywords() {
        return null;
    }

    public void submitNewBicker() {
        // Validate all fields
        String bickTitle = title.getText().toString();
        String bickDesc = description.getText().toString();
        TextView tv = (TextView) catSpin.getSelectedView();
        String bickCat = tv.getText().toString();
        String bickSide = side.getText().toString();

        TextView timer_title = (TextView) findViewById(R.id.timer_title);
        radioGroup = (RadioGroup) findViewById(R.id.timer_radio_group);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);

        boolean rad1Checked = radioButton1.isChecked();
        boolean rad2Checked = radioButton2.isChecked();
        boolean rad3Checked = radioButton3.isChecked();

        // Get tags into a list
        ArrayList<String> tags = new ArrayList<String>();
        if (tag_string1 != null && !tag_string1.equals(""))
            tags.add(tag_string1);

        if (tag_string2 != null && !tag_string2.equals(""))
            tags.add(tag_string2);

        if (tag_string3 != null && !tag_string3.equals(""))
            tags.add(tag_string3);

        boolean failed = false;

        boolean censor_passed = true;
        if(censor.check_chars(bickTitle) == false) censor_passed = false;
        if(censor.check_words(bickTitle) == false) censor_passed = false;
        if(censor.check_chars(bickDesc) == false) censor_passed = false;
        if(censor.check_words(bickDesc) == false) censor_passed = false;
        if(censor.check_chars(bickSide) == false) censor_passed = false;
        if(censor.check_words(bickSide) == false) censor_passed = false;
        if(censor_passed == false) {
            //Log.d(TAG, "Censor not passed");
            Toast.makeText(CreateActivity.this, "Invalid Input.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            //Log.d(TAG, "Censor passed");
        }

        if(rad1Checked == false && rad2Checked == false && rad3Checked == false) {
            failed = true;
            timer_title.setTextColor(Color.parseColor("#FF758C"));
            Toast.makeText(CreateActivity.this, "Input values missing.", Toast.LENGTH_SHORT).show();
        }

        if (bickTitle.trim().equals("")) {
            failed = true;
            bickerTitle.setTextColor(Color.parseColor("#FF758C"));
            Toast.makeText(CreateActivity.this, "Input values missing.", Toast.LENGTH_SHORT).show();
        }

        if (bickDesc.trim().equals("")) {
            failed = true;
            bickerDescription.setTextColor(Color.parseColor("#FF758C"));
            Toast.makeText(CreateActivity.this, "Input values missing.", Toast.LENGTH_SHORT).show();
        }

        if (bickCat.trim().equals("Select Category")) {
            failed = true;
            bickerCategory.setTextColor(Color.parseColor("#FF758C"));
            Toast.makeText(CreateActivity.this, "Input values missing.", Toast.LENGTH_SHORT).show();
        }

        if (bickSide.trim().equals("")) {
            failed = true;
            yourSide.setTextColor(Color.parseColor("#FF758C"));
            Toast.makeText(CreateActivity.this, "Input values missing.", Toast.LENGTH_SHORT).show();
        }

        if (failed) return; // Improper data input. Show bad fields

        // get the time for the timer
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedBtn = (RadioButton) findViewById(selectedId);
        String btnText = selectedBtn.getText().toString();
        String radioText = btnText.substring(0, 2);
        double time_selected = Double.parseDouble(radioText);

        double seconds_until_expired = -1;
        if(btnText.contains("seconds")) {
            seconds_until_expired = time_selected;
        }
        else if(btnText.contains("minutes")) {
            seconds_until_expired = time_selected * 60;
        }
        else if(btnText.contains("hours")) {
            seconds_until_expired = time_selected * 60 * 60;
        }
        else {
            Log.d(TAG, "Create_activity ERROR: time selected is neither \'seconds\', \'minutes\' or \'hours\'");
            Toast.makeText(CreateActivity.this,"ERROR: (CreateActivity.java) time selected is ne" +
                    "ither \'seconds\', \'minutes\' or \'hours\'", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable all fields
        title.setFocusable(false);
        description.setFocusable(false);
        catSpin.setFocusable(false);
        side.setFocusable(false);
        radioButton1.setFocusable(false);
        radioButton2.setFocusable(false);
        radioButton3.setFocusable(false);

        CodeDialog codeDialog = new CodeDialog(); // Get code dialog ready
        // You have to send arguments for the dialog in a bundle because
        // They don't like non-empty constructors
        String c = generateCode(); // Get a random code
        Bundle codeBundle = new Bundle(1);
        codeBundle.putString("code", c);
        codeDialog.setArguments(codeBundle);
        codeDialog.setCancelable(false);
        codeDialog.show(getSupportFragmentManager(), "code dialog"); // Create dialog

        Bicker bicker = new Bicker();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Bicker");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Date temp_approved_date = new Date();
        temp_approved_date.setTime(0);

        // Get keywords for the bicker based on title and content
        String fulltext = bickTitle.trim() + " " + bickSide;
        KeywordTokenizer k = new KeywordTokenizer(fulltext);
        ArrayList<Keyword> keywords = k.getKeywords();
        ArrayList<String> skeys = KeywordTokenizer.keysToStrings(keywords);

        // Initialize the new bicker for the DB
        bicker.setCode(c);
        bicker.setCreate_date(date);
        bicker.setApproved_date(temp_approved_date);
        bicker.setDescription(bickDesc.trim());
        bicker.setLeft_side(bickSide);
        bicker.setRight_side("None");
        bicker.setLeft_votes(0);
        bicker.setRight_votes(0);
        bicker.setTitle(bickTitle.trim());
        bicker.setCategory(bickCat.trim());
        bicker.setSenderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        bicker.setReceiverID("Unknown");
        bicker.setTags(tags);
        bicker.setSeconds_until_expired(seconds_until_expired);
        bicker.setKeywords(skeys);
        ref.push().setValue(bicker);

        // Subscribe creator to messaging
        ref.orderByChild("code").equalTo(c).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    Log.d(TAG, "PUSHID: " + key);
                    /*FirebaseMessaging.getInstance().subscribeToTopic(key + "delete")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Notification success";
                                    if (!task.isSuccessful()) {
                                        msg = "Notification failure";
                                    }
                                    Log.d(TAG, msg);

                                }
                            });*/

                    FirebaseMessaging.getInstance().subscribeToTopic(key + "creatorNotification")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Notification succeeded";
                                    if (!task.isSuccessful()) {
                                        msg = "Notification failed";
                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(CreateActivity.this,"Bicker Sent", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String generateCode() {
        String c = "";
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int dif = r.nextInt(26);
            c += (char)('A'+dif);
        }

        //TODO: For the X in 11,881,376 chance there is a duplicate code floating around. Check DB
        return c;
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(CreateActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void leave() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
