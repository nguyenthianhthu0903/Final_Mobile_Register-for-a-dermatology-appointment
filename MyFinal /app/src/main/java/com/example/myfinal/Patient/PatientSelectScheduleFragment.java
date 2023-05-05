package com.example.myfinal.Patient;

import static com.example.myfinal.Model.CalendarUtils.allDaysInMonthArray;
import static com.example.myfinal.Model.CalendarUtils.convertSelectedDateAtEndDayToTimestamp;
import static com.example.myfinal.Model.CalendarUtils.convertSelectedDateAtStartDayToTimestamp;
import static com.example.myfinal.Model.CalendarUtils.monthYearFromDate;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Adapter.CalendarAdapter;
import com.example.myfinal.Model.CalendarUtils;
import com.example.myfinal.Adapter.ScheduleDoctorAdapter;
import com.example.myfinal.Model.ScheduleDoctor;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientSelectScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private RecyclerView rcv_Schedule;
    private RecyclerView rcv_date;
    private ImageView back;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private LinearLayout bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button btn_booking;
    private String str_status;
    private ImageView iv_backward;
    private ImageView iv_forward;
    private TextView tv_current_monthyear;
    private TextView tv_address_bottom_sheet;
    private ScheduleDoctor selectedScheduleDoctor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_main_layout_booking, container, false);
        rcv_Schedule = (RecyclerView) view.findViewById(R.id.rcv_schedule);

        btn_booking = (Button) view.findViewById(R.id.btn_booking);

        bottom_sheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        rcv_date = (RecyclerView) view.findViewById(R.id.rcv_date);
        tv_current_monthyear = (TextView) view.findViewById(R.id.tv_current_monthyear);

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientScheduleFragment patientScheduleFragment = new PatientScheduleFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_select_schedule, patientScheduleFragment).commit();
            }
        });

        //Mặc định ngày hiện tại là ngày được chọn
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = LocalDate.now();
        }
        ArrayList<LocalDate> daysInMonth = allDaysInMonthArray(CalendarUtils.selectedDate);
        int selectedDatePosition = daysInMonth.indexOf(CalendarUtils.selectedDate);
        setMonthView(selectedDatePosition);

        iv_backward = (ImageView) view.findViewById(R.id.iv_backward);
        iv_backward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.withDayOfMonth(1);
                setMonthView(0);
            }
        });

        iv_forward = (ImageView) view.findViewById(R.id.iv_forward);
        iv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.withDayOfMonth(1);
                setMonthView(0);
            }
        });

        rcv_Schedule.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        rcv_Schedule.setAdapter(adapter);

        tv_address_bottom_sheet = (TextView) view.findViewById(R.id.tv_address_bottom_sheet);


        btn_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Task<QuerySnapshot> scheduleRef = firebaseFirestore
                        .collection("Schedule")
                        .whereEqualTo("date", selectedScheduleDoctor.getDate())
                        .whereEqualTo("id_doctor", selectedScheduleDoctor.getIdDoctor())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        String documentId = documentSnapshot.getId();
                                        DocumentReference documentReference = firebaseFirestore
                                                .collection("Schedule")
                                                .document(documentId);
                                        documentReference.update("status", "full");
                                    }
                                }
                            }
                        });

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore
                        .collection("Patient")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    //tv_address_bottom_sheet.setText("Address: " + documentSnapshot.getString("address"));
                                    String patientName = documentSnapshot.getString("name");
                                    String patientPhone = documentSnapshot.getString("phone");

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date", selectedScheduleDoctor.getDate());
                                    data.put("id_doctor", selectedScheduleDoctor.getIdDoctor());
                                    data.put("id_patient", firebaseAuth.getCurrentUser().getEmail());
                                    data.put("nameDoctor", selectedScheduleDoctor.getNameDoctor());
                                    data.put("namePatient", patientName);
                                    data.put("phonePatient", patientPhone);

                                    firebaseFirestore = FirebaseFirestore.getInstance();

                                    firebaseFirestore
                                            .collection("Request")
                                            .add(data)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    btn_booking.setEnabled(false);
                                                    btn_booking.setText("Booked");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    btn_booking.setText("Fail click to try again");
                                                }
                                            });


                                }
                            }
                        });

            }
        });


        return view;
    }

    private class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_doctor_name;
        private TextView tv_time;
        private ImageView iv_avt;
        private ImageView iv_more;
        private TextView tv_status;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_doctor_name = itemView.findViewById(R.id.tv_doctor_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_avt = itemView.findViewById(R.id.iv_avt);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_status = itemView.findViewById(R.id.tv_status);

            iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });
        }
    }

    /*
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
    */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setScheduleView(){
        rcv_Schedule.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));

        Context this_context = this.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("Schedule")
                .whereGreaterThan("date", convertSelectedDateAtStartDayToTimestamp(CalendarUtils.selectedDate))
                .whereLessThan("date", convertSelectedDateAtEndDayToTimestamp(CalendarUtils.selectedDate))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<ScheduleDoctor> scheduleDoctorArrayList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ScheduleDoctor scheduleDoctor = new ScheduleDoctor();

                                scheduleDoctor.setIdDoctor(document.getString("id_doctor"));
                                scheduleDoctor.setNameDoctor(document.getString("doctor_name"));
                                scheduleDoctor.setDate(document.getDate("date"));
                                scheduleDoctor.setStatus(document.getString("status"));

                                scheduleDoctorArrayList.add(scheduleDoctor);
                            }

                            ScheduleDoctorAdapter scheduleDoctorAdapter = new ScheduleDoctorAdapter(this_context, scheduleDoctorArrayList, PatientSelectScheduleFragment.this::onClickIVMore);
                            scheduleDoctorAdapter.notifyDataSetChanged();
                            rcv_Schedule.setAdapter(scheduleDoctorAdapter);
                        }
                    }
                });
        firebaseFirestore
                .collection("Schedule")
                .whereGreaterThan("date", convertSelectedDateAtStartDayToTimestamp(CalendarUtils.selectedDate))
                .whereLessThan("date", convertSelectedDateAtEndDayToTimestamp(CalendarUtils.selectedDate))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                // Xử lý danh sách lịch trình bác sĩ được tải xuống
                ArrayList<ScheduleDoctor> scheduleDoctorArrayList = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshots) {
                    ScheduleDoctor scheduleDoctor = document.toObject(ScheduleDoctor.class);
                    scheduleDoctor.setIdDoctor(document.getString("id_doctor"));
                    scheduleDoctor.setNameDoctor(document.getString("doctor_name"));
                    scheduleDoctor.setDate(document.getDate("date"));
                    scheduleDoctor.setStatus(document.getString("status"));
                    scheduleDoctorArrayList.add(scheduleDoctor);
                }

                // Hiển thị danh sách lịch trình bác sĩ lên RecyclerView
                ScheduleDoctorAdapter scheduleDoctorAdapter = new ScheduleDoctorAdapter(getContext(), scheduleDoctorArrayList, PatientSelectScheduleFragment.this::onClickIVMore);
                rcv_Schedule.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rcv_Schedule.setAdapter(scheduleDoctorAdapter);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView(int position)
    {
        String monthYear = monthYearFromDate(CalendarUtils.selectedDate);
        tv_current_monthyear.setText(monthYear);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false);

        //Lấy chiều rộng của màn hình để căng giữa cho item được chọn
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        layoutManager.scrollToPositionWithOffset(position, (width-100)/2 - (74+(74/2)));

        rcv_date.setLayoutManager(layoutManager);
        ArrayList<LocalDate> daysInMonth = allDaysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(this.getContext(), daysInMonth, this);
        rcv_date.setAdapter(calendarAdapter);

        //Gọi load dữ liệu schedule
        setScheduleView();

        //Đóng bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null)
        {
            //Biến lưu ngày được chọn (truy cập biến này để truy xuất ngày được chọn)
            CalendarUtils.selectedDate = date;
            setMonthView(position);
        }
    }

    public void onClickIVMore(int position, ScheduleDoctor scheduleDoctor){
        selectedScheduleDoctor = scheduleDoctor;

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("Doctor")
                    .document(selectedScheduleDoctor.getIdDoctor())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                tv_address_bottom_sheet.setText("Address: " + documentSnapshot.getString("address"));
                            }
                        }
                    });

            if(selectedScheduleDoctor.getStatus().equals("full")){
                btn_booking.setVisibility(View.INVISIBLE);
            } else if (selectedScheduleDoctor.getStatus().equals("available")) {
                btn_booking.setText("book");
                btn_booking.setVisibility(View.VISIBLE);
                btn_booking.setEnabled(true);
            }

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        /*
        btn_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_booking.setText("booked");
            }
        });
        */
    }
}
