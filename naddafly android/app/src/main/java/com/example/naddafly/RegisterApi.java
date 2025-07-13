package com.example.naddafly;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterApi {

    @POST("register")
    Call<Void> registerUser(@Body RegisterService registerRequest);

}
