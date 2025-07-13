package com.example.naddafly.Maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.naddafly.R;
import com.example.naddafly.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private List<GarbageResponse> garbageList;
    private LocationCallback locationCallback;
    private Marker currentUserMarker;
    private Location lastKnownLocation;
    private boolean isFirstLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Set up location callback to continuously update location
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lastKnownLocation = location;
                        updateCurrentUserMarker(location);
                        fetchGarbageLocations(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
            }
        };
        startLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();

                            // Fetch garbage locations from the server
                            fetchGarbageLocations(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
        mMap.setMyLocationEnabled(true);
    }

    private void fetchGarbageLocations(LatLng userLocation) {
        GarbageService garbageService = RetrofitClient.getRetrofitInstance().create(GarbageService.class);
        String token = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token", null);

        garbageService.getData(token).enqueue(new Callback<List<GarbageResponse>>() {
            @Override
            public void onResponse(Call<List<GarbageResponse>> call, Response<List<GarbageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    garbageList = response.body();
                    addGarbageMarkers(userLocation);
                }
            }

            @Override
            public void onFailure(Call<List<GarbageResponse>> call, Throwable t) {
                // Handle the failure case
            }
        });
    }

    private void addGarbageMarkers(LatLng userLocation) {
        for (GarbageResponse garbage : garbageList) {
            LatLng garbageLocation = new LatLng(garbage.getLatitude(), garbage.getLongitude());
            mMap.addMarker(new MarkerOptions().position(garbageLocation).title("Garbage ID:" + garbage.getId()));
            // Draw routes to garbage locations
            drawRouteToGarbage(userLocation, garbageLocation, garbage);
        }
    }

    private void drawRouteToGarbage(LatLng userLocation, LatLng garbageLocation, GarbageResponse garbage) {
        String origin = userLocation.latitude + "," + userLocation.longitude;
        String destination = garbageLocation.latitude + "," + garbageLocation.longitude;

        DirectionsService directionsService = RetrofitClient.getRetrofitInstance().create(DirectionsService.class);
        directionsService.getDirections(origin, destination, getString(R.string.MAPS_API_KEY), "driving")
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Route> routes = response.body().routes;
                            if (!routes.isEmpty()) {
                                // Get the first route and its legs
                                Route route = routes.get(0);
                                List<LatLng> points = new ArrayList<>();
                                for (Leg leg : routes.get(0).legs) {
                                    for (Step step : leg.steps) {
                                        points.addAll(PolyUtil.decode(step.polyline.points));
                                    }

                                    // Add garbage item
                                    GarbageItem item = new GarbageItem(
                                            garbage.getLatitude(),
                                            garbage.getLongitude(),
                                            leg.distance.text,
                                            leg.duration.text,
                                            garbage.getSize(),
                                            "nn.jpg",
                                            garbage.getId()
                                    );
                                    // Here you can add the item to a list if needed
                                }
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(points)
                                        .width(10);// Optional: Set color of the polyline
                                mMap.addPolyline(polylineOptions);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        // Handle the failure case
                    }
                });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000); // Update every 10 seconds

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void updateCurrentUserMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentUserMarker == null) {
            //currentUserMarker = mMap.addMarker(new MarkerOptions()
            //        .position(latLng)
            //        .title("You are here"));
            if (isFirstLocationUpdate) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                isFirstLocationUpdate = false;
            }
        } else {
            currentUserMarker.setPosition(latLng);
        }
    }

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastKnownLocation != null) {
            updateCurrentUserMarker(lastKnownLocation);
        }
        startLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
