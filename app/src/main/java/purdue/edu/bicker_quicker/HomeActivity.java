package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements Home_Fragment.OnBickerPressedListener, ExpiredBickers_Fragment.OnBickerPressedListener, FilterDialog.FilterDialogListener {

    //private FirebaseDatabase database;
    //private ArrayList<Bicker> bickers;
    //private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private String sortBy = "recent";

    private Boolean initializeHomeFrag1 = false;

    Button recentButton;
    Button popularButton;
    Button filterButton;

    TextView noFilteredResultsFound;

    Home_Fragment homefrag1 = null;
    Home_Fragment homefrag2 = null;

    ExpiredBickers_Fragment expHomeFrag1 = null;
    ExpiredBickers_Fragment expHomeFrag2 = null;

    // FILTER VARIABLES
    public static boolean showActiveBickers;
    public static boolean showExpiredBickers;
    public static ArrayList<String> categoryFilter; // has list of all categories to filter out (Strings)
    public static String keys; // Keywords used for tag searching

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d("Error", "NULL USER");
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        /*************************************************************************************
         * Below are a list of general settings added (or updated) to the database at the start
         * of the app.
         *
         * Some of these settings include:
         *  allowTalkingToSelf- default value is false. Can be changed by moderator.
         *
         *************************************************************************************/
        // Add bicker Category to the Category section of database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        DatabaseReference databaseReference2 = database.getReference("Database_Settings");

        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() == false) {
                    //This means that the Database_Settings section IS NOT in the database
                    databaseReference.child("Database_Settings").child("allowTalkingToSelf").setValue(false);
                    databaseReference.child("Database_Settings").child("Timer_option_1").setValue("30 seconds");
                    databaseReference.child("Database_Settings").child("Timer_option_2").setValue("24 hours");
                    databaseReference.child("Database_Settings").child("Timer_option_3").setValue("48 hours");
                }
                else {
                    //This means that the Database_Settings section IS in the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        noFilteredResultsFound = findViewById(R.id.nofilteredresultsfound);
        noFilteredResultsFound.setAlpha(0.0f);
        showActiveBickers = true;
        showExpiredBickers = false;

        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_logo_whitegrey);

        mDrawer = findViewById(R.id.drawer_layout);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });
        /*
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton drawerButton = findViewById(R.id.DrawerButton);

        drawerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDrawer.openDrawer(Gravity.LEFT);

            }
        });
        */


        nvDrawer = findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
                startActivity(intent);
            }

        });

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setOffscreenPageLimit(NUM_PAGES);

