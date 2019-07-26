package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Home_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    OnBickerPressedListener callback;

    private OnBickerPressedListener mListener;

    private FirebaseDatabase database;
    private ArrayList<Bicker> bickers;
    private ArrayList<Bicker> filteredBickers;

    private static ArrayList<LinearLayout> closed_bicker_layout_list;
    private static ArrayList<LinearLayout> open_bicker_layout_list;


    private static final String TAG = HomeActivity.class.getSimpleName();
    private boolean voted;

    List<String> votedBickerIds;
    private HashMap<String, String> bickers_votes;

    // This HashMap holds the bickerIDs and their corresponding approved_date time
    private HashMap<String, Long> bickers_approved_time_milliseconds;

    FirebaseUser user;
    String userKey;

    Boolean allowMatureContent;

    private Button leftVote;
    private Button rightVote;
    private Button noVote;
    private Button report;

    private Thread timer_thread;

    private LinearLayout choice_label_holder;

    public static String sortBy = "recent";
    public static Integer listViewPositionFirstFragment = 0;
    public static Integer listViewPositionSecondFragment = 0;
    public static Boolean isFirstFragment = true;

    private HomeActivity homeActivityReference = null;

    private static Fragment home_Fragment = null;
    private static String home_Fragment_tag = null;

    private static ListView listView;

    private static ArrayAdapter<Bicker> adapter;

    private static SwipeRefreshLayout swipeRefreshLayout;

    private static Thread timerThread;

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

    public static void set_home_fragment (Fragment f, String tag) {
        //Log.d(TAG, "Home_fragment: Inside set_home_Fragment");

        home_Fragment = f;
        int id = home_Fragment.getId();
        home_Fragment_tag = tag;

        //Log.d(TAG, "Home_fragment: home_fragment_tag: " + home_Fragment_tag);
        //Log.d(TAG, "Home_fragment: home_fragment id: " + id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "Home_fragment: Inside onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            voted = getArguments().getBoolean("voted");
        }

        database = FirebaseDatabase.getInstance();

        DatabaseReference databaseRef = database.getReference();

        bickers = new ArrayList<>();
        filteredBickers = new ArrayList<>();
        votedBickerIds = new ArrayList<String>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = databaseRef.child("User/" + user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("matureContent").exists() && (Boolean.parseBoolean(dataSnapshot.child("matureContent").getValue().toString()))) {
                    allowMatureContent = true;
                } else {
                    allowMatureContent = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        bickers_approved_time_milliseconds = new HashMap<String, Long>();

        bickers_votes = new HashMap<String, String>();

        closed_bicker_layout_list = new ArrayList<LinearLayout>();
        open_bicker_layout_list = new ArrayList<LinearLayout>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // This is called after onCreate

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.green),
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.orange));



        if (sortBy == "recent") {
            this.sortByRecent();
        } else if (sortBy == "popularity") {
            this.sortByPopularity();
        }
        return rootView;
    }

    @Override
    public void onRefresh() {
        //Log.d(TAG, "Home_fragment: Inside onRefresh");

        //timerThread.interrupt();

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(true);

        refreshContent();
    }

    private void refreshContent() {
        //Log.d(TAG, "Home_fragment: Inside refreshContent");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "Home_fragment: Inside Run");

                //updateBickerList();
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);

                //Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");

                Fragment currentFragment = home_Fragment;

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
                homeActivityReference.refresh_fragment();


                /*Intent intent = new Intent(getActivity(), Home_Fragment.class);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);*/

                //Log.d(TAG, "Home_fragment: end of run()");
            }
        }, 500);
    }

    public ArrayList<Bicker> returnBickerArrayList() {
        this.filteredBickers = new ArrayList<Bicker>(this.bickers);
        return this.filteredBickers;
    }

    public void updateBickerList(ArrayList<Bicker> update) {
        if (update != null) {
            filteredBickers = update;
        }

        if(getActivity() == null) {
            return;
        }

        ArrayAdapter<Bicker> adapter = new Home_Fragment.bickerArrayAdapter(getActivity(), 0, filteredBickers);
        adapter = new Home_Fragment.bickerArrayAdapter(getActivity(), 0, filteredBickers);

        listView = getView().findViewById(R.id.unvotedListView);
        listView.setAdapter(adapter);
        int count = listView.getAdapter().getCount();

        //We can't set visibility to GONE until after all list elements are loaded or they will overlap
        for ( int i=0; i < listView.getAdapter().getCount(); i++) {
            View child = listView.getAdapter().getView(i, null, null);
            LinearLayout open_bicker = child.findViewById(R.id.open_bicker_holder);
            //open_bicker.setVisibility(View.GONE);
        }

        if (isFirstFragment) {
            listView.setSelection(listViewPositionFirstFragment);
        } else {
            listView.setSelection(listViewPositionSecondFragment);
        }
    }

    public void setReferenceToHomeActivity(HomeActivity ref) {
        homeActivityReference = ref;
    }

    public void sortByRecent() {
        sortBy = "recent";

        Query user_create_date = database.getReference("User").orderByChild("create_date");
        Query bicker_create_date = null;
        if (HomeActivity.showActiveBickers) {
            System.out.println("Sorting recent active bickers");
            bicker_create_date = database.getReference("Bicker").orderByChild("create_date"); //create_date
        } else {
            System.out.println("Sorting recent expired bickers");
            bicker_create_date = database.getReference("ExpiredBicker").orderByChild("create_date"); //create_date
        }

        initialize_view(user_create_date, bicker_create_date);
    }

    public void sortByPopularity() {
        sortBy = "popularity";

        Query user_category = database.getReference("User").orderByChild("total_votes");//total_votes
        Query bicker_category = null;
        if (HomeActivity.showActiveBickers) {
            System.out.println("Sorting popular active bickers");
            bicker_category = database.getReference("Bicker").orderByChild("total_votes"); //total votes
        } else {
            System.out.println("Sorting popular expired bickers");
            bicker_category = database.getReference("ExpiredBicker").orderByChild("total_votes"); //total votes
        }
        initialize_view(user_category, bicker_category);
    }

    public void initialize_view (Query userQuery, Query bickerQuery) {

        if(getActivity() == null) {
            return;
        }

        bickers = new ArrayList<>();

        userQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = user.getUid();
                String voted_id;
                String side;
                String code;
                String bicker_id;

                // This loop adds the user's voted on bickers to the votedBickerIds list and bickers_votes map
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    try {
                        if (userSnapshot.child("userId") != null && userSnapshot.child("userId").getValue().toString().equals(id)) {
                            userKey = userSnapshot.getKey();

                            for (DataSnapshot votedId : userSnapshot.child("votedOnBickers").getChildren()) {
                                voted_id = votedId.getKey().toString();
                                side = votedId.child("Side Voted").getValue().toString();
                                votedBickerIds.add(voted_id);
                                if (bickers_votes.isEmpty() == false) {
                                    if (bickers_votes.containsKey(voted_id) == false) {
                                        bickers_votes.put(voted_id, side);
                                    }
                                } else {
                                    bickers_votes.put(voted_id, side);
                                }
                            }
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



        bickerQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long temp_time;

                // This loop adds all bickers to the bickers array
                for (DataSnapshot bickerSnapshot : dataSnapshot.getChildren()) {

                    temp_time = Long.parseLong(bickerSnapshot.child("approved_date").child("time").getValue().toString());
                    bickers_approved_time_milliseconds.put(bickerSnapshot.getKey(), temp_time);


                    Boolean isBickerMature = false;
                    if(bickerSnapshot.child("matureContent").exists()) {
                        isBickerMature = Boolean.parseBoolean(bickerSnapshot.child("matureContent").getValue().toString());
                    }


                    if(bickerSnapshot.child("code").getValue().toString().equals("code_used") && votedBickerIds.contains(bickerSnapshot.getKey()) == voted && (isBickerMature == false || allowMatureContent == true)) {
                        bickers.add(new Bicker(
                                bickerSnapshot.child("title").getValue() != null ? bickerSnapshot.child("title").getValue().toString() : "No title",
                                bickerSnapshot.child("description").getValue() != null ? bickerSnapshot.child("description").getValue().toString() : "No description",
                                bickerSnapshot.child("left_side").getValue() != null ? bickerSnapshot.child("left_side").getValue().toString() : "No left side",
                                bickerSnapshot.child("right_side").getValue() != null ? bickerSnapshot.child("right_side").getValue().toString() : "No right side",
                                null, //(we do not use the date property) bickerSnapshot.child("create_date").getValue() != null ? bickerSnapshot.child("create_date").getValue() : "No create_date"
                                null, //(we do not use the date property) bickerSnapshot.child("approved_date").getValue() != null ? bickerSnapshot.child("approved_date").getValue() : "No approved_date"
                                (int) (long) bickerSnapshot.child("left_votes").getValue(),
                                (int) (long) bickerSnapshot.child("right_votes").getValue(),
                                (int) (long) bickerSnapshot.child("total_votes").getValue(),
                                bickerSnapshot.child("reportCount").getValue() != null ? (int) (long) bickerSnapshot.child("reportCount").getValue() : 0,
                                bickerSnapshot.child("code").getValue() != null ? bickerSnapshot.child("code").getValue().toString() : "No code",
                                bickerSnapshot.child("category").getValue() != null ? bickerSnapshot.child("category").getValue().toString() : "No category",
                                bickerSnapshot.child("senderID").getValue() != null ? bickerSnapshot.child("senderID").getValue().toString() : "No senderID",
                                bickerSnapshot.child("receiverID").getValue() != null ? bickerSnapshot.child("receiverID").getValue().toString() : "No receiverID",
                                bickerSnapshot.getKey(),
                                bickerSnapshot.child("tags").getValue() != null ? (ArrayList<String>)bickerSnapshot.child("tags").getValue() : null,
                                bickerSnapshot.child("keywords").getValue() != null ? (ArrayList<String>)bickerSnapshot.child("keywords").getValue() : null,
                                bickerSnapshot.child("votedUsers").getValue() != null ? (ArrayList<String>)bickerSnapshot.child("votedUsers").getValue() : null,
                                (double) (long) bickerSnapshot.child("seconds_until_expired").getValue()
                        ));
                        if (HomeActivity.showExpiredBickers) {
                            System.out.println("Expired bicker added");
                            System.out.println(bickers);
                        }
                    }
                }

                Collections.reverse(bickers);

                if(getActivity() == null) {
                    return;
                }

                adapter = new Home_Fragment.bickerArrayAdapter(getActivity(), 0, bickers);

                listView = getView().findViewById(R.id.unvotedListView);
                listView.setAdapter(adapter);
                int count = listView.getAdapter().getCount();

                //We can't set visibility to GONE until after all list elements are loaded or they will overlap
                for ( int i=0; i < listView.getAdapter().getCount(); i++) {
                    View child = listView.getAdapter().getView(i, null, null);
                    LinearLayout open_bicker = child.findViewById(R.id.open_bicker_holder);
                    //open_bicker.setVisibility(View.GONE);
                }

                if (isFirstFragment) {
                    listView.setSelection(listViewPositionFirstFragment);
                } else {
                    listView.setSelection(listViewPositionSecondFragment);
                }

                isFirstFragment = !isFirstFragment;

                if (homeActivityReference != null) {
                    homeActivityReference.applyFilter(
                            homeActivityReference.showActiveBickers,
                            homeActivityReference.showExpiredBickers,
                            homeActivityReference.categoryFilter,
                            homeActivityReference.keys);
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isFirstFragment) {
            listView = getView().findViewById(R.id.unvotedListView);
            listViewPositionFirstFragment = listView.getFirstVisiblePosition();
        } else {
            listView = getView().findViewById(R.id.unvotedListView);
            listViewPositionSecondFragment = listView.getFirstVisiblePosition();
        }

        isFirstFragment = !isFirstFragment;
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

            ref.child("Bicker/" + key).addListenerForSingleValueEvent( new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        try {
                            dataSnapshot.child("left_votes").getRef().setValue(
                                    Integer.parseInt(dataSnapshot.child("left_votes").getValue().toString()) + 1);
                            dataSnapshot.child("total_votes").getRef().setValue(
                                    Integer.parseInt(dataSnapshot.child("left_votes").getValue().toString()) + 1 +
                                            Integer.parseInt(dataSnapshot.child("right_votes").getValue().toString()));
                        }
                        catch (Exception e){
                            Log.e(TAG, "ERROR: could not update left_votes for bicker " + dataSnapshot.getKey());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        } else if (response == 2) {

            ref.child("Bicker/" + key).addListenerForSingleValueEvent( new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        try {
                            dataSnapshot.child("right_votes").getRef().setValue(
                                    Integer.parseInt(dataSnapshot.child("right_votes").getValue().toString()) + 1);
                            dataSnapshot.child("total_votes").getRef().setValue(
                                    Integer.parseInt(dataSnapshot.child("left_votes").getValue().toString()) + 1 +
                                            Integer.parseInt(dataSnapshot.child("right_votes").getValue().toString()));
                        }
                        catch (Exception e){
                            Log.e(TAG, "ERROR: could not update right_votes for bicker " + dataSnapshot.getKey());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }

        FirebaseMessaging.getInstance().subscribeToTopic(key)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Notification succeeded";
                if (!task.isSuccessful()) {
                    msg = "Notification failed";
                }
                Log.d(TAG, msg);

            }
        });


        if(response == 0) {
            ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Side Voted").setValue("abstain");
        }
        else if(response == 1) {
            ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Side Voted").setValue("left");
        }
        else if(response == 2) {
            ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Side Voted").setValue("right");
        }

        Date now = new Date();

        ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Date Voted").setValue(now);

        ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Status").setValue("Active");

        ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Winning_Side").setValue(null);

        ref.child("User/" + userKey + "/votedOnBickers/" + key + "/Winning_Side").setValue("still active");

        //Increment the totalVoteCount
        DatabaseReference voteRef = database.getReference("/User/" + userKey + "/totalVoteCount");
        DatabaseReference categoryBickerRef = database.getReference("/Bicker/" + key + "/category");
        DatabaseReference categoryUserRef = database.getReference("/User/" + userKey + "/votedOnBickers/" + key + "/Category");

        if(voteRef == null) {
            Log.d(TAG, "Home_fragment: voteRef is null. This could mean that " +
                    "the 'totalVoteCount' attribute is not present in the database for this use.");
        }
        else {
            voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        try {
                            int count = Integer.parseInt(dataSnapshot.getValue().toString());
                            count++;
                            dataSnapshot.getRef().setValue(count);

                            Log.d(TAG, "Home_fragment: incremented database totalVoteCount by 1");
                        }
                        catch (Exception e) {
                            Log.e(TAG, "Home_fragment ERROR: " + e);
                        }
                    }
                    else {
                        Log.d(TAG, "Home_fragment: voteRef dataSnapshot does not exist.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            categoryBickerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        String cat = dataSnapshot.getValue().toString();
                        categoryUserRef.setValue(cat);

                        Log.d(TAG, "Home_fragment: set category of voted bicker in database");
                    }
                    else {
                        Log.d(TAG, "Home_fragment: categoryBickerRef dataSnapshot does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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

        //Log.d(TAG, "Home_fragment: callback");
    }

    public interface OnBickerPressedListener {
        // Required. Currently does nothing, can be changed and used if fragment needs to communicate with activity
        void onBickerPressed(int position);
    }

    //custom ArrayAdapter for filling the listView
    class bickerArrayAdapter extends ArrayAdapter<Bicker> {

        private Context context;
        private List<Bicker> bickers;
        private DecimalFormat df = new DecimalFormat("0.0");

        DisplayMetrics dm = new DisplayMetrics();
        private int windowWidthPixels;
        private int minimumProgressbarHeight = 85;
        private int minimumProgressbarWidth;
        private int maximumProgressbarWidth;
        private double progressbar1Percent;

        //constructor
        public bickerArrayAdapter(Context context, int resource, ArrayList<Bicker> bickers) {
            super(context, resource, bickers);

            this.context = context;
            this.bickers = bickers;

            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            windowWidthPixels = dm.widthPixels;
            maximumProgressbarWidth = (int) Math.ceil((double) windowWidthPixels * 0.75);
            progressbar1Percent = (((double) maximumProgressbarWidth - (double) minimumProgressbarWidth) / 100);
            minimumProgressbarWidth = (int) (Math.ceil((double) windowWidthPixels / 10.0));
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {

            if (getActivity() == null) {
                return null;
            }

            //get the property we are displaying
            Bicker bicker = bickers.get(position);

            int total = bicker.getLeft_votes() + bicker.getRight_votes();
            String total_votes = display_votes(Double.valueOf(total));

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //View view = inflater.inflate(R.layout.layout_unvoted_bicker, null);
            View view = inflater.inflate(R.layout.layout_unvoted_bicker, swipeRefreshLayout, false);

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
                            "See colors.xml for correct options", Toast.LENGTH_LONG).show();

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
            LinearLayout percentage_holder = view.findViewById(R.id.percentage_holder);

            percentage_holder.setVisibility(View.GONE);

            Long approved_time_milliseconds = bickers_approved_time_milliseconds.get(bicker.getKey());

            if (HomeActivity.showActiveBickers) {
                boolean isActive = isActive(approved_time_milliseconds, (int) bicker.getSeconds_until_expired());

                if (isActive == false) {
                    open_bicker_holder.setVisibility(View.GONE);
                    closed_bicker_holder.setVisibility(View.GONE);
                    return view;
                }
            }

            TextView closed_clock = view.findViewById(R.id.closed_clock);
            TextView open_clock = view.findViewById(R.id.open_clock);

            long remainingTime = getRemainingTime(approved_time_milliseconds, (int) bicker.getSeconds_until_expired());

            //remainingTime += 3000;

            long seconds = (long) (remainingTime / 1000) % 60;
            long minutes = (long) ((remainingTime / (1000 * 60)) % 60);
            long hours = (long) ((remainingTime / (1000 * 60 * 60)) % 24);

            String clock_time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            if (remainingTime < -1) {
                clock_time = "Expired";

            }

            closed_clock.setText(clock_time);
            open_clock.setText(clock_time);

            timer_thread = new Thread() {
                @Override
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            Thread.sleep(1000);

                            if (getActivity() == null) {
                                return;
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    long remainingTime = getRemainingTime(approved_time_milliseconds, (int) bicker.getSeconds_until_expired());

                                    //remainingTime += 3000;

                                    long seconds = (long) (remainingTime / 1000) % 60;
                                    long minutes = (long) ((remainingTime / (1000 * 60)) % 60);
                                    long hours = (long) ((remainingTime / (1000 * 60 * 60)) % 24);

                                    String clock_time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                                    if (remainingTime < -1) {
                                        clock_time = "Expired";
                                    }

                                    closed_clock.setText(clock_time);
                                    open_clock.setText(clock_time);


                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Home_fragment: timerThread interrupted");
                            //Thread.currentThread().interrupt();
                            //break;
                        }
                    }
                }
            };

            timer_thread.start();

            LinearLayout closed_header = view.findViewById(R.id.closed_header);
            LinearLayout open_header = view.findViewById(R.id.open_header);

            LinearLayout closed_voteCountHolder = view.findViewById(R.id.closed_voteCount_holder);
            LinearLayout open_voteCountHolder = view.findViewById(R.id.open_voteCount_holder);

            TextView closed_vote_count = view.findViewById(R.id.closed_vote_count_text);
            TextView open_vote_count = view.findViewById(R.id.open_vote_count_text);
            closed_vote_count.setText(total_votes);
            open_vote_count.setText(total_votes);

            Button progressbar_blue = (Button) view.findViewById(R.id.progressbar_blue);
            Button progressbar_purple = (Button) view.findViewById(R.id.progressbar_purple);
            ImageView votes_icon_blue = view.findViewById(R.id.votes_icon_blue);
            ImageView votes_icon_purple = view.findViewById(R.id.votes_icon_purple);
            TextView votes_count_blue = view.findViewById(R.id.votes_count_blue);
            TextView votes_count_purple = view.findViewById(R.id.votes_count_purple);
            Space progressbar_space_blue = (Space) view.findViewById(R.id.progressbar_space_blue);
            Space progressbar_space_purple = (Space) view.findViewById(R.id.progressbar_space_purple);

            progressbar_blue.setEnabled(false);
            progressbar_purple.setEnabled(false);

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

            progressbar_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            progressbar_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            votes_icon_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            votes_icon_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            votes_count_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            votes_count_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            progressbar_space_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBickerClick(view, closed_bicker_holder, open_bicker_holder);
                }
            });

            progressbar_space_purple.setOnClickListener(new View.OnClickListener() {
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
            report = view.findViewById(R.id.report_flag);

            if (remainingTime < -1) {
                report.setVisibility(View.GONE);

            }

            choice_label_holder = view.findViewById(R.id.choice_label_holder);

            leftVote.setBackgroundResource(R.drawable.side_prechoice_blue);
            rightVote.setBackgroundResource(R.drawable.side_prechoice_purple);

            leftLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    leftLabel.setEnabled(false);
                    rightLabel.setEnabled(false);
                    leftSideClick(view, bicker, open_vote_count, closed_vote_count);
                }
            });

            rightLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rightLabel.setEnabled(false);
                    leftLabel.setEnabled(false);
                    rightSideClick(view, bicker, open_vote_count, closed_vote_count);
                }
            });

            leftVote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    leftLabel.setEnabled(false);
                    rightLabel.setEnabled(false);
                    leftSideClick(v, bicker, open_vote_count, closed_vote_count);
                }
            });

            rightVote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rightLabel.setEnabled(false);
                    leftLabel.setEnabled(false);
                    rightSideClick(v, bicker, open_vote_count, closed_vote_count);
                }
            });

            noVote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rightLabel.setEnabled(false);
                    leftLabel.setEnabled(false);
                    noSideClick(v, bicker, open_vote_count, closed_vote_count);
                }
            });

            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportBickerDialog reportBicker = new ReportBickerDialog();
                    Bundle reportBundle = new Bundle(1);
                    reportBundle.putString("key", bicker.getKey());
                    reportBicker.setArguments(reportBundle);
                    reportBicker.show(getFragmentManager(), "report bicker");
                    //Log.d(TAG, "BICKER KEY: " + bicker.getKey());

                }
            });

                //or HomeActivity.showExpired == true
            if (voted == true || HomeActivity.showExpiredBickers) {
                    //leftVote.setText(Integer.toString(bicker.getLeft_votes()));
                    //rightVote.setText(Integer.toString(bicker.getRight_votes()));
                    //noVote.setText("Already Voted");

                    leftVote.setText(bicker.getLeft_side());
                    rightVote.setText(bicker.getRight_side());
                    noVote.setText("Abstain");
                    noVote.setVisibility(View.GONE);
                    choice_label_holder.setVisibility(View.GONE);
                    percentage_holder.setVisibility(View.VISIBLE);

                    leftVote.setEnabled(false);
                    rightVote.setEnabled(false);
                    noVote.setEnabled(false);
                    leftLabel.setEnabled(false);
                    rightLabel.setEnabled(false);

                    if (voted) {
                        String bickerCode = hiddenKey.getText().toString();
                        String side_voted = bickers_votes.get(bickerCode);

                        if (side_voted.equalsIgnoreCase("left")) {
                            //Log.d(TAG, "Home_fragment: left vote found");
                            leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_blue);
                            rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_blue);
                        } else if (side_voted.equalsIgnoreCase("right")) {
                            //Log.d(TAG, "Home_fragment: right vote found");
                            leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_purple);
                            rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_purple);
                        } else if (side_voted.equalsIgnoreCase("abstain")) {
                            //Log.d(TAG, "Home_fragment: abstain vote found");
                            leftVote.setBackgroundResource(R.drawable.side_postchoice_blue_select_purple);
                            rightVote.setBackgroundResource(R.drawable.side_postchoice_purple_select_blue);
                            noVote.setVisibility(View.VISIBLE);
                            noVote.setTextColor(getResources().getColor(R.color.blue_purple_mix));
                        } else {
                            //Log.d(TAG, "Home_fragment: ERROR- side_voted not assigned");
                            Toast.makeText(getActivity(), "Home_fragment: ERROR- side_voted not assigned", Toast.LENGTH_LONG).show();
                        }
                    }

                    // Setup the progress bars
                    int left_vote_count = bicker.getLeft_votes();
                    int right_vote_count = bicker.getRight_votes();

                    int progressbar_blue_pixel_length = GetProgressBarLength_blue(progressbar1Percent, left_vote_count, right_vote_count);
                    int progressbar_purple_pixel_length = GetProgressBarLength_purple(progressbar1Percent, left_vote_count, right_vote_count);

                    double blue_percent = GetProgressBarPercent_blue((double) left_vote_count, (double) right_vote_count);
                    double purple_percent = GetProgressBarPercent_purple((double) left_vote_count, (double) right_vote_count);

                    int blue_percent_int = GetProperPercentTotal_blue(blue_percent, purple_percent);
                    int purple_percent_int = GetProperPercentTotal_purple(blue_percent, purple_percent);

                    String left_percentage = Integer.toString(blue_percent_int) + "%";
                    String right_percentage = Integer.toString(purple_percent_int) + "%";

                    progressbar_blue.setText(left_percentage);
                    progressbar_purple.setText(right_percentage);

                    String left_votes = display_votes((double) left_vote_count);
                    String right_votes = display_votes((double) right_vote_count);
                    left_votes = left_votes.substring(0, left_votes.length() - 6);
                    right_votes = right_votes.substring(0, right_votes.length() - 6);
                    votes_count_blue.setText(left_votes);
                    votes_count_purple.setText(right_votes);

                    LinearLayout.LayoutParams blue_params = new LinearLayout.LayoutParams(85, 85, 0);
                    blue_params.height = minimumProgressbarHeight;
                    blue_params.width = progressbar_blue_pixel_length;
                    progressbar_blue.setLayoutParams(blue_params);

                    LinearLayout.LayoutParams purple_params = new LinearLayout.LayoutParams(85, 85, 0);
                    purple_params.height = minimumProgressbarHeight;
                    purple_params.width = progressbar_purple_pixel_length;
                    progressbar_purple.setLayoutParams(purple_params);

                    //Log.d(TAG, "Home_fragment: minimumProgressbarWidth = " + minimumProgressbarWidth);
                    //Log.d(TAG, "Home_fragment: maximumProgressbarWidth = " + maximumProgressbarWidth);
                    //Log.d(TAG, "Home_fragment: onePercentage = " + progressbar1Percent);
                    //Log.d(TAG, "Home_fragment: blue_length = " + blue_length);
                    //Log.d(TAG, "Home_fragment: purple_length = " + purple_length);


                    // Below reads from the database
                /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                //String ref_path  ="User/" + userKey + "/votedBickerIds/" + bickerCode + "/Side Voted";
                //Log.d(TAG, "Home_fragment: ref_path=" + ref_path);
                DatabaseReference ref = database.getReference();
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String side_voted = dataSnapshot.child("User").child(userKey).child("votedBickerIds").child(bickerCode).child("Side Voted").getValue().toString();
                        Log.d(TAG, "Home_fragment: side_voted=" + side_voted);
                        Log.d(TAG, "Home_fragment: title=" + bicker.getTitle());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }*/

                    //return view;
                }

            return view;
        }


            public void leftSideClick (View view, Bicker bicker, TextView open_vote_count, TextView
            closed_vote_count){
                View parentView = (View) view.getParent().getParent().getParent().getParent().getParent();
                leftVote = parentView.findViewById(R.id.left);
                rightVote = parentView.findViewById(R.id.right);
                noVote = parentView.findViewById(R.id.abstain);

                TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                //leftVote.setText(Integer.toString(Integer.parseInt(hiddenLeftVotes.getText().toString()) + 1));
                //rightVote.setText(hiddenRightVotes.getText().toString());
                noVote.setText("Blue Side");

                // Not needed now, but may need later
                leftVote.setEnabled(false);
                rightVote.setEnabled(false);


                noVote.setEnabled(false);

                vote(hiddenKey.getText().toString(), 1, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));

                double tot = bicker.getLeft_votes() + bicker.getRight_votes() + 1;
                String tot_str = display_votes(tot);
                open_vote_count.setText(tot_str);
                closed_vote_count.setText(tot_str);

                AnimationHandler.select_blue(leftVote, rightVote, parentView, getActivity());
            }

            public void rightSideClick (View view, Bicker bicker, TextView open_vote_count, TextView
            closed_vote_count){
                View parentView = (View) view.getParent().getParent().getParent().getParent().getParent();
                leftVote = parentView.findViewById(R.id.left);
                rightVote = parentView.findViewById(R.id.right);
                noVote = parentView.findViewById(R.id.abstain);

                TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                //leftVote.setText(hiddenLeftVotes.getText().toString());
                //rightVote.setText(Integer.toString(Integer.parseInt(hiddenRightVotes.getText().toString()) + 1));
                noVote.setText("Purple Side");

                // Not needed now, but may need later
                leftVote.setEnabled(false);
                rightVote.setEnabled(false);


                noVote.setEnabled(false);

                vote(hiddenKey.getText().toString(), 2, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));

                double tot = bicker.getLeft_votes() + bicker.getRight_votes() + 1;
                String tot_str = display_votes(tot);
                open_vote_count.setText(tot_str);
                closed_vote_count.setText(tot_str);

                AnimationHandler.select_purple(leftVote, rightVote, parentView, getActivity());
            }

            public void noSideClick (View view, Bicker bicker, TextView open_vote_count, TextView
            closed_vote_count){
                View parentView = (View) view.getParent().getParent().getParent().getParent().getParent();
                leftVote = parentView.findViewById(R.id.left);
                rightVote = parentView.findViewById(R.id.right);
                noVote = parentView.findViewById(R.id.abstain);

                TextView hiddenKey = parentView.findViewById(R.id.hiddenBickerKey);

                TextView hiddenLeftVotes = parentView.findViewById(R.id.hiddenLeftVotes);

                TextView hiddenRightVotes = parentView.findViewById(R.id.hiddenRightVotes);

                // Not needed now, but may need later
                //leftVote.setText(hiddenLeftVotes.getText().toString());
                //rightVote.setText(hiddenRightVotes.getText().toString());


                noVote.setText("Abstain");

                leftVote.setEnabled(false);
                rightVote.setEnabled(false);
                noVote.setEnabled(false);

                vote(hiddenKey.getText().toString(), 0, Integer.parseInt(hiddenLeftVotes.getText().toString()), Integer.parseInt(hiddenRightVotes.getText().toString()));

                AnimationHandler.select_abstain(leftVote, rightVote, parentView, getActivity());
            }

            public void onBickerClick (View view, LinearLayout closed_bicker, LinearLayout
            open_bicker){
                if (closed_bicker.isShown()) {
                    open_bicker.setVisibility(View.VISIBLE);
                    AnimationHandler.slide_down(getActivity(), open_bicker);
                    closed_bicker.setVisibility(View.GONE);
                } else {
                    AnimationHandler.slide_up(getActivity(), open_bicker);
                    closed_bicker.setVisibility(View.VISIBLE);
                    open_bicker.setVisibility(View.GONE);
                }
            }

            public String display_votes ( double d){
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
                if (d < zero) {
                    Log.d(TAG, "Home_fragment ERROR: vote count is negative in display_votes");
                    Toast.makeText(getActivity(), "Home_fragment ERROR: vote count is negative in display_votes", Toast.LENGTH_LONG).show();
                } else if (d >= zero && d < thousand) {
                    ret = Integer.toString((int) d) + " Votes";
                } else if (d >= thousand && d < ten_thousand) {
                    ret = df.format(d / thousand);
                    ret += "K Votes";
                } else if (d >= ten_thousand && d < hundred_thousand) {
                    ret = df.format(d / thousand);
                    ret += "K Votes";
                } else if (d >= hundred_thousand && d < million) {
                    ret = df.format(d / thousand);
                    ret += "K Votes";
                } else if (d >= million && d < ten_million) {
                    ret = df.format(d / million);
                    ret += "M Votes";
                } else if (d >= ten_million && d < hundred_million) {
                    ret = df.format(d / million);
                    ret += "M Votes";
                } else if (d >= hundred_million && d < billion) {
                    ret = df.format(d / million);
                    ret += "M Votes";
                } else if (d >= billion) {
                    ret = df.format(d / billion);
                    ret += "B Votes";
                } else {
                    Log.d(TAG, "Home_fragment- ERROR in display_votes. d = " + d);
                    Toast.makeText(getActivity(), "Home_fragment- ERROR in display_votes", Toast.LENGTH_LONG).show();
                }

                return ret;
            }

            public boolean isActive (Long approved,int seconds_until_expired){

                boolean ret = true;

                Date now = new Date();

                if ((now.getTime() - approved) > (seconds_until_expired * 1000)) {
                    //bicker has expired. Move it to expiredBicker section of DB
                    ret = false;
                }

                return ret;
            }

            // This returns time remaining in milliseconds
            public long getRemainingTime (Long approved,int seconds_until_expired){

                Date now = new Date();

                long time_until_expired = seconds_until_expired * 1000;

                long active_time = now.getTime() - approved;

                long ret = time_until_expired - active_time;

                return ret;
            }

            public int GetProgressBarLength_blue ( double onePercent, int left_votes,
            int right_votes){

                int ret = -1;
                int total = left_votes + right_votes;
                double left_percentage = ((double) left_votes / (double) total) * 100;
                ret = (int) (Math.floor(onePercent * left_percentage));

                if (ret < minimumProgressbarWidth) {
                    return minimumProgressbarWidth;
                }

                return ret;
            }

            public int GetProgressBarLength_purple ( double onePercent, int left_votes,
            int right_votes){

                int ret = -1;
                int total = left_votes + right_votes;
                double right_percentage = ((double) right_votes / (double) total) * 100;
                ret = (int) (Math.floor(onePercent * right_percentage));

                if (ret < minimumProgressbarWidth) {
                    return minimumProgressbarWidth;
                }

                return ret;
            }

            public double GetProgressBarPercent_blue ( double left_votes, double right_votes){

                double ret = -1;
                double total = left_votes + right_votes;
                ret = (left_votes / total) * 100;
                return ret;
            }

            public double GetProgressBarPercent_purple ( double left_votes, double right_votes){
                double ret = -1;
                double total = left_votes + right_votes;
                ret = (right_votes / total) * 100;
                return ret;
            }

            public int GetProperPercentTotal_blue ( double blue_percent, double purple_percent){

                if (blue_percent > purple_percent) {
                    return (int) (Math.floor(blue_percent));
                } else if (blue_percent < purple_percent) {
                    return (int) (Math.ceil(blue_percent));
                } else {
                    return (int) blue_percent;
                }
            }

            public int GetProperPercentTotal_purple ( double blue_percent, double purple_percent){

                if (purple_percent > blue_percent) {
                    return (int) (Math.floor(purple_percent));
                } else if (purple_percent < blue_percent) {
                    return (int) (Math.ceil(purple_percent));
                } else {
                    return (int) purple_percent;
                }
            }


    }
}

