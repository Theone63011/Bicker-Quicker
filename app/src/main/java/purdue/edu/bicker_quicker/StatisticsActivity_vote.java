package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatisticsActivity_vote extends AppCompatActivity {

    private static final String TAG = StatisticsActivity_vote.class.getSimpleName();

    private FirebaseDatabase database;
    private FirebaseUser user;
    private String userKey;

    public Toolbar toolbar;

    private ImageView home;
    private ImageView vote;
    private ImageView create;
    private ImageView graph;

    PieChart chart;
    List<PieEntry> chart_data;
    PieDataSet dataSet;
    PieData data;
    Map<String, Double> categories;

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

    private static DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_vote);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        userKey = user.getUid();
        DatabaseReference userRef = databaseReference.child("User/" + userKey);

        toolbar = findViewById(R.id.toolbar_statistics);
        home = findViewById(R.id.nav_home);
        vote = findViewById(R.id.nav_vote);
        create = findViewById(R.id.nav_create);
        graph = findViewById(R.id.nav_graph);

        chart = (PieChart) findViewById(R.id.statistics_vote_pieChart);
        chart_data = new ArrayList<PieEntry>();

        categories = new HashMap<String, Double>();
        categories.put("Art", 0.0);
        categories.put("Board Games", 0.0);
        categories.put("Books", 0.0);
        categories.put("Comedy", 0.0);
        categories.put("Food", 0.0);
        categories.put("Movies", 0.0);
        categories.put("Music", 0.0);
        categories.put("Philosophy", 0.0);
        categories.put("Politics", 0.0);
        categories.put("Relationships", 0.0);
        categories.put("Science", 0.0);
        categories.put("Sports", 0.0);
        categories.put("TV Shows", 0.0);
        categories.put("Video Games", 0.0);
        categories.put("Miscellaneous", 0.0);

        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

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
                Intent intent = new Intent(StatisticsActivity_vote.this, StatisticsActivity_home.class);
                startActivity(intent);
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_vote.this, StatisticsActivity_vote.class);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_vote.this, StatisticsActivity_create.class);
                startActivity(intent);            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_vote.this, StatisticsActivity_graph.class);
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

        initializeVotedCategories("Art");
        initializeVotedCategories("Board Games");
        initializeVotedCategories("Books");
        initializeVotedCategories("Comedy");
        initializeVotedCategories("Food");
        initializeVotedCategories("Movies");
        initializeVotedCategories("Music");
        initializeVotedCategories("Philosophy");
        initializeVotedCategories("Politics");
        initializeVotedCategories("Relationships");
        initializeVotedCategories("Science");
        initializeVotedCategories("Sports");
        initializeVotedCategories("TV Shows");
        initializeVotedCategories("Video Games");
        initializeVotedCategories("Miscellaneous");

        // Get user voted categories and add them to the 'categories' arraylist
        userRef.child("votedOnBickers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total_completed = 0.0;
                double wins = 0.0;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot voted : dataSnapshot.getChildren()) {
                        try {
                            if(voted.child("Category") != null) {
                                String to_add = voted.child("Category").getValue().toString();
                                double amount = categories.get(to_add);
                                amount++;
                                categories.put(to_add, amount);
                            }

                            String winning_side = voted.child("Winning_Side").getValue().toString();
                            String side_voted = voted.child("Side Voted").getValue().toString();

                            if(winning_side.equalsIgnoreCase("still active") == false) {
                                if(winning_side.equalsIgnoreCase(side_voted)) {
                                    // Voted for winning side
                                    total_completed++;
                                    wins++;
                                }
                                else {
                                    // Voted for non-winning side
                                    total_completed++;
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "ERROR: " + e);
                        }
                    }
                }
                else {
                    Log.d(TAG, "WARNING: user has not voted yet");
                }

                String win_perct = df.format((wins/total_completed) * 100);
                win_perct += "%";

                double total = 0.0;

                ArrayList<Double> percentages = new ArrayList<Double>();
                ArrayList<String> category = new ArrayList<String>();

                // Calculate total
                Iterator iterator = categories.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry element = (Map.Entry) iterator.next();
                    String key = element.getKey().toString();
                    double value = Double.parseDouble(element.getValue().toString());
                    total += value;
                }

                // Calculate percentages
                Iterator iterator2 = categories.entrySet().iterator();
                while(iterator2.hasNext()) {
                    Map.Entry element = (Map.Entry) iterator2.next();
                    String key = element.getKey().toString();
                    double value = Double.parseDouble(element.getValue().toString());
                    if(value != 0) {
                        double perct = (value / total) * 100;
                        //String formated = df.format(perct);
                        //double to_add = Double.parseDouble(formated);
                        double to_add = perct;
                        categories.put(key, to_add);
                        percentages.add(to_add);
                        category.add(key);
                    }
                }

                // Add arraylist elements to the Pie Chart
                for(int i = 0; i < percentages.size(); i++) {
                    double d = percentages.get(i);
                    String s = Double.toString(d);
                    chart_data.add(new PieEntry(Float.parseFloat(s), category.get(i)));
                }

                dataSet = new PieDataSet(chart_data, "");
                ValueFormatter myVF = new PercentFormatter();
                dataSet.setValueFormatter(myVF);
                dataSet.setColors(setupColors(category));
                dataSet.setValueTextSize(20f);
                dataSet.setValueLineColor(getResources().getColor(R.color.white));
                dataSet.setSelectionShift(15f);
                dataSet.setSliceSpace(3f);
                dataSet.setValueTextColor(getResources().getColor(R.color.white));
                int color = getResources().getColor(R.color.category_Science);
                data = new PieData(dataSet);

                // Setup the chart
                chart.setData(data);
                chart.setUsePercentValues(true);
                chart.setCenterText("Votes by Category");
                chart.setCenterTextSize(30);
                chart.setCenterTextColor(getResources().getColor(R.color.blue_purple_mix));
                chart.setEntryLabelTextSize(13);
                Description description = new Description();
                description.setText("Voting win percentage: " + win_perct);
                description.setTextSize(30);
                description.setTextAlign(Paint.Align.RIGHT);
                description.setXOffset(40);
                description.setYOffset(10);
                chart.setDescription(description);
                chart.setDrawEntryLabels(false);
                chart.animateY(1000);
                Legend legend = chart.getLegend();
                legend.setEnabled(false);
                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public List<Integer> setupColors(List<String> categories) {
        List<Integer> ret = new ArrayList<Integer>();
        for(int i = 0; i < categories.size(); i++) {
            String str = categories.get(i);
            //Below sets the correct color of the category icon
            switch (str) {
                case "Art":
                    ret.add(getResources().getColor(R.color.category_Art));
                    break;
                case "Board Games":
                    ret.add(getResources().getColor(R.color.category_BoardGames));
                    break;
                case "Books":
                    ret.add(getResources().getColor(R.color.category_Books));
                    break;
                case "Comedy":
                    ret.add(getResources().getColor(R.color.category_Comedy));
                    break;
                case "Food":
                    ret.add(getResources().getColor(R.color.category_Food));
                    break;
                case "Movies":
                    ret.add(getResources().getColor(R.color.category_Movies));
                    break;
                case "Music":
                    ret.add(getResources().getColor(R.color.category_Music));
                    break;
                case "Philosophy":
                    ret.add(getResources().getColor(R.color.category_Philosophy));
                    break;
                case "Politics":
                    ret.add(getResources().getColor(R.color.category_Politics));
                    break;
                case "Relationships":
                    ret.add(getResources().getColor(R.color.category_Relationships));
                    break;
                case "Science":
                    ret.add(getResources().getColor(R.color.category_Science));
                    break;
                case "Sports":
                    ret.add(getResources().getColor(R.color.category_Sports));
                    break;
                case "TV Shows":
                    ret.add(getResources().getColor(R.color.category_TvShows));
                    break;
                case "Video Games":
                    ret.add(getResources().getColor(R.color.category_VideoGames));
                    break;
                case "Miscellaneous":
                    ret.add(getResources().getColor(R.color.category_Misc));
                    break;

                default:
                    Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
            }
        }

        return ret;
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

        Drawable catDraw = ContextCompat.getDrawable(StatisticsActivity_vote.this, R.drawable.shape_category);
        //Below sets the correct color of the category icon
        switch (firsWord) {
            case "Art":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Art), PorterDuff.Mode.MULTIPLY);
                Art_voted.setText(catName);
                Art_voted.setTextColor(Color.WHITE);
                Art_voted.setBackground(catDraw);
                Art_voted.setPadding(8, 8, 8, 8);
                Art_voted.setVisibility(View.VISIBLE);
                break;
            case "Board":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_BoardGames), PorterDuff.Mode.MULTIPLY);
                Board_voted.setText(catName);
                Board_voted.setTextColor(Color.WHITE);
                Board_voted.setBackground(catDraw);
                Board_voted.setPadding(8, 8, 8, 8);
                Board_voted.setVisibility(View.VISIBLE);
                break;
            case "Books":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Books), PorterDuff.Mode.MULTIPLY);
                Books_voted.setText(catName);
                Books_voted.setTextColor(Color.WHITE);
                Books_voted.setBackground(catDraw);
                Books_voted.setPadding(8, 8, 8, 8);
                Books_voted.setVisibility(View.VISIBLE);
                break;
            case "Comedy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Comedy), PorterDuff.Mode.MULTIPLY);
                Comedy_voted.setText(catName);
                Comedy_voted.setTextColor(Color.WHITE);
                Comedy_voted.setBackground(catDraw);
                Comedy_voted.setPadding(8, 8, 8, 8);
                Comedy_voted.setVisibility(View.VISIBLE);
                break;
            case "Food":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Food), PorterDuff.Mode.MULTIPLY);
                Food_voted.setText(catName);
                Food_voted.setTextColor(Color.WHITE);
                Food_voted.setBackground(catDraw);
                Food_voted.setPadding(8, 8, 8, 8);
                Food_voted.setVisibility(View.VISIBLE);
                break;
            case "Movies":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Movies), PorterDuff.Mode.MULTIPLY);
                Movies_voted.setText(catName);
                Movies_voted.setTextColor(Color.WHITE);
                Movies_voted.setBackground(catDraw);
                Movies_voted.setPadding(8, 8, 8, 8);
                Movies_voted.setVisibility(View.VISIBLE);
                break;
            case "Music":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Music), PorterDuff.Mode.MULTIPLY);
                Music_voted.setText(catName);
                Music_voted.setTextColor(Color.WHITE);
                Music_voted.setBackground(catDraw);
                Music_voted.setPadding(8, 8, 8, 8);
                Music_voted.setVisibility(View.VISIBLE);
                break;
            case "Philosophy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Philosophy), PorterDuff.Mode.MULTIPLY);
                Philosophy_voted.setText(catName);
                Philosophy_voted.setTextColor(Color.WHITE);
                Philosophy_voted.setBackground(catDraw);
                Philosophy_voted.setPadding(8, 8, 8, 8);
                Philosophy_voted.setVisibility(View.VISIBLE);
                break;
            case "Politics":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Politics), PorterDuff.Mode.MULTIPLY);
                Politics_voted.setText(catName);
                Politics_voted.setTextColor(Color.WHITE);
                Politics_voted.setBackground(catDraw);
                Politics_voted.setPadding(8, 8, 8, 8);
                Politics_voted.setVisibility(View.VISIBLE);
                break;
            case "Relationships":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Relationships), PorterDuff.Mode.MULTIPLY);
                Relationships_voted.setText(catName);
                Relationships_voted.setTextColor(Color.WHITE);
                Relationships_voted.setBackground(catDraw);
                Relationships_voted.setPadding(8, 8, 8, 8);
                Relationships_voted.setVisibility(View.VISIBLE);
                break;
            case "Science":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Science), PorterDuff.Mode.MULTIPLY);
                Science_voted.setText(catName);
                Science_voted.setTextColor(Color.WHITE);
                Science_voted.setBackground(catDraw);
                Science_voted.setPadding(8, 8, 8, 8);
                Science_voted.setVisibility(View.VISIBLE);
                break;
            case "Sports":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Sports), PorterDuff.Mode.MULTIPLY);
                Sports_voted.setText(catName);
                Sports_voted.setTextColor(Color.WHITE);
                Sports_voted.setBackground(catDraw);
                Sports_voted.setPadding(8, 8, 8, 8);
                Sports_voted.setVisibility(View.VISIBLE);
                break;
            case "TV":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_TvShows), PorterDuff.Mode.MULTIPLY);
                TV_voted.setText(catName);
                TV_voted.setTextColor(Color.WHITE);
                TV_voted.setBackground(catDraw);
                TV_voted.setPadding(8, 8, 8, 8);
                TV_voted.setVisibility(View.VISIBLE);
                break;
            case "Video":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_VideoGames), PorterDuff.Mode.MULTIPLY);
                Video_voted.setText(catName);
                Video_voted.setTextColor(Color.WHITE);
                Video_voted.setBackground(catDraw);
                Video_voted.setPadding(8, 8, 8, 8);
                Video_voted.setVisibility(View.VISIBLE);
                break;
            case "Miscellaneous":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_vote.this, R.color.category_Misc), PorterDuff.Mode.MULTIPLY);
                Miscellaneous_voted.setText(catName);
                Miscellaneous_voted.setTextColor(Color.WHITE);
                Miscellaneous_voted.setBackground(catDraw);
                Miscellaneous_voted.setPadding(8, 8, 8, 8);
                Miscellaneous_voted.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                Toast.makeText(StatisticsActivity_vote.this, "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                        "See colors.xml for correct options", Toast.LENGTH_LONG).show();

        }


    }
}
