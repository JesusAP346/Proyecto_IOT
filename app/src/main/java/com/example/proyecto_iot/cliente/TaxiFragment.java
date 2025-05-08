package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import java.util.ArrayList;
import java.util.List;

public class TaxiFragment extends Fragment {

    public TaxiFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxi, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSolicitudesTaxi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Simular datos de prueba
        List<TaxiItem> lista = new ArrayList<>();
        lista.add(new TaxiItem("ABC-123", "Juancito Pérez", "Aeropuerto Internacional Jorge Chavez", R.drawable.roberto, true));
        lista.add(new TaxiItem("DEF-132", "Ppeito Pepin", "Aeropuerto Internacional De Chile", R.drawable.roberto, false));

        TaxiAdapter adapter = new TaxiAdapter(lista, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }
}