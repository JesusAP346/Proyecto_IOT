package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.TaxistasDataStore;
import com.example.proyecto_iot.SuperAdmin.adapter.TaxistasAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_taxistas_superadmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_taxistas_superadmin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_taxistas_superadmin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_taxistas_superadmin.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_taxistas_superadmin newInstance(String param1, String param2) {
        fragment_taxistas_superadmin fragment = new fragment_taxistas_superadmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxistas_superadmin, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TaxistasAdapter adapter = new TaxistasAdapter(TaxistasDataStore.taxistasList);
        recyclerView.setAdapter(adapter);

        Button btnSolicitudes = view.findViewById(R.id.solicitudes);

        btnSolicitudes.setOnClickListener(v -> {
            fragment_solicitudestaxista_superadmin nuevoFragment = new fragment_solicitudestaxista_superadmin();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, nuevoFragment) // <- ahora sí es el ID correcto
                    .addToBackStack(null)
                    .commit();
        });




        return view;
    }

}