package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.InformacionPersonalActivity;
import com.example.proyecto_iot.taxista.perfil.PerfilTaxistaActivity;

public class PerfilFragmentC extends Fragment {

    private Button btnModoCliente;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        btnModoCliente = view.findViewById(R.id.btnModoCliente);

        btnModoCliente.setOnClickListener(v -> {
            // Aquí cambias el modo a taxista, por ejemplo:
            Toast.makeText(getContext(), "Cambiando a modo taxista...", Toast.LENGTH_SHORT).show();

            // Lógica para cambiar de modo:
            // Puede ser abrir una Activity taxista, o guardar preferencia y reiniciar app
            //startActivity(new Intent(getContext(), InformacionPersonalActivity.class));
        });

        // Puedes agregar listeners a otros elementos como "informacionPersonal", "seguridadPersonal"
        view.findViewById(R.id.informacionPersonal).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Abrir información personal", Toast.LENGTH_SHORT).show();
            // Abrir fragmento o actividad de info personal
            // Crear el Intent para abrir la Activity SeguridadActivity
            Intent intent = new Intent(getContext(), InfoPersonal.class);

            // Iniciar la Activity
            startActivity(intent);
        });

        view.findViewById(R.id.seguridadPersonal).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Abrir seguridad", Toast.LENGTH_SHORT).show();

            // Crear el Intent para abrir la Activity SeguridadActivity
            Intent intent = new Intent(getContext(), SeguridadActivity2.class);

            // Iniciar la Activity
            startActivity(intent);
        });


        return view;
    }
}
