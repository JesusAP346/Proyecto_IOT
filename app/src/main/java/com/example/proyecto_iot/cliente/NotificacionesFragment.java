package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Notificacion> notis = new ArrayList<>();
        notis.add(new Notificacion("El cobro se ha realizado con éxito"));
        notis.add(new Notificacion("Se generó QR para el servicio de taxi. Puede verlo en la sección Taxi"));
        notis.add(new Notificacion("El cobro se ha realizado con éxito"));

        NotificacionesAdapter adapter = new NotificacionesAdapter(notis);
        recyclerView.setAdapter(adapter);

        return view;
    }

}

