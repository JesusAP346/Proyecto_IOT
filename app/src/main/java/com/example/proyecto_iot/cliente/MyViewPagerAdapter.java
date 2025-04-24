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
        return 2; // Dos fragmentos (Búsqueda y Favoritos)
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new SearchFragment() : new FavoritesFragment();
    }
}
