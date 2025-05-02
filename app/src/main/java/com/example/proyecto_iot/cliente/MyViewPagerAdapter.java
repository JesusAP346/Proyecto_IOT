package com.example.proyecto_iot.cliente;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 3; // Dos fragmentos (Búsqueda y Favoritos)
    }
/*ESTA PARTE IDK
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new SearchFragment() : new FavoritesFragment();
    }*/

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new FavoritesFragment();
            case 2:
                return new NotificacionesFragment();  // ← Asegúrate de tener este importado
            default:
                return new Fragment(); // Fallback por si algo sale mal
        }
    }

}
