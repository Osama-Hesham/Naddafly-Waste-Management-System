package com.example.naddafly;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("login")
    Call<RetrievedUser> LoginUser(@Body LoginService loginService);
}
