package com.example.voicebm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.voicebm.Connect.AppCommunication;
import com.example.voicebm.Connect.SignupResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.UserDictionary.Words.FREQUENCY;

public class SignUpActivity extends AppCompatActivity {
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int RECORDER_CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    private static final int SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO;
    Button btnSignUp, btnRecord1, btnRecord2, btnRecord3, btnStopRecord1, btnStopRecord2, btnStopRecord3;
    String pathSavePCM1 = "", pathSaveWAV1 = "";
    String pathSavePCM2 = "", pathSaveWAV2 = "";
    String pathSavePCM3 = "", pathSaveWAV3 = "";
    Button btnPlay1, btnStop1,btnPlay2, btnStop2,btnPlay3, btnStop3;
    MediaRecorder mediaRecorder;
    AudioRecord audioRecord;
    MediaPlayer mediaPlayer;
    Context mContext = SignUpActivity.this;
    CountDownTimer cTimer = null;
    int status;
    boolean clicked1 = false, clicked2 = false, clicked3 = false,stopClick1 = false, stopClick2 = false, stopClick3 = false;
    String audio_typeStr = "GT_16k";
    File file1, file2, file3, filePCM1, filePCM2, filePCM3;
    String speakerStr = "";
    String otp_codeStr;
    final int REQUEST_PERMISSION_CODE = 1000;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    private Thread recordingThread = null;
    private boolean isRecording = false;
    int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        speakerStr = getIntent().getStringExtra("phoneNumber");
        otp_codeStr = getIntent().getStringExtra("otpCode");
        System.out.println("speaker" + speakerStr);
        if (!checkPermissionFromDevice())
            requestPermission();



        initView();


    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Cấp quyền thành công", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Cấp quyền thất bại", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void initView() {
        btnPlay1 = findViewById(R.id.btn_play1);
        btnStop1 = findViewById(R.id.btn_stop1);
        btnPlay2 = findViewById(R.id.btn_play2);
        btnStop2 = findViewById(R.id.btn_stop2);
        btnPlay3 = findViewById(R.id.btn_play3);
        btnStop3 = findViewById(R.id.btn_stop3);
        btnRecord1 = findViewById(R.id.btn_record1);
        btnRecord2 = findViewById(R.id.btn_record2);
        btnRecord3 = findViewById(R.id.btn_record3);
        btnStopRecord1 = findViewById(R.id.btn_stopRecord1);
        btnStopRecord2 = findViewById(R.id.btn_stopRecord2);
        btnStopRecord3 = findViewById(R.id.btn_stopRecord3);
        btnSignUp = findViewById(R.id.btn_signUp);

//        btnPlay = findViewById(R.id.btn_play);
//        btnStop = findViewById(R.id.btn_stop);
        btnSignUp.setEnabled(false);
        btnPlay1.setEnabled(false);
        btnStop1.setEnabled(false);
        btnPlay2.setEnabled(false);
        btnStop2.setEnabled(false);
        btnPlay3.setEnabled(false);
        btnStop3.setEnabled(false);

        btnRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionFromDevice()) {
//                    pathSave1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.3gp";
//                    pathSave11 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.wav";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
                        pathSavePCM1 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.pcm";
                        pathSaveWAV1 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.wav";

//                        file1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString()+ "_audio_record.wav");
//                        filePCM1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString() + "_audio_record.pcm");
                        startRecording2(pathSavePCM1,pathSaveWAV1);
                    } else {
                        pathSavePCM1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
                        pathSaveWAV1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
                        startRecording(pathSavePCM1,pathSaveWAV1);
                    }


//                    pathSavePCM1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
//                    pathSaveWAV1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
//                    startRecording(pathSavePCM1,pathSaveWAV1);







                    btnRecord2.setEnabled(false);
                    btnRecord3.setEnabled(false);

