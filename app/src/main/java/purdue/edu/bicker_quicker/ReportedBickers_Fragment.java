package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.facebook.AccessTokenManager.TAG;

public class ReportedBickers_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //private static final String TAG = ReportedBickersActivity.class.getSimpleName();

    private ReportedBickers_Fragment.OnBickerPressedListener mListener;

    ReportedBickers_Fragment.OnBickerPressedListener callback;

    private ArrayList<Bicker> bickers;
    private List<String> votedBickerIds;
    private HashMap<String, String> bickers_votes;
    //private static ArrayList<LinearLayout> closed_bicker_layout_list;
    //private static ArrayList<LinearLayout> open_bicker_layout_list;
    private LinearLayout choice_label_holder;
    private TextView choose_side_label;
    private Button leftVote;
    private Button rightVote;
    private Button noVote;
    private Button remove;
    private Button cancel;
    FirebaseUser user;
    private FirebaseDatabase database;
    String bickerKey;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static Fragment repBickers_Fragment = null;
    private ReportedBickersActivity repBickersActivityRef = null;

    public ReportedBickers_Fragment() {}

    public static ReportedBickers_Fragment newInstance(int param1) {
        ReportedBickers_Fragment fragment = new ReportedBickers_Fragment();

        //Bundle args = new Bundle();
        //args.putInt("ARG_PARAM1", param1);
        //fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_past_bickers);

        database = FirebaseDatabase.getInstance();

        DatabaseReference databaseRef = database.getReference();

        Query sort = database.getReference("Bicker").orderByChild("reportCount");

        bickers = new ArrayList<>();
        votedBickerIds = new ArrayList<String>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        bickers_votes = new HashMap<String, String >();

        //Query user_create_date = database.getReference("User").orderByChild("create_date");
        //Query bicker_create_date = database.getReference("Bicker").orderByChild("create_date"); //create_date

        //setReferenceToRepBickersActivity();

        sort.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = user.getUid();



                // This loop adds all created expired bickers to the bickers array
                for (DataSnapshot bickerSnapshot : dataSnapshot.getChildren()) {
                    if (bickerSnapshot.child("reported").getValue() != null && bickerSnapshot.child("reported").getValue().toString().equals("true")){
                        Bicker newBicker = new Bicker(
                                bickerSnapshot.child("title").getValue() != null ? bickerSnapshot.child("title").getValue().toString() : "No title",
                                bickerSnapshot.child("left_side").getValue() != null ? bickerSnapshot.child("left_side").getValue().toString() : "No left side",
                                bickerSnapshot.child("right_side").getValue() != null ? bickerSnapshot.child("right_side").getValue().toString() : "No right side",
                                (int) (long) bickerSnapshot.child("left_votes").getValue(),
                                (int) (long) bickerSnapshot.child("right_votes").getValue(),
                                (int) (long) bickerSnapshot.child("total_votes").getValue(),
                                bickerSnapshot.child("reportCount").getValue() != null ? (int) (long) bickerSnapshot.child("reportCount").getValue() : 0,
                                bickerSnapshot.child("category").getValue() != null ? bickerSnapshot.child("category").getValue().toString() : "No category",
                                bickerSnapshot.getKey(),
                                (double) (long) bickerSnapshot.child("seconds_until_expired").getValue()
                        );


                         bickers.add(newBicker);
                    }
                }

                Collections.reverse(bickers);

                ArrayAdapter<Bicker> adapter = new ReportedBickers_Fragment.bickerArrayAdapter(getActivity(), 0, bickers);

                ListView listView = getView().findViewById(R.id.bickerListView);
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
        return inflater.inflate(R.layout.activity_expired_bickers__fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReportedBickers_Fragment.OnBickerPressedListener) {
            mListener = (ReportedBickers_Fragment.OnBickerPressedListener) context;
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

    public void setOnBickerPressedListener(ReportedBickers_Fragment.OnBickerPressedListener callback) {
        this.callback = callback;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Home_fragment: Inside onRefresh");

        //timerThread.interrupt();

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(true);

        refreshContent();
    }

    public void set_repBicker_fragment(Fragment f) {
        repBickers_Fragment = f;
    }

    private void refreshContent() {
        Log.d(TAG, "Home_fragment: Inside refreshContent");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Home_fragment: Inside Run");

                //updateBickerList();
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);

                //Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");

                Fragment currentFragment = repBickers_Fragment;

                if(currentFragment == null) {
                    Log.d(TAG, "Home_fragment: ERROR- currentFragment is NULL");
                    return;
                }

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(false);
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commitNow();

                //fragmentTransaction.hide(currentFragment);
                //fragmentTransaction.hide(getActivity().getSupportFragmentManager().getPrimaryNavigationFragment());
                repBickersActivityRef.refresh_fragment();


                /*Intent intent = new Intent(getActivity(), Home_Fragment.class);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);*/

                Log.d(TAG, "Home_fragment: end of run()");
            }
        }, 500);
    }

    public void setReferenceToRepBickersActivity(ReportedBickersActivity ref) {
        repBickersActivityRef = (ReportedBickersActivity) ref;
    }

    public interface OnBickerPressedListener {
        // Required. Currently does nothing, can be changed and used if fragment needs to communicate with activity
        void onBickerPressed(int position);
    }

    class bickerArrayAdapter extends ArrayAdapter<Bicker> {

        private Context context;
        private List<Bicker> bickers;
        private DecimalFormat df = new DecimalFormat("0.0");
        private boolean expired;

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

            //expired = false;

            int total = bicker.getLeft_votes() + bicker.getRight_votes();
            String total_votes = display_votes(Double.valueOf(total));

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_reported_bickers, null);

            final ViewGroup sideContainer = (ViewGroup) view.findViewById(R.id.side_holder);

            TextView leftLabel = view.findViewById(R.id.left_label);
            TextView rightLabel = view.findViewById(R.id.right_label);

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

            //TextView leftSide = view.findViewById(R.id.leftSide);
            //leftSide.setText(bicker.getLeft_side());

            //TextView rightSide = view.findViewById(R.id.rightSide);
            //rightSide.setText(bicker.getRight_side());

            //int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;

            //Log.d(TAG, "Parents width= " + screenWidth);

            LinearLayout closed_bicker_holder = view.findViewById(R.id.closed_bicker_holder);
            LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);

            TextView closed_clock = view.findViewById(R.id.closed_clock);
            TextView open_clock = view.findViewById(R.id.open_clock);

            ImageView closed_timer = view.findViewById(R.id.closed_timer);
            ImageView open_timer = view.findViewById(R.id.open_timer);

            /*closed_clock.setVisibility(View.GONE);
            open_clock.setVisibility(View.GONE);
            closed_timer.setVisibility(View.GONE);
            open_timer.setVisibility(View.GONE);*/


            LinearLayout closed_header = view.findViewById(R.id.closed_header);
            LinearLayout open_header = view.findViewById(R.id.open_header);

            LinearLayout closed_voteCountHolder = view.findViewById(R.id.closed_voteCount_holder);
            LinearLayout open_voteCountHolder = view.findViewById(R.id.open_voteCount_holder);

            TextView closed_vote_count = view.findViewById(R.id.closed_vote_count_text);
            //TextView open_vote_count = view.findViewById(R.id.open_vote_count_text);
            closed_vote_count.setText(Integer.toString(bicker.getReportCount()) + " Reports");
            Log.d(TAG, "Report count: " + bicker.getReportCount());
            //open_vote_count.setText(total_votes);

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
            leftVote.setText(bicker.getLeft_side());
            rightVote = view.findViewById(R.id.right);
            rightVote.setText(bicker.getRight_side());
            noVote = view.findViewById(R.id.abstain);

            choice_label_holder = view.findViewById(R.id.choice_label_holder);
            choose_side_label = view.findViewById(R.id.choose_side_label);

            leftVote.setBackgroundResource(R.drawable.side_prechoice_blue);
            rightVote.setBackgroundResource(R.drawable.side_prechoice_purple);

            remove = view.findViewById(R.id.removedBicker);
            cancel = view.findViewById(R.id.cancelReport);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
                    bob.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            removeBicker(bicker);
                        }
                    });
                    bob.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //hide dialog
                        }
                    });
                    bob.setMessage("Are you sure you want to delete this bicker?");
                    bob.create();
                    bob.show();
                    /*
                    ref.child("Bicker/" + bicker.getKey()).setValue(null);

                   ref.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                               userSnapshot.child("votedBickerIds").child(bicker.getKey()).getRef().setValue(null);
                               userSnapshot.child("sentDeletionRequests").child(bicker.getKey()).getRef().setValue(null);
                               userSnapshot.child("receivedDeletionRequests").child(bicker.getKey()).getRef().setValue(null);

                           }

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
                   */
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref.child("Bicker/" + bicker.getKey() + "/reported").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue().toString().equals("true")){
                                ref.child("Bicker/" + bicker.getKey() + "/reported").setValue(false);
                                ref.child("Bicker/" + bicker.getKey() + "/reportCount").setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            boolean voted = false;

            if(votedBickerIds.contains(bicker.getCode())) {
                voted = true;
            }
            /*
            DatabaseReference databaseRef = database.getReference();

            databaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String id = user.getUid();
                    String voted_id;
                    String side;
                    String code;
                    String bicker_id;

                    // This loop determines if the current bicker is expired
                    for (DataSnapshot userSnapshot : dataSnapshot.child("ExpiredBicker").getChildren()) {
                        try {
                            if (userSnapshot.child("userId") != null && userSnapshot.child("userID").getValue().toString().equals(bicker.getCode())) {
                                expired = true;
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Home_Fragment detected a null user in the database.   " + e);
                        }
                    }

                }


                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

            if(expired) {
                choice_label_holder.setVisibility(View.VISIBLE);
                choose_side_label.setText("Expired");
                choose_side_label.setTextColor(getResources().getColor(R.color.red));

            }
            else {
                choice_label_holder.setVisibility(View.GONE);
            }
            */

            if(voted == true){
                //leftVote.setText(Integer.toString(bicker.getLeft_votes()));
                //rightVote.setText(Integer.toString(bicker.getRight_votes()));
                //noVote.setText("Already Voted");

                leftVote.setText(bicker.getLeft_side());
                rightVote.setText(bicker.getRight_side());
                noVote.setText("Abstain");
                noVote.setVisibility(View.GONE);

                leftVote.setEnabled(false);
                rightVote.setEnabled(false);
                noVote.setEnabled(false);
                leftLabel.setEnabled(false);
                rightLabel.setEnabled(false);

                String bickerCode = hiddenKey.getText().toString();
                String side_voted = bickers_votes.get(bickerCode);

                if(side_voted.equalsIgnoreCase("left")){
                    Log.d(TAG, "Home_fragment: left vote found");
                    leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_blue);
                    rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_blue);
                }
                else if(side_voted.equalsIgnoreCase("right")) {
                    Log.d(TAG, "Home_fragment: right vote found");
                    leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_purple);
                    rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_purple);
                }
                else if(side_voted.equalsIgnoreCase("abstain")) {
                    Log.d(TAG, "Home_fragment: abstain vote found");
                    leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_purple);
                    rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_blue);
                    noVote.setVisibility(View.VISIBLE);
                    noVote.setTextColor(getResources().getColor(R.color.blue_purple_mix));
                }
                else {
                    Log.d(TAG, "Home_fragment: ERROR- side_voted not assigned");
                    Toast.makeText(getActivity(), "Home_fragment: ERROR- side_voted not assigned", Toast.LENGTH_LONG).show();
                }
            }



            return view;
        }

        public void removeBicker(Bicker bicker) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();

            ref.child("Bicker/" + bicker.getKey()).setValue(null);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.child("User").getChildren()) {
                        userSnapshot.child("votedBickerIds").child(bicker.getKey()).getRef().setValue(null);
                        userSnapshot.child("sentDeletionRequests").child(bicker.getKey()).getRef().setValue(null);
                        userSnapshot.child("receivedDeletionRequests").child(bicker.getKey()).getRef().setValue(null);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void requestDelete(final String bickerKey){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseMessaging.getInstance().subscribeToTopic(bickerKey + "delete")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Notification success";
                            if (!task.isSuccessful()) {
                                msg = "Notification failure";
                            }
                            Log.d(TAG, msg);

                        }
                    });
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

                    if(dataSnapshot.child("Bicker").child(bickerKey).child("senderID").getValue().equals(requesterId)){
                        receiverId = dataSnapshot.child("Bicker").child(bickerKey).child("receiverID").getValue().toString();
                    }
                    else{
                        receiverId = dataSnapshot.child("Bicker").child(bickerKey).child("senderID").getValue().toString();
                    }

                    //TODO: Send a message to 'receiverId' that the they have a pending bicker deletion request

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

        public String display_votes(double d) {
            String ret = null;

            double zero = 0.0;
            double thousand = 1000.0;
            double ten_thousand = 10000.0;
            double hundred_thousand = 100000.0;
            double million = 1000000.0;
            double ten_million = 10000000.0;
            double hundred_million = 100000000.0;
            double billion = 1000000000.0;

            df.setRoundingMode(RoundingMode.DOWN);
            if(d < zero) {
                Log.d(TAG, "Home_fragment ERROR: vote count is negative in display_votes");
                Toast.makeText(getActivity(), "Home_fragment ERROR: vote count is negative in display_votes", Toast.LENGTH_LONG).show();
            }
            else if(d >= zero && d < thousand) {
                ret = Integer.toString((int)d) + " Votes";
            }
            else if(d >= thousand && d < ten_thousand) {
                ret = df.format(d / thousand);
                ret += "K Votes";
            }
            else if(d >= ten_thousand && d < hundred_thousand) {
                ret = df.format(d / thousand);
                ret += "K Votes";
            }
            else if(d >= hundred_thousand && d < million) {
                ret = df.format(d / thousand);
                ret += "K Votes";
            }
            else if(d >= million && d < ten_million) {
                ret = df.format(d / million);
                ret += "M Votes";
            }
            else if(d >= ten_million && d < hundred_million) {
                ret = df.format(d / million);
                ret += "M Votes";
            }
            else if(d >= hundred_million && d < billion) {
                ret = df.format(d / million);
                ret += "M Votes";
            }
            else if(d >= billion) {
                ret = df.format(d / billion);
                ret += "B Votes";
            }
            else {
                Log.d(TAG, "Home_fragment- ERROR in display_votes. d = " + d);
                Toast.makeText(getActivity(), "Home_fragment- ERROR in display_votes", Toast.LENGTH_LONG).show();
            }

            return ret;
        }
    }

}
