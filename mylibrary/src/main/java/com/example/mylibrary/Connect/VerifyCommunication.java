package com.example.mylibrary.Connect;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface VerifyCommunication {
    @POST("do-speaker-verification")
    @Multipart
    Call<VerifyResponse> verify (@Part MultipartBody.Part file,
                                 @Part("speaker") RequestBody speaker,
                                 @Part("verify_type") RequestBody verify_type
    );
}
