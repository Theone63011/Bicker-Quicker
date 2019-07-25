package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class StatisticsActivity_vote extends AppCompatActivity {

    public Toolbar toolbar;

    private ImageView home;
    private ImageView vote;
    private ImageView create;
    private ImageView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_vote);

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


    }

    public void leave() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