                    btnRecord1.setVisibility(View.INVISIBLE);
                    btnStopRecord1.setVisibility(View.VISIBLE);
                    btnStopRecord1.setEnabled(false);

//                    btnPlay1.setEnabled(false);
//                    btnPlay2.setEnabled(false);
//                    btnPlay3.setEnabled(false);
//                    btnStop1.setEnabled(false);
//                    btnStop2.setEnabled(false);
//                    btnStop3.setEnabled(false);


                    Toast.makeText(SignUpActivity.this, "Đang ghi âm...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }


                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnRecord1.setEnabled(true);
                            }
                        });
                    }
                }, 500);
                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        btnStopRecord1.setEnabled(true);
                    }

                }.start();
            }

        });

        btnStopRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    stopRecording(pathSavePCM1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mediaRecorder.stop();
                btnRecord1.setVisibility(View.VISIBLE);
                btnStopRecord1.setVisibility(View.INVISIBLE);



                btnRecord2.setEnabled(true);
                btnRecord3.setEnabled(true);

//                btnPlay1.setEnabled(true);
//                btnPlay2.setEnabled(true);
//                btnPlay3.setEnabled(true);
//                btnStop1.setEnabled(true);
//                btnStop2.setEnabled(true);
//                btnStop3.setEnabled(true);

                file1 = new File(pathSaveWAV1);
                filePCM1 = new File(pathSavePCM1);



                clicked1 = true;
                if (clicked1){
                    btnPlay1.setEnabled(true);
                }
                if (clicked3 && clicked1 && clicked2)
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            btnSignUp.setEnabled(true);
                        }

                    }.start();



                System.out.println("file path: -----------" + file1.getAbsolutePath());

                Toast.makeText(SignUpActivity.this, "Tạm dừng...", Toast.LENGTH_SHORT).show();

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStopRecord1.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });
        btnRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pathSavePCM2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
//                filePCM2 = new File(pathSavePCM2);
//                pathSaveWAV2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
//
//
//                startRecording(pathSavePCM2,pathSaveWAV2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
                    pathSavePCM2 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.pcm";
                    pathSaveWAV2 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.wav";

//                    file2 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString()+ "_audio_record.wav");
//                    filePCM2 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString() + "_audio_record.pcm");
                    startRecording2(pathSavePCM2,pathSaveWAV2);
                } else {
                    pathSavePCM2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
                    pathSaveWAV2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
                    startRecording(pathSavePCM2,pathSaveWAV2);
                }

                btnRecord1.setEnabled(false);
                btnRecord3.setEnabled(false);

//                btnPlay1.setEnabled(false);
//                btnPlay2.setEnabled(false);
//                btnPlay3.setEnabled(false);
//                btnStop1.setEnabled(false);
//                btnStop2.setEnabled(false);
//                btnStop3.setEnabled(false);

                btnRecord2.setVisibility(View.INVISIBLE);
                btnStopRecord2.setVisibility(View.VISIBLE);
                btnStopRecord2.setEnabled(false);
                Toast.makeText(SignUpActivity.this, "Đang ghi âm...", Toast.LENGTH_SHORT).show();

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnRecord2.setEnabled(true);
                            }
                        });
                    }
                }, 500);

                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        btnStopRecord2.setEnabled(true);
                    }

                }.start();
            }

        });

        btnStopRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopRecording(pathSavePCM2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnRecord2.setVisibility(View.VISIBLE);
                btnStopRecord2.setVisibility(View.INVISIBLE);
                btnRecord1.setEnabled(true);
                btnRecord3.setEnabled(true);

//                btnPlay1.setEnabled(true);
//                btnPlay2.setEnabled(true);
//                btnPlay3.setEnabled(true);
//                btnStop1.setEnabled(true);
//                btnStop2.setEnabled(true);
//                btnStop3.setEnabled(true);

                file2 = new File(pathSaveWAV2);
                filePCM2 = new File(pathSavePCM2);
                clicked2 = true;
                if (clicked3 && clicked1 && clicked2)
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            btnSignUp.setEnabled(true);
                        }

                    }.start();
                if (clicked2){
                    btnPlay2.setEnabled(true);
                }

