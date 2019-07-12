package purdue.edu.bicker_quicker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.AccessTokenManager.TAG;


public class HomeActivity extends AppCompatActivity implements Home_Fragment.OnBickerPressedListener {

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

    Home_Fragment homefrag1 = null;
    Home_Fragment homefrag2 = null;

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onResume(){

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
            homeFragment.setOnBickerPressedListener(this);

            if (initializeHomeFrag1 == false) {
                homefrag1 = homeFragment;
                initializeHomeFrag1 = true;
            }else if (initializeHomeFrag1 == true) {
                homefrag2 = homeFragment;
                initializeHomeFrag1 = false;
            }
        }
    }

    @Override
    public void onBickerPressed(int position) {
        // Required. Currently does nothing, can be changed and used if fragment needs to communicate with activity
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
}
