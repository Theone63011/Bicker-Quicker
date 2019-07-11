package purdue.edu.bicker_quicker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TempDeleteActivity extends AppCompatActivity {

    private EditText bickerKeyText;
    private EditText requestKeyText;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_delete);

        bickerKeyText = findViewById(R.id.bickerKeyEdit);
        requestKeyText = findViewById(R.id.bickerRequestKeyEdit);
    }

    public void requestDelete(View v){
        final String bickerKey = requestKeyText.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String requesterId = user.getUid();
                String requesterKey = "";

                String receiverId;

                // This loop adds the user's voted on bickers to the votedBickerIds list and bickers_votes map
                for(DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()){
                    if(userSnapshot.child("userId").getValue().toString().equals(requesterId)){
                        requesterKey = userSnapshot.getKey();
                        DatabaseReference requesterRef = userSnapshot.getRef();
                        requesterRef.child("sentDeletionRequests").child(bickerKey).setValue("Pending");
                    }
                }

                if(dataSnapshot.child("Bicker").child(bickerKey).child("senderID").getValue().equals(
                        dataSnapshot.child("User").child(requesterKey).getValue())){
                    receiverId = dataSnapshot.child("Bicker").child(bickerKey).child("receiverID").getValue().toString();
                }
                else{
                    receiverId = dataSnapshot.child("Bicker").child(bickerKey).child("senderID").getValue().toString();
                }

                for(DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()){
                    if(userSnapshot.child("userId").getValue().toString().equals(receiverId)){
                        DatabaseReference requesterRef = userSnapshot.getRef();
                        requesterRef.child("receivedDeletionRequests").child(bickerKey).setValue("Pending");
                    }
                }

            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void delete(View v){
        //TODO: Get key from notification
        final String key = bickerKeyText.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    userSnapshot.child("votedBickerIds").child(key).getRef().setValue(null);
                }

                dataSnapshot.child("Bicker/" + key).getRef().setValue(null);
                dataSnapshot.child("ExpiredBicker/" + key).getRef().setValue(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
