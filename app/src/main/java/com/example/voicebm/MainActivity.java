package com.example.voicebm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button verifyVoiceID,btnLogOut;
    TextView cusNumber,tv_reSummit;
    String phoneNumber,otpCode;
    int status,checkStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        checkStatus = getIntent().getIntExtra("checkStatus",0);
        otpCode = getIntent().getStringExtra("otpCode");
        status = getIntent().getIntExtra("status",0);
        initView();
    }

    private void initView() {
        verifyVoiceID = findViewById(R.id.btn_verifyVoice);
        cusNumber = findViewById(R.id.tv_cusNumber);
        tv_reSummit = findViewById(R.id.tv_reSummit);
        btnLogOut = findViewById(R.id.btn_logOut);
        cusNumber.setText(phoneNumber);

        verifyVoiceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserVerifyVoice();
            }
        });

        tv_reSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserSignUp();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        MainActivity.this);

// Setting Dialog Title
                alertDialog2.setTitle("");

// Setting Dialog Message
                alertDialog2.setMessage("Bạn có muốn đăng xuất...");

// Setting Icon to Dialog
//                alertDialog2.setIcon(R.drawable.delete);

// Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("Có",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(),
                                        "Bạn đã đăng xuất", Toast.LENGTH_SHORT)
                                        .show();
                                sendUserToLogin();
                            }
                        });
// Setting Negative "NO" Btn
                alertDialog2.setNegativeButton("Không",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog

                                dialog.cancel();
                            }
                        });

// Showing Alert Dialog
                alertDialog2.show();


            }
        });

    }

    private void sendUserSignUp() {
        Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
        signUpIntent.putExtra("phoneNumber",phoneNumber);
        signUpIntent.putExtra("otpCode",otpCode);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signUpIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (status == 0) {
            sendUserToLogin();
       }
    }

    public void sendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void sendUserVerifyVoice() {
        Intent verifyIntent = new Intent(MainActivity.this, VerifyVoiceActivity.class);
        verifyIntent.putExtra("phoneNumber",phoneNumber);
        verifyIntent.putExtra("otpCode",otpCode);
        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(verifyIntent);
        finish();
    }

}