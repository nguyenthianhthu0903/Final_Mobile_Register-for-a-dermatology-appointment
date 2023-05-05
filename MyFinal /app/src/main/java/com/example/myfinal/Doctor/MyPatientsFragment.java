package com.example.myfinal.Doctor;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfinal.Model.Patient;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class MyPatientsFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private EditText et_search;
    private RecyclerView recyclerView;
    private ImageView back;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_see_patients, container, false);

        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back = (ImageView) view.findViewById(R.id.back);
        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorHomeFragment doctorHomeFragment = new DoctorHomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_my_patient, doctorHomeFragment).commit();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.rcv);

        Query query = firebaseFirestore.collection("Doctor").document(doctorID).collection("MyPatients");
        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>().setQuery(query,Patient.class).build();
        adapter = new FirestoreRecyclerAdapter<Patient, MyPatientsFragment.PatientViewHolder>(options) {
            @NonNull
            @Override
            public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
                return new PatientViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull PatientViewHolder holder, int position, @NonNull Patient model) {
                holder.tv_name.setText(model.getName());

                holder.iv_medicine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id_patient", model.getId_patient());
                        bundle.putString("namePatient", model.getName());
                        DoctorAddRecipeFragment doctorAddRecipeFragment = new DoctorAddRecipeFragment();
                        doctorAddRecipeFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_my_patient, doctorAddRecipeFragment).commit();
                    }
                });

                holder.iv_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", model.getName());
                        bundle.putString("id_patient", model.getId_patient());
                        DoctorSendMsgFromMyPatientFragment doctorSendMsgFromMyPatientFragment =
                                new DoctorSendMsgFromMyPatientFragment();
                        doctorSendMsgFromMyPatientFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_my_patient, doctorSendMsgFromMyPatientFragment).commit();
                    }
                });

                String userPhotoPath = model.getId_patient().toString() + ".jpg";
                Task<Uri> storageRef = FirebaseStorage.getInstance().getReference().child("PatientProfile/" + userPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Glide.with(getContext()).load(uri).into(holder.iv_avt);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),2,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void filter(String searchTerm) {
        String search;
        if (searchTerm.length() == 0) {
            search = "";
        } else {
            search =
                    searchTerm.substring(0, 1).toUpperCase() + searchTerm.substring(1).toLowerCase();
        }
        Log.d("TAG", "Search text: " + searchTerm);
        Log.d("TAG", "Search string: " + search);
        Query query = firebaseFirestore.collection("Doctor").document(doctorID).collection("MyPatients")
                .whereGreaterThanOrEqualTo("name", search)
                .whereLessThanOrEqualTo("name", search + "\uf8ff")
                .orderBy("name", Query.Direction.ASCENDING);;
        FirestoreRecyclerOptions<Patient> options =
                new FirestoreRecyclerOptions.Builder<Patient>().setQuery(query,Patient.class).build();
        adapter.updateOptions(options);
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_avt, iv_medicine, iv_chat;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_avt = itemView.findViewById(R.id.iv_avt);
            iv_medicine = itemView.findViewById(R.id.iv_medicine);
            iv_chat = itemView.findViewById(R.id.iv_chat);
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
