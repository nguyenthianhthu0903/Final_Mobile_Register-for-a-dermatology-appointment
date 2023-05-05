package com.example.myfinal.Patient;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientSkinDetailFragment extends Fragment {

    private TextView tv_skinType_Age, tv_skinAge, tv_skinTone, tv_date, tv_skin_score,
            tv_wrinkles_score, tv_sagging_score, tv_pigmentation_score, tv_hydration_score,
            tv_redness_score, tv_translucency_score, tv_pores_score, tv_conclusion, tv_dr;
    private ProgressBar p_wrinkles_scoreBar, p_sagging_scoreBar, p_pigmentation_scoreBar,
            p_hydration_scoreBar, p_redness_scoreBar, p_translucency_scoreBar, p_pores_scoreBar;
    private ImageView back;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private String patientID;
    private ImageView iv_skin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_skin_detail, container, false);

        tv_skinType_Age = (TextView) view.findViewById(R.id.tv_skinType_Age);
        tv_skinAge = (TextView) view.findViewById(R.id.tv_skinAge);
        tv_skinTone = (TextView) view.findViewById(R.id.tv_skinTone);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_skin_score = (TextView) view.findViewById(R.id.tv_skin_score);
        tv_wrinkles_score = (TextView) view.findViewById(R.id.tv_wrinkles_score);
        tv_sagging_score = (TextView) view.findViewById(R.id.tv_sagging_score);
        tv_pigmentation_score = (TextView) view.findViewById(R.id.tv_pigmentation_score);
        tv_hydration_score = (TextView) view.findViewById(R.id.tv_hydration_score);
        tv_redness_score = (TextView) view.findViewById(R.id.tv_redness_score);
        tv_translucency_score = (TextView) view.findViewById(R.id.tv_translucency_score);
        tv_pores_score = (TextView) view.findViewById(R.id.tv_pores_score);
        tv_conclusion = (TextView) view.findViewById(R.id.tv_conclusion);
        tv_dr = (TextView) view.findViewById(R.id.tv_dr);

        p_wrinkles_scoreBar = (ProgressBar) view.findViewById(R.id.p_wrinkles_scoreBar);
        p_sagging_scoreBar = (ProgressBar) view.findViewById(R.id.p_sagging_scoreBar);
        p_pigmentation_scoreBar = (ProgressBar) view.findViewById(R.id.p_pigmentation_scoreBar);
        p_hydration_scoreBar = (ProgressBar) view.findViewById(R.id.p_hydration_scoreBar);
        p_redness_scoreBar = (ProgressBar) view.findViewById(R.id.p_redness_scoreBar);
        p_translucency_scoreBar = (ProgressBar) view.findViewById(R.id.p_translucency_scoreBar);
        p_pores_scoreBar = (ProgressBar) view.findViewById(R.id.p_pores_scoreBar);

        iv_skin = (ImageView) view.findViewById(R.id.iv_skin);

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSkinFragment patientSkinFragment = new PatientSkinFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSkinFragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        DocumentReference skinRef = firebaseFirestore.collection("SkinDetail").document(patientID);
//        skinRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
//                    tv_skinType_Age.setText(document.getString("skinType"));
//                    tv_skinAge.setText(document.getString("skinAge"));
//                    tv_skinTone.setText(document.getString("skinTone"));
//                    tv_skin_score.setText("Skin health score " + document.getString("summary") + "/100");
//                    tv_wrinkles_score.setText(document.getString("wrinkles") + "/100");
//                    tv_sagging_score.setText(document.getString("sagging") + "/100");
//                    tv_pigmentation_score.setText(document.getString("pigmentation") + "/100");
//                    tv_hydration_score.setText(document.getString("hydration") + "/100");
//                    tv_redness_score.setText(document.getString("redness") + "/100");
//                    tv_translucency_score.setText(document.getString("translucency") + "/100");
//                    tv_pores_score.setText(document.getString("pores") + "/100");
//                    tv_conclusion.setText("Conclusion: " + document.getString("conclusion"));
//                    tv_dr.setText("Update by: " + document.getString("nameDoctor"));
//
//                    Date date = document.getDate("date");
//                    SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
//                    String strDate = format_day.format(date);
//                    String time = strDate.toString().substring(0, 11);
//                    tv_date.setText(time);
//
//                    p_wrinkles_scoreBar.setProgress(Integer.parseInt(document.getString("wrinkles")));
//                    p_sagging_scoreBar.setProgress(Integer.parseInt(document.getString("sagging")));
//                    p_pigmentation_scoreBar.setProgress(Integer.parseInt(document.getString("pigmentation")));
//                    p_hydration_scoreBar.setProgress(Integer.parseInt(document.getString("hydration")));
//                    p_redness_scoreBar.setProgress(Integer.parseInt(document.getString("redness")));
//                    p_translucency_scoreBar.setProgress(Integer.parseInt(document.getString("translucency")));
//                    p_pores_scoreBar.setProgress(Integer.parseInt(document.getString("pores")));
//                }
//            }
//        });

        skinRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.d("TAG", "Firebase listener error: " + error.getMessage());
            } else {
                    tv_skinType_Age.setText(documentSnapshot.getString("skinType"));
                    tv_skinAge.setText(documentSnapshot.getString("skinAge"));
                    tv_skinTone.setText(documentSnapshot.getString("skinTone"));
                    tv_skin_score.setText("Skin health score " + documentSnapshot.getString("summary") + "/100");
                    tv_wrinkles_score.setText(documentSnapshot.getString("wrinkles") + "/100");
                    tv_sagging_score.setText(documentSnapshot.getString("sagging") + "/100");
                    tv_pigmentation_score.setText(documentSnapshot.getString("pigmentation") + "/100");
                    tv_hydration_score.setText(documentSnapshot.getString("hydration") + "/100");
                    tv_redness_score.setText(documentSnapshot.getString("redness") + "/100");
                    tv_translucency_score.setText(documentSnapshot.getString("translucency") + "/100");
                    tv_pores_score.setText(documentSnapshot.getString("pores") + "/100");
                    tv_conclusion.setText("Conclusion: " + documentSnapshot.getString("conclusion"));
                    tv_dr.setText("Update by: " + documentSnapshot.getString("nameDoctor"));
                    Date date = documentSnapshot.getDate("date");
                    SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
                    String strDate = format_day.format(date);
                    String time = strDate.toString().substring(0, 11);
                    tv_date.setText(time);

                    p_wrinkles_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString(
                            "wrinkles")));
                    p_sagging_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("sagging")));
                    p_pigmentation_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString(
                            "pigmentation")));
                    p_hydration_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString(
                            "hydration")));
                    p_redness_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("redness")));
                    p_translucency_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString(
                            "translucency")));
                    p_pores_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("pores")));
            }
        });

        String skinPhotoPath = patientID + ".skin" + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("SkinPatient/" + skinPhotoPath).child(skinPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(getContext()).load(uri).into(iv_skin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }
}