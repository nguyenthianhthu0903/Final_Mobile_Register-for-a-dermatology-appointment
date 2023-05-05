package com.example.myfinal.Doctor;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfinal.Model.Doctor;
import com.example.myfinal.Model.Medicine;
import com.example.myfinal.Model.Patient;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DoctorAddRecipeFragment extends Fragment {

    private ImageView iv_avt, back;
    private TextView tv_name, tv_phone;
    private EditText et_medicine_name, et_dosage;
    private RecyclerView rcv;
    private Button btn_insert;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private Uri uriImage;
    private FirestoreRecyclerAdapter adapter;
    String patientID, doctorID, nameMedicine, dosageMedicine, nameDoctor, phonePatient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_add_prescription, container, false);

        iv_avt = (ImageView) view.findViewById(R.id.iv_avt);
        back = (ImageView) view.findViewById(R.id.back);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        et_medicine_name = (EditText) view.findViewById(R.id.et_medicine_name);
        et_dosage = (EditText) view.findViewById(R.id.et_dosage);
        btn_insert = (Button) view.findViewById(R.id.btn_insert);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPatientsFragment myPatientsFragment = new MyPatientsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_add_recipe, myPatientsFragment).commit();
            }
        });

        Bundle bundle = getArguments();
        patientID = bundle.getString("id_patient");
        String namePatient = bundle.getString("namePatient");

        DocumentReference documentReference1 =
                firebaseFirestore.collection("Patient").document(patientID);
        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        Patient patient = document.toObject(Patient.class);
                        phonePatient = patient.getPhone();
                        tv_phone.setText(phonePatient);
                        Log.d("TAG", " data: " + patient.toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        tv_name.setText(namePatient);

        rcv = (RecyclerView) view.findViewById(R.id.rcv);
        doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        DocumentReference documentReference =
                firebaseFirestore.collection("Doctor").document(doctorID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        Doctor doctor = document.toObject(Doctor.class);
                        nameDoctor = doctor.getName();
                        Log.d("TAG", " data: " + doctor.toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameMedicine = et_medicine_name.getText().toString();
                dosageMedicine = et_dosage.getText().toString();
                Timestamp date = Timestamp.now();

                if (nameMedicine.isEmpty() && dosageMedicine.isEmpty()) {
                    Toast.makeText(getContext(), "You must enter medicine name and dosage!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    CollectionReference medicineRef =
                            firebaseFirestore.collection("Recipe");
                    Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put("id_doctor", doctorID);
                    objectMap.put("id_patient", patientID);
                    objectMap.put("name", nameMedicine);
                    objectMap.put("dosage", dosageMedicine);
                    objectMap.put("nameDoctor", nameDoctor);
                    objectMap.put("date", date);
                    medicineRef.add(objectMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                    adapter.notifyDataSetChanged();
                }
            }
        });

        Query query = firebaseFirestore
                .collection("Recipe")
                .whereEqualTo("id_doctor", doctorID)
                .whereEqualTo("id_patient", patientID);
        FirestoreRecyclerOptions<Medicine> options =
                new FirestoreRecyclerOptions.Builder<Medicine>().setQuery(query, Medicine.class).build();
        adapter = new FirestoreRecyclerAdapter<Medicine,
                DoctorAddRecipeFragment.MedicineViewHolder>(options) {

            @NonNull
            @Override
            public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent,
                        false);
                return new MedicineViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull MedicineViewHolder holder, int position, @NonNull Medicine model) {
                holder.tv_name.setText(model.getName());
                holder.tv_dosage.setText(model.getDosage());
            }
        };

        adapter.startListening();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                adapter.notifyDataSetChanged();
                    rcv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    rcv.setAdapter(adapter);
            }
        });

        String userPhotoPath = patientID + ".jpg";
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

        return view;
    }

    private class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_dosage;

        public MedicineViewHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_dosage = itemView.findViewById(R.id.tv_dosage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
