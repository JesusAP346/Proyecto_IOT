package com.example.proyecto_iot.taxista.qr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QrFragment extends Fragment {

    private ActivityResultLauncher<ScanOptions> qrLauncher;

    public QrFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qrLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String contenido = result.getContents();

                if (contenido.startsWith("http://") || contenido.startsWith("https://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contenido));
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Contenido: " + contenido, Toast.LENGTH_LONG).show();
                }

            } else {
                // Escaneo cancelado → volver a SolicitudesFragment
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new com.example.proyecto_iot.taxista.solicitudes.SolicitudesFragment())
                        .commit();

                // También actualizar el ítem seleccionado en el BottomNavigationView
                ((MainActivity) requireActivity()).binding.bottomNavigationView.setSelectedItemId(R.id.solicitudes);
            }
        });



        // Lanza automáticamente el escáner al entrar al fragmento
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanea un código QR para finalizar el viaje");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CustomCaptureActivity.class); // tu clase personalizada con linterna
        qrLauncher.launch(options);
    }
}
