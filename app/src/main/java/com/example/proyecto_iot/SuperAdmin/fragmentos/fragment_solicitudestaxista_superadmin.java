package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.UsuariosDataStore;
import com.example.proyecto_iot.SuperAdmin.adapter.SolicitudTaxistaAdapter;
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_solicitudestaxista_superadmin#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class fragment_solicitudestaxista_superadmin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_solicitudestaxista_superadmin.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_solicitudestaxista_superadmin newInstance(String param1, String param2) {
        fragment_solicitudestaxista_superadmin fragment = new fragment_solicitudestaxista_superadmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public fragment_solicitudestaxista_superadmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private RecyclerView recyclerView;
    private SolicitudTaxistaAdapter adapter;
    private List<UsuariosDomain> solicitudes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudestaxista_superadmin, container, false);

        recyclerView = view.findViewById(R.id.rv_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarSolicitudes(); // Carga inicial

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarSolicitudes(); // Se ejecuta cada vez que se vuelve al fragmento
    }

    private void cargarSolicitudes() {
        solicitudes = new ArrayList<>();
        for (UsuariosDomain u : UsuariosDataStore.usuariosList) {
            if (u.getRol().equalsIgnoreCase("Pendiente")) {
                solicitudes.add(u);
            }
        }

        adapter = new SolicitudTaxistaAdapter(solicitudes);
        recyclerView.setAdapter(adapter);
    }



}