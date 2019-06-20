package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    EditText title;             // Editable Fields
    EditText description;
    EditText side;

    Button submitBicker;

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
        toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Bicker");
        toolbar.setSubtitle("Enter Your Bicker Details Below");

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
        catSpin.setOnItemSelectedListener(this);
    }

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

        boolean failed = false;

        if (bickTitle.trim().equals("")) {
            failed = true;
            bickerTitle.setTextColor(Color.parseColor("#FF758C"));
        }

        if (bickDesc.trim().equals("")) {
            failed = true;
            bickerDescription.setTextColor(Color.parseColor("#FF758C"));
        }

        if (bickCat.trim().equals("")) {
            failed = true;
            bickerCategory.setTextColor(Color.parseColor("#FF758C"));
        }

        if (bickSide.trim().equals("")) {
            failed = true;
            yourSide.setTextColor(Color.parseColor("#FF758C"));
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
        bicker.setSenderID("TempSender");
        bicker.setReceiverID("Unknown");
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
        Intent intent = new Intent(this, BickerActivity.class);
        startActivity(intent);
    }
}
