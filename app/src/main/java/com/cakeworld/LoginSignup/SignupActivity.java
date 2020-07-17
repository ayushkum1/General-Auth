package com.cakeworld.LoginSignup;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class SignupActivity extends AppCompatActivity {

    DatabaseReference ref;
    EditText etUserFirstName, etUserLastName, etUserEmail, etUserPwd, etUserCnfmPwd, etUserPhoneno;
    Button btnSignup;
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ref = FirebaseDatabase.getInstance().getReference("UserData");

        etUserFirstName = findViewById(R.id.user_first_name);
        etUserLastName = findViewById(R.id.user_last_name);
        etUserEmail = findViewById(R.id.user_email);
        etUserPwd = findViewById(R.id.user_password);
        etUserCnfmPwd = findViewById(R.id.user_confirm_pass);
        etUserPhoneno = findViewById(R.id.user_phone_number);
        btnSignup = findViewById(R.id.user_signup_btn);



        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserInformation();
            }
        });
    }

    private void addUserInformation(){
        final String userid, fname, lname, email, password, cpwd, phone;

        userid = UUID.randomUUID().toString();
        fname = etUserFirstName.getText().toString().trim();
        lname = etUserLastName.getText().toString().trim();
        email = etUserEmail.getText().toString().trim();
        password = etUserPwd.getText().toString().trim();
        cpwd = etUserCnfmPwd.getText().toString().trim();
        phone = etUserPhoneno.getText().toString().trim();



        if(TextUtils.isEmpty(fname)){
            Toast.makeText(getApplicationContext(), "Pls Enter First Name", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(lname)){
            Toast.makeText(getApplicationContext(), "Pls Enter last Name", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Pls enter the password", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(cpwd)){
            Toast.makeText(getApplicationContext(), "Pls confirm the password", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(cpwd)){
            Toast.makeText(getApplicationContext(), "password do not match", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Phone number is empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() == 0){
            Toast.makeText(getApplicationContext(),"Please enter your password",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length()<8){
            Toast.makeText(getApplicationContext(),"Please enter passwords having more than 8 characters",Toast.LENGTH_LONG).show();
        }

        else{
            ref.child(email.replace(".","_")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Toast.makeText(SignupActivity.this, "That email already exists. Sign in instead", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (password.equals(cpwd)){

                            final String generateHashedPass = BCrypt.hashpw(password, BCrypt.gensalt());

                            final UserModel user = new UserModel(userid, fname, lname, email, generateHashedPass, phone);

                            ref.child(email.replace(".","_")).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "Details added", Toast.LENGTH_SHORT).show();
                                        Common.currentUser = user;
                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, "We're having trouble signing you up", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onFailure: "+ e.getMessage() );

                                }
                            });

                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        }
    }

}
