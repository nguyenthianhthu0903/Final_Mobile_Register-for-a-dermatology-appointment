package com.example.myfinal.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Model.CalendarUtils;
import com.example.myfinal.Model.ScheduleDoctor;
import com.example.myfinal.R;

import java.util.ArrayList;

public class ScheduleDoctorAdapter extends RecyclerView.Adapter<ScheduleDoctorAdapter.ScheduleDoctorViewHolder>{
    private Context context;
    private ArrayList<ScheduleDoctor> listScheduleDoctor;
    private final OnItemListener onItemListener;

    public ScheduleDoctorAdapter(Context context, ArrayList<ScheduleDoctor> listScheduleDoctor, OnItemListener onItemListener) {
        this.context = context;
        this.listScheduleDoctor = listScheduleDoctor;
        this.onItemListener = onItemListener;
    }

    public ScheduleDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule, parent, false);

        return new ScheduleDoctorViewHolder(view, onItemListener, listScheduleDoctor);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleDoctorViewHolder holder, int position) {
        ScheduleDoctor scheduleDoctor = listScheduleDoctor.get(position);

        if(scheduleDoctor != null){
            holder.tv_doctor_name.setText(scheduleDoctor.getNameDoctor());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.tv_time.setText( CalendarUtils.getTimeOfDate(scheduleDoctor.getDate()));
            }
            holder.tv_status.setText(scheduleDoctor.getStatus());
        }
    }

    public int getItemCount() {
        return listScheduleDoctor.size();
    }

    public interface  OnItemListener {
        void onItemClick(int position, ScheduleDoctor scheduleDoctor);
    }

    public class ScheduleDoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ArrayList<ScheduleDoctor> scheduleDoctorArrayList;
        public View parentView;
        public TextView tv_doctor_name;
        public TextView tv_time;
        public ImageView iv_avt;
        public ImageView iv_more;
        public TextView tv_status;

        private final OnItemListener onItemListener;

        public ScheduleDoctorViewHolder(@NonNull View itemView, OnItemListener onItemListener, ArrayList<ScheduleDoctor> scheduleDoctorArrayList) {
            super(itemView);

            tv_doctor_name = itemView.findViewById(R.id.tv_doctor_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_avt = itemView.findViewById(R.id.iv_avt);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_status = itemView.findViewById(R.id.tv_status);
            parentView = itemView.findViewById(R.id.iv_item_schedule_parent_view);

            this.onItemListener = onItemListener;
            //itemView.setOnClickListener(this);
            iv_more.setOnClickListener(this);

            this.scheduleDoctorArrayList = scheduleDoctorArrayList;
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition(), scheduleDoctorArrayList.get(getAdapterPosition()));
            //notifyDataSetChanged();
        }
    }

}

