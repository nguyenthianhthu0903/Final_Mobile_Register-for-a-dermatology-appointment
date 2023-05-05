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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ApptCanceledFragment extends Fragment {
    private RecyclerView rcv;
    private TextView tv_completed, tv_upcoming;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private String doctorID;
    private FrameLayout frame_no_document;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_appt_canceled, container, false);

        frame_no_document = (FrameLayout) view.findViewById(R.id.frame_no_document);

        rcv = (RecyclerView) view.findViewById(R.id.rcv);
        doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore
                .collection("Appt")
                .whereEqualTo("id_doctor", doctorID)
                .whereIn("status", Arrays.asList(new String[]{"canceled", "declined"}));
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>().setQuery(query, Appointment.class).build();
        adapter = new FirestoreRecyclerAdapter<Appointment, ApptCanceledFragment.ApptViewHolder>(options) {
            @NonNull
            @Override
            public ApptCanceledFragment.ApptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment,parent,false);
                return new ApptViewHolder(view1);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull ApptCanceledFragment.ApptViewHolder holder, int position, @NonNull Appointment model) {
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
                holder.iv_more.setVisibility(View.GONE);
                holder.tv_status.setText(model.getStatus());
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_canceled, apptCompletedFragment).commit();
            }
        });

        tv_upcoming = (TextView) view.findViewById(R.id.tv_upcoming);
        tv_upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApptUpcomingFragment apptUpcomingFragment = new ApptUpcomingFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_canceled, apptUpcomingFragment).commit();
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
