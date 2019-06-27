package purdue.edu.bicker_quicker;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.*;
        import com.google.firebase.database.FirebaseDatabase;
        import com.firebase.ui.auth.AuthUI;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;

        import java.util.ArrayList;
        import java.util.List;

public class ViewVoting extends AppCompatActivity {
    Button leftVote;
    Button rightVote;
    Button noVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //have bicker ID passed in
        //DatabaseReference ref = database.getReference("Bicker/" + bickerID);
        DatabaseReference ref = database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR");
        if (ref == null) {
            System.out.println("Nope");
            return;
        }
        setContentView(R.layout.activity_view_voting); //add view_bicker to R.layout?
        leftVote = findViewById(R.id.left);
        rightVote = findViewById(R.id.right);
        noVote = findViewById(R.id.abstain);
        Button exit = findViewById(R.id.exit);
        //final TextView numLeftVotes = findViewById(R.id.leftVotes);
        //final TextView numRightVotes = findViewById(R.id.rightVotes);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Bicker selectedBicker = dataSnapshot.getValue(Bicker.class);
                System.out.println("Left votes: " + selectedBicker.getLeft_votes());
                System.out.println("Right votes: " + selectedBicker.getRight_votes());
                //numLeftVotes.setText(Integer.toString(selectedBicker.getLeft_votes()));
                //numRightVotes.setText(Integer.toString(selectedBicker.getRight_votes()));
                leftVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote(1, selectedBicker.getLeft_votes() + 1, selectedBicker.getRight_votes());
                    }
                });
                rightVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote(2,  selectedBicker.getLeft_votes(), selectedBicker.getRight_votes() + 1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        noVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewVotes();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
    }

    //method to update the vote count for left (1) or right (2)
    public void vote(int response, int updatedLeftCount, int updatedRightCount) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //access bicker where id == LhrhW1FAWRsJXfopsuR
        //DatabaseReference ref = response == 1 ? database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR").orderByChild("left_votes") :
        //        database.getReference("Bicker").child("right_votes");
        DatabaseReference ref = database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR");

        //update textfields of voting counts
        final TextView numLeftVotes = findViewById(R.id.leftVotes);
        final TextView numRightVotes = findViewById(R.id.rightVotes);
        numLeftVotes.setText(Integer.toString(updatedLeftCount));
        numRightVotes.setText(Integer.toString(updatedRightCount));

        if (response == 1) {
            ref.child("left_votes").setValue(updatedLeftCount);
        } else if (response == 2) {
            ref.child("right_votes").setValue(updatedRightCount);
        }
        //update User db w/ this bicker's id
        viewVotes();
    }

    public void viewVotes() {
        //disable voting buttons, show results
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        leftVote.setEnabled(false);
        rightVote.setEnabled(false);
        noVote.setEnabled(false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //List<String> votedBickerIds = new ArrayList<String>();
        System.out.println("CurrUserID: " + user.getUid());
        final DatabaseReference ref = database.getReference("User/" + user.getUid());
        //final User currUser;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currUser = dataSnapshot.getValue(User.class);
                List<String> votedBickerIds = new ArrayList<String>();
                votedBickerIds = currUser.getVotedBickerIds();
                votedBickerIds.add("-LhrhW1FAWRsJXfopsuR");
                ref.child("votedBickerids").setValue(votedBickerIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void exit() {
        Intent intent = new Intent(this, BickerActivity.class);
        startActivity(intent);
    }
}