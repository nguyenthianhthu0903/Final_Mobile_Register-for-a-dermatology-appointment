package com.example.myfinal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.Doctor.BottomNavigationBar;
import com.example.myfinal.Patient.PatientBottomNavigationBar;
import com.example.myfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class CheckLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    public void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // chua Login
            Intent intent = new Intent(CheckLoginActivity.this, CheckRole.class);
            startActivity(intent);
            finish();
        } else {
            String email = user.getEmail();
            CollectionReference doctorRef = FirebaseFirestore.getInstance().collection("Doctor");
            Query query = doctorRef.whereEqualTo("email", email);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User hiện tại là Doctor
                        Intent intent = new Intent(CheckLoginActivity.this, BottomNavigationBar.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // User hiện tại không là Patient
                        Intent intent = new Intent(CheckLoginActivity.this, PatientBottomNavigationBar.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}
