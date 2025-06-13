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

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.modoTaxistaFormulario.RegistroTaxistaActivity;
import com.example.proyecto_iot.login.LoginActivity;
import com.example.proyecto_iot.taxista.perfil.InformacionPersonalActivity;
import com.example.proyecto_iot.taxista.perfil.PerfilTaxistaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilFragmentC extends Fragment {

    private Button btnModoCliente;
    private Button btnCerrarSesion;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Inicializar botones
        btnModoCliente = view.findViewById(R.id.btnModoCliente);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // Listener para cambiar a modo taxista
        btnModoCliente.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "No se encontró sesión activa", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String idRol = documentSnapshot.getString("idRol");
                            Object estadoSolicitud = documentSnapshot.get("estadoSolicitudTaxista"); // puede ser null

                            if ("Cliente".equalsIgnoreCase(idRol) && estadoSolicitud == null) {
                                Intent intent = new Intent(getContext(), RegistroTaxistaActivity.class);
                                startActivity(intent);
                            } else if ("Taxista".equals(idRol)) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Ya enviaste una solicitud o no tienes permiso para registrarte como taxista", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Datos de usuario no encontrados", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    });
        });



        // Listener para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            cerrarSesion();
        });

        // Listeners para otras opciones
        view.findViewById(R.id.informacionPersonal).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Abrir información personal", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), InfoPersonal.class);
            startActivity(intent);
        });

        view.findViewById(R.id.seguridadPersonal).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Abrir seguridad", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), SeguridadActivity2.class);
            startActivity(intent);
        });

        return view;
    }

    private void cerrarSesion() {


        auth.signOut();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}