//                file22 = new File(pathSave22);
//                try {
//                    rawToWave(file2,file22);
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
                System.out.println("file path: -----------" + file2.getAbsolutePath());
                Toast.makeText(SignUpActivity.this, "Tạm dừng...", Toast.LENGTH_SHORT).show();

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStopRecord2.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });
        btnRecord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pathSavePCM3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
//                filePCM3 = new File(pathSavePCM3);
//                pathSaveWAV3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
//                startRecording(pathSavePCM3,pathSaveWAV3);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
                    pathSavePCM3 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.pcm";
                    pathSaveWAV3 = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.wav";

//                        file1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString()+ "_audio_record.wav");
//                        filePCM1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString() + "_audio_record.pcm");
                    startRecording2(pathSavePCM3,pathSaveWAV3);
                } else {
                    pathSavePCM3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
                    pathSaveWAV3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
                    startRecording(pathSavePCM3,pathSaveWAV3);
                }


                btnRecord3.setVisibility(View.INVISIBLE);
                btnStopRecord3.setVisibility(View.VISIBLE);
                btnStopRecord3.setEnabled(false);
                btnRecord1.setEnabled(false);
                btnRecord2.setEnabled(false);

//                btnPlay1.setEnabled(false);
//                btnPlay2.setEnabled(false);
//                btnPlay3.setEnabled(false);
//                btnStop1.setEnabled(false);
//                btnStop2.setEnabled(false);
//                btnStop3.setEnabled(false);

                Toast.makeText(SignUpActivity.this, "Đang ghi âm...", Toast.LENGTH_SHORT).show();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnRecord3.setEnabled(true);
                            }
                        });
                    }
                }, 500);

                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        btnStopRecord3.setEnabled(true);
                    }

                }.start();
            }

        });

        btnStopRecord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopRecording(pathSavePCM3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnRecord3.setVisibility(View.VISIBLE);
                btnStopRecord3.setVisibility(View.INVISIBLE);
                btnRecord1.setEnabled(true);
                btnRecord2.setEnabled(true);

//                btnPlay1.setEnabled(true);
//                btnPlay2.setEnabled(true);
//                btnPlay3.setEnabled(true);
//                btnStop1.setEnabled(true);
//                btnStop2.setEnabled(true);
//                btnStop3.setEnabled(true);

                file3 = new File(pathSaveWAV3);
                filePCM3 = new File(pathSavePCM3);
                clicked3 = true;
                if (clicked3 && clicked1 && clicked2)
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            btnSignUp.setEnabled(true);
                        }

                    }.start();

                if (clicked3){
                    btnPlay3.setEnabled(true);
                }
//                file33 = new File(pathSave33);
//                try {
//                    rawToWave(file3,file33);
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
                System.out.println("file path: -----------" + file3.getAbsolutePath());
                Toast.makeText(SignUpActivity.this, "Tạm dừng...", Toast.LENGTH_SHORT).show();

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStopRecord3.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });


        btnPlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnPlay1.setEnabled(false);
                btnStop1.setEnabled(true);


                btnRecord1.setEnabled(false);
                btnRecord1.setVisibility(View.VISIBLE);
                btnStopRecord1.setEnabled(false);


                setMediaPlayer(pathSaveWAV1,btnPlay1);
                System.out.println("BtnPlay:"+btnPlay1.getText().toString());
                if (btnPlay1.getText().toString().equalsIgnoreCase("Pause"))

                    btnRecord1.setEnabled(true);


//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                }else {
//                    mediaPlayer.start();
//                }


                //mediaPlayer.start();



//                try {
//                    PlayShortAudioFileViaAudioTrack(pathSaveWAV1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }



//                audioRecord = new AudioRecord();
//                try {
//                    audioRecord.set(pathSavePCM1);
//                    audioRecord.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //mediaPlayer.start();
                Toast.makeText(SignUpActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
//                Timer buttonTimer = new Timer();
//                buttonTimer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                btnPlay1.setEnabled(true);
//                            }
//                        });
//                    }
//                }, 500);
            }

        });
        btnStop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                btnStopRecord1.setEnabled(true);
                btnRecord1.setEnabled(true);
