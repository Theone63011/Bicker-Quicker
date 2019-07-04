package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.common.StringUtils;

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
    private TextView bicker_title_censor;
    private TextView bicker_desc_censor;
    private TextView bicker_side_censor;
    private TextView bicker_tag_censor;

    EditText title;             // Editable Fields
    EditText description;
    EditText side;
    EditText tag;

    Button submitBicker;
    FloatingActionButton addTag;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static Censor censor;

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
        bicker_title_censor = findViewById(R.id.bicker_title_censor);
        bicker_desc_censor = findViewById(R.id.bicker_desc_censor);
        bicker_side_censor = findViewById(R.id.bicker_side_censor);
        bicker_tag_censor = findViewById(R.id.bicker_tag_censor);
        bicker_title_censor.setVisibility(View.GONE);
        bicker_desc_censor.setVisibility(View.GONE);
        bicker_side_censor.setVisibility(View.GONE);
        bicker_tag_censor.setVisibility(View.GONE);
        title.addTextChangedListener(titleWatcher);
        description.addTextChangedListener(descWatcher);
        side.addTextChangedListener(sideWatcher);
        tag.addTextChangedListener(tagWatcher);

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.support_simple_spinner_dropdown_item);
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
            bicker_tag_censor.setText("Length Minimum: 2 chars");
            bicker_tag_censor.setVisibility(View.VISIBLE);
            return false;
        }

        s = s.toLowerCase();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1); // Cap first letter

        /*
        if (censor.check_chars(s) == false || censor.check_words(s) == false || censor.check_tag_length(s))
            return false;
        */

        if (bicker_tag_censor.getVisibility() == View.VISIBLE) {
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
                bicker_title_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_title_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_title_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_title_censor.setText("Length Limit: 18 chars");
                }
            }
            else {
                bicker_title_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_title_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_title_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_title_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_title_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_title_censor.setText("Length Limit: 18 chars");
                }
            }
            else {
                bicker_title_censor.setVisibility(View.GONE);
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
                bicker_desc_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_desc_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_desc_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_desc_censor.setText("Length Limit: 50 chars");
                }
            }
            else {
                bicker_desc_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(censor.check_desc_length(s.toString()) == false) valid = 3;
            if(valid > 0) {
                bicker_desc_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_desc_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_desc_censor.setText("Inappropriate Input");
                }
                if(valid == 3) {
                    bicker_desc_censor.setText("Length Limit: 50 chars");
                }
            }
            else {
                bicker_desc_censor.setVisibility(View.GONE);
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
                    bicker_tag_censor.setText("Length Limit: 12 chars");
                } else if (valid == 1) {
                    bicker_tag_censor.setText("Invalid Character");
                } else if (valid == 2) {
                    bicker_tag_censor.setText("Inappropriate Input");
                }

                bicker_tag_censor.setVisibility(View.VISIBLE);
            } else {
                bicker_tag_censor.setVisibility(View.GONE);
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
                    bicker_tag_censor.setText("Tags Must Be Shorter than 12 Characters");
                } else if (valid == 1) {
                    bicker_tag_censor.setText("Invalid Character");
                } else if (valid == 2) {
                    bicker_tag_censor.setText("Inappropriate Input");
                }

                bicker_tag_censor.setVisibility(View.VISIBLE);
            } else {
                bicker_tag_censor.setVisibility(View.GONE);
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
                bicker_side_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_side_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_side_censor.setText("Inappropriate Input");
                }
            }
            else {
                bicker_side_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(valid > 0) {
                bicker_side_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    bicker_side_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    bicker_side_censor.setText("Inappropriate Input");
                }
            }
            else {
                bicker_side_censor.setVisibility(View.GONE);
            }
        }
    };


    public  void resetColor(TextView textView, String color) {
        textView.setTextColor(Color.parseColor(color));
    }

    public void submitNewBicker() {
        // Validate all fields
        String bickTitle = title.getText().toString();
        String bickDesc = description.getText().toString();
        TextView tv = (TextView) catSpin.getSelectedView();
        String bickCat = tv.getText().toString();
        String bickSide = side.getText().toString();

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

        // Disable all fields
        title.setFocusable(false);
        description.setFocusable(false);
        catSpin.setFocusable(false);
        side.setFocusable(false);

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

        // Initialize the new bicker for the DB
        bicker.setCode(c);
        bicker.setCreate_date(date);
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
        ref.push().setValue(bicker);
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
