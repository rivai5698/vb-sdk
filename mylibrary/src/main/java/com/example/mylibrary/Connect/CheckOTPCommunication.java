package com.example.mylibrary.Connect;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CheckOTPCommunication {
    @POST("check-otp")
    @Multipart
    Call<CheckOTPResponse> checkOTP(
            @Part("speaker") RequestBody speaker,
            @Part("otp_code") RequestBody otp_code
    );
}
