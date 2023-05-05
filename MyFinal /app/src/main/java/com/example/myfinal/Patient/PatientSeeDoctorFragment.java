package com.example.myfinal.Patient;

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
import com.example.myfinal.Model.Doctor;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class PatientSeeDoctorFragment extends Fragment {

    private RecyclerView rcv;
    private ImageView back;
    private EditText et_search;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_see_doctors, container, false);

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

        rcv = (RecyclerView) view.findViewById(R.id.rcv);
        Query query = firebaseFirestore.collection("Doctor");
        FirestoreRecyclerOptions<Doctor> options =
                new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query,Doctor.class).build();
        adapter =
                new FirestoreRecyclerAdapter<Doctor, PatientSeeDoctorFragment.DoctorViewHolder>(options) {
            @NonNull
            @Override
            public PatientSeeDoctorFragment.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent,
                        false);
                return new PatientSeeDoctorFragment.DoctorViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull PatientSeeDoctorFragment.DoctorViewHolder holder, int position, @NonNull Doctor model) {

                String rating = String.valueOf(model.getRating());
                holder.tv_name.setText(model.getName());
                holder.tv_rating.setText(rating);

                String userPhotoPath = model.getEmail().toString() + ".jpg";
                FirebaseStorage.getInstance().getReference().child("DoctorProfile/" + userPhotoPath).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getContext()).load(uri).into(holder.iv_avt);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

                holder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id_doctor", model.getEmail().toString());
                        PatientSeeDoctorDetailFragment patientSeeDoctorDetailFragment =
                                new PatientSeeDoctorDetailFragment();
                        patientSeeDoctorDetailFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSeeDoctorDetailFragment).commit();
                    }
                });

                holder.iv_avt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id_doctor", model.getEmail().toString());
                        PatientSeeDoctorDetailFragment patientSeeDoctorDetailFragment =
                                new PatientSeeDoctorDetailFragment();
                        patientSeeDoctorDetailFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, patientSeeDoctorDetailFragment).commit();
                    }
                });
            }
        };
        rcv.setLayoutManager(new GridLayoutManager(this.getContext(),2,RecyclerView.VERTICAL,false));
        rcv.setAdapter(adapter);

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

    private void filter(String searchTerm) {
        String search;
        if (searchTerm.length() == 0) {
            search = "Dr. ";
        } else {
            search =
                    "Dr. " + searchTerm.substring(0, 1).toUpperCase() + searchTerm.substring(1).toLowerCase();
        }
        Log.d("TAG", "Search text: " + searchTerm);
        Log.d("TAG", "Search string: " + search);
        Query query = firebaseFirestore.collection("Doctor")
                .whereGreaterThanOrEqualTo("name", search)
                .whereLessThanOrEqualTo("name", search + "\uf8ff")
                .orderBy("name", Query.Direction.ASCENDING);;
        FirestoreRecyclerOptions<Doctor> options =
                new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query,Doctor.class).build();
        adapter.updateOptions(options);
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_rating;
        private ImageView iv_avt;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_rating = itemView.findViewById(R.id.tv_rating);
            iv_avt = itemView.findViewById(R.id.iv_avt);
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