package com.example.naddafly;

import java.util.Date;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public interface ApiInterface {
    @Multipart
    @POST("upload-image")
    Call<CoinsResponse> uploadImage(
            @Part("latitude") String latitude,
            @Part("longitude") String longitude,
            @Part("detection_date") String detectionDate,
            @Part MultipartBody.Part image
    , @Header("Cookie") String cookieValue);
}
