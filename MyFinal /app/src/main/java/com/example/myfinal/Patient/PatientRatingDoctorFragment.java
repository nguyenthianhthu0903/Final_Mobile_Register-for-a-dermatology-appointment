package com.example.myfinal.Patient;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientRatingDoctorFragment extends Fragment {

    private ImageView iv_avt, back;
    private TextView tv_name;
    private EditText et_comment;
    private Button btn_rating;
    private RatingBar star;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String patientID, doctorID, strDate, nameDoctor, comment, namePatient;
    int rating;
    Timestamp timestamp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_rating_doctor, container, false);

        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        et_comment = (EditText) view.findViewById(R.id.et_comment);
        btn_rating =(Button) view.findViewById(R.id.btn_rating);
        star = (RatingBar) view.findViewById(R.id.star);

        Bundle bundle = getArguments();
        patientID = bundle.getString("id_patient");
        doctorID = bundle.getString("id_doctor");
        nameDoctor = bundle.getString("nameDoctor");
        strDate = bundle.getString("date");
        if (strDate != null) {
            SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
            try {
                Date date = format_day.parse(strDate);
                timestamp = new Timestamp(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "strDate is null");
        }

        String userPhotoPath = doctorID + ".jpg";
        Task<Uri> storageRef = FirebaseStorage.getInstance().getReference()
                .child("DoctorProfile/" + userPhotoPath)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        tv_name.setText(nameDoctor);

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppointmentCompletedFragment appointmentCompletedFragment = new AppointmentCompletedFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_rating, appointmentCompletedFragment).commit();
            }
        });

        Query query = firebaseFirestore
                .collection("Doctor")
                .document(doctorID)
                .collection("Rating")
                .whereEqualTo("id_doctor", doctorID)
                .whereEqualTo("id_patient", patientID)
                .whereEqualTo("date_completed", timestamp)
                .whereEqualTo("status", "rated");

        query.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("TAG", "Error getting rating", error);
                return;
            }
            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                if (documentSnapshot.exists()) {
                    star.setRating(documentSnapshot.getLong("star"));
                    et_comment.setText(documentSnapshot.getString("comment"));
                    btn_rating.setEnabled(false);
                    star.setEnabled(false);
                    et_comment.setEnabled(false);
                    return;
                }
            }
        });

        DocumentReference documentReference =
                firebaseFirestore.collection("Patient").document(patientID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        namePatient = document.getString("name");
                        Log.d("TAG", "DocumentSnapshot data: " + namePatient);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = star.getNumStars();
                comment = et_comment.getText().toString();
                DocumentReference RatingRef = firebaseFirestore
                        .collection("Doctor")
                        .document(doctorID)
                        .collection("Rating")
                        .document();

                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("id_doctor", doctorID);
                objectMap.put("id_patient", patientID);
                objectMap.put("nameDoctor", nameDoctor);
                objectMap.put("namePatient",namePatient);
                objectMap.put("date_completed", timestamp);
                objectMap.put("date", Timestamp.now());
                objectMap.put("star", rating);
                objectMap.put("comment", comment);
                objectMap.put("status", "rated");
                RatingRef.set(objectMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });


                return view;
    }
}
