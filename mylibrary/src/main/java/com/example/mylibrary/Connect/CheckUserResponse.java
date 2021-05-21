package com.example.mylibrary.Connect;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data

public class CheckUserResponse {
    @SerializedName("status")
    private Integer status;

    public CheckUserResponse(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CheckUserResponse{" +
                "status=" + status +
                '}';
    }
}
