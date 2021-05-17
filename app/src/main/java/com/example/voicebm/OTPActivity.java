package com.example.voicebm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voicebm.Connect.CheckOTPCommunication;
import com.example.voicebm.Connect.CheckOTPResponse;
import com.example.voicebm.Connect.CheckUserCommunication;
import com.example.voicebm.Connect.NetworkProvider;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    public static final String TAG = "OTPActivity";
    Button btnCtn;
    EditText otpET;
    String phoneNumber,otpCode;
    int checkStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        checkStatus = getIntent().getIntExtra("checkStatus",0);
        System.out.println("Check stt OTPAcc: "+checkStatus);

        initView();

    }

    private void initView() {
        otpET = findViewById(R.id.otpEd);
        btnCtn = findViewById(R.id.btn_ctn);

        btnCtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCtn.setEnabled(false);
                otpCode = otpET.getText().toString();

                if (otpCode.equalsIgnoreCase("")){
                    Toast.makeText(OTPActivity.this,"Quý khách chưa nhập mã OTP",Toast.LENGTH_SHORT).show();
                    btnCtn.setEnabled(true);
                }else if(otpCode.length()<6){
                    Toast.makeText(OTPActivity.this,"Mã OTP sai định dạng",Toast.LENGTH_SHORT).show();
                    btnCtn.setEnabled(true);
                }else {
                    System.out.println("OTP code: "+otpCode);
                    CheckOTPCommunication checkOTPCommunication = NetworkProvider.self().getRetrofit().create(CheckOTPCommunication.class);
                    RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"), phoneNumber);
                    RequestBody otp_code = RequestBody.create(MediaType.parse("multipart/form-data"), otpCode);
                    checkOTPCommunication.checkOTP(speaker,otp_code)
                            .enqueue(new Callback<CheckOTPResponse>() {
                                @Override
                                public void onResponse(Call<CheckOTPResponse> call, Response<CheckOTPResponse> response) {
                                    if(response.body().getStatus()==1){
                                        if (response.body().getMsg().equalsIgnoreCase("OTP correct")){
                                            if (checkStatus==1){
                                                sendUsertoMain();
                                            }else if(checkStatus==0){
                                                sendUsertoSignUp();
                                            }else {
                                                Log.e(TAG,"Lỗi hệ thống");
                                            }

                                            Log.i(TAG,"Xác thực thành công");
                                        }
                                        else if(response.body().getMsg().equalsIgnoreCase("OTP has not been sent")){
                                            Toast.makeText(OTPActivity.this,"Xin quý khách vui lòng thử lại sau",Toast.LENGTH_SHORT).show();
                                            Log.i(TAG,"User chưa có mã OTP");
                                        }else {
                                            Toast.makeText(OTPActivity.this,"Mã OTP sai",Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "Mã OTP sai");
                                        }
                                    }else {
                                        Toast.makeText(OTPActivity.this,"Mã OTP sai",Toast.LENGTH_SHORT).show();
                                        Log.i(TAG,"Từ chối gửi mã");
                                    }
                                    btnCtn.setEnabled(true);
                                }

                                @Override
                                public void onFailure(Call<CheckOTPResponse> call, Throwable t) {
                                    Log.e(TAG,"Lỗi API");
                                    btnCtn.setEnabled(true);
                                }
                            });
                }

            }
        });
    }

    private void sendUsertoSignUp() {
        Intent signUpIntent = new Intent(OTPActivity.this,SignUpActivity.class);
        signUpIntent.putExtra("phoneNumber",phoneNumber);
        signUpIntent.putExtra("checkStatus",checkStatus);
        signUpIntent.putExtra("otpCode",otpCode);
        startActivity(signUpIntent);

        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

//    @Override
//    protected void onStart() {
//        if (phoneNumber != null) {
//            sendUsertoMain();
//        }
//        super.onStart();
//    }

    private void sendUsertoMain() {
        Intent mainIntent = new Intent(OTPActivity.this,MainActivity.class);
        mainIntent.putExtra("phoneNumber",phoneNumber);
        mainIntent.putExtra("checkStatus",checkStatus);
        mainIntent.putExtra("status",1);
        mainIntent.putExtra("otpCode",otpCode);
        startActivity(mainIntent);

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }
    @Override
    public void onBackPressed() {
        // your code.
        Intent loginIntent = new Intent(OTPActivity.this,LoginActivity.class);
        startActivity(loginIntent);

        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }
}