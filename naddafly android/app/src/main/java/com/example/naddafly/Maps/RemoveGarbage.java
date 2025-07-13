package com.example.naddafly.Maps;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RemoveGarbage {
    @POST("remove-garbage/{id}")
    Call<Void> removeGarbage(@Path("id") int id, @Header("Cookie") String Cookie);
}
