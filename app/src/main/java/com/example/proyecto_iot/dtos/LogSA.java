package com.example.proyecto_iot.dtos;
import java.util.Date;

public class LogSA {

    private String IdLog;
    private String titulo;
    private String mensaje;
    private String nombreEditor;



    private String rolEditor;
    private String uidUsuario;
    private String nombreUsuario;
    private Date timestamp;
    private String tipoLog;
    public LogSA() {} // Necesario para Firestore

    public LogSA(String IdLog, String titulo, String mensaje, String nombreEditor, String rolEditor, String uidUsuario, String nombreUsuario, Date timestamp, String tipoLog) {
        this.titulo = titulo;
        this.IdLog = IdLog;
        this.nombreEditor = nombreEditor;
        this.mensaje = mensaje;
        this.uidUsuario = uidUsuario;
        this.nombreUsuario = nombreUsuario;
        this.timestamp = timestamp;
        this.rolEditor = rolEditor;
        this.tipoLog = tipoLog;
    }

    public String getRolEditor() {
        return rolEditor;
    }

    public void setRolEditor(String rolEditor) {
        this.rolEditor = rolEditor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getIdLog() {
        return IdLog;
    }

    public void setIdLog(String idLog) {
        IdLog = idLog;
    }

    public String getNombreEditor() {
        return nombreEditor;
    }

    public void setNombreEditor(String nombreEditor) {
        this.nombreEditor = nombreEditor;
    }
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