//                btnRecord1.setVisibility(View.VISIBLE);
                btnStop1.setEnabled(false);
                btnPlay1.setEnabled(true);
                String url = pathSaveWAV1; // your URL here
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }

//                Timer buttonTimer = new Timer();
//                buttonTimer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                btnStop1.setEnabled(true);
//                            }
//                        });
//                    }
//                }, 500);
            }

        });


        btnPlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop2.setEnabled(true);
                btnRecord2.setEnabled(false);
                btnRecord2.setVisibility(View.VISIBLE);
                btnStopRecord2.setEnabled(false);
                try {
                    PlayShortAudioFileViaAudioTrack(pathSaveWAV2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                audioRecord = new AudioRecord();
//                try {
//                    audioRecord.set(pathSavePCM1);
//                    audioRecord.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //mediaPlayer.start();
                Toast.makeText(SignUpActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnPlay2.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });
        btnStop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord2.setEnabled(true);
                btnRecord2.setEnabled(true);
                btnRecord2.setVisibility(View.VISIBLE);
                btnStop2.setEnabled(false);
                btnPlay2.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStop2.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });
        btnPlay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay3.setEnabled(false);
                btnStop3.setEnabled(true);
                btnRecord3.setEnabled(false);
                btnRecord3.setVisibility(View.VISIBLE);
                btnStopRecord3.setEnabled(false);
                try {
                    PlayShortAudioFileViaAudioTrack(pathSaveWAV3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                audioRecord = new AudioRecord();
//                try {
//                    audioRecord.set(pathSavePCM1);
//                    audioRecord.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //mediaPlayer.start();
                Toast.makeText(SignUpActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
//                Timer buttonTimer = new Timer();
//                buttonTimer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                btnPlay3.setEnabled(true);
//                            }
//                        });
//                    }
//                }, 500);
            }

        });
        btnStop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord3.setEnabled(true);
                btnRecord3.setEnabled(true);
                btnRecord3.setVisibility(View.VISIBLE);
                btnStop3.setEnabled(false);
                btnPlay3.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStop3.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setEnabled(false);
                if (clicked1 && clicked2 && clicked3)
                    btnSignUp.setEnabled(true);

                AppCommunication apiService = com.example.voicebm.Connect.NetworkProvider.self().getRetrofit().create(AppCommunication.class);
