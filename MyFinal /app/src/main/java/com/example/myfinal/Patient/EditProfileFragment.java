package com.example.myfinal.Patient;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinal.Model.Patient;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment {

    private ImageView iv_avt, iv_change_avt, back;
    private Button btn_save;
    private EditText et_name, et_dob, et_phone, et_address;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private static final int PICK_IMAGE_REQUEST = 1;
    final String currentPatientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    final String patientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    private Uri uriImage;

    private StorageReference pStorageRef;
    private DatabaseReference pDatabaseRef;
    private FirebaseFirestore patientRef;
    private StorageReference pathReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_edit_profile, container, false);

        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        iv_change_avt = (ImageView) view.findViewById(R.id.iv_change_avt);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_dob = (EditText) view.findViewById(R.id.et_dob);
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_address = (EditText) view.findViewById(R.id.et_address);

        mAuth = FirebaseAuth.getInstance();
        pDatabaseRef = FirebaseDatabase.getInstance().getReference().child("PatientProfile");
        pStorageRef = FirebaseStorage.getInstance().getReference().child("PatientProfile");
        patientRef = FirebaseFirestore.getInstance();

        Intent intent = getActivity().getIntent();
        String current_name = intent.getStringExtra("CURRENT_NAME");
        String current_dob = intent.getStringExtra("CURRENT_DOB");
        String current_phone = intent.getStringExtra("CURRENT_PHONE");
        String current_address = intent.getStringExtra("CURRENT_ADDRESS");

        et_name.setText(current_name);
        et_dob.setText(current_dob);
        et_phone.setText(current_phone);
        et_address.setText(current_address);

        String userPhotoPath = currentPatientID + ".jpg";
        pathReference = storageRef.child("PatientProfile/" + userPhotoPath); // photo in database
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(EditProfileFragment.this.getContext())
                        .load(uri)
                        .placeholder(R.drawable.img_avt_default)
                        .fit()
                        .centerCrop()
                        .into(iv_avt);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileFragment.this.getContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateName = et_name.getText().toString();
                String updateDob = et_dob.getText().toString();
                String updatePhone = et_phone.getText().toString();
                String updateAddress = et_address.getText().toString();
                uploadProfileImage();
                updatePatientProfile(updateName, updateDob, updatePhone, updateAddress);
            }
        });
        iv_change_avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientAccountFragment patientAccountFragment = new PatientAccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_edit, patientAccountFragment).commit();
            }
        });

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
        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            et_name.setText(documentSnapshot.getString("name"));
            et_dob.setText(documentSnapshot.getString("dob"));
            et_phone.setText(documentSnapshot.getString("phone"));
            et_address.setText(documentSnapshot.getString("address"));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.with(this.getContext()).load(uriImage).into(iv_avt);
        }
    }

    public void updatePatientProfile(String name, String dob, String phone, String address) {
        DocumentReference documentReference = patientRef.collection("Patient").document("" + patientID + "");
        documentReference.update("address", address);
        documentReference.update("dob", dob);
        documentReference.update("name", name);
        documentReference.update("phone", phone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileFragment.this.getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("Androidview", e.getMessage());
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getApplication().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadProfileImage() {
        if (uriImage != null) {
            StorageReference storageReference = pStorageRef.child(currentPatientID
                    + "." + getFileExtension(uriImage));
            storageReference.putFile(uriImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return pStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());

                        UploadImage upload = new UploadImage(currentPatientID,
                                downloadUri.toString());
                        pDatabaseRef.push().setValue(upload);
                    }
                    else {
                        Toast.makeText(EditProfileFragment.this.getContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
