package com.example.myfinal.Doctor;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.myfinal.Model.Rating;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DoctorSeeRatingFragment extends Fragment {

    private ImageView iv_avt;
    private TextView tv_name, tv_rating, tv_quantity_review, tv_about_doctor;
    private ImageView back;
    private FrameLayout frame_chat;
    String nameDoctor, id_doctor, patientID;
    private RecyclerView rcv;
    private TextView tv_dr;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    double totalRating = 0.0;
    int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_rating, container, false);

        tv_rating = (TextView) view.findViewById(R.id.tv_rating);
        tv_quantity_review = (TextView) view.findViewById(R.id.tv_quantity_review);

        id_doctor = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        DocumentReference doctorRef =
                FirebaseFirestore.getInstance().collection("Doctor").document(id_doctor);
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
            tv_rating.setText(String.valueOf(rating));
        });

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorAccountFragment doctorAccountFragment = new DoctorAccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_rating,
                        doctorAccountFragment).commit();
            }
        });

        rcv = (RecyclerView) view.findViewById(R.id.rcv);

        Query query = firebaseFirestore
                .collection("Doctor")
                .document(id_doctor)
                .collection("Rating")
                .orderBy("date");

        FirestoreRecyclerOptions<Rating> options =
                new FirestoreRecyclerOptions.Builder<Rating>().setQuery(query, Rating.class).build();

        adapter = new FirestoreRecyclerAdapter<Rating, RatingViewHolder> (options) {

            @NonNull
            @Override
            public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,
                        parent,
                        false);
                return new RatingViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull RatingViewHolder holder, int position, @NonNull Rating model) {
                holder.tv_name.setText(model.getNamePatient());
                holder.tv_rating.setText(String.valueOf(model.getStar()));
                holder.tv_comment.setText(model.getComment());

                Date date = model.getDate().toDate();
                SimpleDateFormat format_day = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a", Locale.ENGLISH);
                String strDate = format_day.format(date);
                String time = strDate.toString();
                holder.tv_time.setText(time);

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

        adapter.startListening();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() > 0) {
                    rcv.setLayoutManager(new LinearLayoutManager(getContext(),
                            RecyclerView.VERTICAL, false));
                    rcv.setAdapter(adapter);
//                    frame_no_document.setVisibility(View.GONE);
                } else {
//                    frame_no_document.setVisibility(View.VISIBLE);
                }
            }
        });

        // tính rating của doctor
        CollectionReference collectionReference = firebaseFirestore.collection("Doctor")
                .document(id_doctor)
                .collection("Rating");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    count = task.getResult().size();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        double rating = document.getDouble("star");
                        totalRating += rating;
                    }
                    double averageRating = totalRating / count;
                    Task<Void> scheduleRef = firebaseFirestore
                            .collection("Doctor")
                            .document(id_doctor).update("rating", totalRating);
                    tv_quantity_review.setText("("+count+")");
                    Log.d("TAG", "Average rating: " + averageRating);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        return view;
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_avt;
        private TextView tv_name, tv_time, tv_comment, tv_rating;

        public RatingViewHolder (View itemView) {
            super(itemView);

            iv_avt = itemView.findViewById(R.id.iv_avt);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_rating = itemView.findViewById(R.id.tv_rating);
            tv_comment = itemView.findViewById(R.id.tv_comment);
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
