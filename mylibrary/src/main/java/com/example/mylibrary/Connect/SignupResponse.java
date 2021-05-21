package com.example.mylibrary.Connect;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data

public class SignupResponse {
    @SerializedName("embedding_path")
    private String embedding_path;
    @SerializedName("status")
    private Integer status;

    SignupResponse(String embedding_path, Integer status) {
        this.embedding_path = embedding_path;
        this.status = status;
    }

    @Override
    public String toString() {
        return "SignupResponse{" +
                "embedding_path='" + embedding_path + '\'' +
                ", status=" + status +
                '}';
    }

    public String getEmbedding_path() {
        return embedding_path;
    }

    public void setEmbedding_path(String embedding_path) {
        this.embedding_path = embedding_path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

