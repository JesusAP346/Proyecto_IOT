package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;

public class RegistroInformacionAutoFragment extends Fragment {


    public RegistroInformacionAutoFragment() {
        super(R.layout.fragment_registro_informacion_auto);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro_informacion_auto, container, false);
    }
}