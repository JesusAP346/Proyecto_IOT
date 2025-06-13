package com.example.proyecto_iot.taxista.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.databinding.FragmentPerfilBinding;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    private Button btnCerrarSesion;
    private FirebaseAuth auth;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        auth = FirebaseAuth.getInstance();

        btnCerrarSesion.setOnClickListener(v -> {
            cerrarSesion();
        });

        cargarImagenInterna(); // carga la foto guardada en interno

        // Actualizar badge de notificaciones
        int cantidad = obtenerCantidadNotificaciones();
        if (cantidad > 0) {
            binding.notificacionBadge.setText(String.valueOf(cantidad));
            binding.notificacionBadge.setVisibility(View.VISIBLE);
        } else {
            binding.notificacionBadge.setVisibility(View.GONE);
        }

        // Listener para abrir fragmento de notificaciones
        binding.iconoCampana.setOnClickListener(v -> {
            Fragment notificacionesTaxistaFragment = new NotificacionesTaxistaFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, notificacionesTaxistaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Listener para información personal
        binding.informacionPersonal.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), InformacionPersonalActivity.class));
        });

        // Listener para seguridad
        binding.seguridadPersonal.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SeguridadActivity.class));
        });

        // Listener para perfil del taxista
        binding.cardPerfil.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PerfilTaxistaActivity.class));
        });

        // Listener para cambiar a modo cliente
        binding.btnModoCliente.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ClienteBusquedaActivity.class));
        });

        return view;
    }

    private void cargarImagenInterna() {
        try {
            String filename = "perfil_taxista.jpg";
            File file = new File(requireContext().getFilesDir(), filename);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                binding.ivFotoPerfil.setImageURI(uri);
            } else {
                binding.ivFotoPerfil.setImageResource(R.drawable.roberto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.ivFotoPerfil.setImageResource(R.drawable.roberto);
        }
    }

    public void abrirEditarPerfil() {
        startActivity(new Intent(requireContext(), InformacionPersonalActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // evitar memory leaks
    }

    //storage:
    private static final String FILE_NOTIFICACIONES = "notificaciones.json";

    private int obtenerCantidadNotificaciones() {
        int cantidad = 0;
        try {
            FileInputStream fis = requireContext().openFileInput(FILE_NOTIFICACIONES);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            br.close();

            String json = sb.toString();

            Type listType = new TypeToken<List<NotificacionDTO>>() {}.getType();
            List<NotificacionDTO> dtoList = new Gson().fromJson(json, listType);

            cantidad = dtoList != null ? dtoList.size() : 0;

        } catch (Exception e) {
            // archivo puede no existir o estar vacío
            cantidad = 0;
        }
        return cantidad;
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
