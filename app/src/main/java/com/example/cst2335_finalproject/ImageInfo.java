package com.example.cst2335_finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageInfo extends Fragment {

    private TextView dateText, urlText, hdUrlText;
    private String selectedDate;

    public void setSelectedDate(String selectedDate){
        this.selectedDate = selectedDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_info, container, false);

        dateText = view.findViewById(R.id.dateText);
        urlText = view.findViewById(R.id.urlText);
        hdUrlText = view.findViewById(R.id.hdUrlText);

        ImageButton backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        hdUrlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hdUrl = hdUrlText.getText().toString();
                if (!TextUtils.isEmpty(hdUrl)) {
                    Uri uri = Uri.parse(hdUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        if (!TextUtils.isEmpty(selectedDate)) {
            fetchData();
        }

        return view;
    }

    private void fetchData() {
            String apiKey = "aIFzJPI6VrmhU7pf2u3ORCNsMywgwvB6f6Fjugwl";
            String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + selectedDate;

            new FetchDataAsyncTask().execute(apiUrl);
    }

    private class FetchDataAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String ... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                jsonResponse = buffer.toString();
                return new JSONObject(jsonResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    Log.d("FetchDataAsyncTask", "JSON data retrieved: " + jsonObject.toString());
                    String date = jsonObject.getString("date");
                    String url = jsonObject.getString("url");
                    String hdUrl = jsonObject.optString("hdurl", "No link for HD Image.");

                    Log.d("FetchDataAsyncTask", "Date: " + date);
                    Log.d("FetchDataAsyncTask", "URL: " + url);
                    Log.d("FetchDataAsyncTask", "HD URL: " + hdUrl);

                    // Update TextViews with the extracted information
                    dateText.setText(date);
                    urlText.setText(url);
                    hdUrlText.setText(hdUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                dateText.setText("null");
                urlText.setText("null");
                hdUrlText.setText("null");

                Log.e("FetchDataAsyncTask", "Failed to retrieve JSON data");
            }
        }
    }
}