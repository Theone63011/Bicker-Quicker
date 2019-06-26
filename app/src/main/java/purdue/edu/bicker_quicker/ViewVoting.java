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

        import com.google.firebase.database.*;
        import com.google.firebase.database.FirebaseDatabase;
        import com.firebase.ui.auth.AuthUI;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;

public class ViewVoting extends AppCompatActivity {
    Button leftVote;
    Button rightVote;
    Button noVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
        final TextView numLeftVotes = findViewById(R.id.leftVotes);
        final TextView numRightVotes = findViewById(R.id.rightVotes);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Bicker selectedBicker = dataSnapshot.getValue(Bicker.class);
                System.out.println("Left votes: " + selectedBicker.getLeft_votes());
                System.out.println("Right votes: " + selectedBicker.getRight_votes());
                numLeftVotes.setText(Integer.toString(selectedBicker.getLeft_votes()));
                numRightVotes.setText(Integer.toString(selectedBicker.getRight_votes()));
                leftVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote(1, selectedBicker.getLeft_votes() + 1);
                    }
                });
                rightVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote(2, selectedBicker.getRight_votes() + 1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        //set listeners for voting buttons
        /*leftVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(1);
            }
        });
        rightVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(2);
            }
        }); */
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
    public void vote(int response, int updatedCount) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //access bicker where id == LhrhW1FAWRsJXfopsuR
        //DatabaseReference ref = response == 1 ? database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR").orderByChild("left_votes") :
        //        database.getReference("Bicker").child("right_votes");
        DatabaseReference ref = database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR");

        if (response == 1) {
            ref.child("left_votes").setValue(updatedCount);
        } else if (response == 2) {
            ref.child("right_votes").setValue(updatedCount);
        }
        //update User db w/ this bicker's id
        viewVotes();
    }

    public void viewVotes() {
        //disable voting buttons, show results
        leftVote.setEnabled(false);
        rightVote.setEnabled(false);
        noVote.setEnabled(false);


    }

    public void exit() {
        Intent intent = new Intent(this, BickerActivity.class);
        startActivity(intent);
    }
}