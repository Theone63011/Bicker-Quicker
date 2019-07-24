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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements Home_Fragment.OnBickerPressedListener, FilterDialog.FilterDialogListener {

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

    Home_Fragment homefrag1 = null;
    Home_Fragment homefrag2 = null;

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
                homefrag1.sortByPopularity();
                homefrag2.sortByPopularity();
            }
        });

        this.recentButton = findViewById(R.id.recent);
        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBy = "recent";
                homefrag1.sortByRecent();
                homefrag2.sortByRecent();
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

            Home_Fragment homeFragment = new Home_Fragment();
            Bundle args = new Bundle();
            if(position == 0){
                args.putBoolean("voted", false);
            }
            else{
                args.putBoolean("voted", true);
            }
            homeFragment.setArguments(args);
            return homeFragment;
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

        //set reference to this so homefrag1 can call applyFilter after it has fetched the latest list of bickers
        this.homefrag1.setReferenceToHomeActivity(this);
        ArrayList<Bicker> bickersHomeFrag1 = this.homefrag1.returnBickerArrayList();
        for (int i = 0; i < bickersHomeFrag1.size(); i++) {
            if (this.categoryFilter.contains(bickersHomeFrag1.get(i).getCategory())) {
                bickersHomeFrag1.remove(i);
                i--;
            }
        }
        this.homefrag1.updateBickerList(); //update bicker list with filtered bickers

        //set reference to this so homefrag2 can call applyFilter after it has fetched the latest list of bickers
        this.homefrag2.setReferenceToHomeActivity(this);
        ArrayList<Bicker> bickersHomeFrag2 = this.homefrag2.returnBickerArrayList();
        for (int i = 0; i < bickersHomeFrag2.size(); i++) {
            if (this.categoryFilter.contains(bickersHomeFrag2.get(i).getCategory())) {
                bickersHomeFrag2.remove(i);
                i--;
            }
        }
        this.homefrag2.updateBickerList(); //update bicker list with filtered bickers

    }

    public void refresh_fragment() {
        mPager.getAdapter().notifyDataSetChanged();
    }
}
