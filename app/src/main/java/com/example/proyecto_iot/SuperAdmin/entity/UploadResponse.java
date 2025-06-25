package com.example.proyecto_iot.SuperAdmin.entity;

public class UploadResponse {
    private boolean success;
    private String url;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }
}