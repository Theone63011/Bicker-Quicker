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
import android.widget.EditText;
import android.widget.TextView;


import org.w3c.dom.Text;

public class RespondActivity extends AppCompatActivity {

    TextView title_code;
    TextView title_title;
    TextView title_description;
    TextView title_side;
    TextView code_here;
    TextView title_here;
    TextView description_here;
    EditText side;
    Button send_side;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);

        title_code = findViewById(R.id.bickerCode);
        title_description = findViewById(R.id.bickerDescription);
        title_title = findViewById(R.id.bickerTitle);
        title_side = findViewById(R.id.yourSide);
        code_here = findViewById(R.id.codeHere);
        title_here = findViewById(R.id.titleHere);
        description_here = findViewById(R.id.descHere);
        side = findViewById(R.id.respondSide);
        send_side = findViewById(R.id.submitResponse);
        toolbar = findViewById(R.id.toolbarRespond);

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
    }


    public void leave() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
