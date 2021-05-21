package com.example.mylibrary.Connect;

import com.google.gson.annotations.SerializedName;

public class VerifyResponse {
    @SerializedName("id")
    private Integer embedding_path;
    @SerializedName("result")
    private String result;
    @SerializedName("raw_score")
    private String raw_score;
    @SerializedName("score")
    private Float score;
    @SerializedName("verify_status")
    private Integer status;


    public VerifyResponse(Integer embedding_path, String result, String raw_score, Float score, Integer status) {
        this.embedding_path = embedding_path;
        this.result = result;
        this.raw_score = raw_score;
        this.score = score;
        this.status = status;
    }

    public Integer getEmbedding_path() {
        return embedding_path;
    }

    public void setEmbedding_path(Integer embedding_path) {
        this.embedding_path = embedding_path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRaw_score() {
        return raw_score;
    }

    public void setRaw_score(String raw_score) {
        this.raw_score = raw_score;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "VerifyResponse{" +
                "embedding_path=" + embedding_path +
                ", result='" + result + '\'' +
                ", raw_score='" + raw_score + '\'' +
                ", score=" + score +
                ", status=" + status +
                '}';
    }
}
