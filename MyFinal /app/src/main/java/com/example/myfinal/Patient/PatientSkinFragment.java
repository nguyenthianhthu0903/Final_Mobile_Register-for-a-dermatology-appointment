package com.example.myfinal.Patient;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientSkinFragment extends Fragment {

    private TextView tv_skinType_Age, tv_skinAge, tv_skinTone, tv_date, tv_skin_score,
            tv_wrinkles_score, tv_sagging_score, tv_pigmentation_score, tv_hydration_score,
            tv_sub_1, tv_sub_2, tv_1;
    private ProgressBar p_wrinkles_scoreBar, p_sagging_scoreBar, p_pigmentation_scoreBar,
            p_hydration_scoreBar;
    private FrameLayout frame_score, frame_no_document_skin;
    private ImageView iv_skin;
    private ImageView back;
    private FrameLayout see_detail;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private String patientID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_skin, container, false);

        iv_skin = (ImageView) view.findViewById(R.id.iv_skin);

        frame_score = (FrameLayout) view.findViewById(R.id.frame_score) ;
        frame_no_document_skin = (FrameLayout) view.findViewById(R.id.frame_no_document_skin);
        tv_sub_1 = (TextView) view.findViewById(R.id.tv_sub_1);
        tv_sub_2 = (TextView) view.findViewById(R.id.tv_sub_2);
        tv_1 = (TextView) view.findViewById(R.id.tv_1);

        tv_skinType_Age = (TextView) view.findViewById(R.id.tv_skinType_Age);
        tv_skinAge = (TextView) view.findViewById(R.id.tv_skinAge);
        tv_skinTone = (TextView) view.findViewById(R.id.tv_skinTone);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_skin_score = (TextView) view.findViewById(R.id.tv_skin_score);
        tv_wrinkles_score = (TextView) view.findViewById(R.id.tv_wrinkles_score);
        tv_sagging_score = (TextView) view.findViewById(R.id.tv_sagging_score);
        tv_pigmentation_score = (TextView) view.findViewById(R.id.tv_pigmentation_score);
        tv_hydration_score = (TextView) view.findViewById(R.id.tv_hydration_score);

        p_wrinkles_scoreBar = (ProgressBar) view.findViewById(R.id.p_wrinkles_scoreBar);
        p_sagging_scoreBar = (ProgressBar) view.findViewById(R.id.p_sagging_scoreBar);
        p_pigmentation_scoreBar = (ProgressBar) view.findViewById(R.id.p_pigmentation_scoreBar);
        p_hydration_scoreBar = (ProgressBar) view.findViewById(R.id.p_hydration_scoreBar);

        see_detail = (FrameLayout) view.findViewById(R.id.see_detail);
        see_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientSkinDetailFragment patientSkinDetailFragment = new PatientSkinDetailFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSkinDetailFragment).commit();
            }
        });

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientHomeFragment patientHomeFragment = new PatientHomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientHomeFragment).commit();
            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        DocumentReference skinRef = firebaseFirestore.collection("SkinDetail").document(patientID);
        skinRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        tv_skinType_Age.setText(document.getString("skinType"));
                        tv_skinAge.setText(document.getString("skinAge"));
                        tv_skinTone.setText(document.getString("skinTone"));
                        tv_skin_score.setText("Skin health score " + document.getString("summary") + "/100");
                        tv_wrinkles_score.setText(document.getString("wrinkles") + "/100");
                        tv_sagging_score.setText(document.getString("sagging") + "/100");
                        tv_pigmentation_score.setText(document.getString("pigmentation") + "/100");
                        tv_hydration_score.setText(document.getString("pores") + "/100");

                        Date date = document.getDate("date");
                        SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
                        String strDate = format_day.format(date);
                        String time = strDate.toString().substring(0, 11);
                        tv_date.setText(time);

                        p_wrinkles_scoreBar.setProgress(Integer.parseInt(document.getString("wrinkles")));
                        p_sagging_scoreBar.setProgress(Integer.parseInt(document.getString("sagging")));
                        p_pigmentation_scoreBar.setProgress(Integer.parseInt(document.getString("pigmentation")));
                        p_hydration_scoreBar.setProgress(Integer.parseInt(document.getString("pores")));
                    } else {
                        Log.d("TAG", "No such document");
                        frame_no_document_skin.setVisibility(View.VISIBLE);
                        frame_score.setVisibility(View.GONE);
                        iv_skin.setVisibility(View.GONE);
                        tv_skinType_Age.setVisibility(View.GONE);
                        tv_skinAge.setVisibility(View.GONE);
                        tv_skinTone.setVisibility(View.GONE);
                        tv_sub_1.setVisibility(View.GONE);
                        tv_sub_2.setVisibility(View.GONE);
                        tv_1.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        skinRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.d("TAG", "Firebase listener error: " + error.getMessage());
            } else {
                if (documentSnapshot.exists()) {
                    // Nếu document tồn tại, hiển thị thông tin lên giao diện
                    tv_skinType_Age.setText(documentSnapshot.getString("skinType"));
                    tv_skinAge.setText(documentSnapshot.getString("skinAge"));
                    tv_skinTone.setText(documentSnapshot.getString("skinTone"));
                    tv_skin_score.setText("Skin health score " + documentSnapshot.getString("summary") + "/100");
                    tv_wrinkles_score.setText(documentSnapshot.getString("wrinkles") + "/100");
                    tv_sagging_score.setText(documentSnapshot.getString("sagging") + "/100");
                    tv_pigmentation_score.setText(documentSnapshot.getString("pigmentation") + "/100");
                    tv_hydration_score.setText(documentSnapshot.getString("pores") + "/100");

                    Date date = documentSnapshot.getDate("date");
                    SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
                    String strDate = format_day.format(date);
                    String time = strDate.toString().substring(0, 11);
                    tv_date.setText(time);

                    p_wrinkles_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("wrinkles")));
                    p_sagging_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("sagging")));
                    p_pigmentation_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("pigmentation")));
                    p_hydration_scoreBar.setProgress(Integer.parseInt(documentSnapshot.getString("pores")));
                } else {
                    frame_no_document_skin.setVisibility(View.VISIBLE);
                    frame_score.setVisibility(View.GONE);
                    iv_skin.setVisibility(View.GONE);
                    tv_skinType_Age.setVisibility(View.GONE);
                    tv_skinAge.setVisibility(View.GONE);
                    tv_skinTone.setVisibility(View.GONE);
                    tv_sub_1.setVisibility(View.GONE);
                    tv_sub_2.setVisibility(View.GONE);
                }
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