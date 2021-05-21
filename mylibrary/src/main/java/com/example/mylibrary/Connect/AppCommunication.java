package com.example.mylibrary.Connect;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppCommunication {

    @POST("do-enroll")
    @Multipart
    Call<SignupResponse> enroll (@Part MultipartBody.Part file1,
                                 @Part MultipartBody.Part file2 ,
                                 @Part MultipartBody.Part file3 ,
                                 @Part("audio_type") RequestBody audio_type,
                                 @Part("speaker") RequestBody speaker,
                                 @Part("otp_code") RequestBody otp_code
    );


}
