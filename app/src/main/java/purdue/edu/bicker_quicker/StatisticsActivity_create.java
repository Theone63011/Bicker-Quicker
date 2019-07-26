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

public class StatisticsActivity_create extends AppCompatActivity {

    private static final String TAG = StatisticsActivity_create.class.getSimpleName();

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

    private static DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_create);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        userKey = user.getUid();
        DatabaseReference userRef = databaseReference.child("User/" + userKey);

        chart = (PieChart) findViewById(R.id.statistics_create_pieChart);
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

        toolbar = findViewById(R.id.toolbar_statistics);
        home = findViewById(R.id.nav_home);
        vote = findViewById(R.id.nav_vote);
        create = findViewById(R.id.nav_create);
        graph = findViewById(R.id.nav_graph);

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
                Intent intent = new Intent(StatisticsActivity_create.this, StatisticsActivity_create.class);
                startActivity(intent);
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_create.this, StatisticsActivity_vote.class);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_create.this, StatisticsActivity_create.class);
                startActivity(intent);
            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity_create.this, StatisticsActivity_graph.class);
                startActivity(intent);
            }
        });

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

        initializeCreatedCategories("Art");
        initializeCreatedCategories("Board Games");
        initializeCreatedCategories("Books");
        initializeCreatedCategories("Comedy");
        initializeCreatedCategories("Food");
        initializeCreatedCategories("Movies");
        initializeCreatedCategories("Music");
        initializeCreatedCategories("Philosophy");
        initializeCreatedCategories("Politics");
        initializeCreatedCategories("Relationships");
        initializeCreatedCategories("Science");
        initializeCreatedCategories("Sports");
        initializeCreatedCategories("TV Shows");
        initializeCreatedCategories("Video Games");
        initializeCreatedCategories("Miscellaneous");

        // Get user voted categories and add them to the 'categories' arraylist
        userRef.child("CreatedBickers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total_completed = 0.0;
                double wins = 0.0;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot voted : dataSnapshot.getChildren()) {
                        try {
                            if(voted.child("category") != null) {
                                String to_add = voted.child("category").getValue().toString();
                                double amount = categories.get(to_add);
                                amount++;
                                categories.put(to_add, amount);
                            }

                            String winning_side = voted.child("Winning_Side").getValue().toString();

                            if(winning_side.equalsIgnoreCase("still active") == false) {
                                if(winning_side.equalsIgnoreCase("left")) {
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
                chart.setCenterText("Created Bickers by Category");
                chart.setCenterTextSize(30);
                chart.setCenterTextColor(getResources().getColor(R.color.blue_purple_mix));
                chart.setEntryLabelTextSize(13);
                Description description = new Description();
                description.setText("Created side win percentage: " + win_perct);
                description.setTextSize(30);
                description.setTextAlign(Paint.Align.RIGHT);
                description.setXOffset(10);
                description.setYOffset(10);
                chart.setDescription(description);
                chart.setDrawEntryLabels(false);
                chart.animateY(1000);
                Legend legend = chart.getLegend();
                legend.setTextSize(20);
                legend.setWordWrapEnabled(true);
                legend.setYOffset(50);
                legend.setEnabled(false);
                chart.invalidate();
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

    public void initializeCreatedCategories (String cat) {
        String catName = cat;
        String tokens [] = catName.split(" ", 2);
        String firsWord = tokens[0];

        Log.d(TAG, "firstWord: " + firsWord);

        Drawable catDraw = ContextCompat.getDrawable(StatisticsActivity_create.this, R.drawable.shape_category);
        //Below sets the correct color of the category icon
        switch (firsWord) {
            case "Art":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Art), PorterDuff.Mode.MULTIPLY);
                Art_created.setText(catName);
                Art_created.setTextColor(Color.WHITE);
                Art_created.setBackground(catDraw);
                Art_created.setPadding(8, 8, 8, 8);
                Art_created.setVisibility(View.VISIBLE);
                break;
            case "Board":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_BoardGames), PorterDuff.Mode.MULTIPLY);
                Board_created.setText(catName);
                Board_created.setTextColor(Color.WHITE);
                Board_created.setBackground(catDraw);
                Board_created.setPadding(8, 8, 8, 8);
                Board_created.setVisibility(View.VISIBLE);
                break;
            case "Books":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Books), PorterDuff.Mode.MULTIPLY);
                Books_created.setText(catName);
                Books_created.setTextColor(Color.WHITE);
                Books_created.setBackground(catDraw);
                Books_created.setPadding(8, 8, 8, 8);
                Books_created.setVisibility(View.VISIBLE);
                break;
            case "Comedy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Comedy), PorterDuff.Mode.MULTIPLY);
                Comedy_created.setText(catName);
                Comedy_created.setTextColor(Color.WHITE);
                Comedy_created.setBackground(catDraw);
                Comedy_created.setPadding(8, 8, 8, 8);
                Comedy_created.setVisibility(View.VISIBLE);
                break;
            case "Food":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Food), PorterDuff.Mode.MULTIPLY);
                Food_created.setText(catName);
                Food_created.setTextColor(Color.WHITE);
                Food_created.setBackground(catDraw);
                Food_created.setPadding(8, 8, 8, 8);
                Food_created.setVisibility(View.VISIBLE);
                break;
            case "Movies":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Movies), PorterDuff.Mode.MULTIPLY);
                Movies_created.setText(catName);
                Movies_created.setTextColor(Color.WHITE);
                Movies_created.setBackground(catDraw);
                Movies_created.setPadding(8, 8, 8, 8);
                Movies_created.setVisibility(View.VISIBLE);
                break;
            case "Music":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Music), PorterDuff.Mode.MULTIPLY);
                Music_created.setText(catName);
                Music_created.setTextColor(Color.WHITE);
                Music_created.setBackground(catDraw);
                Music_created.setPadding(8, 8, 8, 8);
                Music_created.setVisibility(View.VISIBLE);
                break;
            case "Philosophy":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Philosophy), PorterDuff.Mode.MULTIPLY);
                Philosophy_created.setText(catName);
                Philosophy_created.setTextColor(Color.WHITE);
                Philosophy_created.setBackground(catDraw);
                Philosophy_created.setPadding(8, 8, 8, 8);
                Philosophy_created.setVisibility(View.VISIBLE);
                break;
            case "Politics":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Politics), PorterDuff.Mode.MULTIPLY);
                Politics_created.setText(catName);
                Politics_created.setTextColor(Color.WHITE);
                Politics_created.setBackground(catDraw);
                Politics_created.setPadding(8, 8, 8, 8);
                Politics_created.setVisibility(View.VISIBLE);
                break;
            case "Relationships":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Relationships), PorterDuff.Mode.MULTIPLY);
                Relationships_created.setText(catName);
                Relationships_created.setTextColor(Color.WHITE);
                Relationships_created.setBackground(catDraw);
                Relationships_created.setPadding(8, 8, 8, 8);
                Relationships_created.setVisibility(View.VISIBLE);
                break;
            case "Science":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Science), PorterDuff.Mode.MULTIPLY);
                Science_created.setText(catName);
                Science_created.setTextColor(Color.WHITE);
                Science_created.setBackground(catDraw);
                Science_created.setPadding(8, 8, 8, 8);
                Science_created.setVisibility(View.VISIBLE);
                break;
            case "Sports":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Sports), PorterDuff.Mode.MULTIPLY);
                Sports_created.setText(catName);
                Sports_created.setTextColor(Color.WHITE);
                Sports_created.setBackground(catDraw);
                Sports_created.setPadding(8, 8, 8, 8);
                Sports_created.setVisibility(View.VISIBLE);
                break;
            case "TV":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_TvShows), PorterDuff.Mode.MULTIPLY);
                TV_created.setText(catName);
                TV_created.setTextColor(Color.WHITE);
                TV_created.setBackground(catDraw);
                TV_created.setPadding(8, 8, 8, 8);
                TV_created.setVisibility(View.VISIBLE);
                break;
            case "Video":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_VideoGames), PorterDuff.Mode.MULTIPLY);
                Video_created.setText(catName);
                Video_created.setTextColor(Color.WHITE);
                Video_created.setBackground(catDraw);
                Video_created.setPadding(8, 8, 8, 8);
                Video_created.setVisibility(View.VISIBLE);
                break;
            case "Miscellaneous":
                catDraw.setColorFilter(ContextCompat.getColor(StatisticsActivity_create.this, R.color.category_Misc), PorterDuff.Mode.MULTIPLY);
                Miscellaneous_created.setText(catName);
                Miscellaneous_created.setTextColor(Color.WHITE);
                Miscellaneous_created.setBackground(catDraw);
                Miscellaneous_created.setPadding(8, 8, 8, 8);
                Miscellaneous_created.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(TAG, "ERROR: Could not find a corresponding color category. See colors.xml for correct options");
                Toast.makeText(StatisticsActivity_create.this, "Home_Fragment: ERROR: Could not find a corresponding color category. " +
                        "See colors.xml for correct options", Toast.LENGTH_LONG).show();

        }


    }

}
