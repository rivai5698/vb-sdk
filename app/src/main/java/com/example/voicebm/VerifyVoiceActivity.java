package com.example.voicebm;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.DnsResolver;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mylibrary.ResultResponse;
import com.example.mylibrary.VerifyVoiceId;

import com.example.voicebm.Connect.NetworkProvider;
import com.example.voicebm.Connect.SignupResponse;
import com.example.voicebm.Connect.VerifyCommunication;
import com.example.voicebm.Connect.VerifyResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class VerifyVoiceActivity extends AppCompatActivity {
    private VerifyVoiceId verifyVoiceId = new VerifyVoiceId();


    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int RECORDER_CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO;
    private static final int RECORDER_CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    private static final String TAG = "VerifyVoiceActivity";
    final int REQUEST_PERMISSION_CODE = 1000;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    private Thread recordingThread = null;
    private boolean isRecording = false;
    String pathSavePCM,pathSaveWAV;
    File filePCM,fileWAV;
    MediaPlayer mediaPlayer;
    Button btnRecord,btnStopRecord,btnBack,btnCtn,btnPlay,btnStop;
    TextView tvResultPercent, tvResultText, tvGuide;
    String speakerStr,verify_typeStr,otpStr;
    int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    Boolean clicked1 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_voice);
        speakerStr = getIntent().getStringExtra("phoneNumber");
        otpStr = getIntent().getStringExtra("otpCode");
        verify_typeStr = "16to16";
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_VVback);
        btnRecord = findViewById(R.id.btn_recordVV);
        btnStopRecord = findViewById(R.id.btn_stopRecordVV);
        tvResultPercent = findViewById(R.id.tv_resultPercent);
        tvResultText = findViewById(R.id.tv_resultText);
        tvGuide = findViewById(R.id.tv_guideVerification);
        btnCtn = findViewById(R.id.btn_ctnVV);
        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);

        btnCtn.setEnabled(false);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionFromDevice()){
//                    pathSave1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.3gp";
//                    pathSave11 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.wav";
//                    pathSavePCM = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.pcm";
//                    filePCM = new File(pathSavePCM);
//                    pathSaveWAV = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.wav";
//                    startRecording(pathSavePCM,pathSaveWAV);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
                        pathSavePCM = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.pcm";
                        pathSaveWAV = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) +"/"+UUID.randomUUID().toString() + "_audio_record.wav";

//                        file1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString()+ "_audio_record.wav");
//                        filePCM1 = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +UUID.randomUUID().toString() + "_audio_record.pcm");
                        startRecording2(pathSavePCM,pathSaveWAV);
                    } else {
                        pathSavePCM = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.pcm";
                        pathSaveWAV = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.wav";
                        startRecording(pathSavePCM,pathSaveWAV);
                    }

//                    setupVoiceRecorder(pathSave1);
//                    try {
//                        mediaRecorder.prepare();;
//                        mediaRecorder.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    btnRecord.setVisibility(View.INVISIBLE);
                    btnStopRecord.setVisibility(View.VISIBLE);
                    btnStopRecord.setEnabled(false);
                    btnBack.setEnabled(false);
                    Toast.makeText(VerifyVoiceActivity.this,"Đang ghi âm...",Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermission();
                }
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnRecord.setEnabled(true);
                            }
                        });
                    }
                }, 500);

                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        btnStopRecord.setEnabled(true);
                    }

                }.start();

            }

        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clicked1 = true;
                try {
                    stopRecording(pathSavePCM);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mediaRecorder.stop();
                btnStopRecord.setVisibility(View.INVISIBLE);
                btnCtn.setVisibility(View.VISIBLE);
                tvGuide.setVisibility(View.INVISIBLE);
                filePCM = new File(pathSavePCM);
                fileWAV = new File(pathSaveWAV);



                if (clicked1==true){
                    btnCtn.setEnabled(true);
                    btnBack.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnPlay.setEnabled(true);
                }






                System.out.println("file path: -----------" + fileWAV.getAbsolutePath());


                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnStopRecord.setEnabled(true);
                            }
                        });
                    }
                }, 500);

            }
        });
        btnCtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBack.setEnabled(false);
                btnStop.setEnabled(false);
                btnPlay.setEnabled(false);
