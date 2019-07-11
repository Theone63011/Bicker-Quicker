package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PastBickersActivity extends AppCompatActivity {

    private ArrayList<Bicker> bickers;
    private static ArrayList<LinearLayout> open_bicker_layout_list;

    public PastBickersActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_bickers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        bickers = new ArrayList<Bicker>();
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currId = currUser.getUid();
                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {
                    if (bickerSnapshot.child("senderID").equals(currId)) {
                        //if the sender is the current user, add the bicker to the list
                        bickers.add(bickerSnapshot.getValue(Bicker.class));
                    }
                }
                for (DataSnapshot expBickerSnapshot : dataSnapshot.child("ExpiredBicker").getChildren()) {
                    if (expBickerSnapshot.child("senderID").equals(currId)) {
                        bickers.add(expBickerSnapshot.getValue(Bicker.class));
                    }
                }
                ArrayAdapter<Bicker> adapter = new PastBickersActivity.bickerArrayAdapter(getApplicationContext(), 0, bickers);
                ListView listView = getView().findViewById(R.id.unvotedListView);
                listView.setAdapter(adapter);
                int count = listView.getAdapter().getCount();

                //We can't set visibility to GONE until after all list elements are loaded or they will overlap
                for ( int i=0; i < listView.getAdapter().getCount(); i++) {
                    View child = listView.getAdapter().getView(i, null, null);
                    LinearLayout open_bicker = child.findViewById(R.id.open_bicker_holder);
                    //open_bicker.setVisibility(View.GONE);
                }
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    class bickerArrayAdapter extends ArrayAdapter<Bicker> {
        private Context context;
        private List<Bicker> bickers;

        //constructor
        public bickerArrayAdapter(Context context, int resource, ArrayList<Bicker> bickers) {
            super(context, resource, bickers);

            this.context = context;
            this.bickers = bickers;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Bicker bicker = bickers.get(position);
            int total = bicker.getLeft_votes() + bicker.getRight_votes();
            String total_votes = Integer.toString(total);
            total_votes += " Votes";

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_voted_bicker, null);

            TextView leftLabel = view.findViewById(R.id.left_label);
            TextView rightLabel = view.findViewById(R.id.right_label);
            TextView leftVotes = view.findViewById(R.id.left_votes);
            TextView rightVotes = view.findViewById(R.id.right_votes);
            TextView open_title = view.findViewById(R.id.open_title);
            TextView open_category = view.findViewById(R.id.open_category);

            String catName = bicker.getCategory();
            leftLabel.setText(bicker.getLeft_side());
            rightLabel.setText(bicker.getRight_side());
            leftVotes.setText(bicker.getLeft_votes());
            rightVotes.setText(bicker.getRight_votes());
            open_category.setText(catName);
            open_title.setText(bicker.getTitle());
            open_category.setTextColor(Color.WHITE);
            Drawable catDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_category);

            //Below sets the correct color of the category icon
            switch (catName) {
                case "Art":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Art), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Board Games":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_BoardGames), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Books":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Books), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Comedy":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Comedy), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Food":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Food), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Movies":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Movies), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Music":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Music), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Philosophy":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Philosophy), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Politics":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Politics), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Relationships":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Relationships), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Science":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Science), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Sports":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Sports), PorterDuff.Mode.MULTIPLY);
                    break;
                case "TV Shows":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_TvShows), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Video Games":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_VideoGames), PorterDuff.Mode.MULTIPLY);
                    break;
                case "Miscellaneous":
                    catDraw.setColorFilter(ContextCompat.getColor(context, R.color.category_Misc), PorterDuff.Mode.MULTIPLY);
                    break;

                default:
                    //Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                    Toast.makeText(getApplicationContext(), "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                            "See colors.xml for correct options" , Toast.LENGTH_LONG).show();

            }


            open_category.setBackground(catDraw);
            open_category.setPadding(8, 8, 8, 8);

            LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);
            LinearLayout open_header = view.findViewById(R.id.open_header);
            LinearLayout open_voteCountHolder = view.findViewById(R.id.open_voteCount_holder);
            TextView open_vote_count = view.findViewById(R.id.open_vote_count_text);
            open_vote_count.setText(total_votes);

            return convertView;
        }
    }
}


