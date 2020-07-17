package com.cakeworld.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cakeworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Button getlinkbtn, backbtn;
    EditText email_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getlinkbtn = findViewById(R.id.reset_pass_btn);
        backbtn = findViewById(R.id.back_btn);
        email_edit = findViewById(R.id.email_edt_text);

        getlinkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edit.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this,"enter mail",Toast.LENGTH_LONG).show();
                }
                else{
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ForgotPasswordActivity.this,"link sent to mail", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(ForgotPasswordActivity.this,"unable to send link", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(myintent);
            }
        });
    }
}

