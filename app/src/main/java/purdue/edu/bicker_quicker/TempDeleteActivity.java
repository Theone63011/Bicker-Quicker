package purdue.edu.bicker_quicker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TempDeleteActivity extends AppCompatActivity {

    private EditText bickerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_delete);

        bickerKey = findViewById(R.id.bickerKeyEdit);
    }

    public void delete(View v){
        //TODO: Get key from notification
        String key = bickerKey.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                    userSnapshot.child("votedBickerIds").child(key).getRef().setValue(null);;
                }

                dataSnapshot.child("Bicker/" + key).getRef().setValue(null);
                //TODO: uncomment when past_bickers is implemented
                //dataSnapshot.child("IMPLEMENT ME/" + key).getRef().setValue(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
