package purdue.edu.bicker_quicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterCodeDialog extends AppCompatDialogFragment {

    private Button submitButton;
    private EditText codeInput;
    private TextView enterBelow;
    private EnterCodeDialogListener listener;
    final Bicker bick = new Bicker();

    boolean allowTalkingToSelf = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EnterCodeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement EnterCodeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_entercode, null);
        setCancelable(false);
        builder.setView(view).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.leave();
            }
        });

        submitButton = view.findViewById(R.id.getBickerInfo);
        codeInput = view.findViewById(R.id.enterCodeHere);
        enterBelow = view.findViewById(R.id.enterCodeBelow);

        codeInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                enterBelow.setTextColor(Color.parseColor("#777777"));
                enterBelow.setText("Enter the Code Given to You Below");
                return false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeInput.getText().toString().toUpperCase().trim();
                if (code.length() != 5) {
                    enterBelow.setText("Invalid Code: Code should be 5 letters");
                    enterBelow.setTextColor(Color.parseColor("#FF758C"));
                    return;
                }
                String bickerID = null;
                try {
                    bickerID = queryDatabase(code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return builder.create();
    }

    // Use this to check for codes
    public void loadDatabase(String bickerCode) {

        final String code = bickerCode;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.orderByChild("code").equalTo(bickerCode);
        bick.setCode(null);
        ref.child("Bicker").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();  // Get bickers

                // USE THIS BICKER.CLASS FOR FUTURE USE!!!!
                for (DataSnapshot bicker : children) {
                    Bicker value = bicker.getValue(Bicker.class);
                    if (value.getCode().equals(code)) {
                        bick.setCode(value.getCode());
                        bick.setTitle(value.getTitle());
                        bick.setDescription(value.getDescription());
                        bick.setCategory(value.getCategory());
                        bick.setSenderID(value.getSenderID());
                        bick.setRight_votes(value.getRight_votes());
                        bick.setLeft_side(value.getLeft_side());
                        bick.setLeft_votes(value.getLeft_votes());
                        bick.setCreate_date(value.getCreate_date());
                        bick.setReceiverID(bicker.getKey());
                        bick.setTags(value.getTags());
                        bick.setKeywords(value.getKeywords());
                        bick.setSeconds_until_expired(value.getSeconds_until_expired());
                    }
                }

                callback(); // Done iterating, data found, initiate callback
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // See if that code is valid. Return id
    public String queryDatabase(final String code) throws InterruptedException {

        loadDatabase(code);
        submitButton.setText("Fetching...");
        return "";
    }

    public void callback() {
        enterBelow.setTextColor(Color.parseColor("#00FF00"));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        DatabaseReference databaseReference2 = database.getReference("Database_Settings");

        // This is used to find the boolean value of allowTalkingToSelf in database and set variable
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() == false) {
                    //This means that the Database_Settings section IS NOT in the database
                    Log.d("EnterCodeDialog.java", "Enter_code_dialog: databaseReference2 does not exist.");
                }
                else {
                    //This means that the Database_Settings section IS in the database
                    String data = dataSnapshot.child("allowTalkingToSelf").getValue().toString();
                    allowTalkingToSelf = Boolean.parseBoolean(data);
                }

                // Continue EnterCodeDiaglog operations
                if (bick.getCode() == null) {    // Bicker with given code not found
                    enterBelow.setText("That Code Didn't Work, Try Again");
                    enterBelow.setTextColor(Color.parseColor("#FF758C"));
                    submitButton.setText("Get Bicker");
                    return;
                }

                else if (allowTalkingToSelf == false && bick.getSenderID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) { // Check if the person is talking with themselves
                    enterBelow.setText("Stop Talking to Yourself");
                    enterBelow.setTextColor(Color.parseColor("#FF758C"));
                    submitButton.setText("Get Bicker");
                }
                else {
                    listener.receiveCode(bick);
                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface EnterCodeDialogListener {
        void receiveCode(Bicker bicker);
        void leave(); // Leave on cancel button pressed
    }


}
