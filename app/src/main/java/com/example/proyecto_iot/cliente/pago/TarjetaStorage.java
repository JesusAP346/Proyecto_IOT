package com.example.proyecto_iot.cliente.pago;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TarjetaStorage {

    private static final String PREF_NAME = "tarjetas_pref";
    private static final String KEY_TARJETAS = "tarjetas";

    public static void guardarTarjetas(Context context, List<Tarjeta> tarjetas) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(tarjetas);
        editor.putString(KEY_TARJETAS, json);
        editor.apply();
    }

    public static List<Tarjeta> obtenerTarjetas(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_TARJETAS, null);
        if (json != null) {
            Type type = new TypeToken<List<Tarjeta>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void eliminarTarjeta(Context context, Tarjeta tarjeta) {
        List<Tarjeta> tarjetas = obtenerTarjetas(context);
        tarjetas.removeIf(t -> t.getNumero().equals(tarjeta.getNumero())); // Usa un identificador único
        guardarTarjetas(context, tarjetas);
    }

}
