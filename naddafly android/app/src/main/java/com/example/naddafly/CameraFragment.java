package com.example.naddafly;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment implements View.OnClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFeature;
    private ApiInterface apiService;
    Button bTakePicture;
    private boolean isCameraToggledOn = false;
    private Handler handler = new Handler();
    PreviewView previewView;
    private ImageCapture imageCapture;

    private TextView coinsTextView;
    private String longitude = "0.0";
    private String latitude = "0.0";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        bTakePicture = rootView.findViewById(R.id.bCapture);
        previewView = rootView.findViewById(R.id.previewView);
        bTakePicture.setOnClickListener(this);
        coinsTextView = rootView.findViewById(R.id.CoinsTextView);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String score = sharedPreferences.getString("score", null);
        coinsTextView.setText(score);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFeature.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFeature.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());

        // Initialize Retrofit
        apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

        return rootView;
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(requireContext());
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCapture) {
            toggleCamera();
        }
    }

    private void toggleCamera() {
        isCameraToggledOn = !isCameraToggledOn;
        if (isCameraToggledOn) {
            startCapturingPhotos();
            bTakePicture.setText("Stop Recording");
            bTakePicture.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            stopCapturingPhotos();
            bTakePicture.setText("Start Recording");
            bTakePicture.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
        }
    }

    private void startCapturingPhotos() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCameraToggledOn) {
                    capturePhoto();
                    handler.postDelayed(this, 3000); // Capture photo every 1 second
                }
            }
        }, 3000); // Initial delay of 1 second before capturing the first photo
    }

    private void stopCapturingPhotos() {
        // Stop capturing photos by removing any pending callbacks
        handler.removeCallbacksAndMessages(null);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    private void capturePhoto() {
        File photoFile = getPhotoFile();

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // After saving the image, send it using Retrofit
                        sendImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(requireContext(), "Error Saving photo" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private File getPhotoFile() {
        File photoDir = new File(requireContext().getFilesDir(), "photos");

        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }

        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String photoFilePath = photoDir.getAbsolutePath() + '/' + timestamp + ".jpg";
        return new File(photoFilePath);
    }









    private void sendImage(File photoFile) {



        getCurrentLocation(); // Make sure location is updated before sending the image
        String detectionDate = getCurrentDate();
        String Cookie = null;
        Toast.makeText(requireContext(), longitude + latitude + detectionDate, Toast.LENGTH_SHORT).show();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestBody);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedToken = sharedPreferences.getString("token", null);
        if (savedToken != null) {
            Cookie =savedToken;
        } else {
            // Token not found, handle accordingly
        }
        
        Call<CoinsResponse> call = apiService.uploadImage(latitude, longitude, detectionDate, imagePart,Cookie);

        call.enqueue(new Callback<CoinsResponse>() {
            @Override
            public void onResponse(Call<CoinsResponse> call, Response<CoinsResponse> response) {
                if (response.isSuccessful()) {
                    CoinsResponse coinsResponse = response.body();

                    String coins = coinsResponse.getCoins();
                    coinsTextView.setText(coins);
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CoinsResponse> call, Throwable t) {
                // Handle failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
