package com.example.cst2335_finalproject;

import static androidx.core.content.PermissionChecker.checkPermission;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageInfo extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView dateText, urlText, hdUrlText;
    private Button saveImageButton;
    private String selectedDate;
    private String hdUrl;
    private List<SavedImageData> imageDataList;
    private CustomArrayAdapter adapter;

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_info, container, false);

        dateText = view.findViewById(R.id.dateText);
        urlText = view.findViewById(R.id.urlText);
        hdUrlText = view.findViewById(R.id.hdUrlText);
        saveImageButton = view.findViewById(R.id.saveImageButton);
        imageDataList = new ArrayList<>();
        adapter = new CustomArrayAdapter(requireContext(), R.layout.list_items_layout, imageDataList);

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    String imagePath = saveImageToDevice(hdUrl);
                    if (imagePath != null){
                        saveImageData(imagePath, selectedDate);
                    }
                } else {
                    requestPermission();
                }
            }
        });

        ImageButton backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        hdUrlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hdUrl = hdUrlText.getText().toString();
                if (!TextUtils.isEmpty(hdUrl) && hdUrl.startsWith("http")) {
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

    private String saveImageToDevice(String imageUrl) {
        String filePath = null;
        if (!TextUtils.isEmpty(imageUrl) && imageUrl.startsWith("http")) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
            request.setDescription("Downloading image");
            request.setTitle("NASA Image");

            // Allow the media scanner to scan the file
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            String fileName="NASA_" + selectedDate+".jpg";
            // Set the destination for the downloaded file
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);

            // Get the DownloadManager and enqueue the request
            DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(getContext(), "Image download started", Toast.LENGTH_SHORT).show();
                filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + fileName;
            } else {
                Toast.makeText(getContext(), "Download manager not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Invalid HD URL", Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }

    private void fetchData() {
        String apiKey = "aIFzJPI6VrmhU7pf2u3ORCNsMywgwvB6f6Fjugwl";
        String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + selectedDate;

        new FetchDataAsyncTask().execute(apiUrl);
    }

    private void saveImageData(String imagePath, String date) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ImageData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String existingData = sharedPreferences.getString("imageList", "");

        if (!TextUtils.isEmpty(existingData)) {
            existingData += ";";
        }
        existingData += imagePath + "," + date;

        editor.putString("imageList", existingData);
        editor.apply();

        SavedImageData newDataItem = new SavedImageData(imagePath, date);
        imageDataList.add(newDataItem);
        adapter.notifyDataSetChanged();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String imagePath = saveImageToDevice(hdUrl);
                if (imagePath != null) {
                    saveImageData(imagePath, selectedDate);
                }
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchDataAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
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
                    hdUrl = jsonObject.optString("hdurl", "No link for HD Image.");

                    Log.d("FetchDataAsyncTask", "Date: " + date);
                    Log.d("FetchDataAsyncTask", "URL: " + url);
                    Log.d("FetchDataAsyncTask", "HD URL: " + hdUrl);

                    // Update TextViews with the extracted information
                    dateText.setText(date);
                    urlText.setText(url);
                    hdUrlText.setText(hdUrl);

                    // Show save button only if hdUrl is a valid URL
                    if (hdUrl != null && hdUrl.startsWith("http")) {
                        saveImageButton.setVisibility(View.VISIBLE);
                    } else {
                        saveImageButton.setVisibility(View.GONE);
                    }
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