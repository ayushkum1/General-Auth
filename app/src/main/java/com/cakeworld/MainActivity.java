package com.cakeworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cakeworld.LoginSignup.LoginActivity;
import com.cakeworld.Models.CakeModelClass;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference ref;
    private GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    RecyclerView rvCake;
    CakeAdapter cakeAdapter;
    List<CakeModelClass> cakeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvCake = findViewById(R.id.cake_details_rv);

        auth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ref = FirebaseDatabase.getInstance().getReference("CakeDetails");

        rvCake.setLayoutManager(new LinearLayoutManager(this));
        cakeAdapter = new CakeAdapter(MainActivity.this, cakeList);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    cakeList.add(ds.getValue(CakeModelClass.class));
                }
                rvCake.setAdapter(cakeAdapter);
                cakeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error in Loading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //options menu, profile and other options can be set here, add more options in menu file of res
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.logout:
            {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(auth.getCurrentUser() == null && accessToken == null){
                    mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "logged out", Toast.LENGTH_LONG).show();
                            Intent logoutintent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(logoutintent);
                        }
                    }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else if(auth.getCurrentUser() == null && gso.getAccount() == null){
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                else if(gso.getAccount() == null && accessToken == null){
                    auth.signOut();
                }
            }
            break;
        }
        return true;
    }
}
