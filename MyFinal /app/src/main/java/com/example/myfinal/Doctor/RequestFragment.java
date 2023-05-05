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
import com.example.myfinal.Model.Request;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView back;
    private String doctorID;
    int position;
    private FrameLayout frame_no_document;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_see_request, container, false);

        frame_no_document = (FrameLayout) view.findViewById(R.id.frame_no_document);

        back = (ImageView) view.findViewById(R.id.back);
        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorHomeFragment doctorHomeFragment = new DoctorHomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_my_request, doctorHomeFragment).commit();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.rcv);
        doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = firebaseFirestore.collection("Request")
                .whereEqualTo("id_doctor", doctorID);
        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>().setQuery(query,Request.class).build();
        adapter = new FirestoreRecyclerAdapter<Request, RequestFragment.RequestViewHolder>(options) {
            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
                return new RequestViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Request model) {
                Date date = model.getDate().toDate();
                SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
                String strDate = format_day.format(date);
                String time = strDate.toString().substring(0, 23);

                holder.tv_name.setText(model.getNamePatient());
                holder.tv_phone.setText(model.getPhonePatient());
                holder.tv_time.setText(time);

                String userPhotoPath = model.getId_patient().toString() + ".jpg";
                Task<Uri> storageRef = FirebaseStorage.getInstance().getReference()
                        .child("PatientProfile/" + userPhotoPath)
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                holder.frame_btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CollectionReference ApptRef = firebaseFirestore.collection("Appt");
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("id_doctor", model.getId_doctor());
                        objectMap.put("id_patient", model.getId_patient());
                        objectMap.put("nameDoctor", model.getNameDoctor());
                        objectMap.put("namePatient", model.getNamePatient());
                        objectMap.put("phonePatient", model.getPhonePatient());
                        objectMap.put("date", model.getDate());
                        objectMap.put("status", "confirmed");
                        ApptRef.add(objectMap)
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
                        firebaseFirestore.collection("Request").whereEqualTo("id_doctor", model.getId_doctor()).whereEqualTo("id_patient", model.getId_patient()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        String documentId = documentSnapshot.getId();
                                        Log.d("TAG", "Document id: " + documentId);
                                        // Xoá document bằng documentId
                                        firebaseFirestore.collection("Request").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "Accept Success!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Accept Fail!", e);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });

                holder.frame_btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CollectionReference ApptRef = firebaseFirestore.collection("Appt");
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("id_doctor", model.getId_doctor());
                        objectMap.put("id_patient", model.getId_patient());
                        objectMap.put("nameDoctor", model.getNameDoctor());
                        objectMap.put("namePatient", model.getNamePatient());
                        objectMap.put("phonePatient", model.getPhonePatient());
                        objectMap.put("date", model.getDate());
                        objectMap.put("status", "declined");
                        ApptRef.add(objectMap)
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

                        Task<QuerySnapshot> scheduleRef = firebaseFirestore
                                .collection("Schedule")
                                .whereEqualTo("date", model.getDate())
                                .whereEqualTo("id_doctor", model.getId_doctor())
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                String documentId = documentSnapshot.getId();
                                                DocumentReference documentReference = firebaseFirestore
                                                        .collection("Schedule")
                                                        .document(documentId);
                                                documentReference.update("status", "available");
                                            }
                                        }
                                    }
                                });

                        firebaseFirestore.collection("Request")
                                .whereEqualTo("id_doctor", model.getId_doctor())
                                .whereEqualTo("id_patient", model.getId_patient())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        String documentId = documentSnapshot.getId();
                                        Log.d("TAG", "Document id: " + documentId);
                                        // Xoá document bằng documentId
                                        firebaseFirestore.collection("Request").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "Decline Success!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Decline Fail!", e);
                                            }
                                        });
                                    }
                                }
                            }
                        });
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            RecyclerView.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                    frame_no_document.setVisibility(View.GONE);
                } else {
                    frame_no_document.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_time, tv_phone;
        ImageView iv_avt;
        FrameLayout frame_btn_decline, frame_btn_accept;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            iv_avt = itemView.findViewById(R.id.iv_avt);
            frame_btn_decline = itemView.findViewById(R.id.frame_btn_decline);
            frame_btn_accept = itemView.findViewById(R.id.frame_btn_accept);
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
