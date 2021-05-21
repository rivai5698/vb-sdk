package com.example.mylibrary;

public class ResultResponse {
    String textResult;
    String percentResult;

    public ResultResponse() {
    }

    public ResultResponse(String textResult, String percentResult) {
        this.textResult = textResult;
        this.percentResult = percentResult;
    }

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }

    public String getPercentResult() {
        return percentResult;
    }

    public void setPercentResult(String percentResult) {
        this.percentResult = percentResult;
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
                "textResult='" + textResult + '\'' +
                ", percentResult='" + percentResult + '\'' +
                '}';
    }
}
