package purdue.edu.bicker_quicker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    This class is a very simplistic way of testing if bickers are fetched properly from the DB
    If the flag is true, it will fetch all expired bickers in the DB
    Otherwise, it will fetch all of the current user's bickers
    These bickers are then displayed in a simple TextView
*/
public class BasicBickerView extends AppCompatActivity {

    boolean flag;
    private ArrayList<Bicker> bickers;
    private FirebaseUser currUser;
    public TextView bickerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_bicker_view);
        Bundle b = getIntent().getExtras();
        flag = false;
        if (b != null) {
            flag = b.getBoolean("expBick");
            //if flag is true, get expired bickers from db
            //otherwise, get all bickers created by current user
        }

        bickerInfo = (TextView) findViewById(R.id.bickerText);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        bickers = new ArrayList<Bicker>();

        if (flag) {
            //get expired bickers
            ((TextView) findViewById(R.id.typeOfView)).setText("Expired bickers");
            getExpiredBickers(ref);
        } else {
            ((TextView) findViewById(R.id.typeOfView)).setText("Your created bickers");
            getUsersBickers(ref);
        }
    }

    //method to populate bickers arraylist with expired bickers
    public void getExpiredBickers(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            String res = "";
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currId = currUser.getUid();
                for (DataSnapshot expBickerSnapshot : dataSnapshot.child("ExpiredBicker").getChildren()) {
                    bickers.add(expBickerSnapshot.getValue(Bicker.class));
                    System.out.println("Expired bicker added: " + expBickerSnapshot.getValue(Bicker.class));
                    res += (expBickerSnapshot.getValue(Bicker.class)).toString();
                }
                if (!res.equals("")) {
                    ((TextView) findViewById(R.id.bickerText)).setText(res);
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //method to populate bickers arraylist with bickers current user created
    public void getUsersBickers(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String res = "";
                String currId = currUser.getUid();
                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {
                    if (bickerSnapshot != null && bickerSnapshot.child("senderID").getValue().toString().equals(currId)) {
                        //if the sender is the current user, add the bicker to the list
                        bickers.add(bickerSnapshot.getValue(Bicker.class));
                        res += (bickerSnapshot.getValue(Bicker.class)).toString();
                        System.out.println("Bicker added: " + bickerSnapshot.getValue(Bicker.class));
                    }
                }
                for (DataSnapshot expBickerSnapshot : dataSnapshot.child("ExpiredBicker").getChildren()) {
                    if (expBickerSnapshot != null && expBickerSnapshot.child("senderID").getValue().toString().equals(currId)) {
                        bickers.add(expBickerSnapshot.getValue(Bicker.class));
                        System.out.println("Expired bicker added: " + expBickerSnapshot.getValue(Bicker.class));
                        res += (expBickerSnapshot.getValue(Bicker.class)).toString();
                    }
                }
                if (!res.equals("")) {
                    ((TextView) findViewById(R.id.bickerText)).setText(res);
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
