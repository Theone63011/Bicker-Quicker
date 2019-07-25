package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    TextView tag1;
    TextView tag2;
    TextView tag3;
    int numTags = 0;
    String tag_string1;
    String tag_string2;
    String tag_string3;
    FloatingActionButton addTag;
    EditText tag;
    Censor censor;
    ArrayList<String> recvTags;
    ArrayList<String> recvKeys;
    private TextView bicker_tag_censor;
    TextView side_censor;

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
        side_censor = findViewById(R.id.bicker_side_censor);
        side_censor.setVisibility(View.GONE);

        tag = findViewById(R.id.tagFieldR);
        addTag = findViewById(R.id.fabAddTagR);
        tag1 = findViewById(R.id.liveTagR1);
        tag2 = findViewById(R.id.liveTagR2);
        tag3 = findViewById(R.id.liveTagR3);
        numTags = 0;
        tag_string1 = null;
        tag_string2 = null;
        tag_string3 = null;
        bicker_tag_censor = findViewById(R.id.bicker_tag_censorR);
        bicker_tag_censor.setVisibility(View.GONE);

        side.addTextChangedListener(sideWatcher);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User/" + userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("matureContent").exists() && (Boolean.parseBoolean(dataSnapshot.child("matureContent").getValue().toString()))) {
                    censor = new Censor(true);
                } else {
                    censor = new Censor(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        tag.addTextChangedListener(tagWatcher);

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

        // Ensure no duplicate tags from same user
        if (tag_string1 != null && s.toLowerCase().compareTo(tag_string1.toLowerCase()) == 0) {
            return false;
        }

        if (tag_string2 != null && s.toLowerCase().compareTo(tag_string2.toLowerCase()) == 0) {
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

    private final TextWatcher sideWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(valid > 0) {
                side_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    side_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    side_censor.setText("Inappropriate Input");
                }
            }
            else {
                side_censor.setVisibility(View.GONE);
            }
        }

        public void afterTextChanged(Editable s) {
            int valid = 0;
            if(censor.check_chars(s.toString()) == false) valid = 1;
            if(censor.check_words(s.toString()) == false) valid = 2;
            if(valid > 0) {
                side_censor.setVisibility(View.VISIBLE);
                if(valid == 1) {
                    side_censor.setText("Invalid Character");
                }
                if(valid == 2) {
                    side_censor.setText("Inappropriate Input");
                }
            }
            else {
                side_censor.setVisibility(View.GONE);
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
    };

    @Override
    public void onStart(){
        if(bicker != null) {
            if (bicker.getCode().equals("code_used")) {
                startActivity(new Intent(RespondActivity.this, HomeActivity.class));
            }
        }
        super.onStart();
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

        Boolean censor_passed = true;
        if(censor.check_chars(bicker.getRight_side()) == false) censor_passed = false;
        if(censor.check_words(bicker.getRight_side()) == false) censor_passed = false;
        if(censor_passed == false) {
            //Log.d(TAG, "Censor not passed");
            Toast.makeText(RespondActivity.this, "Invalid Input.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(censor.getUseMatureWordList()){
            Censor nonMatureCensor = new Censor(false);

            if(nonMatureCensor.check_words(bicker.getRight_side()) == false) {
                bicker.setMatureContent(true);
            }
        }

        ArrayList respTags = new ArrayList();

        if (tag_string1 != null && !tag_string1.equals(""))
            respTags.add(tag_string1);

        if (tag_string2 != null && !tag_string2.equals(""))
            respTags.add(tag_string2);

        if (tag_string3 != null && !tag_string3.equals(""))
            respTags.add(tag_string3);

        bicker.setTags(unionTags(recvTags, respTags));

        ArrayList<Keyword> keys = KeywordTokenizer.stringsToKeys(recvKeys);
        String fulltext = bicker.getTitle().trim() + " " + side.getText().toString();
        KeywordTokenizer k = new KeywordTokenizer(fulltext);
        ArrayList<Keyword> keywords = k.getKeywords();
        ArrayList<Keyword> union = unionKeys(keys, keywords);

        bicker.setKeywords(KeywordTokenizer.keysToStrings(unionKeys(keys, keywords)));

        Date approved_date = new Date();
        bicker.setApproved_date(approved_date);

        //subscribing to deletion topic
        DatabaseReference ref2 = db.getReference("Bicker");
        ref2.orderByChild("code").equalTo(bicker.getCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    bicker.setKey(key);
                    Log.d("Tag: ", "@PUSHID: " + key);
                    /*FirebaseMessaging.getInstance().subscribeToTopic(key + "delete")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Notification success";
                                    if (!task.isSuccessful()) {
                                        msg = "Notification failure";
                                    }
                                    Log.d("Tag:", msg);

                                }
                            });*/

                    FirebaseMessaging.getInstance().subscribeToTopic(key + "creatorNotification")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Notification succeeded: " + key + "creatorNotification";
                                    if (!task.isSuccessful()) {
                                        msg = "Notification failed";
                                    }
                                    Log.d("Tag:", msg);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Subscribe creators to messaging
      /* DatabaseReference ref3 = db.getReference();
        ref3.child("Bicker/" + bicker.getKey()).addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic(bicker.getKey() + "creatorNotification")
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
                    catch (Exception e){
                        //Log.e(TAG, "ERROR: could not update left_votes for bicker " + dataSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });*/

        ref.setValue(bicker);

        // Add bicker Category to the Category section of database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String category = bicker.getCategory();
        DatabaseReference databaseReference = database.getReference();
        DatabaseReference categoryRef = database.getReference("Category");
        DatabaseReference categoryRef2 = database.getReference("Category/" + category);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() == false) {
                    //Log.d(TAG, "Create_activity: categoryRef dataSnapshot does not exists");
                    categoryRef2.child("Active_IDs").child("1").setValue(bicker.getKey());
                    categoryRef2.child("count").setValue(1);
                }
                else {
                    //Log.d(TAG, "Create_activity: categoryRef dataSnapshot exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        categoryRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() == false) {
                    //Log.d(TAG, "Create_activity: ref3 dataSnapshot does not exists");
                    categoryRef2.child("Active_IDs").child("1").setValue(bicker.getKey());
                    categoryRef2.child("count").setValue(1);
                }
                else {
                    //Log.d(TAG, "Create_activity: ref3 dataSnapshot exists");
                    int count = Integer.parseInt(dataSnapshot.child("count").getValue().toString());
                    count++;
                    categoryRef2.child("IDs").child(Integer.toString(count)).setValue(bicker.getKey());
                    categoryRef2.child("count").setValue(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        recvTags = bicker.getTags();
        recvKeys = bicker.getKeywords();
    }

    public ArrayList<String> unionTags(ArrayList<String> old, ArrayList<String> resp) {
        ArrayList<String> union = new ArrayList<String>();
        if (old != null && old.size() != 0)
            union.addAll(old);
        else
            return resp;

        for (String n : resp) {
            boolean add = true;
            for (String s : old) {
                if (s.toLowerCase().equals(n.toLowerCase()))
                    add = false;
            }
            if (add)
                union.add(n);
        }

        return union;
    }

    public ArrayList<Keyword> unionKeys(ArrayList<Keyword> old, ArrayList<Keyword> resp) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (Keyword k : old) {
            map.put(k.word, k.value);
        }

        for (Keyword k : resp) {
            if (map.get(k.word) == null) {
                map.put(k.word, k.value);
            } else {
                map.put(k.word, k.value + map.get(k.word));
            }
        }

        KeywordTokenizer k = new KeywordTokenizer("");
        ArrayList<Keyword> sorted = k.mapToList(map);
        return sorted;
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
