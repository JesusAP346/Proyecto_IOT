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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

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

        escucharNotificaciones();
    }

    private void escucharNotificaciones() {
        String uidAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uidAdmin)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String idHotel = snapshot.getString("idHotel");

                        if (idHotel != null) {
                            FirebaseFirestore.getInstance()
                                    .collection("notificaciones")
                                    .whereEqualTo("idHotel", idHotel)
                                    .whereEqualTo("rol", "administrador")
                                    .addSnapshotListener((snapshots, error) -> {
                                        if (error != null || snapshots == null) return;

                                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                                String mensaje = dc.getDocument().getString("mensaje");
                                                mostrarNotificacion(mensaje);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void mostrarNotificacion(String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalAdmin)
                .setSmallIcon(R.drawable.ic_boleta)  // tu ícono aquí
                .setContentTitle("Notificación de Checkout")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
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
