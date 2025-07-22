package com.example.proyecto_iot.cliente.busqueda;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

//import com.example.proyecto_iot.Manifest;
import android.Manifest;
import android.util.Log;

import com.example.proyecto_iot.R;
//import com.example.proyecto_iot.administradorHotel.fragmentos.ReservasFragment;
import com.example.proyecto_iot.cliente.MisReservasFragment;

import com.example.proyecto_iot.cliente.NotificacionesFragment;
import com.example.proyecto_iot.cliente.PerfilFragmentC;
import com.example.proyecto_iot.cliente.TaxiFragment;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.login.UsuarioClienteViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ClienteBusquedaActivity extends AppCompatActivity {

    private UsuarioClienteViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ///super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_busqueda);
        //setContentView(R.layout.activity_solicitud_taxi);

        crearCanalNotificacion();

        boolean mostrarReservas = getIntent().getBooleanExtra("mostrar_reservas", false);


        if (mostrarReservas) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_reservas);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new MisReservasFragment())
                    .commit();
        } else if (savedInstanceState == null) {
            // Carga fragmento por defecto (Buscar)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new BusquedaFragment())
                    .commit();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container_busqueda), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Solo aplicar padding superior al contenedor de fragmentos
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

// Y para el BottomNavigationView:
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_navigation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplicar padding inferior para que no se superponga con la barra de navegación del sistema
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
/*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new BusquedaFragment())
                    .commit();
        }*/
        Locale locale = new Locale("es");
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Navegación del menú inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.nav_buscar) {
                fragment = new BusquedaFragment();
            } else if (id == R.id.nav_notificaciones) {
                fragment = new NotificacionesFragment();
            } else if (id == R.id.nav_reservas) {
                fragment = new MisReservasFragment();
            } else if (id == R.id.nav_taxi) {
                fragment = new TaxiFragment();  // ✅ Aquí agregamos el fragmento de taxi
            } else if (id == R.id.nav_perfil) {
                fragment = new PerfilFragmentC();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_busqueda, fragment)
                        .commit();
                return true;
            }
            return false;
        });

        viewModel = new ViewModelProvider(this).get(UsuarioClienteViewModel.class);

        String idUsuario = getIntent().getStringExtra("idUsuario");

        if (idUsuario != null) {
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(idUsuario)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Usuario cliente = doc.toObject(Usuario.class);
                            viewModel.setUsuario(cliente);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error al cargar usuario", e);
                    });
        }

    }


    private static final String CHANNEL_ID = "canal_reservas";

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Reservas";
            String description = "Canal para notificaciones de reservas y taxi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacion() {
        // Solicitar permiso para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return; // Espera a que el usuario conceda permiso
            }
        }

        Intent intent = new Intent(this, ClienteBusquedaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_notification) // Cambia por tu ícono
                .setContentTitle("Reserva Confirmada")
                .setContentText("Tu solicitud de taxi fue registrada correctamente.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1001, builder.build());
    }


}