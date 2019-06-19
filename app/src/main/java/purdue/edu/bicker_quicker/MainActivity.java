package purdue.edu.bicker_quicker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String EMAIL = "email";

    private CallbackManager mCallbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Inside MainActivity onActivityResult");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        // check to see if they are already logged in:

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn) {
            // TODO
            String message = "User is Logged into Facebook";
            ShowPopup(message);
            // Set the 'FacebookLoginActivity' to the next activity
            //Intent loginIntent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            //startActivity(loginIntent);
        }
        else {
            String message = "User is NOT Logged into Facebook";
            ShowPopup(message);
        }

        Button popupButton = (Button) findViewById(R.id.btnShowSamplePopup);
        popupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowPopup("Button Pressed");
            }
        });


        mCallbackManager = CallbackManager.Factory.create();

        LoginButton mLoginButton = findViewById(R.id.login_button);

        // Set initial permissions to request from the user while logging in
        mLoginButton.setPermissions(Arrays.asList(EMAIL));

        // To Login Outside of the login button:
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        // Callback registration
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // If successful, the loginResult is the AccessToken
                // ADD CODE HERE
                Log.d(TAG, "Login Successful");
                setResult(RESULT_OK);
                ShowPopup("Login Successful");
            }

            @Override
            public void onCancel() {
                // ADD CODE HERE
                Log.d(TAG, "Login Canceled");
                setResult(RESULT_CANCELED);
                //ShowPopup("Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // ADD CODE HERE
                Log.d(TAG, "Login Error");
                Log.d(TAG, exception.toString());
                ShowPopup("Login Error");
            }
        });


        // To Logout of Facebook, do this:
        //LoginManager.getInstance().logOut();
        //Intent loginIntent = new Intent(MainActivity.this, FacebookLoginActivity.class);
        //startActivity(loginIntent);
    }

    public void ShowPopup(String msg) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.simple_popup_window);
        TextView message;
        message =(TextView) myDialog.findViewById(R.id.popup_message);
        message.setText(msg);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}
