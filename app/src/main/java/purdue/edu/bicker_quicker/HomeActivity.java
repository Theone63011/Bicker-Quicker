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
import android.view.Gravity;
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

import java.util.ArrayList;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*database = FirebaseDatabase.getInstance();

        DatabaseReference databaseRef = database.getReference();

        bickers = new ArrayList<>();

        databaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {
                    bickers.add(new Bicker(
                            bickerSnapshot.child("title").getValue().toString(),
                            bickerSnapshot.child("description").getValue().toString()));
                }

            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/

        Button signOut = findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout);

        ImageButton drawerButton = findViewById(R.id.DrawerButton);

        drawerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDrawer.openDrawer(Gravity.LEFT);

            }
        });

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

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof Home_Fragment) {
            Home_Fragment homeFragment = (Home_Fragment) fragment;
            homeFragment.setOnBickerPressedListener(this);
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


    /*//custom ArrayAdapter for filling the listView
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

            TextView description = view.findViewById(R.id.description);
            description.setText(bicker.getDescription());

            LinearLayout header = view.findViewById(R.id.header);

            LinearLayout dropdown = view.findViewById(R.id.dropdown);
            dropdown.setVisibility(View.GONE);

            header.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LinearLayout parentView = (LinearLayout)v.getParent();

                    LinearLayout dropdown = parentView.findViewById(R.id.dropdown);
                    if(dropdown.isShown()){
                        AnimationHandler.slide_up(HomeActivity.this, dropdown);
                        dropdown.setVisibility(View.GONE);
                    }
                    else{
                        dropdown.setVisibility(View.VISIBLE);
                        AnimationHandler.slide_down(HomeActivity.this, dropdown);
                    }
                }
            });

            return view;
        }
    }*/

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



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.

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
