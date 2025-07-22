package com.example.proyecto_iot.taxista.qr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.Notificacion;
import com.example.proyecto_iot.taxista.perfil.NotificacionDTO;
import com.example.proyecto_iot.taxista.perfil.NotificacionUtils;
import com.example.proyecto_iot.taxista.solicitudes.SolicitudesFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class QrFragment extends Fragment {

    private ActivityResultLauncher<ScanOptions> qrLauncher;
    private static final String CANAL_ID = "canal_viajes";
    private static final String FILE_NOTIFICACIONES = "notificaciones.json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                regresarASolicitudes();
            }
        });

        qrLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (!isAdded()) return;

            if (result.getContents() != null) {
                String contenido = result.getContents();
                Log.d("QrFragment", "Contenido escaneado: " + contenido);

                if (contenido.startsWith("http://") || contenido.startsWith("https://")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contenido)));
                } else if (contenido.startsWith("serviciotaxi:")) {
                    final String idServicio = contenido
                            .substring("serviciotaxi:".length())
                            .trim()
                            .replaceAll("[^a-zA-Z0-9]", ""); // ← declarado como final directamente

                    if (idServicio.isEmpty()) {
                        Toast.makeText(requireContext(), "ID de servicio vacío o inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("QrFragment", "Intentando finalizar servicio con ID: " + idServicio);

                    FirebaseFirestore.getInstance().collection("servicios_taxi")
                            .document(idServicio)
                            .update("estado", "finalizado")
                            .addOnSuccessListener(aVoid -> {
                                String mensaje = "Servicio #" + idServicio + " finalizado con éxito.";
                                Context context = requireContext();
                                lanzarNotificacion(context, mensaje);
                                guardarNotificacionEnStorage(context, new Notificacion(mensaje, obtenerHoraActual(), R.drawable.ic_qr));
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                                regresarASolicitudes();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("QrFragment", "Error al finalizar el servicio", e);
                                Toast.makeText(requireContext(), "Error al finalizar el servicio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                } else {
                    Toast.makeText(getContext(), "Código QR no válido: " + contenido, Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                regresarASolicitudes();
            }
        });

        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanea un código QR para finalizar el viaje");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CustomCaptureActivity.class);
        qrLauncher.launch(options);
    }

    private void regresarASolicitudes() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new SolicitudesFragment())
                .commit();

        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).binding.bottomNavigationView.setSelectedItemId(R.id.solicitudes);
        }
    }

    private void lanzarNotificacion(Context context, String mensaje) {
        crearCanalNotificacion(context);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_qr)
                .setContentTitle("QR Escaneado")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1002, builder.build());
        }
    }

    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null && manager.getNotificationChannel(CANAL_ID) == null) {
                NotificationChannel canal = new NotificationChannel(
                        CANAL_ID, "Notificaciones de Viajes", NotificationManager.IMPORTANCE_DEFAULT);
                canal.setDescription("Notificaciones de escaneo QR");
                manager.createNotificationChannel(canal);
            }
        }
    }

    private void guardarNotificacionEnStorage(Context context, Notificacion notificacion) {
        List<Notificacion> lista = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NOTIFICACIONES);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }

            Type listType = new TypeToken<List<NotificacionDTO>>() {}.getType();
            List<NotificacionDTO> dtoList = new Gson().fromJson(sb.toString(), listType);
            lista = NotificacionUtils.fromDTOList(dtoList);

        } catch (Exception ignored) {}

        lista.add(notificacion);
        List<NotificacionDTO> dtoList = new ArrayList<>();
        for (Notificacion n : lista) {
            dtoList.add(new NotificacionDTO(n.getMensaje(), n.getHora(), n.getIconoResId()));
        }

        String json = new Gson().toJson(dtoList);

        try (FileOutputStream fos = context.openFileOutput(FILE_NOTIFICACIONES, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
