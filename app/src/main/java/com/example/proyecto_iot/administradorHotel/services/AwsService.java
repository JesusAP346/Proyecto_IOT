package com.example.proyecto_iot.administradorHotel.services;

import retrofit2.Call;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AwsService {

    //MultipartBody.Part es el tipo de objeto que representa el archivo binario que vas a subir
    @Multipart
    @POST("upload")
    Call<UploadResponse> subirImagen(@Part MultipartBody.Part file);



}
