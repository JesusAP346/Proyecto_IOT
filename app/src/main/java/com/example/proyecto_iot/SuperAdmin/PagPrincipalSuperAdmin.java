package com.example.proyecto_iot.SuperAdmin;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.fragmentos.PerfilSuperAdminFragment;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_home_superadmin;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_administradores_superadmin;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_reportes_superadmin;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_usuarios_superadmin;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_taxistas_superadmin;
import com.example.proyecto_iot.SuperAdmin.fragmentos.LogsFragment;
import com.example.proyecto_iot.SuperAdmin.fragmentos.fragment_reservas_superadmin;
import com.example.proyecto_iot.databinding.ActivityMainSuperAdminBinding;

public class PagPrincipalSuperAdmin extends AppCompatActivity {

    ActivityMainSuperAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainSuperAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_usuarios",
                    "Notificaciones de Usuarios",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        //---------------------------------------------------------

        // Fragmento por defecto
        replaceFragment(new fragment_home_superadmin());

        binding.menuNavSup.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                replaceFragment(new fragment_home_superadmin());
            } else if (itemId == R.id.nav_admins) {
                replaceFragment(new fragment_administradores_superadmin());
            } else if (itemId == R.id.nav_taxis) {
                replaceFragment(new fragment_taxistas_superadmin());
            } else if (itemId == R.id.nav_users) {
                replaceFragment(new fragment_usuarios_superadmin());
            } else if (itemId == R.id.nav_reservas) {
                replaceFragment(new LogsFragment());
            }

            return true;
        });

        binding.btnPerfil.setOnClickListener(v -> {
            replaceFragment(new PerfilSuperAdminFragment());
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}