//                VerifyCommunication verifyService = NetworkProvider.self().getRetrofit().create(VerifyCommunication.class);
//                RequestBody verify_type = RequestBody.create(MediaType.parse("multipart/form-data"),verify_typeStr);
//                RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"),speakerStr);
//                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileWAV);
//                MultipartBody.Part reqFile1 = MultipartBody.Part.createFormData("file", fileWAV.getName(), requestFile);
//                verifyService.verify(reqFile1,speaker,verify_type)
//                        .enqueue(new Callback<VerifyResponse>() {
//                            @Override
//                            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
//
//                                if (response.body().getStatus()==1){
//                                    Double score = Math.round(response.body().getScore() * 100.0) / 100.0;
//                                    tvResultPercent.setText("Độ chính xác: "+ score.toString()+"%");
//                                    btnCtn.setVisibility(View.INVISIBLE);
//
//                                    if(response.body().getResult().equalsIgnoreCase("true")){
//                                        tvResultText.setText("Kết quả chính xác.");
//                                    }
//                                    else {
//                                        tvResultText.setText("Kết quả chưa chính xác.");
//                                    }
//
//                                    tvResultPercent.setVisibility(View.VISIBLE);
//                                    // tvResultText.setVisibility(View.VISIBLE);
//                                }
//                                else {
//                                    tvResultText.setText("Thời gian thu âm quá ngắn.");
//                                    tvResultText.setVisibility(View.VISIBLE);
//                                    Toast.makeText(VerifyVoiceActivity.this,"Thất bại",Toast.LENGTH_SHORT).show();
//                                }
//                                System.out.println(response.body());
//                                btnBack.setEnabled(true);
//
//                                fileWAV.delete();
//                            }
//
//
//                            @Override
//                            public void onFailure(Call<VerifyResponse> call, Throwable t) {
//                                Toast.makeText(VerifyVoiceActivity.this,"Thất bại",Toast.LENGTH_SHORT).show();
//                                System.out.println(t);
//                                btnBack.setEnabled(true);
//                                fileWAV.delete();
//                                filePCM.delete();
//                            }
//
//                        });

                verifyVoiceId.setMyFileRecorder(fileWAV);
                verifyVoiceId.setMySpeakerStr(speakerStr);

                ResultResponse rR= verifyVoiceId.solveFile();
                if(rR!=null){
                    String rS = rR.getTextResult();
                    String rP = rR.getPercentResult();
                    if (rP==null){
                        tvResultPercent.setText("0%");
                    }else {
                        tvResultPercent.setText("Độ chính xác: "+ rP+"%");
                    }

                    btnCtn.setVisibility(View.INVISIBLE);
                    tvResultPercent.setVisibility(View.VISIBLE);
                    btnBack.setEnabled(true);
                    System.out.println("rS: " + rS + " " +rP);
                }else {
                    Toast.makeText(VerifyVoiceActivity.this,"Thất bại",Toast.LENGTH_SHORT).show();
                    btnBack.setEnabled(true);
                }




//                tvResultText.setText(verifyVoiceId.getResultTextResponse());





                Toast.makeText(VerifyVoiceActivity.this,"Đang kiểm tra",Toast.LENGTH_SHORT).show();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnCtn.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsertoMain();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnBack.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setEnabled(true);
                btnRecord.setEnabled(false);
                btnRecord.setVisibility(View.VISIBLE);
                btnStopRecord.setEnabled(false);
                try {
                    PlayShortAudioFileViaAudioTrack(pathSaveWAV);
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
                Toast.makeText(VerifyVoiceActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnPlay.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(true);
                btnRecord.setEnabled(true);
                btnRecord.setVisibility(View.VISIBLE);
                btnStop.setEnabled(false);
                btnPlay.setEnabled(true);


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
                                btnStop.setEnabled(true);
                            }
                        });
                    }
                }, 500);
            }

        });


    }

    private void sendUsertoMain() {
        Intent mainIntent = new Intent(VerifyVoiceActivity.this,MainActivity.class);
        mainIntent.putExtra("status",1);
        mainIntent.putExtra("phoneNumber",speakerStr);
        mainIntent.putExtra("otpCode",otpStr);

        mainIntent.setType(Settings.ACTION_SYNC_SETTINGS);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent mainIntent = new Intent(VerifyVoiceActivity.this,MainActivity.class);
        mainIntent.putExtra("status",1);
        mainIntent.putExtra("phoneNumber",speakerStr);
        mainIntent.putExtra("otpCode",otpStr);


        mainIntent.setType(Settings.ACTION_SYNC_SETTINGS);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
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



    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},REQUEST_PERMISSION_CODE);
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

}