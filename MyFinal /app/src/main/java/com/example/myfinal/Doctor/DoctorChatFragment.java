package com.example.myfinal.Doctor;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class DoctorChatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rcv;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_chat, container, false);

        setHasOptionsMenu(true);

        rcv = (RecyclerView) view.findViewById(R.id.rcv);
        Query query = firebaseFirestore.collection("Doctor")
                .document(doctorID).collection("MyPatients");
        FirestoreRecyclerOptions<Patient> options =
                new FirestoreRecyclerOptions.Builder<Patient>().setQuery(query,Patient.class).build();
        adapter =
                new FirestoreRecyclerAdapter<Patient, DoctorChatFragment.UserViewHolder>(options) {
            @NonNull
            @Override
            public DoctorChatFragment.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                        int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat,
                        parent, false);
                return new UserViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorChatFragment.UserViewHolder holder, int position,
                                            @NonNull Patient model) {
                holder.tv_name.setText(model.getName());

                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", model.getName());
                        bundle.putString("id_patient", model.getId_patient());
                        DoctorSendMsg doctorSendMsg = new DoctorSendMsg();
                        doctorSendMsg.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_chat, doctorSendMsg).commit();
                    }
                });

                String userPhotoPath = model.getId_patient().toString() + ".jpg";
                Task<Uri> storageRef = FirebaseStorage.getInstance().getReference().child(
                        "PatientProfile/" + userPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        rcv.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.VERTICAL,
                false));
        rcv.setAdapter(adapter);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_avt;
        private FrameLayout parentView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_avt = itemView.findViewById(R.id.iv_avt);
            parentView = itemView.findViewById(R.id.parentView);
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
