package com.example.proyecto_iot.cliente.pago;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.cliente.busqueda.Reserva;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasarellaDePago extends AppCompatActivity implements TarjetaAdapter.OnTarjetaSelectedListener {

    private static final String CHANNEL_ID = "PAYMENT_NOTIFICATIONS";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "PasarellaDePago";

    private RecyclerView recyclerView;
    private Button btnPagar;
    private TarjetaAdapter adapter;

    // Firestore Database reference
    private FirebaseFirestore db;

    // Objeto reserva recibido desde el fragment
    private Reserva reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasarella_de_pago);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            findViewById(R.id.pasarelaRoot).setOnApplyWindowInsetsListener((v, insets) -> {
                int bottomInset = insets.getInsets(WindowInsets.Type.systemBars()).bottom;
                Button btnPagar = findViewById(R.id.btnPagar);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnPagar.getLayoutParams();
                params.bottomMargin = bottomInset + 16; // 16dp original + altura de la barra
                btnPagar.setLayoutParams(params);
                return insets;
            });
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener el objeto reserva del Intent
        reserva = (Reserva) getIntent().getSerializableExtra("reserva");

        TextView textMontoTotal = findViewById(R.id.textMontoTotal);
        TextView textCantNoches = findViewById(R.id.textCantNoches);

        if (reserva != null) {
            double montoDouble = Double.parseDouble(reserva.getMonto()); // convertir String a double
            String montoStr = String.format("Monto total: S/. %.2f", montoDouble);
            String nochesStr = "Noches: " + reserva.getCantNoches();

            textMontoTotal.setText(montoStr);
            textCantNoches.setText(nochesStr);

            // Validar la fecha de entrada
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date fechaEntrada = sdf.parse(reserva.getFechaEntrada());

                Calendar calEntrada = Calendar.getInstance();
                calEntrada.setTime(fechaEntrada);

                Calendar calActual = Calendar.getInstance();

                // Eliminar la hora de ambos para comparar solo fechas
                calEntrada.set(Calendar.HOUR_OF_DAY, 0);
                calEntrada.set(Calendar.MINUTE, 0);
                calEntrada.set(Calendar.SECOND, 0);
                calEntrada.set(Calendar.MILLISECOND, 0);

                calActual.set(Calendar.HOUR_OF_DAY, 0);
                calActual.set(Calendar.MINUTE, 0);
                calActual.set(Calendar.SECOND, 0);
                calActual.set(Calendar.MILLISECOND, 0);

                if (calEntrada.equals(calActual)) {
                    reserva.setEstado("ACTIVO");
                } else if (calEntrada.after(calActual)) {
                    reserva.setEstado("PENDIENTE");
                }

            } catch (ParseException e) {
                Log.e(TAG, "Error al parsear la fecha de entrada", e);
            }
        }

        if (reserva == null) {
            Log.e(TAG, "No se recibió el objeto reserva");
            Toast.makeText(this, "Error: No se encontró información de la reserva", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Crear canal de notificaciones
        createNotificationChannel();

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerTarjetas);
        btnPagar = findViewById(R.id.btnPagar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar tarjetas y configurar adapter
        List<Tarjeta> listaTarjetas = TarjetaStorage.obtenerTarjetas(this);
        adapter = new TarjetaAdapter(listaTarjetas);
        adapter.setOnTarjetaSelectedListener(this);
        recyclerView.setAdapter(adapter);

        // Inicialmente el botón está deshabilitado
        btnPagar.setEnabled(false);
        btnPagar.setAlpha(0.5f);

        // Configurar botón de retroceso
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Configurar botón de pago
        btnPagar.setOnClickListener(v -> procesarPago());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar lista de tarjetas cuando se regrese a la actividad
        List<Tarjeta> listaTarjetasActualizadas = TarjetaStorage.obtenerTarjetas(this);
        adapter = new TarjetaAdapter(listaTarjetasActualizadas);
        adapter.setOnTarjetaSelectedListener(this);
        recyclerView.setAdapter(adapter);

        // Resetear estado del botón
        btnPagar.setEnabled(false);
        btnPagar.setAlpha(0.5f);
    }

    @Override
    public void onTarjetaSelected(boolean haySeleccion) {
        // Habilitar/deshabilitar botón según si hay tarjeta seleccionada
        btnPagar.setEnabled(haySeleccion);
        btnPagar.setAlpha(haySeleccion ? 1.0f : 0.5f);
    }

    private void procesarPago() {
        Tarjeta tarjetaSeleccionada = adapter.getTarjetaSeleccionada();

        if (tarjetaSeleccionada != null) {
            // Deshabilitar botón para evitar múltiples clics
            btnPagar.setEnabled(false);
            btnPagar.setText("Procesando...");

            // Guardar reserva en Firebase Database
            guardarReservaEnFirebase(tarjetaSeleccionada);

        } else {
            Toast.makeText(this, "Por favor selecciona una tarjeta", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para enmascarar el número de tarjeta
     * Solo muestra los últimos 4 dígitos, el resto como asteriscos
     */
    private String enmascararNumeroTarjeta(String numeroCompleto) {
        if (numeroCompleto == null || numeroCompleto.length() < 4) {
            return "**** **** **** ****";
        }

        String ultimosCuatro = numeroCompleto.substring(numeroCompleto.length() - 4);
        String asteriscos = "*".repeat(numeroCompleto.length() - 4);

        return asteriscos + ultimosCuatro;
    }

    /**
     * Crear el objeto con los datos de pago para guardar en Firestore
     */
    private Map<String, Object> crearDatosPago(Tarjeta tarjeta) {
        Map<String, Object> datosPago = new HashMap<>();

        // Guardar información de la tarjeta de forma segura
        datosPago.put("numeroTarjetaEnmascarado", enmascararNumeroTarjeta(tarjeta.getNumero()));
        datosPago.put("banco", tarjeta.getBanco());
        datosPago.put("titular", tarjeta.getTitular());
        datosPago.put("tipo", tarjeta.getTipo());
        datosPago.put("marca", tarjeta.getMarca());


        return datosPago;
    }

    private void guardarReservaEnFirebase(Tarjeta tarjetaSeleccionada) {

        // Crear el documento de reserva con los datos de pago incluidos
        String documentId = db.collection("reservas").document().getId();
        reserva.setIdReserva(documentId);

        Map<String, Object> documentoReserva = new HashMap<>();


        documentoReserva.put("idReserva", reserva.getIdReserva());
        documentoReserva.put("monto", reserva.getMonto());
        documentoReserva.put("cantNoches", reserva.getCantNoches());
        documentoReserva.put("fechaEntrada", reserva.getFechaEntrada());
        documentoReserva.put("estado", reserva.getEstado());
        documentoReserva.put("idCliente", reserva.getIdCliente());
        documentoReserva.put("fechaSalida", reserva.getFechaSalida());
        documentoReserva.put("idHabitacion", reserva.getIdHabitacion());
        documentoReserva.put("idHotel", reserva.getIdHotel());
        documentoReserva.put("serviciosAdicionales", reserva.getServiciosAdicionales());

        // Agregar los datos de pago como un objeto anidado
        documentoReserva.put("datosPago", crearDatosPago(tarjetaSeleccionada));

        // Guardar en Firestore
        db.collection("reservas")
                .document(documentId)
                .set(documentoReserva)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Reserva guardada exitosamente en Firestore con ID: " + documentId);
                    Log.d(TAG, "Datos de pago incluidos: Tarjeta " +
                            enmascararNumeroTarjeta(tarjetaSeleccionada.getNumero()) +
                            " del banco " + tarjetaSeleccionada.getBanco());

                    // Mostrar notificación de pago exitoso
                    mostrarNotificacionPagoExitoso(tarjetaSeleccionada);

                    Toast.makeText(this, "Reserva procesada exitosamente", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, ClienteBusquedaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar reserva en Firestore", e);
                    Toast.makeText(this, "Error al procesar el pago. Intenta nuevamente.", Toast.LENGTH_SHORT).show();

                    btnPagar.setEnabled(true);
                    btnPagar.setText("Reservar");
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de Pago";
            String description = "Canal para notificaciones de pagos exitosos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacionPagoExitoso(Tarjeta tarjeta) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_circle)
                .setContentTitle("Reserva Exitosa")
                .setContentText("Reserva procesado con tarjeta " + tarjeta.getBanco() + " terminada en " +
                        tarjeta.getNumero().substring(tarjeta.getNumero().length() - 4))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}