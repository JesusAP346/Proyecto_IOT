package com.example.proyecto_iot.taxista.perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.databinding.FragmentPerfilBinding;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> editarPerfilLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Mostrar datos cacheados inmediatamente
        mostrarDatosDesdeCache();

        // Launcher para editar perfil
        editarPerfilLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        recargarDatosDesdeFirestore();
                    }
                });

        // Siempre actualizar en segundo plano
        recargarDatosDesdeFirestore();

        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        binding.informacionPersonal.setOnClickListener(v -> startActivity(new Intent(requireContext(), InformacionPersonalActivity.class)));
        binding.seguridadPersonal.setOnClickListener(v -> startActivity(new Intent(requireContext(), SeguridadActivity.class)));
        binding.cardPerfil.setOnClickListener(v -> editarPerfilLauncher.launch(new Intent(requireContext(), PerfilTaxistaActivity.class)));
        binding.btnModoCliente.setOnClickListener(v -> startActivity(new Intent(requireContext(), ClienteBusquedaActivity.class)));

        // Notificaciones
        int cantidad = obtenerCantidadNotificaciones();
        if (cantidad > 0) {
            binding.notificacionBadge.setText(String.valueOf(cantidad));
            binding.notificacionBadge.setVisibility(View.VISIBLE);
        } else {
            binding.notificacionBadge.setVisibility(View.GONE);
        }

        binding.iconoCampana.setOnClickListener(v -> {
            Fragment notificacionesTaxistaFragment = new NotificacionesTaxistaFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, notificacionesTaxistaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void mostrarDatosDesdeCache() {
        SharedPreferences prefs = requireContext().getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String nombreCompleto = prefs.getString("nombreCompleto", "Cargando...");
        String urlFotoPerfil = prefs.getString("urlFotoPerfil", "");

        TextView nombreTextView = binding.cardPerfil.findViewById(R.id.tituloNombre);
        nombreTextView.setText(nombreCompleto);

        if (!urlFotoPerfil.isEmpty()) {
            Picasso.get()
                    .load(urlFotoPerfil)
                    .placeholder(R.drawable.ic_perfil_circulo)
                    .error(R.drawable.ic_perfil_circulo)
                    .into(binding.ivFotoPerfil);
        } else {
            binding.ivFotoPerfil.setImageResource(R.drawable.ic_perfil_circulo);
        }
    }

    private void recargarDatosDesdeFirestore() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombres = documentSnapshot.getString("nombres");
                String apellidos = documentSnapshot.getString("apellidos");
                String nombreCompleto = nombres + " " + apellidos;
                String urlFotoPerfil = documentSnapshot.getString("urlFotoPerfil");

                Log.d("PERFIL_FRAGMENT", "URL recibida de Firestore: " + urlFotoPerfil);

                // Actualizar UI
                TextView nombreTextView = binding.cardPerfil.findViewById(R.id.tituloNombre);
                nombreTextView.setText(nombreCompleto);

                if (urlFotoPerfil != null && !urlFotoPerfil.isEmpty()) {
                    Picasso.get()
                            .load(urlFotoPerfil)
                            .placeholder(R.drawable.ic_perfil_circulo)
                            .error(R.drawable.ic_perfil_circulo)
                            .into(binding.ivFotoPerfil);
                }

                // Guardar en cache
                SharedPreferences.Editor editor = requireContext()
                        .getSharedPreferences("perfil", Context.MODE_PRIVATE)
                        .edit();
                editor.putString("nombreCompleto", nombreCompleto);
                editor.putString("urlFotoPerfil", urlFotoPerfil);
                editor.apply();
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void cerrarSesion() {
        auth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    private int obtenerCantidadNotificaciones() {
        try {
            FileInputStream fis = requireContext().openFileInput("notificaciones.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) sb.append(linea);
            Type listType = new TypeToken<List<NotificacionDTO>>() {}.getType();
            List<NotificacionDTO> dtoList = new Gson().fromJson(sb.toString(), listType);
            return dtoList != null ? dtoList.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
