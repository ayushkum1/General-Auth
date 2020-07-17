package com.cakeworld.LoginSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cakeworld.LoginSignup.Common.Common;
import com.cakeworld.LoginSignup.Models.UserModel;
import com.cakeworld.MainActivity;
import com.cakeworld.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.Arrays;

//email verification

public class LoginActivity extends AppCompatActivity {

    //firebase
    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    private DatabaseReference ref;
    //google
    private static final int RC_SIGN_IN = 1;
    //fb
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    AccessToken accessToken;
    //general
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp, btnGoogleSignIn, btnFacebookLogin, btnForgotPassword;

    @Override
    public void onStart() {
        super.onStart();
        //checks if the user is of firebase or not. if not firebase then checks for google sign in
        if (auth.getCurrentUser() == null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            updateUIgoogle(account);
        }
        // if not google user or is a firebase user, update UI with firebase user
        else {  FirebaseUser user = auth.getCurrentUser();
            updateUI(user);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("UserData");
        //facebook
        callbackManager = CallbackManager.Factory.create();
        //google sign in
        final GoogleSignInClient mGoogleSignInClient;

        etEmail = findViewById(R.id.user_email);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login_btn);
        btnSignUp = findViewById(R.id.singup_btn);
        btnGoogleSignIn = findViewById(R.id.google_signin_btn);
        btnFacebookLogin = findViewById(R.id.fb_login_button);
        btnForgotPassword = findViewById(R.id.forgot_password_btn);

        //Firebase login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                final String pwd = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, "Enter full details", Toast.LENGTH_LONG).show();
                }
                else{
                    ref.child(email.replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            //crypting the password and matching it with the crypted password stored in database
                            if(user == null){
                                Toast.makeText(LoginActivity.this, "user not registered", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(BCrypt.checkpw(pwd,user.getPassword())){
                                    Common.currentUser = user;
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    Toast.makeText(LoginActivity.this, "Welcome " +user.getFname(), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Check your credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        //Login with Google
        //configuring and requesting basic details of user like email and others, currently requesting only  email
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
            private void signin() {
                Intent intentsignin = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intentsignin, RC_SIGN_IN);//onActivityResult
            }
        });

        //FaceBook
        //add function to get user profiles and thier details like profile picture and name
        //when retrieving data, store in firebase
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            btnFacebookLogin.setVisibility(View.GONE);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toast.makeText(LoginActivity.this, "Logged in with facebook", Toast.LENGTH_SHORT).show();
        }

        ((LoginButton) btnFacebookLogin).setPermissions(Arrays.asList(EMAIL));
        ((LoginButton) btnFacebookLogin).registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                btnFacebookLogin.setVisibility(View.GONE);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Toast.makeText(LoginActivity.this, "Logged in with facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error in Logging in", Toast.LENGTH_SHORT).show();
            }
        });

        //Sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(in);
                Toast.makeText(LoginActivity.this, "Signup Here", Toast.LENGTH_SHORT).show();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            }
        });
    }

    //google signed in object
    // this object will have details of the user
    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);

        //this is for facebook and signs in the user with facebook
        callbackManager.onActivityResult(requestcode, resultcode, data);

        // this is for google and signs in the user with google
        if(requestcode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

//  ******Firebase**********
    //update UI for firebase
    private void updateUI(FirebaseUser currentUser) {
        currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
        else{
            Toast.makeText(LoginActivity.this,"Pls login or signup", Toast.LENGTH_LONG).show();
        }
    }


    //  ******Google Sign in**********
    //handle google sign in result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUIgoogle(account);
        }
        catch(ApiException e){
            Log.w(TAG,"signin failed code="+ e.getStatusCode());
            updateUIgoogle(null); //3rd updateUi function
        }
    }

    //update UI for google
    private void updateUIgoogle(@Nullable GoogleSignInAccount account){
        if(account != null){
            Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
        else{
            Log.e(TAG, "updateUIgooglesignedin: google sign in failed" );
        }
    }



}
