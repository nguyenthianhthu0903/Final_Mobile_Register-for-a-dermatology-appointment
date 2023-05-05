package com.example.myfinal.Doctor;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myfinal.Model.UploadImage;
import com.example.myfinal.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorEditApptFragment extends Fragment {

    private ImageView iv_avt, iv_skin, iv_change_img, back;
    private CheckBox cb_completed;
    private TextView tv_name, tv_phone, tv_time;
    private EditText et_skinType_Age, et_skinAge, et_skinTone, et_wrinkles, et_sagging,
            et_pigmentation, et_pores, et_translucency, et_redness, et_hydration, et_conclution;
    private Button btn_update;
    private StorageReference skinImgRef;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DatabaseReference skinDetailRef;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    String patientID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_edit_appt, container, false);

        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        iv_skin = (ImageView) view.findViewById(R.id.iv_skin);
        iv_change_img = (ImageView) view.findViewById(R.id.iv_change_img);
        back = (ImageView) view.findViewById(R.id.back);
        cb_completed = (CheckBox) view.findViewById(R.id.cb_completed);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        et_conclution = (EditText) view.findViewById(R.id.et_conclusion);
        et_skinType_Age = (EditText) view.findViewById(R.id.et_skinType_Age);
        et_skinAge = (EditText) view.findViewById(R.id.et_skinAge);
        et_skinTone = (EditText) view.findViewById(R.id.et_skinTone);
        et_wrinkles = (EditText) view.findViewById(R.id.et_wrinkles);
        et_sagging = (EditText) view.findViewById(R.id.et_sagging);
        et_pigmentation = (EditText) view.findViewById(R.id.et_pigmentation);
        et_pores = (EditText) view.findViewById(R.id.et_pores);
        et_translucency = (EditText) view.findViewById(R.id.et_translucency);
        et_redness = (EditText) view.findViewById(R.id.et_redness);
        et_hydration = (EditText) view.findViewById(R.id.et_hydration);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        cb_completed.setChecked(true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();


                ApptUpcomingFragment apptUpcomingFragment = new ApptUpcomingFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_appt_detail,
                        apptUpcomingFragment).commit();
            }
        });

        Bundle bundle = getArguments();
        String documentID = bundle.getString("documentID");
        String date = bundle.getString("date");
        String nameDoctor = bundle.getString("nameDoctor");
        patientID = bundle.getString("id_patient");

        tv_time.setText(date.substring(0, 23));

        DocumentReference ApptRef =
                FirebaseFirestore.getInstance().collection("Appt").document(documentID);
        ApptRef.addSnapshotListener((documentSnapshot, error) -> {
            tv_name.setText(documentSnapshot.getString("namePatient"));
            tv_phone.setText(documentSnapshot.getString("phonePatient"));
            String userPhotoPath = (documentSnapshot.getString("id_patient")) + ".jpg";
            Task<Uri> storageRef = FirebaseStorage.getInstance().getReference()
                    .child("PatientProfile/" + userPhotoPath)
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
        });

        skinImgRef = FirebaseStorage.getInstance().getReference();
        skinDetailRef = FirebaseDatabase.getInstance().getReference().child("SkinDetail");

