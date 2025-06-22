package com.example.proyecto_iot.administradorHotel.entity;

import android.graphics.Bitmap;
import android.net.Uri;

public class FotoItem {

    public Uri uri;
    public Bitmap bitmap;
    public boolean esDeCamara;

    public FotoItem(Uri uri) {
        this.uri = uri;
        this.esDeCamara = false;
    }

    public FotoItem(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.esDeCamara = true;
    }
}
