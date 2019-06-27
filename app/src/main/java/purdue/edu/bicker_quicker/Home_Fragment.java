package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Home_Fragment extends Fragment {
    OnBickerPressedListener callback;

    private OnBickerPressedListener mListener;

    private FirebaseDatabase database;
    private ArrayList<Bicker> bickers;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private boolean voted;

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

        databaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {

                    bickers.add(new Bicker(
                            bickerSnapshot.child("title").getValue() != null ? bickerSnapshot.child("title").getValue().toString() : "No title",
                            bickerSnapshot.child("right_side").getValue() != null ? bickerSnapshot.child("right_side").getValue().toString() : "No right side",
                            bickerSnapshot.child("left_side").getValue() != null ? bickerSnapshot.child("left_side").getValue().toString() : "No left side",
                            (int) (long) bickerSnapshot.child("right_votes").getValue(),
                            (int) (long) bickerSnapshot.child("left_votes").getValue(),
                            bickerSnapshot.child("category").getValue() != null ? bickerSnapshot.child("category").getValue().toString() : "No category",
                            bickerSnapshot.getKey()));
                }



                ArrayAdapter<Bicker> adapter = new Home_Fragment.bickerArrayAdapter(getActivity(), 0, bickers);

                ListView listView = getView().findViewById(R.id.unvotedListView);
                listView.setAdapter(adapter);
                int count = listView.getAdapter().getCount();

                //We can't set visibility to GONE until after all list elements are loaded or they will overlap
                for ( int i=0; i < listView.getAdapter().getCount(); i++) {
                    View child = listView.getAdapter().getView(i, null, null);
                    LinearLayout dropdown = child.findViewById(R.id.dropdown);
                    dropdown.setVisibility(View.GONE);
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
        DatabaseReference ref = database.getReference("Bicker/" + key);

        if (response == 1) {
            leftSideVotes++;
            ref.child("left_votes").setValue(leftSideVotes);
        } else if (response == 2) {
            rightSideVotes++;
            ref.child("right_votes").setValue(rightSideVotes);
        }

        //update textfields of voting counts


        //update User db w/ this bicker's id

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //List<String> votedBickerIds = new ArrayList<String>();
        System.out.println("CurrUserID: " + user.getUid());
        final DatabaseReference userRef = database.getReference("User/" + user.getUid());
        //final User currUser;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currUser = dataSnapshot.getValue(User.class);
                List<String> votedBickerIds = new ArrayList<String>();
                votedBickerIds = currUser.getVotedBickerIds();
                votedBickerIds.add("-LhrhW1FAWRsJXfopsuR");
                userRef.child("votedBickerids").setValue(votedBickerIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_unvoted_bicker, null);

            TextView title = view.findViewById(R.id.title);
            title.setText(bicker.getTitle());

            TextView leftSide = view.findViewById(R.id.leftSide);
            leftSide.setText(bicker.getRight_side());

            TextView rightSide = view.findViewById(R.id.rightSide);
            rightSide.setText(bicker.getLeft_side());

            LinearLayout header = view.findViewById(R.id.header);

            LinearLayout dropdown = view.findViewById(R.id.dropdown);
            dropdown.setVisibility(View.GONE);

            header.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LinearLayout parentView = (LinearLayout)v.getParent();

                    LinearLayout dropdown = parentView.findViewById(R.id.dropdown);
                    if(dropdown.isShown()){
                        AnimationHandler.slide_up(getActivity(), dropdown);
                        dropdown.setVisibility(View.GONE);
                    }
                    else{
                        dropdown.setVisibility(View.VISIBLE);
                        AnimationHandler.slide_down(getActivity(), dropdown);
                    }
                }
            });

            leftVote = view.findViewById(R.id.left);
            rightVote = view.findViewById(R.id.right);
            noVote = view.findViewById(R.id.abstain);

            leftVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View V){

                    leftVote.setText(Integer.toString(bicker.getLeft_votes() + 1));
                    rightVote.setText(Integer.toString(bicker.getRight_votes()));
                    noVote.setText("Left");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);

                    vote(bicker.getKey(), 1, bicker.getLeft_votes(), bicker.getRight_votes());
                }
            });

            rightVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View V){

                    leftVote.setText(Integer.toString(bicker.getLeft_votes()));
                    rightVote.setText(Integer.toString(bicker.getRight_votes() + 1));
                    noVote.setText("Right");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);

                    vote(bicker.getKey(), 2, bicker.getLeft_votes(), bicker.getRight_votes());
                }
            });

            noVote.setOnClickListener(new View.OnClickListener(){
                public void onClick(View V){
                    leftVote.setText(Integer.toString(bicker.getLeft_votes()));
                    rightVote.setText(Integer.toString(bicker.getRight_votes()));
                    noVote.setText("Abstain");

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);
                }
            });

            return view;
        }
    }
}
