package com.example.cst2335_finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<SavedImageData> {

    private Context mContent;
    private int mResource;
    private List<SavedImageData> mData;

    public CustomArrayAdapter(Context context, int resource, List<SavedImageData> objects) {
        super(context, resource, objects);
        mContent = context;
        mResource = resource;
        mData = objects;
    }

    public List<SavedImageData> getData() {
        return mData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContent).inflate(mResource, parent, false);
        }

        SavedImageData savedImageData = getItem(position);

        // Set the image path and date to the appropriate TextViews in your custom layout
        if (savedImageData != null) {
            TextView dateTextView = convertView.findViewById(R.id.listTextDate);
            dateTextView.setText(savedImageData.getDate());
        }

        return convertView;
    }
}