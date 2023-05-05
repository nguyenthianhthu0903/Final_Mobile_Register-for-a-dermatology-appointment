package com.example.myfinal.Doctor;

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
import com.example.myfinal.Model.Doctor;
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

public class DoctorHomeFragment extends Fragment {

    private ImageView iv_avt;
    private TextView tv_name, tv_mail, tv_date, tv_month, tv_day, tv_patients, tv_requests, tv_hour;
    private FirebaseAuth mAuth;
    private FirebaseFirestore doctorRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String doctorID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_home, container, false);
        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_mail = (TextView) view.findViewById(R.id.tv_mail);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_month = (TextView) view.findViewById(R.id.tv_month);
        tv_day = (TextView) view.findViewById(R.id.tv_day);
        tv_patients = (TextView) view.findViewById(R.id.tv_patients);
        tv_requests = (TextView) view.findViewById(R.id.tv_requests);
        tv_hour = (TextView) view.findViewById(R.id.tv_hour);

        showCalendar();
        showUserInfo();

        mAuth = FirebaseAuth.getInstance();
        doctorRef = FirebaseFirestore.getInstance();
        doctorID = mAuth.getCurrentUser().getEmail();

        tv_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestFragment requestFragment = new RequestFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, requestFragment).commit();
            }
        });

        tv_patients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPatientsFragment myPatientsFragment = new MyPatientsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, myPatientsFragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DocumentReference documentReference = doctorRef.collection("Doctor").document(doctorID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        Doctor doctor = document.toObject(Doctor.class);
                        Log.d("TAG", " data: " + doctor.toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        documentReference.addSnapshotListener((documentSnapshot, error) -> tv_name.setText(documentSnapshot.getString("name")));

        String userPhotoPath = doctorID + ".jpg";
        StorageReference storageRef = storage.getReference();

        storageRef.child("DoctorProfile/" + userPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        Glide.with(this).load(photo).error(R.drawable.img_doctor_7).into(iv_avt );
    }
}
