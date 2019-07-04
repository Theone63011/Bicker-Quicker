package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


public class Home_Fragment extends Fragment {
    OnBickerPressedListener callback;

    private OnBickerPressedListener mListener;

    private FirebaseDatabase database;
    private ArrayList<Bicker> bickers;

    private static ArrayList<LinearLayout> closed_bicker_layout_list;
    private static ArrayList<LinearLayout> open_bicker_layout_list;


    private static final String TAG = HomeActivity.class.getSimpleName();
    private boolean voted;
    List<String> votedBickerIds;
    FirebaseUser user;
    String userKey;

    private Button leftVote;
    private Button rightVote;
    private Button noVote;

    public Home_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_Fragment newInstance(String param1, String param2) {
        Home_Fragment fragment = new Home_Fragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            voted = getArguments().getBoolean("voted");
        }

        database = FirebaseDatabase.getInstance();

        DatabaseReference databaseRef = database.getReference();

        bickers = new ArrayList<>();
        votedBickerIds = new ArrayList<String>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        closed_bicker_layout_list = new ArrayList<LinearLayout>();
        open_bicker_layout_list = new ArrayList<LinearLayout>();


        databaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = user.getUid();

                for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()){
                    try {
                        if (userSnapshot.child("userId") != null && userSnapshot.child("userId").getValue().toString().equals(id)) {
                            userKey = userSnapshot.getKey();

                            for (DataSnapshot votedId : userSnapshot.child("votedBickerIds").getChildren()) {
                                votedBickerIds.add(votedId.getValue().toString());
                            }
                        }
                    }
                    catch (Exception e){
                        Log.w(TAG, "Home_Fragment detected a null user in the database.   " + e);
                    }
                }

                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {

                    if(bickerSnapshot.child("code").getValue().toString().equals("code_used") && votedBickerIds.contains(bickerSnapshot.getKey()) == voted) {
                        bickers.add(new Bicker(
                                bickerSnapshot.child("title").getValue() != null ? bickerSnapshot.child("title").getValue().toString() : "No title",
                                bickerSnapshot.child("left_side").getValue() != null ? bickerSnapshot.child("left_side").getValue().toString() : "No left side",
                                bickerSnapshot.child("right_side").getValue() != null ? bickerSnapshot.child("right_side").getValue().toString() : "No right side",
                                (int) (long) bickerSnapshot.child("left_votes").getValue(),
                                (int) (long) bickerSnapshot.child("right_votes").getValue(),
                                bickerSnapshot.child("category").getValue() != null ? bickerSnapshot.child("category").getValue().toString() : "No category",
                                bickerSnapshot.getKey()));
                    }
                }

                ArrayAdapter<Bicker> adapter = new Home_Fragment.bickerArrayAdapter(getActivity(), 0, bickers);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBickerPressedListener) {
            mListener = (OnBickerPressedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //method to update the vote count for left (1) or right (2)
    public void vote(String key, int response, int leftSideVotes, int rightSideVotes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //access bicker where id == LhrhW1FAWRsJXfopsuR
        //DatabaseReference ref = response == 1 ? database.getReference("Bicker/-LhrhW1FAWRsJXfopsuR").orderByChild("left_votes") :
        //        database.getReference("Bicker").child("right_votes");
        DatabaseReference ref = database.getReference();

        String path = "Bicker/" + key + "/left_votes";

        path.charAt(2);

        if (response == 1) {
            leftSideVotes++;
            ref.child("Bicker/" + key + "/left_votes").setValue(leftSideVotes);
        } else if (response == 2) {
            rightSideVotes++;
            ref.child("Bicker/" + key + "/right_votes").setValue(rightSideVotes);
        }

        //update User db w/ this bicker's id
        ref.child("User/" + userKey + "/votedBickerIds").push().setValue(key);

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public void setOnBickerPressedListener(OnBickerPressedListener callback) {
        this.callback = callback;
    }

    public interface OnBickerPressedListener {
        // Required. Currently does nothing, can be changed and used if fragment needs to communicate with activity
        void onBickerPressed(int position);
    }

    //custom ArrayAdapter for filling the listView
    class bickerArrayAdapter extends ArrayAdapter<Bicker> {

        private Context context;
        private List<Bicker> bickers;

        //constructor
        public bickerArrayAdapter(Context context, int resource, ArrayList<Bicker> bickers) {
            super(context, resource, bickers);

            this.context = context;
            this.bickers = bickers;
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            Bicker bicker = bickers.get(position);

            int total = bicker.getLeft_votes() + bicker.getRight_votes();
            String total_votes = Integer.toString(total);
            total_votes += " Votes";

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_unvoted_bicker, null);

            TextView hiddenKey = view.findViewById(R.id.hiddenBickerKey);
            hiddenKey.setText((bicker.getKey()));

            TextView hiddenLeftVotes = view.findViewById(R.id.hiddenLeftVotes);
            hiddenLeftVotes.setText(String.valueOf(bicker.getLeft_votes()));

            TextView hiddenRightVotes = view.findViewById(R.id.hiddenRightVotes);
            hiddenRightVotes.setText(String.valueOf(bicker.getRight_votes()));

            TextView closed_title = view.findViewById(R.id.closed_title);
            closed_title.setText(bicker.getTitle());

            TextView open_title = view.findViewById(R.id.open_title);
            open_title.setText(bicker.getTitle());

            TextView closed_category = view.findViewById(R.id.closed_category);
            TextView open_category = view.findViewById(R.id.open_category);
            String catName = bicker.getCategory();
            closed_category.setText(catName);
            closed_category.setTextColor(Color.WHITE);
            open_category.setText(catName);
            open_category.setTextColor(Color.WHITE);
            Drawable catDraw = ContextCompat.getDrawable(getActivity(), R.drawable.shape_category);

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
                        Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                        Toast.makeText(getActivity(), "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                                "See colors.xml for correct options" , Toast.LENGTH_LONG).show();

            }

            closed_category.setBackground(catDraw);
            closed_category.setPadding(8, 8, 8, 8);
            open_category.setBackground(catDraw);
            open_category.setPadding(8, 8, 8, 8);

            TextView leftSide = view.findViewById(R.id.leftSide);
            leftSide.setText(bicker.getLeft_side());

            TextView rightSide = view.findViewById(R.id.rightSide);
            rightSide.setText(bicker.getRight_side());

            //int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;

            //Log.d(TAG, "Parents width= " + screenWidth);

            LinearLayout closed_bicker_holder = view.findViewById(R.id.closed_bicker_holder);
            LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);

            LinearLayout closed_header = view.findViewById(R.id.closed_header);
            LinearLayout open_header = view.findViewById(R.id.open_header);

            LinearLayout closed_voteCountHolder = view.findViewById(R.id.closed_voteCount_holder);
            LinearLayout open_voteCountHolder = view.findViewById(R.id.open_voteCount_holder);

            TextView closed_vote_count = view.findViewById(R.id.closed_vote_count_text);
            TextView open_vote_count = view.findViewById(R.id.open_vote_count_text);
            closed_vote_count.setText(total_votes);
            open_vote_count.setText(total_votes);

            open_bicker_holder.setVisibility(View.GONE);

            closed_bicker_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBickerClick(v, closed_bicker_holder, open_bicker_holder);
                }
            });

            open_bicker_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBickerClick(v, closed_bicker_holder, open_bicker_holder);
                }
            });

            closed_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBickerClick(v, closed_bicker_holder, open_bicker_holder);
                }
            });

            open_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBickerClick(v, closed_bicker_holder, open_bicker_holder);
                }
            });

            closed_voteCountHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            open_voteCountHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });


            leftVote = view.findViewById(R.id.left);
            rightVote = view.findViewById(R.id.right);
            noVote = view.findViewById(R.id.abstain);

            if(voted == true){
                leftVote.setText(Integer.toString(bicker.getLeft_votes()));
                rightVote.setText(Integer.toString(bicker.getRight_votes()));
                noVote.setText("Already Voted");

                leftVote.setEnabled(false);
                rightVote.setEnabled(false);
                noVote.setEnabled(false);
            }

            leftVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    LinearLayout parentView = (LinearLayout)v.getParent();
                    leftVote = parentView.findViewById(R.id.left);
                    rightVote = parentView.findViewById(R.id.right);
                    noVote = parentView.findViewById(R.id.abstain);

                    TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                    TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                    TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                    leftVote.setText(Integer.toString(Integer.parseInt(hiddenLeftVotes.getText().toString()) + 1));
                    rightVote.setText(hiddenRightVotes.getText().toString());
                    noVote.setText("Left");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);

                    vote(hiddenKey.getText().toString(), 1, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));

                    int tot = bicker.getLeft_votes() + bicker.getRight_votes() + 1;
                    String tot_str = Integer.toString(tot);
                    tot_str += " Votes";
                    open_vote_count.setText(tot_str);
                    closed_vote_count.setText(tot_str);
                }
            });

            rightVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    LinearLayout parentView = (LinearLayout)v.getParent();
                    leftVote = parentView.findViewById(R.id.left);
                    rightVote = parentView.findViewById(R.id.right);
                    noVote = parentView.findViewById(R.id.abstain);

                    TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                    TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                    TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                    leftVote.setText(hiddenLeftVotes.getText().toString());
                    rightVote.setText(Integer.toString(Integer.parseInt(hiddenRightVotes.getText().toString()) + 1));
                    noVote.setText("Right");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);

                    vote(hiddenKey.getText().toString(), 2, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));

                    int tot = bicker.getLeft_votes() + bicker.getRight_votes() + 1;
                    String tot_str = Integer.toString(tot);
                    tot_str += " Votes";
                    open_vote_count.setText(tot_str);
                    closed_vote_count.setText(tot_str);

                }
            });

            noVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    LinearLayout parentView = (LinearLayout)v.getParent();
                    leftVote = parentView.findViewById(R.id.left);
                    rightVote = parentView.findViewById(R.id.right);
                    noVote = parentView.findViewById(R.id.abstain);

                    TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                    TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                    TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                    leftVote.setText(hiddenLeftVotes.getText().toString());
                    rightVote.setText(hiddenRightVotes.getText().toString());
                    noVote.setText("Abstain");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);

                    vote(hiddenKey.getText().toString(), 0, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));
                }
            });

            return view;
        }

        public void onBickerClick (View view, LinearLayout closed_bicker, LinearLayout open_bicker) {
            if(closed_bicker.isShown()){
                open_bicker.setVisibility(View.VISIBLE);
                AnimationHandler.slide_down(getActivity(), open_bicker);
                closed_bicker.setVisibility(View.GONE);
            }
            else{
                AnimationHandler.slide_up(getActivity(), open_bicker);
                closed_bicker.setVisibility(View.VISIBLE);
                open_bicker.setVisibility(View.GONE);
            }
        }
    }
}
