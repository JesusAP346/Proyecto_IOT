package com.example.proyecto_iot.SuperAdmin.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class TaxistaDomain implements Parcelable {
    private String nombre;
    private String numeroTelefono;
    private String imagenPerfil;
    private String correo;
    private String estadoCuenta;
    private String indiceViajes;
    private double calificacion;
    private String colorAuto;
    private String modeloAuto;
    private String placaAuto;
    private String imagenAuto;

    public TaxistaDomain(String nombre, String numeroTelefono, String imagenPerfil, String correo, String estadoCuenta,
                         String indiceViajes, double calificacion, String colorAuto, String modeloAuto, String placaAuto, String imagenAuto) {
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.imagenPerfil = imagenPerfil;
        this.correo = correo;
        this.estadoCuenta = estadoCuenta;
        this.indiceViajes = indiceViajes;
        this.calificacion = calificacion;
        this.colorAuto = colorAuto;
        this.modeloAuto = modeloAuto;
        this.placaAuto = placaAuto;
        this.imagenAuto = imagenAuto;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getNumeroTelefono() { return numeroTelefono; }
    public String getImagenPerfil() { return imagenPerfil; }
    public String getCorreo() { return correo; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public String getIndiceViajes() { return indiceViajes; }
    public double getCalificacion() { return calificacion; }
    public String getColorAuto() { return colorAuto; }
    public String getModeloAuto() { return modeloAuto; }
    public String getPlacaAuto() { return placaAuto; }
    public String getImagenAuto() { return imagenAuto; }

    // Setters
    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    // Parcelable implementation
    protected TaxistaDomain(Parcel in) {
        nombre = in.readString();
        numeroTelefono = in.readString();
        imagenPerfil = in.readString();
        correo = in.readString();
        estadoCuenta = in.readString();
        indiceViajes = in.readString();
        calificacion = in.readDouble();
        colorAuto = in.readString();
        modeloAuto = in.readString();
        placaAuto = in.readString();
        imagenAuto = in.readString();
    }

    public static final Creator<TaxistaDomain> CREATOR = new Creator<TaxistaDomain>() {
        @Override
        public TaxistaDomain createFromParcel(Parcel in) {
            return new TaxistaDomain(in);
        }

        @Override
        public TaxistaDomain[] newArray(int size) {
            return new TaxistaDomain[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(numeroTelefono);
        dest.writeString(imagenPerfil);
        dest.writeString(correo);
        dest.writeString(estadoCuenta);
        dest.writeString(indiceViajes);
        dest.writeDouble(calificacion);
        dest.writeString(colorAuto);
        dest.writeString(modeloAuto);
        dest.writeString(placaAuto);
        dest.writeString(imagenAuto);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
