package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    Button respondToBicker;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar); // Disable action bar (should be by default but this is precautionary)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        respondToBicker = findViewById(R.id.bickerRespond);
        toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Profile");
        Drawable drawable= getResources().getDrawable(R.drawable.backicon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setNavigationIcon(newdrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });

        respondToBicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRespond();
            }
        });

    }

    public void leave() {
        Intent intent = new Intent(this, BickerActivity.class);
        startActivity(intent);
    }

    public void goToRespond() {
        Intent intent = new Intent(this, RespondActivity.class);
        startActivity(intent);
    }
}
