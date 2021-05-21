package com.example.mylibrary.Connect;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CheckUserCommunication {
    @POST("check-user")
    @Multipart
    Call<CheckUserResponse> check (
               @Part("speaker") RequestBody speaker
    );
}
