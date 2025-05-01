package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.proyecto_iot.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusquedaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusquedaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BusquedaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusquedaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusquedaFragment newInstance(String param1, String param2) {
        BusquedaFragment fragment = new BusquedaFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        // Referencias a los TextViews
        TextView txtFechas = view.findViewById(R.id.txtFechas);       // Asegúrate de que este ID exista
        TextView txtHuespedes = view.findViewById(R.id.txtHuespedes); // Asegúrate de que este ID exista

        // Abrir el diálogo de fechas
        txtFechas.setOnClickListener(v -> {
            FechaDialogFragment fechaDialog = new FechaDialogFragment();
            fechaDialog.show(getChildFragmentManager(), "fechaDialog");
        });

        // Abrir el diálogo de huéspedes
        txtHuespedes.setOnClickListener(v -> {
            HuespedDialogFragment huespedDialog = new HuespedDialogFragment();
            huespedDialog.show(getChildFragmentManager(), "huespedDialog");
        });
        return view;
    }
}