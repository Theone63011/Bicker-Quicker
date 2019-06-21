package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RespondActivity extends AppCompatActivity implements EnterCodeDialog.EnterCodeDialogListener, CancelBickerDialog.CancelBickerDialogListener {

    TextView title_code;
    TextView title_title;
    TextView title_description;
    TextView title_side;
    TextView code_here;
    TextView title_here;
    TextView description_here;
    EditText side;
    Button cancelBicker;
    Button send_side;
    Toolbar toolbar;
    Bicker bicker;
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);
        cl = findViewById(R.id.RespondContainer);
        cl.setVisibility(View.INVISIBLE);

        title_code = findViewById(R.id.bickerCode);
        title_description = findViewById(R.id.bickerDescription);
        title_title = findViewById(R.id.bickerTitle);
        title_side = findViewById(R.id.yourSide);
        code_here = findViewById(R.id.codeHere);
        title_here = findViewById(R.id.titleHere);
        description_here = findViewById(R.id.descHere);
        side = findViewById(R.id.respondSide);
        send_side = findViewById(R.id.submitResponse);
        cancelBicker = findViewById(R.id.cancelbutton);
        toolbar = findViewById(R.id.toolbarRespond);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Profile");
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

        openDialog();

        send_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (side.getText().toString().trim().equals(""))
                    failEntry();
                else
                    submitEntry();
            }
        });

        cancelBicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog();
            }
        });
    }


    public void openCancelDialog() {
        CancelBickerDialog cancelDialog = new CancelBickerDialog();
        cancelDialog.show(getSupportFragmentManager(), "Cancel Dialog");
    }


    public void cancelBicker() {
        // Delete bicker from database
        Toast.makeText(this, "Cancel Bicker", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("Bicker").child(bicker.getReceiverID()).removeValue();
        leave();
    }


    public void failEntry() {
        title_side.setTextColor(Color.parseColor("#FF758C"));
    }

    public void submitEntry() {
        bicker.setRight_side(side.getText().toString());
        String bickerID = bicker.getReceiverID();
        bicker.setReceiverID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Bicker/"+bickerID);
        bicker.setCode("code_used");
        ref.setValue(bicker);
        Toast.makeText(this, "Response Sent", Toast.LENGTH_LONG).show();
        leave();
    }

    @Override
    public void receiveCode(Bicker code) {
        cl.setVisibility(View.VISIBLE);
        code_here.setText(code.getCode().toUpperCase());
        title_here.setText(code.getTitle());
        description_here.setText(code.getDescription());
        bicker = code;
    }

    public void openDialog() {
        EnterCodeDialog enterCode = new EnterCodeDialog();
        enterCode.show(getSupportFragmentManager(), "enter code dialog");
    }

    public void leave() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
