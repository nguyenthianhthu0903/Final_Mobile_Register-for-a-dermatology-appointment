package com.example.myfinal.Doctor;

import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.Activity.CheckRole;
import com.example.myfinal.Model.Doctor;
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

public class DoctorAccountFragment extends Fragment {

    private ImageView iv_avt;
    private TextView tv_name, tv_mail, tv_star;
    private CardView cv_edit, cv_log_out;
    private FirebaseAuth mAuth;
    private FirebaseFirestore doctorRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String doctorID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_account, container, false);
        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_mail = (TextView) view.findViewById(R.id.tv_mail);
        tv_star = (TextView) view.findViewById(R.id.tv_star);

        cv_edit = (CardView) view.findViewById(R.id.cv_edit);
        cv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorEditProfileFragment doctorEditProfileFragment = new DoctorEditProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_account, doctorEditProfileFragment).commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        doctorRef = FirebaseFirestore.getInstance();
        doctorID = mAuth.getCurrentUser().getEmail();

        cv_log_out = (CardView) view.findViewById(R.id.cv_log_out);
        cv_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), CheckRole.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        DocumentReference doctorRef =
                FirebaseFirestore.getInstance().collection("Doctor").document(doctorID);
        doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        doctorRef.addSnapshotListener((documentSnapshot, error) -> {
            Double rating = documentSnapshot.getDouble("rating");
            tv_star.setText("Doctor rating: " + String.valueOf(rating));
        });

        tv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorSeeRatingFragment doctorSeeRatingFragment = new DoctorSeeRatingFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_account, doctorSeeRatingFragment).commit();
            }
        });

        showUserInfo();

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

    public void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

//        String name = user.getDisplayName();
        String mail = user.getEmail();
        Uri photo = user.getPhotoUrl();

//        if (name == null) {
//            tv_name.setVisibility(View.GONE);
//        } else {
//            tv_name.setVisibility(View.VISIBLE);
//            tv_mail.setText(name);
//        }

        tv_mail.setText(mail);
        Glide.with(this).load(photo).error(R.drawable.img_doctor_7).into(iv_avt);
    }
}