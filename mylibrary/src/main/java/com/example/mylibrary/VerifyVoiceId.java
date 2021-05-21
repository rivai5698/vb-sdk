package com.example.mylibrary;

import com.example.mylibrary.Connect.NetworkProvider;
import com.example.mylibrary.Connect.VerifyCommunication;
import com.example.mylibrary.Connect.VerifyResponse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyVoiceId {
    String verify_typeStr="16to16";

    String mySpeakerStr;
    File myFileRecorder;


    public void setMySpeakerStr(String mySpeakerStr) {
        this.mySpeakerStr = mySpeakerStr;
    }

    public void setMyFileRecorder(File myFileRecorder) {
        this.myFileRecorder = myFileRecorder;
    }

    public ResultResponse solveFile(){
        ExecutorService threadPoll = Executors.newFixedThreadPool(1);
        Future<ResultResponse> future = threadPoll.submit(new Callable<ResultResponse>() {
            @Override
            public ResultResponse call() throws Exception {
                VerifyCommunication verifyService = NetworkProvider.self().getRetrofit().create(VerifyCommunication.class);
                RequestBody verify_type = RequestBody.create(MediaType.parse("multipart/form-data"),verify_typeStr);
                RequestBody speaker = RequestBody.create(MediaType.parse("multipart/form-data"),mySpeakerStr);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), myFileRecorder);
                MultipartBody.Part reqFile1 = MultipartBody.Part.createFormData("file", myFileRecorder.getName(), requestFile);
                Call<VerifyResponse> call = verifyService.verify(reqFile1,speaker,verify_type);
                Response<VerifyResponse> response= call.execute();
                ResultResponse responseResult = new ResultResponse();
                if(response.isSuccessful()){
                    System.out.println("res in: "+response.body());

                    if (response.body().getStatus()==1){
                        Double score = Math.round(response.body().getScore() * 100.0) / 100.0;
                        responseResult.setPercentResult(score.toString());
                        responseResult.setTextResult(response.body().getResult());
                    }
                    else {
                        responseResult.setTextResult("File Error.");
                    }
                }else{
                    responseResult.setTextResult("System Error");
                }
                return responseResult;
            }
        });
        ResultResponse responseResult = null;
        try {
            responseResult = future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResultResponse();
            responseResult.setTextResult("System Error");
        }
        System.out.println("Result: "+ responseResult);
        return responseResult;
    }



//
//    @Override
//    public void takeRecordFile(File recordFile, String speakerStr) {
//        myFileRecorder = recordFile;
//        mySpeakerStr = speakerStr;
//    }


}