//        Intent intent = getActivity().getIntent();
//        String current_conclusion = intent.getStringExtra("CURRENT_conclusion");
//        String current_skinType_Age = intent.getStringExtra("CURRENT_skinType_Age");
//        String current_skinAge = intent.getStringExtra("CURRENT_skinAge");
//        String current_skinTone = intent.getStringExtra("CURRENT_skinTone");
//        String current_wrinkles = intent.getStringExtra("CURRENT_wrinkles");
//        String current_sagging = intent.getStringExtra("CURRENT_sagging");
//        String current_pigmentation = intent.getStringExtra("CURRENT_pigmentation");
//        String current_pores = intent.getStringExtra("CURRENT_pores");
//        String current_translucency = intent.getStringExtra("CURRENT_translucency");
//        String current_redness = intent.getStringExtra("CURRENT_redness");
//        String current_hydration = intent.getStringExtra("CURRENT_hydration");
//
//        et_conclution.setText(current_conclusion);
//        et_skinType_Age.setText(current_skinType_Age);
//        et_skinAge.setText(current_skinAge);
//        et_skinTone.setText(current_skinTone);
//        et_wrinkles.setText(current_wrinkles);
//        et_sagging.setText(current_sagging);
//        et_pigmentation.setText(current_pigmentation);
//        et_pores.setText(current_pores);
//        et_translucency.setText(current_translucency);
//        et_redness.setText(current_redness);
//        et_hydration.setText(current_hydration);

        String skinPhotoPath = patientID + ".skin" + ".jpg";
        skinImgRef = storageRef.child("SkinPatient/" + skinPhotoPath).child(skinPhotoPath); // photo in
        // database
        skinImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(DoctorEditApptFragment.this.getContext())
                        .load(uri)
                        .fit()
                        .centerCrop()
                        .into(iv_skin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(DoctorEditApptFragment.this.getContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_doctor, id_patient, conclusion, skinType, skinAge, skinTone, wrinkles,
                        sagging, pigmentation, pores, translucency, redness, hydration, summary;
                Date date = new Date();

                id_doctor = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                id_patient = patientID;
                conclusion = et_conclution.getText().toString();
                skinType = et_skinType_Age.getText().toString();
                skinAge = et_skinAge.getText().toString();
                skinTone = et_skinTone.getText().toString();
                wrinkles = et_wrinkles.getText().toString();
                sagging = et_sagging.getText().toString();
                pigmentation = et_pigmentation.getText().toString();
                pores = et_pores.getText().toString();
                translucency = et_translucency.getText().toString();
                redness = et_redness.getText().toString();
                hydration = et_hydration.getText().toString();
                summary =
                        String.valueOf(Integer.valueOf((Integer.valueOf(wrinkles) + Integer.valueOf(sagging) + Integer.valueOf(pigmentation) + Integer.valueOf(pores) + Integer.valueOf(translucency) + Integer.valueOf(redness) + Integer.valueOf(hydration)))/7);

//                summary = String.valueOf(0);
                uploadSkinImage();

                DocumentReference skinRef = firebaseFirestore.collection("SkinDetail").document(patientID);
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("id_doctor", id_doctor);
                objectMap.put("id_patient", id_patient);
                objectMap.put("conclusion", conclusion);
                objectMap.put("skinType", skinType);
                objectMap.put("skinAge", skinAge);
                objectMap.put("skinTone", skinTone);
                objectMap.put("date", date);
                objectMap.put("wrinkles", wrinkles);
                objectMap.put("sagging", sagging);
                objectMap.put("pigmentation", pigmentation);
                objectMap.put("pores", pores);
                objectMap.put("translucency", translucency);
                objectMap.put("redness", redness);
                objectMap.put("hydration", hydration);
                objectMap.put("summary", summary);
                objectMap.put("nameDoctor", nameDoctor);
                skinRef.set(objectMap, SetOptions.merge());
//                skinRef.add(objectMap)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Toast.makeText(DoctorEditApptFragment.this.getContext(), "Updated",
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                            }
//                        });

                DocumentReference apptRef = firebaseFirestore
                        .collection("Appt")
                        .document(documentID);
                apptRef.update("status", "completed");

                //                updateSkinDetail(id_doctor, id_patient, conclusion,
//                        skinType, skinAge, skinTone, date,
//                        wrinkles, sagging, pigmentation, pores, translucency, redness, hydration, summary);

            }
        });

        iv_change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.with(this.getContext()).load(uriImage).into(iv_skin);
        }
    }

    public void updateSkinDetail(String id_doctor, String id_patient, String conclusion,
                                 String skinType, String skinAge, String skinTone, Date date,
                                 String wrinkles, String sagging, String pigmentation, String pores, String translucency, String redness, String hydration, String summary) {
        DocumentReference documentReference =
                firebaseFirestore.collection("SkinDetail").document();
        documentReference.update("id_doctor", id_doctor);
        documentReference.update("id_patient", id_patient);
        documentReference.update("conclusion", conclusion);
        documentReference.update("skinType", skinType);
        documentReference.update("skinAge", skinAge);
        documentReference.update("skinTone", skinTone);
        documentReference.update("date", date);
        documentReference.update("wrinkles", wrinkles);
        documentReference.update("sagging", sagging);
        documentReference.update("pigmentation", pigmentation);
        documentReference.update("pores", pores);
        documentReference.update("translucency", translucency);
        documentReference.update("redness", redness);
        documentReference.update("hydration", hydration);
        documentReference.update("summary", summary)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DoctorEditApptFragment.this.getContext(), "Profile Updated",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorEditApptFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("Androidview", e.getMessage());
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getApplication().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadSkinImage() {
        if (uriImage != null) {
            StorageReference storageReference =
                    skinImgRef.child(patientID + ".skin" + "." + getFileExtension(uriImage));
            storageReference.putFile(uriImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return skinImgRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());

                        UploadImage upload = new UploadImage(patientID + ".skin",
                                downloadUri.toString());
                        skinDetailRef.push().setValue(upload);
                    }
                    else {
//                        Toast.makeText(DoctorEditApptFragment.this.getContext(),
//                                "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Bundle bundle = getArguments();
//        //        String id_doctor = bundle.getString("id_doctor");
//        String documentID = bundle.getString("documentID");
//        String id_patient = bundle.getString("id_patient");
//        String date = bundle.getString("date");
//
//        tv_time.setText(date.substring(0, 23));
//
//        DocumentReference ApptRef =
//                FirebaseFirestore.getInstance().collection("Appt").document(documentID);
//        ApptRef.addSnapshotListener((documentSnapshot, error) -> {
//            tv_name.setText(documentSnapshot.getString("namePatient"));
//            tv_phone.setText(documentSnapshot.getString("phonePatient"));
//            String userPhotoPath = (documentSnapshot.getString("id_patient")) + ".jpg";
//            Task<Uri> storageRef = FirebaseStorage.getInstance().getReference().child("PatientProfile/" + userPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    // Got the download URL for 'users/me/profile.png'
//                    Glide.with(getContext()).load(uri).into(iv_avt);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
//        });
//    }
}
