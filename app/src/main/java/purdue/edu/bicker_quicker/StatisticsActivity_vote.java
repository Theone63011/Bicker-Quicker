package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
                description.setXOffset(50);
                description.setYOffset(30);
                chart.setDescription(description);
                chart.setDrawEntryLabels(false);
                chart.animateY(1000);
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

    public class MyValueFormatter extends ValueFormatter {// IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("##.##"); // use no decimals
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + "%"; // in case you want to add percent
        }
    }

}
