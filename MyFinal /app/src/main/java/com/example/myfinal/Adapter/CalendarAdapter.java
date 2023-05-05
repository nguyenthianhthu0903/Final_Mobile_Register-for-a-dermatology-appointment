package com.example.myfinal.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Model.CalendarUtils;
import com.example.myfinal.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>
{
    private Context context;
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(Context context, ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.context = context;
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule_date, parent, false);

        return new CalendarViewHolder(view, onItemListener, days);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        if (date == null) {
            //do nothing
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            }
            holder.dayOfWeek.setText(CalendarUtils.formattedWeek(date));

            if(date.equals(CalendarUtils.selectedDate)){
                //holder.parentView.setBackgroundColor(Color.LTGRAY);
                holder.parentView.findViewById(R.id.bg_item_schedule_date).setBackgroundResource(R.drawable.rec_schedule_black);

                TextView tv_dayOfMonth = holder.parentView.findViewById(R.id.tv_date);
                tv_dayOfMonth.setTextColor(tv_dayOfMonth.getResources().getColor(R.color.white));

                TextView tv_dayOfWeek = holder.parentView.findViewById(R.id.tv_day);
                tv_dayOfWeek.setTextColor(tv_dayOfWeek.getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ArrayList<LocalDate> days;
        public final View parentView;
        public final TextView dayOfMonth;
        public final TextView dayOfWeek;
        private final OnItemListener onItemListener;
        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener, ArrayList<LocalDate> days)
        {
            super(itemView);
            parentView = itemView.findViewById(R.id.parentView);
            dayOfMonth = itemView.findViewById(R.id.tv_date);
            dayOfWeek = itemView.findViewById(R.id.tv_day);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            this.days = days;
        }

        @Override
        public void onClick(View view)
        {
            onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
            //notifyDataSetChanged();
        }
    }
}
