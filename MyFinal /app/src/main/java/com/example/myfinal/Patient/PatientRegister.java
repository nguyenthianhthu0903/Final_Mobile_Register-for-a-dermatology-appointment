package com.example.myfinal.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientRegister extends AppCompatActivity {

    private EditText et_mail, et_pass1, et_pass2, et_phone;
    private TextView tv_layout_signin;
    private ImageButton btn_signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_register);

        et_mail = findViewById(R.id.et_mail);
        et_pass1 = findViewById(R.id.et_pass1);
        et_pass2 = findViewById(R.id.et_pass2);
        et_phone = findViewById(R.id.et_phone);
        tv_layout_signin = findViewById(R.id.tv_layout_signin);
        btn_signup = findViewById(R.id.btn_signup);

        tv_layout_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientRegister.this, PatientLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    public void onClickSignUp() {
        mAuth = FirebaseAuth.getInstance();
        String str_mail = et_mail.getText().toString().trim();
        String str_pass1 = et_pass1.getText().toString().trim();
        String str_pass2 = et_pass2.getText().toString().trim();
        String str_phone = et_phone.getText().toString().trim();

        if (TextUtils.isEmpty(str_mail)) {
            Toast.makeText(PatientRegister.this, "Enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(str_pass1)) {
            Toast.makeText(PatientRegister.this, "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(str_pass2)) {
            Toast.makeText(PatientRegister.this, "Confirm  password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(str_phone)) {
            Toast.makeText(PatientRegister.this, "Enter phone!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!str_pass1.equals(str_pass2)) {
            Toast.makeText(PatientRegister.this, "Password not match!", Toast.LENGTH_SHORT).show();
        }

        mAuth.createUserWithEmailAndPassword(str_mail, str_pass1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(PatientRegister.this,PatientLogin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PatientRegister.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        // Add documentID bằng email đã đăng ký vào collection "Patient"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("Patient");

        String documentId = str_mail;

        Map<String, Object> user = new HashMap<>();
        user.put("email", str_mail);
        user.put("phone", str_phone);
        userRef.document(documentId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        // Tạo collection "MyDoctors" trong document(patientID)
//        DocumentReference patientRef = db.collection("Patient").document(documentId);
//        CollectionReference collectionReference = patientRef.collection("MyDoctors");
//        Map<String, Object> doctor = new HashMap<>();
//        doctor.put("name", "John Doe");
//        doctor.put("specialty", "Cardiology");
//        doctor.put("address", "123 Main St");
//        collectionReference.add(doctor)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        // Thêm Doctor thành công
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Thêm Doctor thất bại
//                    }
//                });

    }
}
