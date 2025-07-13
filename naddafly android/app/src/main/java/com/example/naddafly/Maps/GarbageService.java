package com.example.naddafly.Maps;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GarbageService {
    @GET("map")
    Call<List<GarbageResponse>> getData(@Header("Cookie") String cookieValue);

}
