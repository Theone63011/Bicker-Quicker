package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticsActivity_home extends AppCompatActivity {

    private static final String TAG = StatisticsActivity_home.class.getSimpleName();

    private FirebaseDatabase database;
    private FirebaseUser user;
    private String userKey;

    public Toolbar toolbar;

    private ImageView home;
    private ImageView vote;
    private ImageView create;
    private ImageView graph;

    private TextView Art_voted;
    private TextView Board_voted;
    private TextView Books_voted;
    private TextView Comedy_voted;
    private TextView Food_voted;
    private TextView Movies_voted;
    private TextView Music_voted;
    private TextView Philosophy_voted;
    private TextView Politics_voted;
    private TextView Relationships_voted;
    private TextView Science_voted;
    private TextView Sports_voted;
    private TextView TV_voted;
    private TextView Video_voted;
    private TextView Miscellaneous_voted;

    private TextView Art_created;
    private TextView Board_created;
    private TextView Books_created;
    private TextView Comedy_created;
    private TextView Food_created;
    private TextView Movies_created;
    private TextView Music_created;
    private TextView Philosophy_created;
    private TextView Politics_created;
    private TextView Relationships_created;
    private TextView Science_created;
    private TextView Sports_created;
    private TextView TV_created;
    private TextView Video_created;
    private TextView Miscellaneous_created;

    private TextView totalVoteCount;
    private TextView totalCreateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        userKey = user.getUid();
        DatabaseReference userRef = databaseReference.child("User/" + userKey);

        Log.d(TAG, "userRef: " + userRef);


        toolbar = findViewById(R.id.toolbar_statistics);
        home = findViewById(R.id.nav_home);
        vote = findViewById(R.id.nav_vote);
        create = findViewById(R.id.nav_create);
        graph = findViewById(R.id.nav_graph);

        totalVoteCount = findViewById(R.id.total_vote_count_textView);
        totalCreateCount = findViewById(R.id.total_create_count_textView);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Statistics");
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setTitle("Statistics");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });

        home.setClickable(true);
        vote.setClickable(true);
        create.setClickable(true);
        graph.setClickable(true);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_home.this, StatisticsActivity_home.class);
                startActivity(intent);
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_home.this, StatisticsActivity_vote.class);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_home.this, StatisticsActivity_create.class);
                startActivity(intent);
            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_home.this, StatisticsActivity_graph.class);
                startActivity(intent);
            }
        });

        Art_voted = findViewById(R.id.Art_category_voted);
        Board_voted = findViewById(R.id.Board_category_voted);
        Books_voted = findViewById(R.id.Books_category_voted);
        Comedy_voted = findViewById(R.id.Comedy_category_voted);
        Food_voted = findViewById(R.id.Food_category_voted);
        Movies_voted = findViewById(R.id.Movies_category_voted);
        Music_voted = findViewById(R.id.Music_category_voted);
        Philosophy_voted = findViewById(R.id.Philosophy_category_voted);
        Politics_voted = findViewById(R.id.Politics_category_voted);
        Relationships_voted = findViewById(R.id.Relationships_category_voted);
        Science_voted = findViewById(R.id.Science_category_voted);
        Sports_voted = findViewById(R.id.Sports_category_voted);
        TV_voted = findViewById(R.id.TV_category_voted);
        Video_voted = findViewById(R.id.Video_category_voted);
        Miscellaneous_voted = findViewById(R.id.Miscellaneous_category_voted);

        Art_voted.setVisibility(View.GONE);
        Board_voted.setVisibility(View.GONE);
        Books_voted.setVisibility(View.GONE);
        Comedy_voted.setVisibility(View.GONE);
        Food_voted.setVisibility(View.GONE);
        Movies_voted.setVisibility(View.GONE);
        Music_voted.setVisibility(View.GONE);
        Philosophy_voted.setVisibility(View.GONE);
        Politics_voted.setVisibility(View.GONE);
        Relationships_voted.setVisibility(View.GONE);
        Science_voted.setVisibility(View.GONE);
        Sports_voted.setVisibility(View.GONE);
        TV_voted.setVisibility(View.GONE);
        Video_voted.setVisibility(View.GONE);
        Miscellaneous_voted.setVisibility(View.GONE);

        Art_created = findViewById(R.id.Art_category_voted_created);
        Board_created = findViewById(R.id.Board_category_voted_created);
        Books_created = findViewById(R.id.Books_category_voted_created);
        Comedy_created = findViewById(R.id.Comedy_category_voted_created);
        Food_created = findViewById(R.id.Food_category_voted_created);
        Movies_created = findViewById(R.id.Movies_category_voted_created);
        Music_created = findViewById(R.id.Music_category_voted_created);
        Philosophy_created = findViewById(R.id.Philosophy_category_voted_created);
        Politics_created = findViewById(R.id.Politics_category_voted_created);
        Relationships_created = findViewById(R.id.Relationships_category_voted_created);
        Science_created = findViewById(R.id.Science_category_voted_created);
        Sports_created = findViewById(R.id.Sports_category_voted_created);
        TV_created = findViewById(R.id.TV_category_voted_created);
        Video_created = findViewById(R.id.Video_category_voted_created);
        Miscellaneous_created = findViewById(R.id.Miscellaneous_category_voted_created);

        Art_created.setVisibility(View.GONE);
        Board_created.setVisibility(View.GONE);
        Books_created.setVisibility(View.GONE);
        Comedy_created.setVisibility(View.GONE);
        Food_created.setVisibility(View.GONE);
        Movies_created.setVisibility(View.GONE);
        Music_created.setVisibility(View.GONE);
        Philosophy_created.setVisibility(View.GONE);
        Politics_created.setVisibility(View.GONE);
        Relationships_created.setVisibility(View.GONE);
        Science_created.setVisibility(View.GONE);
        Sports_created.setVisibility(View.GONE);
        TV_created.setVisibility(View.GONE);
        Video_created.setVisibility(View.GONE);
        Miscellaneous_created.setVisibility(View.GONE);



        // Set total vote/creates counts
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String votes = dataSnapshot.child("totalVoteCount").getValue().toString();
                    String creates = dataSnapshot.child("totalCreateCount").getValue().toString();

                    String votes_text = totalVoteCount.getText().toString();
                    String creates_text = totalCreateCount.getText().toString();

                    votes_text += votes;
                    votes_text += " votes";
                    creates_text += creates;
                    creates_text += " created bickers";

                    totalVoteCount.setText(votes_text);
                    totalCreateCount.setText(creates_text);
                }
                else {
                    Log.d(TAG, "ERROR: dataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Get user voted categories and add them to the 'categories' arraylist
        userRef.child("votedOnBickers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot voted : dataSnapshot.getChildren()) {
                        try {
                            if(voted.child("Category") != null) {
                                String to_add = voted.child("Category").getValue().toString();
                                initializeVotedCategories(to_add);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR: " + e);
                        }
                    }
                }
                else {
                    Log.d(TAG, "WARNING: user has not voted yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Get user created categories and add them to the 'categories' arraylist
        userRef.child("CreatedBickers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot voted : dataSnapshot.getChildren()) {
                        try {
                            if(voted.child("Category") != null) {
                                String to_add = voted.child("category").getValue().toString();
                                initializeCreatedCategories(to_add);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR: " + e);
                        }
                    }
                }
                else {
                    Log.d(TAG, "WARNING: user has not created a bicker yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void leave() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void initializeVotedCategories (String cat) {
        String catName = cat;
        String tokens [] = catName.split(" ", 2);
        String firsWord = tokens[0];

        Log.d(TAG, "firstWord: " + firsWord);

        Drawable catDraw = ContextCompat.getDrawable(StatisticsActivity_home.this, R.drawable.shape_category);
        //Below sets the correct color of the category icon
        switch (firsWord) {
            case "Art":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Art), PorterDuff.Mode.MULTIPLY);
                Art_voted.setText(catName);
                Art_voted.setTextColor(Color.WHITE);
                Art_voted.setBackground(catDraw);
                Art_voted.setPadding(8, 8, 8, 8);
                Art_voted.setVisibility(View.VISIBLE);
                break;
            case "Board":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_BoardGames), PorterDuff.Mode.MULTIPLY);
                Board_voted.setText(catName);
                Board_voted.setTextColor(Color.WHITE);
                Board_voted.setBackground(catDraw);
                Board_voted.setPadding(8, 8, 8, 8);
                Board_voted.setVisibility(View.VISIBLE);
                break;
            case "Books":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Books), PorterDuff.Mode.MULTIPLY);
                Books_voted.setText(catName);
                Books_voted.setTextColor(Color.WHITE);
                Books_voted.setBackground(catDraw);
                Books_voted.setPadding(8, 8, 8, 8);
                Books_voted.setVisibility(View.VISIBLE);
                break;
            case "Comedy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Comedy), PorterDuff.Mode.MULTIPLY);
                Comedy_voted.setText(catName);
                Comedy_voted.setTextColor(Color.WHITE);
                Comedy_voted.setBackground(catDraw);
                Comedy_voted.setPadding(8, 8, 8, 8);
                Comedy_voted.setVisibility(View.VISIBLE);
                break;
            case "Food":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Food), PorterDuff.Mode.MULTIPLY);
                Food_voted.setText(catName);
                Food_voted.setTextColor(Color.WHITE);
                Food_voted.setBackground(catDraw);
                Food_voted.setPadding(8, 8, 8, 8);
                Food_voted.setVisibility(View.VISIBLE);
                break;
            case "Movies":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Movies), PorterDuff.Mode.MULTIPLY);
                Movies_voted.setText(catName);
                Movies_voted.setTextColor(Color.WHITE);
                Movies_voted.setBackground(catDraw);
                Movies_voted.setPadding(8, 8, 8, 8);
                Movies_voted.setVisibility(View.VISIBLE);
                break;
            case "Music":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Music), PorterDuff.Mode.MULTIPLY);
                Music_voted.setText(catName);
                Music_voted.setTextColor(Color.WHITE);
                Music_voted.setBackground(catDraw);
                Music_voted.setPadding(8, 8, 8, 8);
                Music_voted.setVisibility(View.VISIBLE);
                break;
            case "Philosophy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Philosophy), PorterDuff.Mode.MULTIPLY);
                Philosophy_voted.setText(catName);
                Philosophy_voted.setTextColor(Color.WHITE);
                Philosophy_voted.setBackground(catDraw);
                Philosophy_voted.setPadding(8, 8, 8, 8);
                Philosophy_voted.setVisibility(View.VISIBLE);
                break;
            case "Politics":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Politics), PorterDuff.Mode.MULTIPLY);
                Politics_voted.setText(catName);
                Politics_voted.setTextColor(Color.WHITE);
                Politics_voted.setBackground(catDraw);
                Politics_voted.setPadding(8, 8, 8, 8);
                Politics_voted.setVisibility(View.VISIBLE);
                break;
            case "Relationships":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Relationships), PorterDuff.Mode.MULTIPLY);
                Relationships_voted.setText(catName);
                Relationships_voted.setTextColor(Color.WHITE);
                Relationships_voted.setBackground(catDraw);
                Relationships_voted.setPadding(8, 8, 8, 8);
                Relationships_voted.setVisibility(View.VISIBLE);
                break;
            case "Science":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Science), PorterDuff.Mode.MULTIPLY);
                Science_voted.setText(catName);
                Science_voted.setTextColor(Color.WHITE);
                Science_voted.setBackground(catDraw);
                Science_voted.setPadding(8, 8, 8, 8);
                Science_voted.setVisibility(View.VISIBLE);
                break;
            case "Sports":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Sports), PorterDuff.Mode.MULTIPLY);
                Sports_voted.setText(catName);
                Sports_voted.setTextColor(Color.WHITE);
                Sports_voted.setBackground(catDraw);
                Sports_voted.setPadding(8, 8, 8, 8);
                Sports_voted.setVisibility(View.VISIBLE);
                break;
            case "TV":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_TvShows), PorterDuff.Mode.MULTIPLY);
                TV_voted.setText(catName);
                TV_voted.setTextColor(Color.WHITE);
                TV_voted.setBackground(catDraw);
                TV_voted.setPadding(8, 8, 8, 8);
                TV_voted.setVisibility(View.VISIBLE);
                break;
            case "Video":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_VideoGames), PorterDuff.Mode.MULTIPLY);
                Video_voted.setText(catName);
                Video_voted.setTextColor(Color.WHITE);
                Video_voted.setBackground(catDraw);
                Video_voted.setPadding(8, 8, 8, 8);
                Video_voted.setVisibility(View.VISIBLE);
                break;
            case "Miscellaneous":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Misc), PorterDuff.Mode.MULTIPLY);
                Miscellaneous_voted.setText(catName);
                Miscellaneous_voted.setTextColor(Color.WHITE);
                Miscellaneous_voted.setBackground(catDraw);
                Miscellaneous_voted.setPadding(8, 8, 8, 8);
                Miscellaneous_voted.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                Toast.makeText(StatisticsActivity_home.this, "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                        "See colors.xml for correct options", Toast.LENGTH_LONG).show();

        }


    }

    public void initializeCreatedCategories (String cat) {
        String catName = cat;
        String tokens [] = catName.split(" ", 2);
        String firsWord = tokens[0];

        Log.d(TAG, "firstWord: " + firsWord);

        Drawable catDraw = ContextCompat.getDrawable(StatisticsActivity_home.this, R.drawable.shape_category);
        //Below sets the correct color of the category icon
        switch (firsWord) {
            case "Art":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Art), PorterDuff.Mode.MULTIPLY);
                Art_created.setText(catName);
                Art_created.setTextColor(Color.WHITE);
                Art_created.setBackground(catDraw);
                Art_created.setPadding(8, 8, 8, 8);
                Art_created.setVisibility(View.VISIBLE);
                break;
            case "Board":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_BoardGames), PorterDuff.Mode.MULTIPLY);
                Board_created.setText(catName);
                Board_created.setTextColor(Color.WHITE);
                Board_created.setBackground(catDraw);
                Board_created.setPadding(8, 8, 8, 8);
                Board_created.setVisibility(View.VISIBLE);
                break;
            case "Books":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Books), PorterDuff.Mode.MULTIPLY);
                Books_created.setText(catName);
                Books_created.setTextColor(Color.WHITE);
                Books_created.setBackground(catDraw);
                Books_created.setPadding(8, 8, 8, 8);
                Books_created.setVisibility(View.VISIBLE);
                break;
            case "Comedy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Comedy), PorterDuff.Mode.MULTIPLY);
                Comedy_created.setText(catName);
                Comedy_created.setTextColor(Color.WHITE);
                Comedy_created.setBackground(catDraw);
                Comedy_created.setPadding(8, 8, 8, 8);
                Comedy_created.setVisibility(View.VISIBLE);
                break;
            case "Food":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Food), PorterDuff.Mode.MULTIPLY);
                Food_created.setText(catName);
                Food_created.setTextColor(Color.WHITE);
                Food_created.setBackground(catDraw);
                Food_created.setPadding(8, 8, 8, 8);
                Food_created.setVisibility(View.VISIBLE);
                break;
            case "Movies":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Movies), PorterDuff.Mode.MULTIPLY);
                Movies_created.setText(catName);
                Movies_created.setTextColor(Color.WHITE);
                Movies_created.setBackground(catDraw);
                Movies_created.setPadding(8, 8, 8, 8);
                Movies_created.setVisibility(View.VISIBLE);
                break;
            case "Music":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Music), PorterDuff.Mode.MULTIPLY);
                Music_created.setText(catName);
                Music_created.setTextColor(Color.WHITE);
                Music_created.setBackground(catDraw);
                Music_created.setPadding(8, 8, 8, 8);
                Music_created.setVisibility(View.VISIBLE);
                break;
            case "Philosophy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Philosophy), PorterDuff.Mode.MULTIPLY);
                Philosophy_created.setText(catName);
                Philosophy_created.setTextColor(Color.WHITE);
                Philosophy_created.setBackground(catDraw);
                Philosophy_created.setPadding(8, 8, 8, 8);
                Philosophy_created.setVisibility(View.VISIBLE);
                break;
            case "Politics":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Politics), PorterDuff.Mode.MULTIPLY);
                Politics_created.setText(catName);
                Politics_created.setTextColor(Color.WHITE);
                Politics_created.setBackground(catDraw);
                Politics_created.setPadding(8, 8, 8, 8);
                Politics_created.setVisibility(View.VISIBLE);
                break;
            case "Relationships":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Relationships), PorterDuff.Mode.MULTIPLY);
                Relationships_created.setText(catName);
                Relationships_created.setTextColor(Color.WHITE);
                Relationships_created.setBackground(catDraw);
                Relationships_created.setPadding(8, 8, 8, 8);
                Relationships_created.setVisibility(View.VISIBLE);
                break;
            case "Science":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Science), PorterDuff.Mode.MULTIPLY);
                Science_created.setText(catName);
                Science_created.setTextColor(Color.WHITE);
                Science_created.setBackground(catDraw);
                Science_created.setPadding(8, 8, 8, 8);
                Science_created.setVisibility(View.VISIBLE);
                break;
            case "Sports":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Sports), PorterDuff.Mode.MULTIPLY);
                Sports_created.setText(catName);
                Sports_created.setTextColor(Color.WHITE);
                Sports_created.setBackground(catDraw);
                Sports_created.setPadding(8, 8, 8, 8);
                Sports_created.setVisibility(View.VISIBLE);
                break;
            case "TV":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_TvShows), PorterDuff.Mode.MULTIPLY);
                TV_created.setText(catName);
                TV_created.setTextColor(Color.WHITE);
                TV_created.setBackground(catDraw);
                TV_created.setPadding(8, 8, 8, 8);
                TV_created.setVisibility(View.VISIBLE);
                break;
            case "Video":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_VideoGames), PorterDuff.Mode.MULTIPLY);
                Video_created.setText(catName);
                Video_created.setTextColor(Color.WHITE);
                Video_created.setBackground(catDraw);
                Video_created.setPadding(8, 8, 8, 8);
                Video_created.setVisibility(View.VISIBLE);
                break;
            case "Miscellaneous":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_home.this, R.color.category_Misc), PorterDuff.Mode.MULTIPLY);
                Miscellaneous_created.setText(catName);
                Miscellaneous_created.setTextColor(Color.WHITE);
                Miscellaneous_created.setBackground(catDraw);
                Miscellaneous_created.setPadding(8, 8, 8, 8);
                Miscellaneous_created.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                Toast.makeText(StatisticsActivity_home.this, "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                        "See colors.xml for correct options", Toast.LENGTH_LONG).show();

        }


    }
}
