package com.example.naddafly;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RedeemApi {

    @GET("redeem")
    Call<Rewards> RedeemVoucher(@Header("Cookie") String cookieValue);
}
