package com.example.proyecto_iot;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.databinding.ActivityMainBinding;
import com.example.proyecto_iot.taxista.perfil.PerfilFragment;
import com.example.proyecto_iot.taxista.qr.QrFragment;
import com.example.proyecto_iot.taxista.solicitudes.SolicitudesFragment;

public class MainActivity extends AppCompatActivity {

    public static final String CANAL_ID = "canal_viajes";
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        crearCanalNotificacion();
        pedirPermisosNotificacion();

        replaceFragment(new SolicitudesFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.perfil) {
                replaceFragment(new PerfilFragment());
            } else if (itemId == R.id.qr) {
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
                    NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription("Notificaciones sobre viajes aceptados");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    private void pedirPermisosNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    public void navegarA(Fragment fragment) {
        replaceFragment(fragment);
    }
}
