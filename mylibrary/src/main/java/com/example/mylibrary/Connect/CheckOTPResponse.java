package com.example.mylibrary.Connect;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CheckOTPResponse {
    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;

    public CheckOTPResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CheckOTPResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
