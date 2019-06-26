package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private ArrayList<Bicker> bickers;
    private static final String TAG = HomeActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = FirebaseDatabase.getInstance();

        DatabaseReference databaseRef = database.getReference();

        bickers = new ArrayList<>();

        databaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot bickerSnapshot : dataSnapshot.child("Bicker").getChildren()) {
                    bickers.add(new Bicker(
                            bickerSnapshot.child("title").getValue().toString(),
                            bickerSnapshot.child("description").getValue().toString()));
                }

                ArrayAdapter<Bicker> adapter = new bickerArrayAdapter(HomeActivity.this, 0, bickers);

                ListView listView = findViewById(R.id.unvotedListView);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
                startActivity(intent);
            }

        });
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
}
