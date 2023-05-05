package com.example.myfinal.Patient;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.Model.Patient;
import com.example.myfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PatientHomeFragment extends Fragment {

    private ImageView iv_avt, iv_doctor, iv_myskin, iv_medicine;
    private TextView tv_name, tv_mail, tv_date, tv_month, tv_day, tv_doctor, tv_myskin,
    tv_medicine, tv_hour;
    private FirebaseAuth mAuth;
    private FirebaseFirestore patientRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String patientID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_home, container, false);
        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        iv_doctor = (ImageView) view.findViewById(R.id.iv_doctor);
        iv_myskin = (ImageView) view.findViewById(R.id.iv_myskin);
        iv_medicine = (ImageView) view.findViewById(R.id.iv_medicine);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_mail = (TextView) view.findViewById(R.id.tv_mail);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_month = (TextView) view.findViewById(R.id.tv_month);
        tv_day = (TextView) view.findViewById(R.id.tv_day);
        tv_doctor = (TextView) view.findViewById(R.id.tv_doctor);
        tv_myskin = (TextView) view.findViewById(R.id.tv_myskin);
        tv_medicine = (TextView) view.findViewById(R.id.tv_medicine);
        tv_hour = (TextView) view.findViewById(R.id.tv_hour);

        showCalendar();
        showUserInfo();

        iv_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSeeDoctorFragment patientSeeDoctorFragment = new PatientSeeDoctorFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSeeDoctorFragment).commit();
            }
        });

        tv_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSeeDoctorFragment patientSeeDoctorFragment = new PatientSeeDoctorFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSeeDoctorFragment).commit();
            }
        });

        iv_myskin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSkinFragment patientSkinFragment = new PatientSkinFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSkinFragment).commit();
            }
        });

        tv_myskin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSkinFragment patientSkinFragment = new PatientSkinFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSkinFragment).commit();
            }
        });

        iv_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSeeMedicineFragment patientSeeMedicineFragment =
                        new PatientSeeMedicineFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        patientSeeMedicineFragment).commit();
            }
        });

        tv_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSeeMedicineFragment patientSeeMedicineFragment =
                        new PatientSeeMedicineFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        patientSeeMedicineFragment).commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        patientRef = FirebaseFirestore.getInstance();
        patientID = mAuth.getCurrentUser().getEmail();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DocumentReference documentReference = patientRef.collection("Patient").document(patientID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        Patient patient = document.toObject(Patient.class);
                        Log.d("TAG", " data: " + patient.toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        documentReference.addSnapshotListener((documentSnapshot, error) -> tv_name.setText(documentSnapshot.getString("name")));

        String userPhotoPath = patientID + ".jpg";
        StorageReference storageRef = storage.getReference();

        storageRef.child("PatientProfile/" + userPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(getContext()).load(uri).into(iv_avt);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    public void showCalendar() {
        Calendar calendar = Calendar.getInstance();
        tv_date.setText(calendar.get(Calendar.DATE) + "");

        Date date = new Date();
        SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
        String strDay = format_day.format(date);
        tv_day.setText(strDay);

        SimpleDateFormat format_month = new SimpleDateFormat("MMM");
        String strMonth = format_month.format(date);
        tv_month.setText(strMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm");
        String formattedTime = dateFormat.format(date);
        tv_hour.setText(formattedTime);
    }

    public void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String name = user.getDisplayName();
        String mail = user.getEmail();
        Uri photo = user.getPhotoUrl();

        if (name == null) {
            tv_name.setText("Hello");
        } else {
            tv_mail.setText(name);
        }

        tv_mail.setText(mail);
        Glide.with(this).load(photo).error(R.drawable.img_avt_default).into(iv_avt );
    }
}
