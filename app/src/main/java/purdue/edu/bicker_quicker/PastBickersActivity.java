package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.firebase.ui.auth.AuthUI;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PastBickersActivity extends AppCompatActivity implements PastBickers_Fragment.OnBickerPressedListener{

    private static final int NUM_PAGES = 1;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    //private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private String sortBy = "recent";

    private Boolean initializeFragment = false;

    //Button recentButton;
    //Button popularButton;

    PastBickers_Fragment fragment1 = null;
    PastBickers_Fragment fragment2 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d("Error", "NULL USER");
            startActivity(new Intent(PastBickersActivity.this, MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_bickers);


        //nvDrawer = findViewById(R.id.nav_view);
        //setupDrawerContent(nvDrawer);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PastBickersActivity.this, CreateActivity.class);
                startActivity(intent);
            }

        });*/



        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PastBickersActivity.ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Created Bickers");
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setTitle("Your Created Bickers");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
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
            startActivity(new Intent(PastBickersActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof PastBickers_Fragment) {
            PastBickers_Fragment past_fragment = (PastBickers_Fragment) fragment;
            past_fragment.setOnBickerPressedListener(this);

            /*if (initializeHomeFrag1 == false) {
                homefrag1 = homeFragment;
                initializeHomeFrag1 = true;
            }else if (initializeHomeFrag1 == true) {
                homefrag2 = homeFragment;
                initializeHomeFrag1 = false;
            }*/
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
            PastBickers_Fragment past_fragment = new PastBickers_Fragment();
            Bundle args = new Bundle();
            if(position == 0){
                args.putBoolean("voted", false);
            }
            else{
                args.putBoolean("voted", true);
            }
            past_fragment.setArguments(args);

            return past_fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /*private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override

                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);

                        return true;

                    }

                });

    }*/

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.profile:
                Intent intent = new Intent(PastBickersActivity.this, ProfileActivity.class);
                startActivity(intent);

                break;

            case R.id.settings:
                //TODO: settings page

                break;

            case R.id.signOut:

                signOut();

                break;

            case  R.id.delete://Temporary until notifications are figured out
                Intent tempIntent = new Intent(PastBickersActivity.this, TempDeleteActivity.class);
                startActivity(tempIntent);

                break;

        }
    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.profButton) {
            Intent intent = new Intent(PastBickersActivity.this, ProfileActivity.class);
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
                        startActivity(new Intent(PastBickersActivity.this, MainActivity.class));
                    }
                });
    }

    public void leave() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}


