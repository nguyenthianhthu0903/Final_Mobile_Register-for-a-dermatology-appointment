package com.example.myfinal.Model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class CalendarUtils {
    public static LocalDate selectedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, yyyy");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formattedWeek(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTimeOfDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Timestamp convertSelectedDateAtStartDayToTimestamp(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf(String.valueOf(date.atStartOfDay().format(formatter)));

        return timestamp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Timestamp convertSelectedDateAtEndDayToTimestamp(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf(String.valueOf(date.atTime(23, 59).format(formatter)));

        return  timestamp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> allDaysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        for(int i = 1; i <= daysInMonth; i++){
            daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(), i));
        }
        return  daysInMonthArray;
    }

}