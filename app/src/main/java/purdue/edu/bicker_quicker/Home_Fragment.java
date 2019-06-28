package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_unvoted_bicker, null);

            TextView hiddenKey = view.findViewById(R.id.hiddenBickerKey);
            hiddenKey.setText((bicker.getKey()));

            TextView hiddenLeftVotes = view.findViewById(R.id.hiddenLeftVotes);
            hiddenLeftVotes.setText(String.valueOf(bicker.getLeft_votes()));

            TextView hiddenRightVotes = view.findViewById(R.id.hiddenRightVotes);
            hiddenRightVotes.setText(String.valueOf(bicker.getRight_votes()));

            TextView title = view.findViewById(R.id.title);
            title.setText(bicker.getTitle());

            TextView category = view.findViewById(R.id.category);
            category.setText(bicker.getCategory());

            TextView leftSide = view.findViewById(R.id.leftSide);
            leftSide.setText(bicker.getLeft_side());

            TextView rightSide = view.findViewById(R.id.rightSide);
            rightSide.setText(bicker.getRight_side());

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
    }
}