/*
        ImageButton profileButton = findViewById(R.id.profButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }

        });
*/

        this.popularButton = findViewById(R.id.popular);
        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBy = "popular";
                if (showActiveBickers) {
                    homefrag1.sortByPopularity();
                    homefrag2.sortByPopularity();
                } else {
                    expHomeFrag1.sortByPopularity();
                    expHomeFrag2.sortByPopularity();
                }
            }
        });

        this.recentButton = findViewById(R.id.recent);
        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBy = "recent";
                if (showActiveBickers) {
                    homefrag1.sortByRecent();
                    homefrag2.sortByRecent();
                } else {
                    expHomeFrag1.sortByRecent();
                    expHomeFrag2.sortByRecent();
                }
            }
        });

        this.filterButton = findViewById(R.id.filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d("HomeActivity", "Inside onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onResume(){

        Log.d("HomeActivity", "Inside onResume");

        Log.d("sortBy: ", this.sortBy.toString());

        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d("Error", "NULL USER");
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof Home_Fragment) {
            Home_Fragment homeFragment = (Home_Fragment) fragment;

            String tag = fragment.getTag();

            homeFragment.set_home_fragment(fragment, tag);

            homeFragment.setOnBickerPressedListener(this);
            System.out.println("Fragment is instanceof home_fragment");
            if (initializeHomeFrag1 == false) {
                //Log.d(TAG, "Home_activity: initializeHomeFrag1 == false");
                homefrag1 = homeFragment;
                this.homefrag1.setReferenceToHomeActivity(this);
                initializeHomeFrag1 = true;
            }else if (initializeHomeFrag1 == true) {
                //Log.d(TAG, "Home_activity: initializeHomeFrag1 == true");

                homefrag2 = homeFragment;
                this.homefrag2.setReferenceToHomeActivity(this);
                initializeHomeFrag1 = false;
            }
        } else if (fragment instanceof ExpiredBickers_Fragment) {
            ExpiredBickers_Fragment f = (ExpiredBickers_Fragment) fragment;

            String tag = f.getTag();

            f.set_home_fragment(fragment, tag);

            f.setOnBickerPressedListener(this);
            System.out.println("Fragment is instanceof expBick_fragment");
            if (initializeHomeFrag1 == false) {
                expHomeFrag1 = f;
                this.expHomeFrag1.setReferenceToHomeActivity(this);
                initializeHomeFrag1 = true;
            } else {
                expHomeFrag2 = f;
                this.expHomeFrag2.setReferenceToHomeActivity(this);
                initializeHomeFrag1 = false;
            }
        }
    }

    public void openFilterDialog() {
        FilterDialog fd = new FilterDialog();
        fd.show(getSupportFragmentManager(), "filter dialog");
    }

    @Override
    public void onBickerPressed(int position) {
        // Required. Currently does nothing, can be changed and used if fragment needs to communicate with activity
        Log.d("HomeActivity", "Inside onBickerPressed");
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Log.d("HomeActivity", "Inside getItem");

            Home_Fragment homeFragment = null; //new Home_Fragment();
            ExpiredBickers_Fragment expFragment = null;
            if (showActiveBickers) {
                homeFragment = new Home_Fragment();
            } else {
                expFragment = new ExpiredBickers_Fragment();
            }
            Bundle args = new Bundle();
            if(position == 0){
                args.putBoolean("voted", false);
            }
            else{
                args.putBoolean("voted", true);
            }
            if (showActiveBickers) {
                homeFragment.setArguments(args);
                return homeFragment;
            } else {
                expFragment.setArguments(args);
                return expFragment;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }




    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override

                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);

                        return true;

                    }

                });

    }


    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.profile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);

                break;

            case R.id.settings:
                //TODO: settings page

                break;

            case R.id.signOut:

                signOut();

                break;

            case  R.id.delete://Temporary until notifications are figured out
                Intent tempIntent = new Intent(HomeActivity.this, TempDeleteActivity.class);
                startActivity(tempIntent);

                break;

        }
    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.profButton) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        }


        switch (item.getItemId()) {

            case android.R.id.home:

                mDrawer.openDrawer(GravityCompat.START);

                return true;


        }

        return super.onOptionsItemSelected(item);

    }

    public void onFilterButton() {
        /*update to show expired bickers
        Intent intent = new Intent(this, BasicBickerView.class);
        Bundle b = new Bundle();
        b.putBoolean("expBick", true);
        intent.putExtras(b);
        startActivity(intent);
        */
        Intent intent = new Intent(this, ExpiredBickersActivity.class);
        startActivity(intent);
    }

    public void signOut(){

        // To Sign Out of Facebook, do this:
        MainActivity.signOut();

        //sign out of google and take back to MainActivity on success
        FirebaseAuth.getInstance().signOut();
        MainActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    }
                });
    }

    @Override
    public void applyFilter(boolean showActive, boolean showExpired, ArrayList<String> categories, String keywords) {
        this.showActiveBickers = showActive;
        this.showExpiredBickers = showExpired;
        this.categoryFilter = categories; // NOTE THESE ARE DISABLED CATEGORIES
        this.keys = keywords; // TODO: Put this instead in a separate search bar section. Will work on second pass of filtering

        //do not execute filtering if no categoryFilter or keys have been given to filter by
        if (categoryFilter == null || keys == null) {
            return;
        }

        if (showActiveBickers) {
            //*********************filter bickersHomeFrag1 by category***************************************
            //set reference to this so homefrag1 can call applyFilter after it has fetched the latest list of bickers
            this.homefrag1.setReferenceToHomeActivity(this);
            ArrayList<Bicker> bickersHomeFrag1 = this.homefrag1.returnBickerArrayList();

            //filter by category
            for (int i = 0; i < bickersHomeFrag1.size(); i++) {
                if (this.categoryFilter.contains(bickersHomeFrag1.get(i).getCategory())) {
                    bickersHomeFrag1.remove(i);
                    i--;
                }
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag1 by keywords***************************************
            ArrayList<Bicker> filteredKeywordList = new ArrayList<>();
            Map<Bicker, Double> filteredKeywordMap = new HashMap<>();

            if (this.keys != "") {
                for (int i = 0; i < bickersHomeFrag1.size(); i++) {
                    double similarityNumber = KeywordTokenizer.similarity(this.keys, bickersHomeFrag1.get(i).getKeywords(), bickersHomeFrag1.get(i).getTags());

                    if (similarityNumber != 0.0) {
                        filteredKeywordMap.put(bickersHomeFrag1.get(i), similarityNumber);
                    }
                }
            }

            filteredKeywordList = new ArrayList<Bicker>(filteredKeywordMap.keySet());

            Collections.reverse(filteredKeywordList);

            if (this.keys.equals("")) {
                this.homefrag1.updateBickerList(bickersHomeFrag1); //update bicker list with filtered bickers
            } else {
                this.homefrag1.updateBickerList(filteredKeywordList); //update bicker list with filtered bickers
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag2 by category***************************************
            //set reference to this so homefrag2 can call applyFilter after it has fetched the latest list of bickers
            this.homefrag2.setReferenceToHomeActivity(this);
            ArrayList<Bicker> bickersHomeFrag2 = this.homefrag2.returnBickerArrayList();
            for (int i = 0; i < bickersHomeFrag2.size(); i++) {
                if (this.categoryFilter.contains(bickersHomeFrag2.get(i).getCategory())) {
                    bickersHomeFrag2.remove(i);
                    i--;
                }
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag2 by keywords***************************************
            ArrayList<Bicker> filteredKeywordList2 = new ArrayList<>();
            Map<Bicker, Double> filteredKeywordMap2 = new HashMap<>();

            if (this.keys != "") {
                for (int i = 0; i < bickersHomeFrag2.size(); i++) {
                    double similarityNumber = KeywordTokenizer.similarity(this.keys, bickersHomeFrag2.get(i).getKeywords(), bickersHomeFrag2.get(i).getTags());

                    if (similarityNumber != 0.0) {
                        filteredKeywordMap2.put(bickersHomeFrag2.get(i), similarityNumber);
                    }
                }
            }

            filteredKeywordList2 = new ArrayList<Bicker>(filteredKeywordMap2.keySet());

            Collections.reverse(filteredKeywordList2);

            if (this.keys.equals("")) {
                this.homefrag2.updateBickerList(bickersHomeFrag2); //update bicker list with filtered bickers
            } else {
                this.homefrag2.updateBickerList(filteredKeywordList2); //update bicker list with filtered bickers
            }
            //*********************end***************************************
        } else {
            //*********************filter bickersHomeFrag1 by category***************************************
            //set reference to this so homefrag1 can call applyFilter after it has fetched the latest list of bickers
            this.expHomeFrag1.setReferenceToHomeActivity(this);
            ArrayList<Bicker> expBickersHomeFrag1 = this.expHomeFrag1.returnBickerArrayList();

            //filter by category
            for (int i = 0; i < expBickersHomeFrag1.size(); i++) {
                if (this.categoryFilter.contains(expBickersHomeFrag1.get(i).getCategory())) {
                    expBickersHomeFrag1.remove(i);
                    i--;
                }
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag1 by keywords***************************************
            ArrayList<Bicker> filteredKeywordList = new ArrayList<>();
            Map<Bicker, Double> filteredKeywordMap = new HashMap<>();

            if (this.keys != "") {
                for (int i = 0; i < expBickersHomeFrag1.size(); i++) {
                    double similarityNumber = KeywordTokenizer.similarity(this.keys, expBickersHomeFrag1.get(i).getKeywords(), expBickersHomeFrag1.get(i).getTags());

                    if (similarityNumber != 0.0) {
                        filteredKeywordMap.put(expBickersHomeFrag1.get(i), similarityNumber);
                    }
                }
            }

            filteredKeywordList = new ArrayList<Bicker>(filteredKeywordMap.keySet());

            Collections.reverse(filteredKeywordList);

            if (this.keys.equals("")) {
                this.expHomeFrag1.updateBickerList(expBickersHomeFrag1); //update bicker list with filtered bickers
            } else {
                this.expHomeFrag1.updateBickerList(filteredKeywordList); //update bicker list with filtered bickers
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag2 by category***************************************
            //set reference to this so homefrag2 can call applyFilter after it has fetched the latest list of bickers
            this.expHomeFrag2.setReferenceToHomeActivity(this);
            ArrayList<Bicker> expBickersHomeFrag2 = this.expHomeFrag2.returnBickerArrayList();
            for (int i = 0; i < expBickersHomeFrag2.size(); i++) {
                if (this.categoryFilter.contains(expBickersHomeFrag2.get(i).getCategory())) {
                    expBickersHomeFrag2.remove(i);
                    i--;
                }
            }
            //*********************end***************************************

            //*********************filter bickersHomeFrag2 by keywords***************************************
            ArrayList<Bicker> filteredKeywordList2 = new ArrayList<>();
            Map<Bicker, Double> filteredKeywordMap2 = new HashMap<>();

            if (this.keys != "") {
                for (int i = 0; i < expBickersHomeFrag2.size(); i++) {
                    double similarityNumber = KeywordTokenizer.similarity(this.keys, expBickersHomeFrag2.get(i).getKeywords(), expBickersHomeFrag2.get(i).getTags());

                    if (similarityNumber != 0.0) {
                        filteredKeywordMap2.put(expBickersHomeFrag2.get(i), similarityNumber);
                    }
                }
            }

            filteredKeywordList2 = new ArrayList<Bicker>(filteredKeywordMap2.keySet());

            Collections.reverse(filteredKeywordList2);

            if (this.keys.equals("")) {
                this.expHomeFrag2.updateBickerList(expBickersHomeFrag2); //update bicker list with filtered bickers
            } else {
                this.expHomeFrag2.updateBickerList(filteredKeywordList2); //update bicker list with filtered bickers
            }
            //*********************end***************************************
        }
    }

    public void refresh_fragment() {
        mPager.getAdapter().notifyDataSetChanged();
    }
}
