package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.pago.PasarellaDePago;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.ViewHolder> {

    private List<Habitacion> listaHabitaciones;
    private Context context;
    private OnHabitacionClickListener listener;
    private String hotelId;

    private String fechaInicioGlobal;
    private String fechaFinGlobal;
    private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
    private List<ServicioAdicional> serviciosSeleccionados = new ArrayList<>();

    private FirebaseFirestore db;

    private Hotel hotel;

    public interface OnHabitacionClickListener{
        void onHabitacionClick(Habitacion2 habitacion);
    }

    public HabitacionAdapter(Context context, List<Habitacion> listaHabitaciones,
                             OnHabitacionClickListener listener, String hotelId,
                             String fechaInicio, String fechaFin) {
        this.listaHabitaciones = listaHabitaciones;
        this.context = context;
        this.listener = listener;
        this.hotelId = hotelId;
        this.fechaInicioGlobal = fechaInicio;
        this.fechaFinGlobal = fechaFin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habitacion, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habitacion h = listaHabitaciones.get(position);

        // Limpiar datos previos del ViewHolder
        holder.habitacion2 = null;
        holder.textPrecio.setText("Cargando...");

        // Configurar datos básicos inmediatamente
        holder.textEtiqueta.setText(h.getEtiqueta());
        holder.textDescripcion.setText(h.getDescripcion());

        // Obtener Habitacion2 de la base de datos
        if (hotelId != null && h.getId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("hoteles")
                    .document(hotelId)
                    .collection("habitaciones")
                    .document(h.getId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Habitacion2 hab2 = doc.toObject(Habitacion2.class);
                            if (hab2 != null) {
                                hab2.setId(doc.getId());
                                DecimalFormat df = new DecimalFormat("#,##0.00");
                                holder.textPrecio.setText("S/. " + df.format(hab2.getPrecioPorNoche()));
                                holder.habitacion2 = hab2;

                                // CONFIGURAR EL CLICK LISTENER AQUÍ, CUANDO YA TENEMOS LOS DATOS
                                setupClickListeners(holder, hab2);
                            } else {
                                Log.e("HabitacionAdapter", "El documento existe pero no se pudo convertir a Habitacion2");
                                holder.textPrecio.setText("Error al cargar");
                            }
                        } else {
                            Log.w("HabitacionAdapter", "El documento de la habitación no existe");
                            holder.textPrecio.setText("No disponible");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al obtener habitación", e);
                        holder.textPrecio.setText("Error al cargar");
                    });
        } else {
            holder.textPrecio.setText("Datos inválidos");
        }
    }

    // Método para configurar los click listeners una vez que tenemos los datos
    private void setupClickListeners(ViewHolder holder, Habitacion2 habitacion2) {
        holder.btnReservar.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e("RESERVA", "No hay usuario autenticado");
                return;
            }

            if (habitacion2 == null || hotelId == null || fechaInicioGlobal == null || fechaFinGlobal == null) {
                Log.e("RESERVA", "Hotel ID: " + hotelId);
                Log.e("RESERVA", "Fecha Inicio y fecha fin: " + fechaFinGlobal + fechaInicioGlobal);
                Log.e("RESERVA", "habitación ID: " + (habitacion2 != null ? habitacion2.getId() : "null"));
                Log.e("RESERVA", "Datos insuficientes para crear la reserva");
                return;
            }

            String idCliente = user.getUid();
            int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);
            double precioTotal = getPrecioTotal(habitacion2);

            List<ServicioAdicionalReserva> serviciosParaReserva = new ArrayList<>();
            for (ServicioAdicional servicio : serviciosSeleccionados) {
                serviciosParaReserva.add(new ServicioAdicionalReserva(servicio.getId(), numeroDeNoches));
            }

            // CARGAR DATOS DEL HOTEL Y PROCESAR LA RESERVA EN EL CALLBACK
            cargarDatosHotelYProcesarReserva(hotelId, habitacion2, idCliente, numeroDeNoches,
                    precioTotal, serviciosParaReserva, v.getContext());
        });

        holder.btnVerDetalle.setOnClickListener(v -> {
            if (listener != null && habitacion2 != null) {
                listener.onHabitacionClick(habitacion2);
            }
        });
    }

    private void cargarDatosHotelYProcesarReserva(String hotelId, Habitacion2 habitacion2,
                                                  String idCliente, int numeroDeNoches,
                                                  double precioTotal,
                                                  List<ServicioAdicionalReserva> serviciosParaReserva,
                                                  Context context) {
        db = FirebaseFirestore.getInstance();
        db.collection("hoteles").document(hotelId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Hotel hotel = documentSnapshot.toObject(Hotel.class);
                        if (hotel != null) {
                            Log.d("DETALLE_HOTEL", "Hotel encontrado: " + hotel.getNombre());

                            // AQUÍ YA TENEMOS EL OBJETO HOTEL, PODEMOS CONTINUAR CON LA RESERVA
                            procesarReserva(hotel, habitacion2, idCliente, numeroDeNoches,
                                    precioTotal, serviciosParaReserva, context);
                        } else {
                            Log.e("RESERVA", "Error: no se pudo convertir el documento a Hotel");
                            // Opcional: mostrar mensaje de error al usuario
                        }
                    } else {
                        Log.w("DETALLE_HOTEL", "No se encontró el hotel con ID: " + hotelId);
                        // Opcional: mostrar mensaje de error al usuario
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener hotel", e);
                    // Opcional: mostrar mensaje de error al usuario
                });
    }

    private void procesarReserva(Hotel hotel, Habitacion2 habitacion2, String idCliente,
                                 int numeroDeNoches, double precioTotal,
                                 List<ServicioAdicionalReserva> serviciosParaReserva,
                                 Context context) {

        String idReserva = "xd"; // Considera generar un ID único aquí

        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);
        reserva.setIdHotel(hotelId);
        reserva.setIdCliente(idCliente);
        reserva.setIdHabitacion(habitacion2.getId());
        reserva.setEstado("Activo");
        reserva.setFechaEntrada(fechaInicioGlobal);
        reserva.setFechaSalida(fechaFinGlobal);
        reserva.setCantNoches(numeroDeNoches);

        Log.d("TAXI", "Monto mínimo de taxi: "+hotel.getMontoMinimoTaxi());

        // AHORA SÍ PODEMOS USAR hotel.getMontoMinimoTaxi() PORQUE YA NO ES NULL
        reserva.setServicioTaxiHabilitado(!(hotel.getMontoMinimoTaxi() > precioTotal));

        String precioTotalStr = Double.toString(precioTotal);
        reserva.setMonto(precioTotalStr);
        reserva.setServiciosAdicionales(serviciosParaReserva);

        Log.d("RESERVA", "Reserva creada:");
        Log.d("RESERVA", "ID: " + reserva.getIdReserva());
        Log.d("RESERVA", "Hotel ID: " + reserva.getIdHotel());
        Log.d("RESERVA", "Servicio de taxi: " + reserva.isServicioTaxiHabilitado());
        Log.d("RESERVA", "Cliente ID: " + reserva.getIdCliente());
        Log.d("RESERVA", "Habitación ID: " + reserva.getIdHabitacion());
        Log.d("RESERVA", "Estado: " + reserva.getEstado());
        Log.d("RESERVA", "Fecha entrada: " + reserva.getFechaEntrada());
        Log.d("RESERVA", "Fecha salida: " + reserva.getFechaSalida());
        Log.d("RESERVA", "Cantidad de noches: " + reserva.getCantNoches());
        Log.d("RESERVA", "Monto total: S/. " + reserva.getMonto());
        Log.d("RESERVA", "Servicios adicionales seleccionados: " + serviciosParaReserva.size());

        Intent intent = new Intent(context, PasarellaDePago.class);
        intent.putExtra("reserva", reserva);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listaHabitaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPrecio, textEtiqueta, textDescripcion;
        Button btnReservar, btnVerDetalle;
        Habitacion2 habitacion2; // Almacenar la habitacion2 obtenida de la BD

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textEtiqueta = itemView.findViewById(R.id.textEtiqueta);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            btnReservar = itemView.findViewById(R.id.btnReservar);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);
        }
    }

    public double getPrecioTotal(Habitacion2 habitacion) {
        if (habitacion == null) return 0;

        int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);
        double precioHabitacion = habitacion.getPrecioPorNoche() * numeroDeNoches;
        double precioServicios = 0;

        for (ServicioAdicional servicio : serviciosSeleccionados) {
            precioServicios += servicio.getPrecio() * numeroDeNoches; // Multiplicar por noches
        }

        return precioHabitacion + precioServicios;
    }

    public List<ServicioAdicional> getServiciosSeleccionados() {
        return new ArrayList<>(serviciosSeleccionados);
    }

    // Método para actualizar las fechas si es necesario
    public void actualizarFechas(String fechaInicio, String fechaFin) {
        this.fechaInicioGlobal = fechaInicio;
        this.fechaFinGlobal = fechaFin;
        notifyDataSetChanged(); // Refrescar la vista si es necesario
    }

    private int calcularNumeroDeNoches(String fechaInicio, String fechaFin) {
        try {
            // Ajusta el formato según el formato de tus fechas
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Date dateInicio = sdf.parse(fechaInicio);
            Date dateFin = sdf.parse(fechaFin);

            if (dateInicio != null && dateFin != null) {
                long diferenciaMilisegundos = dateFin.getTime() - dateInicio.getTime();
                int numeroNoches = (int) (diferenciaMilisegundos / (1000 * 60 * 60 * 24)); // Convertir a días

                Log.d("DETALLE_HABITACION", "Fecha inicio: " + fechaInicio + ", Fecha fin: " + fechaFin);
                Log.d("DETALLE_HABITACION", "Diferencia en milisegundos: " + diferenciaMilisegundos);
                Log.d("DETALLE_HABITACION", "Número de noches calculado: " + numeroNoches);

                return numeroNoches > 0 ? numeroNoches : 1; // Mínimo 1 noche
            }
        } catch (ParseException e) {
            Log.e("DETALLE_HABITACION", "Error al parsear fechas: " + e.getMessage());
        }

        return 1; // Valor por defecto: 1 noche
    }
    private void cargarDatosHotel(String hotelId) {
        db = FirebaseFirestore.getInstance();
        db.collection("hoteles").document(hotelId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        hotel = documentSnapshot.toObject(Hotel.class);
                        assert hotel != null;
                        Log.d("DETALLE_HOTEL", "Hotel encontrado: " + hotel.getNombre());

                    } else {
                        Log.w("DETALLE_HOTEL", "No se encontró el hotel con ID: " + hotelId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener hotel", e);
                });
    }
}