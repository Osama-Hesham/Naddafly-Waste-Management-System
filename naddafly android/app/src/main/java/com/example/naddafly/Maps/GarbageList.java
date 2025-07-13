package com.example.naddafly.Maps;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.naddafly.R;
import com.example.naddafly.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GarbageList extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private GarbageAdapter garbageAdapter;
    private List<GarbageItem> garbageItemList;
    SharedPreferences sharedPreferences ;
    String savedToken ;

    private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private static final String API_KEY = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_garbage_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedToken = sharedPreferences.getString("token", null);
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        garbageItemList = new ArrayList<>();
        garbageAdapter = new GarbageAdapter(garbageItemList);
        recyclerView.setAdapter(garbageAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if they are not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location currentLocation = task.getResult();
                            double currentLatitude = currentLocation.getLatitude();
                            double currentLongitude = currentLocation.getLongitude();

                            calculateDistances(currentLatitude, currentLongitude);
                        }
                    }
                });
    }

    private void calculateDistances(double currentLatitude, double currentLongitude) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DIRECTIONS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GarbageService garbageService = RetrofitClient.getRetrofitInstance().create(GarbageService.class);
        garbageService.getData(savedToken).enqueue(new Callback<List<GarbageResponse>>() {
            @Override
            public void onResponse(Call<List<GarbageResponse>> call, Response<List<GarbageResponse>> response) {
                List<GarbageResponse> dataList = response.body();
                FindTimeAndDistance(dataList);
            }

            private void FindTimeAndDistance(List<GarbageResponse> dataList) {
                DirectionsService service = retrofit.create(DirectionsService.class);

                for (GarbageResponse garbage : dataList) {
                    String origin = currentLatitude + "," + currentLongitude;
                    String destination = garbage.getLatitude() + "," + garbage.getLongitude();

                    service.getDirections(origin, destination, API_KEY, "driving").enqueue(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                DirectionsResponse directionsResponse = response.body();
                                if (directionsResponse.routes != null && !directionsResponse.routes.isEmpty()) {
                                    Route route = directionsResponse.routes.get(0);
                                    if (route.legs != null && !route.legs.isEmpty()) {
                                        Leg leg = route.legs.get(0);
                                        GarbageItem item = new GarbageItem(garbage.getLatitude() , garbage.getLongitude(), leg.distance.text, leg.duration.text, garbage.getSize() ,"nn.jpg",garbage.getId());
                                        garbageItemList.add(item);
                                        garbageAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                            // Handle the failure case
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<GarbageResponse>> call, Throwable t) {

            }
        });


    }

}