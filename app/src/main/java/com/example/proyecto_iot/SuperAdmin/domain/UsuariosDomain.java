package com.example.proyecto_iot.SuperAdmin.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class UsuariosDomain implements Parcelable {
    private String nombre;
    private String numeroTelefono;
    private String imagenPerfil;
    private String correo;
    private String direccion;
    private String fechaNacimiento;
    private String rol;
    private String documentoIdentidad;
    private int habitacionesRegistradas;
    private String estadoCuenta;
    private String totalViajes;
    private String calificacion;

    // Constructor completo
    public UsuariosDomain(String nombre, String numeroTelefono, String imagenPerfil,
                          String correo, String direccion, String fechaNacimiento, String rol,
                          String documentoIdentidad, int habitacionesRegistradas, String estadoCuenta,
                          String totalViajes, String calificacion) {
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.imagenPerfil = imagenPerfil;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.documentoIdentidad = documentoIdentidad;
        this.habitacionesRegistradas = habitacionesRegistradas;
        this.estadoCuenta = estadoCuenta;
        this.totalViajes = totalViajes;
        this.calificacion = calificacion;
    }
    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public void setHabitacionesRegistradas(int habitacionesRegistradas) {
        this.habitacionesRegistradas = habitacionesRegistradas;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public void setTotalViajes(String totalViajes) {
        this.totalViajes = totalViajes;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getNumeroTelefono() { return numeroTelefono; }
    public String getImagenPerfil() { return imagenPerfil; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getRol() { return rol; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public int getHabitacionesRegistradas() { return habitacionesRegistradas; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public String getTotalViajes() { return totalViajes; }
    public String getCalificacion() { return calificacion; }

    // Parcelable
    protected UsuariosDomain(Parcel in) {
        nombre = in.readString();
        numeroTelefono = in.readString();
        imagenPerfil = in.readString();
        correo = in.readString();
        direccion = in.readString();
        fechaNacimiento = in.readString();
        rol = in.readString();
        documentoIdentidad = in.readString();
        habitacionesRegistradas = in.readInt();
        estadoCuenta = in.readString();
        totalViajes = in.readString();
        calificacion = in.readString();
    }

    public static final Creator<UsuariosDomain> CREATOR = new Creator<UsuariosDomain>() {
        @Override
        public UsuariosDomain createFromParcel(Parcel in) {
            return new UsuariosDomain(in);
        }

        @Override
        public UsuariosDomain[] newArray(int size) {
            return new UsuariosDomain[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(numeroTelefono);
        parcel.writeString(imagenPerfil);
        parcel.writeString(correo);
        parcel.writeString(direccion);
        parcel.writeString(fechaNacimiento);
        parcel.writeString(rol);
        parcel.writeString(documentoIdentidad);
        parcel.writeInt(habitacionesRegistradas);
        parcel.writeString(estadoCuenta);
        parcel.writeString(totalViajes);
        parcel.writeString(calificacion);
    }
}
