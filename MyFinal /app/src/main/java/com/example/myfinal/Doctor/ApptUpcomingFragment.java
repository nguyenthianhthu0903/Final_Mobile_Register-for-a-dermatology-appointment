package com.example.myfinal.Doctor;

import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Model.Appointment;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApptUpcomingFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rcv;
    private TextView tv_completed, tv_canceled;
    private String doctorID;
    private FrameLayout frame_no_document;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_appt_upcoming, container, false);

        frame_no_document = (FrameLayout) view.findViewById(R.id.frame_no_document);

        rcv = (RecyclerView) view.findViewById(R.id.rcv);
        doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("Appt").whereEqualTo("id_doctor", doctorID).whereEqualTo("status", "confirmed");
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>().setQuery(query, Appointment.class).build();
        adapter = new FirestoreRecyclerAdapter<Appointment, ApptViewHolder>(options) {
            @NonNull
            @Override
            public ApptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment,parent,false);
                return new ApptViewHolder(view1);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull ApptViewHolder holder, int position, @NonNull Appointment model) {
                Date date = model.getDate().toDate();
                SimpleDateFormat format_day = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a 'utc' XXX", Locale.ENGLISH);
                String strDate = format_day.format(date);
                String month = strDate.toString().substring(0, 3);
                String dayOfMonth = strDate.toString().substring(4, 5);
                String time = strDate.toString().substring(14, 23);
                holder.tv_name.setText(model.getNamePatient());
                holder.tv_day.setText(month);
                holder.tv_date.setText(dayOfMonth);
                holder.tv_time.setText(time);
                holder.tv_status.setText(model.getStatus());

                holder.iv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        firebaseFirestore.collection("Appt")
                                .whereEqualTo("id_doctor", model.getId_doctor())
                                .whereEqualTo("id_patient", model.getId_patient())
                                .whereEqualTo("date", model.getDate())
                                .whereEqualTo("status", "confirmed")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        String documentId = documentSnapshot.getId();
                                        Log.d("TAG", "Document id: " + documentId);

                                        Bundle bundle = new Bundle();
                                        bundle.putString("documentID", documentId);
                                        bundle.putString("id_patient", model.getId_patient());
                                        bundle.putString("nameDoctor", model.getNameDoctor());
                                        bundle.putString("date", strDate);
                                        DoctorEditApptFragment doctorEditApptFragment =
                                                new DoctorEditApptFragment();
                                        doctorEditApptFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_upcoming, doctorEditApptFragment).commit();
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
                    rcv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    rcv.setAdapter(adapter);
                    frame_no_document.setVisibility(View.GONE);
                } else {
                    frame_no_document.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_completed = (TextView) view.findViewById(R.id.tv_completed);
        tv_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApptCompletedFragment apptCompletedFragment = new ApptCompletedFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_upcoming, apptCompletedFragment).commit();
            }
        });

        tv_canceled = (TextView) view.findViewById(R.id.tv_canceled);
        tv_canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApptCanceledFragment apptCanceledFragment = new ApptCanceledFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_upcoming, apptCanceledFragment).commit();
            }
        });

        return view;
    }

    private class ApptViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_date;
        private TextView tv_day;
        private TextView tv_status;
        private ImageView iv_more;

        public ApptViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_day = itemView.findViewById(R.id.tv_day);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_status = itemView.findViewById(R.id.tv_status);
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
