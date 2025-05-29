package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class HotelPreferences {

    private static final String PREF_NAME = "hotel_favoritos";
    private static final String KEY_FAVORITOS = "favoritos";

    public static void guardarFavorito(Context context, int hotelId, boolean esFavorito) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favoritos = new HashSet<>(prefs.getStringSet(KEY_FAVORITOS, new HashSet<>()));

        if (esFavorito) {
            favoritos.add(String.valueOf(hotelId));
        } else {
            favoritos.remove(String.valueOf(hotelId));
        }

        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply();
    }

    public static boolean esFavorito(Context context, int hotelId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favoritos = prefs.getStringSet(KEY_FAVORITOS, new HashSet<>());
        return favoritos.contains(String.valueOf(hotelId));
    }
}

