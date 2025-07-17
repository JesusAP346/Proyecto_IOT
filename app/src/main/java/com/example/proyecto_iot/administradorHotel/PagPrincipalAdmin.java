package com.example.proyecto_iot.administradorHotel;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.Reserva;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.administradorHotel.fragmentos.DetalleHuespedFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.HomeFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.HotelFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.PerfilAdminFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.ReservasFragment;
import com.example.proyecto_iot.databinding.ActivityPagPrincipalAdminBinding;

import java.util.Arrays;

public class PagPrincipalAdmin extends AppCompatActivity {

    ActivityPagPrincipalAdminBinding binding;

    String canalAdmin = "importanteDefault";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPagPrincipalAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        crearCanalesNotificacion();

        // ⬇️ Configura el BottomNavigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.inicio) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.hotel) {
                // 🔁 Resetear estado para evitar que se quede en "servicios", "reportes", etc.
                EstadoHotelUI.seccionSeleccionada = null;
                replaceFragment(HotelFragment.newInstance("info"));
            } else if (itemId == R.id.reservas) {
                replaceFragment(new ReservasFragment());
            } else if (itemId == R.id.perfil) {
                replaceFragment(new PerfilAdminFragment());
            }

            return true;
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("reservaCompleta")) {
            ReservaCompletaHotel reservaCompleta = (ReservaCompletaHotel) intent.getSerializableExtra("reservaCompleta");

            if (reservaCompleta != null) {
                binding.bottomNavigationView.setSelectedItemId(R.id.reservas);

                DetalleHuespedFragment detalleFragment = new DetalleHuespedFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("reservaCompleta", reservaCompleta);
                detalleFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, detalleFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            replaceFragment(new HomeFragment());
        }

    }



    private void replaceFragment(Fragment fragment) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (current != null && current.getClass().equals(fragment.getClass())) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }


    // PARA LAS NOTIFICACIONES
    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canalAdmin,
                "Canal para admin",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(PagPrincipalAdmin.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }
    private Reserva buscarReservaPorNombre(String nombre) {
        // Mismo mock que usas en ReservasTodasFragment
        return Arrays.asList(
                new Reserva("Jesús Romero", "7654433", "jesus.gonzales@gmail.com", "94787842",
                        "Deluxe", "2 adultos", 30,
                        Arrays.asList("TV", "Toallas", "Wifi", "2 camas", "Escritorio"),
                        "24/04/2025", "26/04/2025",
                        Arrays.asList("Gimnasio", "Desayuno"), 240.00),

                new Reserva("María López", "8745521", "maria.lopez@mail.com", "936582741",
                        "Suite Ejecutiva", "1 adulto", 45,
                        Arrays.asList("Mini bar", "Caja fuerte", "Aire acondicionado", "Frigobar"),
                        "01/05/2025", "05/05/2025",
                        Arrays.asList("Spa", "Room Service"), 360.00),

                new Reserva("Carlos Fernández", "6523412", "carlosf@gmail.com", "921547836",
                        "Familiar", "2 adultos, 2 niños", 60,
                        Arrays.asList("Cocina", "TV", "Balcón", "Wi-Fi"),
                        "10/06/2025", "15/06/2025",
                        Arrays.asList("Piscina", "Parqueo"), 480.00),

                new Reserva("Lucía Gómez", "7921345", "lucia.gomez@hotmail.com", "978452130",
                        "Suite Presidencial", "2 adultos", 80,
                        Arrays.asList("Jacuzzi", "Escritorio", "Sofá cama", "Frigobar"),
                        "20/07/2025", "25/07/2025",
                        Arrays.asList("Desayuno", "Servicio de taxi"), 600.00)
        ).stream().filter(r -> r.getNombreCompleto().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    public void seleccionarTab(int itemId) {
        binding.bottomNavigationView.setSelectedItemId(itemId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String redireccion = data.getStringExtra("redireccion");
            if ("habitaciones".equals(redireccion)) {
                seleccionarTab(R.id.hotel);
                Fragment hotelFragment = HotelFragment.newInstance("habitaciones");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, hotelFragment)
                        .commit();
            }
        }
    }

}
