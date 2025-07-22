package com.example.proyecto_iot;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.databinding.ActivityMainBinding;
import com.example.proyecto_iot.taxista.perfil.PerfilFragment;
import com.example.proyecto_iot.taxista.qr.QrFragment;
import com.example.proyecto_iot.taxista.solicitudes.SolicitudesFragment;

public class MainActivity extends AppCompatActivity {

    public static final String CANAL_ID = "canal_viajes";
    private static final int CODIGO_PERMISOS_INICIALES = 200;
    private static final int CODIGO_NOTIFICACIONES = 999;
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        crearCanalNotificacion();
        pedirPermisosNotificacionPrimeroYLuegoOtros();

        // Logs para ver el estado actual de los permisos
        Log.d("PERMISOS", "CAMERA: " +
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED));
        Log.d("PERMISOS", "LOCATION_FINE: " +
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
        Log.d("PERMISOS", "LOCATION_COARSE: " +
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));

        replaceFragment(new SolicitudesFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.perfil) {
                replaceFragment(new PerfilFragment());

            } else if (itemId == R.id.qr) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, 102);

                    Toast.makeText(this,
                            "Debes aceptar el permiso de cámara para usar el lector QR",
                            Toast.LENGTH_LONG).show();

                    return false;
                }

                replaceFragment(new QrFragment());

            } else if (itemId == R.id.solicitudes) {
                replaceFragment(new SolicitudesFragment());
            }

            return true;
        });

    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "Notificaciones de Viajes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            canal.setDescription("Notificaciones sobre viajes aceptados");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    private void pedirPermisosNotificacionPrimeroYLuegoOtros() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        CODIGO_NOTIFICACIONES);
                return; // Muy importante: esperar que el usuario responda antes de pedir otros permisos
            }
        }

        pedirPermisosUnaSolaVez(); // Si ya tiene permiso o es Android <13
    }

    private void pedirPermisosIniciales() {
        String[] permisos = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        boolean permisosFaltantes = false;
        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                permisosFaltantes = true;
                break;
            }
        }

        if (permisosFaltantes) {
            ActivityCompat.requestPermissions(this, permisos, CODIGO_PERMISOS_INICIALES);
        }
    }

    private void pedirPermisosUnaSolaVez() {
        SharedPreferences prefs = getSharedPreferences("preferencias_app", MODE_PRIVATE);
        boolean yaPidio = prefs.getBoolean("permisos_solicitados", false);

        if (!yaPidio) {
            pedirPermisosIniciales();
            prefs.edit().putBoolean("permisos_solicitados", true).apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_NOTIFICACIONES) {
            Log.d("PERMISOS", "Permiso de notificación procesado. Ahora se piden los demás.");
            pedirPermisosUnaSolaVez();
        }

        if (requestCode == CODIGO_PERMISOS_INICIALES) {
            for (int i = 0; i < permissions.length; i++) {
                Log.d("PERMISOS", permissions[i] + ": " + (grantResults[i] == PackageManager.PERMISSION_GRANTED));
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void navegarA(Fragment fragment) {
        replaceFragment(fragment);
    }
}
