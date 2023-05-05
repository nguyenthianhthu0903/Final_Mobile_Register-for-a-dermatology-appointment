package com.example.myfinal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.Doctor.DoctorLogin;
import com.example.myfinal.Patient.PatientLogin;
import com.example.myfinal.R;

public class CheckRole extends AppCompatActivity {

    private TextView tv_doctor;
    private TextView tv_patient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_role);

        tv_doctor = findViewById(R.id.tv_doctor);
        tv_patient = findViewById(R.id.tv_patient);

        tv_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckRole.this, DoctorLogin.class);
                startActivity(intent);
                finish();
            }
        });

        tv_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckRole.this, PatientLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