//                RequestBody reqFile1 = RequestBody.create(MediaType.parse("audio/wav"),fileWAV1);
//                RequestBody reqFile2 = RequestBody.create(MediaType.parse("audio/wav"),fileWAV2);
//                RequestBody reqFile3 = RequestBody.create(MediaType.parse("audio/wav"),fileWAV3);
                RequestBody audio_type = RequestBody.create(MediaType.parse("multipart/form-data"), audio_typeStr);
                RequestBody otp_code = RequestBody.create(MediaType.parse("multipart/form-data"),otp_codeStr);
                RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"), speakerStr);
                RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
                RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
                RequestBody requestFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), file3);
                MultipartBody.Part reqFile1 = MultipartBody.Part.createFormData("file1", file1.getName(), requestFile1);
                MultipartBody.Part reqFile2 = MultipartBody.Part.createFormData("file2", file2.getName(), requestFile2);
                MultipartBody.Part reqFile3 = MultipartBody.Part.createFormData("file3", file3.getName(), requestFile3);
                apiService.enroll(reqFile1, reqFile2, reqFile3, audio_type, speaker,otp_code)
                        .enqueue(new Callback<SignupResponse>() {
                            @Override
                            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                                //TODO Xử lý dữ liệu trả về
                                if(response.body().getStatus() == null){
                                    Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                }
                                else if (response.body().getStatus() == 1) {
                                    status = response.body().getStatus();
                                    sendUsertoMain();
                                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                }
                                System.out.println(response.body());
                                file1.delete();
                                file2.delete();
                                file3.delete();
                                filePCM1.delete();
                                filePCM2.delete();
                                filePCM3.delete();
                            }

                            @Override
                            public void onFailure(Call<SignupResponse> call, Throwable t) {
                                //TODO Xử lý lỗi
                                System.out.println(t);
                                file1.delete();
                                file2.delete();
                                file3.delete();
                                filePCM1.delete();
                                filePCM2.delete();
                                filePCM3.delete();
                            }
                        });
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnSignUp.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });


    }

    private void startRecording2(String filePath, String filePathWav) {
        if( bufferSize == AudioRecord.ERROR_BAD_VALUE)
            System.out.println( "Bad Value for \"bufferSize\", recording parameters are not supported by the hardware");

        if( bufferSize == AudioRecord.ERROR )
            System.out.println("Bad Value for \"bufferSize\", implementation was unable to query the hardware for its output properties");

        System.out.println("\"bufferSize\"="+bufferSize);

        // Initialize Audio Recorder.
        recorder = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING, /*BufferElements2Rec * BytesPerElement*/bufferSize);
        // Starts recording from the AudioRecord instance.
        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile2(filePath, filePathWav);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile2(String filePath, String filePathWav) {
        byte saudioBuffer[] = new byte[bufferSize];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(saudioBuffer, 0, bufferSize);
            try {
                //  writes the data to file from buffer stores the voice buffer
                os.write(saudioBuffer, 0, bufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File filePCM = new File(filePath);
//
        File fileWav = new File(filePathWav);
        try {
            rawToWave(filePCM,fileWav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startRecording(String filePath, String filePathWav) {
        if( bufferSize == AudioRecord.ERROR_BAD_VALUE)
            System.out.println( "Bad Value for \"bufferSize\", recording parameters are not supported by the hardware");

        if( bufferSize == AudioRecord.ERROR )
            System.out.println("Bad Value for \"bufferSize\", implementation was unable to query the hardware for its output properties");

        System.out.println("\"bufferSize\"="+bufferSize);

        // Initialize Audio Recorder.
        recorder = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        // Starts recording from the AudioRecord instance.
        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(filePath, filePathWav);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile(String filePath, String filePathWav) {
        //Write the output audio in byte
        byte saudioBuffer[] = new byte[bufferSize];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(saudioBuffer, 0, bufferSize);
            try {
                //  writes the data to file from buffer stores the voice buffer
                os.write(saudioBuffer, 0, bufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File filePCM = new File(filePath);
        File fileWav = new File(filePathWav);
        try {
            rawToWave(filePCM,fileWav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording(String filePath) throws IOException {
        //  stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
           // PlayShortAudioFileViaAudioTrack(filePath);
        }
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 16000); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }
    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }




    private void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException{
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null)
            return;

        //Reading the file..
        File file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
        byte[] byteData = new byte[(int) file.length()];


        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( byteData );
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Set and push to audio track..
        int intSize = android.media.AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING);


        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);
        if (at!=null) {
            at.play();
            // Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            at.release();
        }
        else{

        }

    }


    private void sendUsertoMain() {
        Intent mainIntent = new Intent(SignUpActivity.this,MainActivity.class);
        mainIntent.putExtra("status",1);
        mainIntent.putExtra("phoneNumber",speakerStr);
        mainIntent.putExtra("otpCode",otp_codeStr);
        startActivity(mainIntent);

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    private void setMediaPlayer( String url, Button btnPlay){
        MediaPlayer mP = new MediaPlayer();
        try {
            mP.setDataSource(getApplicationContext(), Uri.parse(url));
            mP.prepare();
            mP.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mP.start();
                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mP.isPlaying()){
                                btnPlay.setText("Play");
                                mP.pause();
                            }else{
                                btnPlay.setText("Pause");
                                mP.start();
                            }
                        }
                    });
                    Toast.makeText(SignUpActivity.this,"onPrepared",Toast.LENGTH_SHORT).show();


                }
            });
            mP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(SignUpActivity.this,"onCompletion",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}