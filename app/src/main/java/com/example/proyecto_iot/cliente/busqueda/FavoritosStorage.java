package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoritosStorage {
    private static final String PREF_NAME = "favoritos_pref";
    private static final String KEY_FAVORITOS = "hoteles_favoritos";

    public static void guardarFavoritos(Context context, Set<Integer> idsFavoritos) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> idStrings = new HashSet<>();
        for (int id : idsFavoritos) {
            idStrings.add(String.valueOf(id));
        }
        prefs.edit().putStringSet(KEY_FAVORITOS, idStrings).apply();
    }

    public static Set<Integer> obtenerFavoritos(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> idStrings = prefs.getStringSet(KEY_FAVORITOS, new HashSet<>());
        Set<Integer> ids = new HashSet<>();
        for (String idStr : idStrings) {
            ids.add(Integer.parseInt(idStr));
        }
        return ids;
    }
}
