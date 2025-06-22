package com.example.proyecto_iot.administradorHotel.entity;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("url")
    private String url;

    @SerializedName("message")
    private String message;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileSize")
    private long fileSize;

    @SerializedName("error")
    private String error;

    // Constructores
    public UploadResponse() {}

    public UploadResponse(boolean success, String url, String message) {
        this.success = success;
        this.url = url;
        this.message = message;
    }

    // Getters y setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "UploadResponse{" +
                "success=" + success +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", error='" + error + '\'' +
                '}';
    }
}
