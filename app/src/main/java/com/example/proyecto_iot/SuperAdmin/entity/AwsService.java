package com.example.proyecto_iot.SuperAdmin.entity; // o network si decides moverlo

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AwsService {
    @Multipart
    @POST("upload")  // Solo el path, ya que el dominio va en Retrofit
    Call<UploadResponse> subirImagen(@Part MultipartBody.Part file);
}
