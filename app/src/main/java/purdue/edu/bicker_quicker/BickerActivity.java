package purdue.edu.bicker_quicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class BickerActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profButton) {
            goToProfile();
        }

        if (id == R.id.searchthrough) {
            Toast.makeText(getApplicationContext(),"Search", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar); // Need this so we can program our toolbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicker);
        Button signOut = findViewById(R.id.signOutButton);
        FloatingActionButton createBicker = findViewById(R.id.createNewBickerButton);
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bicker Page");

        // Set up profile icon
        /*
        Drawable drawable = getResources().getDrawable(R.drawable.profile_icon);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
        toolbar.setNavigationIcon(newdrawable);

        */

        createBicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateBicker();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    // Change scene to the create bicker page
    public void openCreateBicker() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    public void signOut(){

        // To Sign Out of Facebook, do this:
        MainActivity.signOut();

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        startActivity(new Intent(BickerActivity.this, MainActivity.class));
                    }
                });
    }

    public void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
