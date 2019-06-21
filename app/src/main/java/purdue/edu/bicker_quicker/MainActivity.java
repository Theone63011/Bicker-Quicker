package purdue.edu.bicker_quicker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import bolts.Task;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder message;

    Dialog myDialog;

    private static FirebaseAuth mAuth;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String EMAIL = "email";

    private CallbackManager mCallbackManager;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        myDialog = new Dialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // check to see if they are already logged in:
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInToFacebook = accessToken != null && !accessToken.isExpired();

        if(isLoggedInToFacebook) {
            // TODO
            String message = "User is Logged into Facebook";
            Log.d(TAG, message);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            startActivity(new Intent(MainActivity.this, BickerActivity.class));
        }
        else {
            String message = "User is NOT Logged into Facebook";
            Log.d(TAG, message);
        }

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton mLoginButton = findViewById(R.id.login_button);

        // Set initial permissions to request from the user while logging in
        mLoginButton.setPermissions(Arrays.asList(EMAIL));
        mLoginButton.setPermissions(Arrays.asList("email", "public_profile"));

        // To Login Outside of the login button:
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        // Callback registration
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // If successful, the loginResult is the AccessToken
                Log.d(TAG, "Facebook Login Successful");
                handleFacebookAccessToken(loginResult.getAccessToken());
                setResult(RESULT_OK);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook Login Canceled");
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Login Error");
                Log.d(TAG, exception.toString());
                ShowPopup("Login Error");
                finish();
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        // If successful, the loginResult is the AccessToken
                        Log.d(TAG, "Facebook Login Successful");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "Facebook Login Canceled");
                        setResult(RESULT_CANCELED);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "Login Error");
                        Log.d(TAG, exception.toString());
                        ShowPopup("Login Error");
                        finish();
                    }
                });

        // To Logout of Facebook, do this:
        //LoginManager.getInstance().logOut();
        //Intent loginIntent = new Intent(MainActivity.this, FacebookLoginActivity.class);
        //startActivity(loginIntent);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Inside OnActivityResult in MainActivity.java");
        Log.d(TAG, "requestCode == " + requestCode);
        Log.d(TAG, "resultCode == " + resultCode);

        if (requestCode == 100 || requestCode == 64206) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(new Intent(MainActivity.this, BickerActivity.class));
                // ...
            } else {
                message = new AlertDialog.Builder(this);
                message.setMessage("Sign in failed");
                message.show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        //new AuthUI.IdpConfig.GoogleBuilder().build(),
        //new AuthUI.IdpConfig.FacebookBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                100);
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        //If the call to signInWithCredential succeeds, you can use
        // the getCurrentUser method to get the user's account data.

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Sign in success
                String username = authResult.getAdditionalUserInfo().getUsername();
                String email = authResult.getUser().getEmail();
                Log.d(TAG, "Facebook signInWithCredential: Success");
                Log.d(TAG, "authResult username: " + username);
                Log.d(TAG, "authResult email: " + email);
                FirebaseUser user = mAuth.getCurrentUser();
                email = user.getEmail();
                String uid = user.getUid();
                Log.d(TAG, "user.getEmail(): " + email);
                Log.d(TAG, "user.getUid(): " + uid);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Facebook signInWithCredential: FAIL");
                Toast.makeText(MainActivity.this, "Facebook Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

               /*.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isCompleted()) {
                            // Sign in success
                            Log.d(TAG, "Facebook signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails
                            Log.w(TAG, "Facebook signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Facebook Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });*/
    }

    public static void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        //updateUI(null);
    }

}
