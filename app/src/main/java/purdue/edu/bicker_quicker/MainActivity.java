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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

//import bolts.Task;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder message;


    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    static final int GOOGLE_SIGN = 1;
    TextView text;
    SignInButton google_btn_login;
    public static View custom_google_login_button;
    public static GoogleSignInClient mGoogleSignInClient;

    public static LoginButton mLoginButton;
    public static View custom_facebook_login_button;

    Dialog myDialog;

    private static final String EMAIL = "email";

    private CallbackManager mCallbackManager;

    @Override
    public void onResume() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

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

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Log.d(TAG, "Token: " + token);

                    }
                });


        google_btn_login = findViewById(R.id.google_sign_in_button);
        custom_google_login_button = (Button)findViewById(R.id.custom_google_login_button);
        custom_google_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCustomGoogleButton(v);
            }
        });

        myDialog = new Dialog(this);

        final Button signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_button);
        //this.mAuth = FirebaseAuth.getInstance();

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
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        else {
            String message = "User is NOT Logged into Facebook";
            Log.d(TAG, message);
        }

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = findViewById(R.id.facebook_login_button);
        custom_facebook_login_button = (Button)findViewById(R.id.custom_facebook_login_button);
        custom_facebook_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCustomFacebookButton(v);
            }
        });

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
                        Log.d(TAG, "2 Facebook Login Successful");

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

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken("835478961620-dqld0u2nsle6otd38f47tbbac1j0cvjj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        //google_btn_login.setOnClickListener(v -> SignInGoogle());
        google_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInGoogle();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
        }

        //check if a user is already logged in and by pass login page
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    void SignInGoogle() {
        Log.d(TAG, "SignIn with Google");
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    public void onClickCustomFacebookButton(View view){
        if(view == custom_facebook_login_button) {
            mLoginButton.performClick();
        }
    }

    public void onClickCustomGoogleButton(View view) {
        if(view == custom_google_login_button) {
            SignInGoogle();
        }
    }

    //try google sign in if google sign in button was pressed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Inside OnActivityResult in MainActivity.java");
        Log.d(TAG, "requestCode == " + requestCode);
        Log.d(TAG, "resultCode == " + resultCode);
        if (resultCode == RESULT_OK && requestCode == 100) {
            // Successfully signed in
            IdpResponse response = IdpResponse.fromResultIntent(data);
            boolean newUser = false;
            if(response != null) {
                newUser = response.isNewUser();
            }

            if(newUser == true){
                User user = new User();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("User");

                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                user.setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.setModerator(false);
                //user.setBickerId("test1");
                //user.setBickerId("test2");
                //user.setVotedBickerIds("test3");
                //user.setVotedBickerIds("test4");
                ref.push().setValue(user);

            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            // ...
        } else {
            //message = new AlertDialog.Builder(this);
            //message.setMessage("Sign in failed");
            //message.show();
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }

    }

    //create credentials of firebase user after authenticating google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signin success");
                        boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        if(newUser == true){
                            User user = new User();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("User");

                            user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            user.setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            user.setModerator(false);
                            //user.setBickerId("test1");
                            //user.setBickerId("test2");
                            //user.setVotedBickerIds("test3");
                            //user.setVotedBickerIds("test4");
                            ref.push().setValue(user);

                        }
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);

                        FirebaseUser user = mAuth.getCurrentUser();


                    } else {
                        Log.d(TAG, "Google Signin Failed");
                        Toast.makeText(MainActivity.this, "Google Signin Failed" , Toast.LENGTH_SHORT).show();
                    }
                });
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            AuthResult authResult = task.getResult();
                            boolean newUser = authResult.getAdditionalUserInfo().isNewUser();

                            Log.d(TAG, "Facebook Login Successful");

                            if(newUser == true){

                                Toast.makeText(MainActivity.this, "New user." ,
                                        Toast.LENGTH_SHORT).show();

                                User user = new User();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ref = database.getReference("User");

                                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                user.setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                user.setModerator(false);
                                //user.setBickerId("test1");
                                //user.setBickerId("test2");
                                //user.setVotedBickerIds("test3");
                                //user.setVotedBickerIds("test4");
                                ref.push().setValue(user);

                            }

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
                            // Sign in success, update UI with the signed-in user's information


                            startActivity(new Intent(MainActivity.this, HomeActivity.class));

                        } else {
                            Log.d(TAG, "Facebook signInWithCredential: FAIL");
                            Toast.makeText(MainActivity.this, "Facebook Authentication failed." , Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }

    @Override
    public void onBackPressed() {

        finish();
        return;
    }

    //signs out of facebook
    public static void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }
}
