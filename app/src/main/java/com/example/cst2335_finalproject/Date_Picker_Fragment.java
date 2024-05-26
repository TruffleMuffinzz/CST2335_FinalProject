package com.example.cst2335_finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;


public class Date_Picker_Fragment extends Fragment {

    private DatePicker datePicker;
    private Button fetchImageButton;
    private int selectedYear, selectedMonth, selectedDay;

    public interface FetchImageClickListener {
        void fetchImageClicked(int year, int month, int day);
    }

    private FetchImageClickListener fetchImageClickListener;

    public void setFetchImageClickListener(FetchImageClickListener listener) {
        this.fetchImageClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_picker_fragment, container, false);
        datePicker = view.findViewById(R.id.datePickerObject);
        fetchImageButton = view.findViewById(R.id.fetchImgBtn);

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(1995, Calendar.JUNE, 16);
        datePicker.setMinDate(minCalendar.getTimeInMillis());

        Calendar maxCalendar = Calendar.getInstance();
        datePicker.setMaxDate(maxCalendar.getTimeInMillis());

        fetchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedYear = datePicker.getYear();
                selectedMonth = datePicker.getMonth();
                selectedDay = datePicker.getDayOfMonth();
                if (fetchImageClickListener != null) {
                    fetchImageClickListener.fetchImageClicked(selectedYear, selectedMonth, selectedDay);
                }
            }
        });

        return view;

    }

    public void hideDatePickerFragment() {
        if (getView() != null) {
            getView().setVisibility(View.GONE);
        }
    }

}