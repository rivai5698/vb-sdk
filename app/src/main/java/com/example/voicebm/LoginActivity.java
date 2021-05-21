package com.example.voicebm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.voicebm.Connect.CheckUserCommunication;
import com.example.voicebm.Connect.CheckUserResponse;
import com.example.voicebm.Connect.NetworkProvider;
import com.example.voicebm.Connect.OTPCommunication;
import com.example.voicebm.Connect.OTPResponse;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    Button btnStart;
    EditText etPhone;
    String phoneNumber;
    int checkStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();


    }

    private void initView() {
            btnStart = findViewById(R.id.btn_start);
            etPhone = findViewById(R.id.phoneText);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    phoneNumber = etPhone.getText().toString();
                    System.out.println("phone: "+phoneNumber);
                    if (phoneNumber.equalsIgnoreCase("")){
                        System.out.println("Phone empty");
                        Toast.makeText(LoginActivity.this,"Quý khách chưa nhập mã số điện thoại",Toast.LENGTH_SHORT).show();
                    }
                    else if(phoneNumber.length()<10){
                        System.out.println("Length smaller than 10");
                        Toast.makeText(LoginActivity.this,"Số điện thoại sai định dạng",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        System.out.println("Mèo méo meo mèo meo");
                        OTPCommunication otpCommunication = NetworkProvider.self().getRetrofit().create(OTPCommunication.class);
                        RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"),phoneNumber);
                        otpCommunication.sendOTP(speaker)
                                .enqueue(new Callback<OTPResponse>() {
                                    @Override
                                    public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                                        System.out.println("AAAAAAAAAAAA: "+response.body());
                                        if(response.body().getStatus()==1){
                                            Toast.makeText(LoginActivity.this,"Mã OTP đang được gửi tới quý khách",Toast.LENGTH_SHORT).show();
                                            Log.i(TAG,"Gửi OTP thành công");
                                            CheckUserCommunication checkUserCommunication = NetworkProvider.self().getRetrofit().create(CheckUserCommunication.class);
                                            RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"),phoneNumber);
                                            System.out.println("Phone number need to check: "+ phoneNumber);
                                            checkUserCommunication.check(speaker)
                                                    .enqueue(new Callback<CheckUserResponse>() {
                                                        @Override
                                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                                            if(response.body().getStatus()!=null){

                                                                Log.i(TAG,"Check khách hàng thành công");
                                                                if(response.body().getStatus()==1){
                                                                    checkStatus = 1;
                                                                    Log.i(TAG,"Khách hàng đã tồn tại");
                                                                    sendUsertoOTP();

                                                                }else if(response.body().getStatus()==0) {
                                                                    checkStatus = 0;
                                                                    Log.i(TAG,"Khách hàng chưa tồn tại");
                                                                    sendUsertoOTP();

                                                                }else {
                                                                    checkStatus = 0;
                                                                    Log.e(TAG,"Lỗi hệ thống");
                                                                }

                                                            }else {
                                                                Toast.makeText(LoginActivity.this,"Lỗi khi check khách hàng",Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG,"Lỗi từ hệ thống");
                                                            }
                                                            System.out.println("Response otp: " + response.body());

                                                        }

                                                        @Override
                                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                                            Log.e(TAG, String.valueOf(t));
                                                            Log.e(TAG,"Lỗi từ API");
                                                        }
                                                    });


                                        }else if(response.body().getStatus()==0){
                                            Toast.makeText(LoginActivity.this,"Lỗi khi gửi mã OTP",Toast.LENGTH_SHORT).show();
                                            Log.e(TAG,"Lỗi gửi mã OTP từ API");
                                        }else {
                                            Toast.makeText(LoginActivity.this,"Lỗi khi gửi mã OTP",Toast.LENGTH_SHORT).show();
                                            Log.e(TAG,"Lỗi gửi mã OTP từ hệ thống");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<OTPResponse> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this,"Lỗi khi gửi mã OTP",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG,"Lỗi gửi mã OTP từ hệ thống0000");
                                    }
                                });
                    }

                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    btnStart.setEnabled(true);
                                }
                            });
                        }
                    }, 500);




                }
            });
    }

    private void sendUsertoOTP() {
        Intent otpIntent = new Intent(LoginActivity.this,OTPActivity.class);
        otpIntent.putExtra("phoneNumber",phoneNumber);
        otpIntent.putExtra("checkStatus",checkStatus);

        System.out.println("Check stt Login: "+ checkStatus);
        otpIntent.setType(Settings.ACTION_SYNC_SETTINGS);
        otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(otpIntent);

        finish();
    }


}