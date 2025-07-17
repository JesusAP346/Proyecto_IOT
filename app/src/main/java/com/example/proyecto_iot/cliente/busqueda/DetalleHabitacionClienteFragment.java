package com.example.proyecto_iot.cliente.busqueda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.chat.ChatBottomSheet;
import com.example.proyecto_iot.cliente.pago.PasarellaDePago;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetalleHabitacionClienteFragment extends Fragment implements ServicioAdicionalAdapter.OnServicioSeleccionadoListener {

    private static final String ARG_HABITACION = "habitacion";
    private static final String ARG_HOTEL_ID = "hotel_id";
    private String fechaInicioGlobal;
    private String fechaFinGlobal;

    private Habitacion2 habitacion;
    private String hotelId;

    private ViewPager2 viewPagerHabitacion;
    private LinearLayout indicatorContainer;
    private TextView textTitulo, textPrecio, textCapacidad, textTamanho, textCantidadHabitaciones;
    private TextView textPrecioTotal, textServiciosSeleccionados, textNoServiciosAdicionales;
    private TextView textFechasEstadia; // Nueva TextView para mostrar fechas
    private LinearLayout equipamientoContainer, serviciosContainer, layoutServiciosSeleccionados;
    private RecyclerView recyclerServiciosAdicionales;

    private ServicioAdicionalAdapter servicioAdicionalAdapter;
    private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
    private List<ServicioAdicional> serviciosSeleccionados = new ArrayList<>();
    private FirebaseFirestore db;

    private Hotel hotel;

    public DetalleHabitacionClienteFragment() {
        // Required empty public constructor
    }

    public static DetalleHabitacionClienteFragment newInstance(Habitacion2 habitacion, String hotelId) {
        DetalleHabitacionClienteFragment fragment = new DetalleHabitacionClienteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HABITACION, habitacion);
        args.putString(ARG_HOTEL_ID, hotelId);
        args.putString("fechaInicio", habitacion.getFechaInicio());
        args.putString("fechaFin", habitacion.getFechaFin());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            habitacion = (Habitacion2) getArguments().getSerializable(ARG_HABITACION);
            hotelId = getArguments().getString(ARG_HOTEL_ID);
            cargarDatosHotel(hotelId);
            fechaInicioGlobal = getArguments().getString("fechaInicio");
            fechaFinGlobal = getArguments().getString("fechaFin");
            if (habitacion != null) {
                Log.d("DETALLE_HABITACION", "Habitación recibida: ID = " + habitacion.getId());
                Log.d("DETALLE_HABITACION", "Hotel ID: " + hotelId);
            } else {
                Log.w("DETALLE_HABITACION", "Habitación es null");
            }
        } else {
            Log.w("DETALLE_HABITACION", "getArguments() es null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_habitacion_cliente, container, false);

        initializeViews(view);
        setupImageCarousel();
        populateHabitacionData();
        setupServiciosAdicionales();
        setupClickListeners(view);
        cargarServiciosAdicionales();

        return view;
    }

    private void initializeViews(View view) {
        viewPagerHabitacion = view.findViewById(R.id.viewPagerHabitacion);
        indicatorContainer = view.findViewById(R.id.indicatorContainer);
        textTitulo = view.findViewById(R.id.textTitulo);
        textPrecio = view.findViewById(R.id.textPrecio);
        textCapacidad = view.findViewById(R.id.textCapacidad);
        textTamanho = view.findViewById(R.id.textTamanho);
        textCantidadHabitaciones = view.findViewById(R.id.textCantidadHabitaciones);
        textPrecioTotal = view.findViewById(R.id.textPrecioTotal);
        textServiciosSeleccionados = view.findViewById(R.id.textServiciosSeleccionados);
        textNoServiciosAdicionales = view.findViewById(R.id.textNoServiciosAdicionales);
        textFechasEstadia = view.findViewById(R.id.textFechasEstadia); // Inicializar nueva TextView
        equipamientoContainer = view.findViewById(R.id.equipamientoContainer);
        serviciosContainer = view.findViewById(R.id.serviciosContainer);
        layoutServiciosSeleccionados = view.findViewById(R.id.layoutServiciosSeleccionados);
        recyclerServiciosAdicionales = view.findViewById(R.id.recyclerServiciosAdicionales);
    }

    private void setupServiciosAdicionales() {
        servicioAdicionalAdapter = new ServicioAdicionalAdapter(getContext(), serviciosAdicionales, this);
        recyclerServiciosAdicionales.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerServiciosAdicionales.setAdapter(servicioAdicionalAdapter);
    }

    private void cargarServiciosAdicionales() {
        if (hotelId == null) {
            Log.w("DETALLE_HABITACION", "Hotel ID es null, no se pueden cargar servicios adicionales");
            mostrarNoServiciosAdicionales();
            return;
        }

        db.collection("hoteles")
                .document(hotelId)
                .collection("servicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviciosAdicionales.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        mostrarNoServiciosAdicionales();
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ServicioAdicional servicio = document.toObject(ServicioAdicional.class);
                        servicio.setId(document.getId());
                        serviciosAdicionales.add(servicio);
                    }

                    if (serviciosAdicionales.isEmpty()) {
                        mostrarNoServiciosAdicionales();
                    } else {
                        ocultarNoServiciosAdicionales();
                        servicioAdicionalAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HABITACION", "Error al cargar servicios adicionales", e);
                    mostrarNoServiciosAdicionales();
                });
    }

    private void mostrarNoServiciosAdicionales() {
        textNoServiciosAdicionales.setVisibility(View.VISIBLE);
        recyclerServiciosAdicionales.setVisibility(View.GONE);
    }

    private void ocultarNoServiciosAdicionales() {
        textNoServiciosAdicionales.setVisibility(View.GONE);
        recyclerServiciosAdicionales.setVisibility(View.VISIBLE);
    }

    @Override
    public void onServicioSeleccionado(ServicioAdicional servicio, boolean seleccionado) {
        if (seleccionado) {
            if (!serviciosSeleccionados.contains(servicio)) {
                serviciosSeleccionados.add(servicio);
            }
        } else {
            serviciosSeleccionados.remove(servicio);
        }

        actualizarPrecioTotal();
        actualizarServiciosSeleccionados();
    }

    // Método para calcular el número de noches entre dos fechas
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

    // Método para formatear fechas para mostrar al usuario
    private String formatearFechaParaMostrar(String fecha) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

            Date date = inputFormat.parse(fecha);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            Log.e("DETALLE_HABITACION", "Error al formatear fecha: " + e.getMessage());
        }
        return fecha; // Retornar fecha original si hay error
    }

    // Método para mostrar las fechas de estadía
    private void mostrarFechasEstadia() {
        if (fechaInicioGlobal != null && fechaFinGlobal != null) {
            String fechaEntrada = formatearFechaParaMostrar(fechaInicioGlobal);
            String fechaSalida = formatearFechaParaMostrar(fechaFinGlobal);

            String textoFechas = "Entrada: " + fechaEntrada + " - Salida: " + fechaSalida;
            textFechasEstadia.setText(textoFechas);
        }
    }

    // Método modificado para actualizar el precio total
    private void actualizarPrecioTotal() {
        if (habitacion == null) return;

        // Calcular número de noches
        int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);

        // Precio de la habitación por el número de noches
        double precioHabitacion = habitacion.getPrecioPorNoche() * numeroDeNoches;

        // Precio de servicios adicionales MULTIPLICADO por el número de noches
        double precioServicios = 0;
        for (ServicioAdicional servicio : serviciosSeleccionados) {
            precioServicios += servicio.getPrecio() * numeroDeNoches;
        }

        double precioTotal = precioHabitacion + precioServicios;

        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Mostrar información más detallada
        String textoTotal = "Total: S/. " + df.format(precioTotal);
        if (numeroDeNoches > 1) {
            textoTotal += " (" + numeroDeNoches + " noches)";
        }

        textPrecioTotal.setText(textoTotal);

        Log.d("DETALLE_HABITACION", "Número de noches: " + numeroDeNoches +
                ", Precio por noche: " + habitacion.getPrecioPorNoche() +
                ", Precio total habitación: " + precioHabitacion +
                ", Precio servicios (por " + numeroDeNoches + " noches): " + precioServicios +
                ", Precio total final: " + precioTotal);
    }

    private void actualizarServiciosSeleccionados() {
        if (serviciosSeleccionados.isEmpty()) {
            layoutServiciosSeleccionados.setVisibility(View.GONE);
        } else {
            layoutServiciosSeleccionados.setVisibility(View.VISIBLE);

            StringBuilder serviciosText = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#,##0.00");
            int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);

            for (int i = 0; i < serviciosSeleccionados.size(); i++) {
                ServicioAdicional servicio = serviciosSeleccionados.get(i);
                double precioTotalServicio = servicio.getPrecio() * numeroDeNoches;

                serviciosText.append("• ").append(servicio.getNombre())
                        .append(" - S/. ").append(df.format(precioTotalServicio));

                if (numeroDeNoches > 1) {
                    serviciosText.append(" (").append(numeroDeNoches).append(" noches)");
                }

                if (i < serviciosSeleccionados.size() - 1) {
                    serviciosText.append("\n");
                }
            }

            textServiciosSeleccionados.setText(serviciosText.toString());
        }
    }

    private void setupImageCarousel() {
        if (habitacion != null && habitacion.getFotosUrls() != null && !habitacion.getFotosUrls().isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(habitacion.getFotosUrls());
            viewPagerHabitacion.setAdapter(adapter);

            // Configurar indicadores
            setupIndicators(habitacion.getFotosUrls().size());

            // Configurar listener para cambiar indicadores
            viewPagerHabitacion.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateIndicators(position);
                }
            });
        }
    }

    private void setupIndicators(int count) {
        indicatorContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 24);
            params.setMargins(8, 0, 8, 0);
            indicator.setLayoutParams(params);
            indicator.setImageResource(R.drawable.ic_indicator_inactive);
            indicatorContainer.addView(indicator);
        }

        if (count > 0) {
            updateIndicators(0);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorContainer.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.ic_indicator_active);
            } else {
                indicator.setImageResource(R.drawable.ic_indicator_inactive);
            }
        }
    }

    private void populateHabitacionData() {
        if (habitacion == null) return;

        // Título y tipo
        textTitulo.setText(habitacion.getTipo() != null ? habitacion.getTipo() : "Habitación");

        // Precio
        DecimalFormat df = new DecimalFormat("#,##0.00");
        textPrecio.setText("S/. " + df.format(habitacion.getPrecioPorNoche()));

        // Capacidad
        String capacidadText = "Máx " + habitacion.getCapacidadAdultos() + " adultos";
        if (habitacion.getCapacidadNinos() > 0) {
            capacidadText += ", " + habitacion.getCapacidadNinos() + " niños";
        }
        textCapacidad.setText(capacidadText);
        Log.d("DETALLE_HABBBBBBBBB", "Fechas recibidas en el fragmento: " + fechaInicioGlobal + " - " + fechaFinGlobal);

        // Tamaño
        textTamanho.setText(habitacion.getTamanho() + " m²");

        // Cantidad de habitaciones
        String cantidadText = habitacion.getCantidadHabitaciones() + " habitacion" +
                (habitacion.getCantidadHabitaciones() != 1 ? "es" : "") + " disponible" +
                (habitacion.getCantidadHabitaciones() != 1 ? "s" : "");
        textCantidadHabitaciones.setText(cantidadText);

        // Equipamiento
        populateEquipamiento();

        // Servicios
        populateServicios();

        // Mostrar fechas de estadía
        mostrarFechasEstadia();

        // Inicializar precio total
        actualizarPrecioTotal();
    }

    private void populateEquipamiento() {
        equipamientoContainer.removeAllViews();

        if (habitacion.getEquipamiento() != null) {
            for (String equipo : habitacion.getEquipamiento()) {
                View equipoView = createFeatureView(equipo, getEquipamientoIcon(equipo));
                equipamientoContainer.addView(equipoView);
            }
        }
    }

    private void populateServicios() {
        serviciosContainer.removeAllViews();

        if (habitacion.getServicio() != null) {
            for (String servicio : habitacion.getServicio()) {
                View servicioView = createFeatureView(servicio, getServicioIcon(servicio));
                serviciosContainer.addView(servicioView);
            }
        }
    }

    private View createFeatureView(String text, int iconResId) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 12, 16, 12);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 8);
        layout.setLayoutParams(layoutParams);

        ImageView icon = new ImageView(getContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48);
        iconParams.setMargins(0, 0, 16, 0);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconResId);
        icon.setColorFilter(getResources().getColor(R.color.black));

        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.black));

        layout.addView(icon);
        layout.addView(textView);

        return layout;
    }

    private int getEquipamientoIcon(String equipamiento) {
        switch (equipamiento.toLowerCase()) {
            case "wifi":
            case "wi-fi":
                return R.drawable.ic_circle;
            case "aire acondicionado":
            case "ac":
                return R.drawable.ic_circle;
            case "tv":
            case "televisión":
                return R.drawable.ic_circle;
            case "frigobar":
            case "minibar":
                return R.drawable.ic_circle;
            case "caja fuerte":
                return R.drawable.ic_circle;
            default:
                return R.drawable.ic_circle;
        }
    }

    private int getServicioIcon(String servicio) {
        switch (servicio.toLowerCase()) {
            case "servicio de limpieza":
            case "limpieza":
                return R.drawable.ic_circle;
            case "room service":
            case "servicio a la habitación":
                return R.drawable.ic_circle;
            case "desayuno":
                return R.drawable.ic_circle;
            case "lavandería":
                return R.drawable.ic_circle;
            default:
                return R.drawable.ic_circle;
        }
    }

    private void setupClickListeners(View view) {
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        fabChat.setOnClickListener(v -> {
            if (getArguments() != null && hotel != null) {
                String hotelId = getArguments().getString(ARG_HOTEL_ID);

                // Crear el Bundle con el ID del administrador
                Bundle args = new Bundle();
                args.putString("idAdministrador", hotel.getIdAdministrador());

                Log.d("HOLAAAAAAA", "ID Administrador: " + hotel.getIdAdministrador());

                // Crear el ChatBottomSheet y pasarle los argumentos
                ChatBottomSheet chatBottomSheet = new ChatBottomSheet();
                chatBottomSheet.setArguments(args);  // Esta línea faltaba
                chatBottomSheet.show(getParentFragmentManager(), "ChatBottomSheet");
            }
        });

        Button btnReservar = view.findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(v -> {
            procesarReserva();
        });
    }

    // Método para obtener los servicios seleccionados (útil para el proceso de reserva)
    public List<ServicioAdicional> getServiciosSeleccionados() {
        return new ArrayList<>(serviciosSeleccionados);
    }

    // Método modificado para obtener el precio total
    public double getPrecioTotal() {
        if (habitacion == null) return 0;

        int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);
        double precioHabitacion = habitacion.getPrecioPorNoche() * numeroDeNoches;
        double precioServicios = 0;

        for (ServicioAdicional servicio : serviciosSeleccionados) {
            precioServicios += servicio.getPrecio() * numeroDeNoches; // Multiplicar por noches
        }

        return precioHabitacion + precioServicios;
    }
    private void procesarReserva() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("RESERVA", "No hay usuario autenticado");
            return;
        }

        if (habitacion == null || hotelId == null || fechaInicioGlobal == null || fechaFinGlobal == null) {
            Log.e("RESERVA", "Datos insuficientes para crear la reserva");
            return;
        }

        String idCliente = user.getUid();

        int numeroDeNoches = calcularNumeroDeNoches(fechaInicioGlobal, fechaFinGlobal);
        double precioTotal = getPrecioTotal(); // Ya lo tienes calculado en tu fragmento

        // Construir la lista de servicios seleccionados con duración
        List<ServicioAdicionalReserva> serviciosParaReserva = new ArrayList<>();
        for (ServicioAdicional servicio : serviciosSeleccionados) {
            serviciosParaReserva.add(new ServicioAdicionalReserva(servicio.getId(), numeroDeNoches));
        }


        String idReserva = "xd";



        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);
        reserva.setIdHotel(hotelId);
        reserva.setIdCliente(idCliente);
        reserva.setIdHabitacion(habitacion.getId());
        reserva.setEstado("Activo"); //
        reserva.setFechaEntrada(fechaInicioGlobal);
        reserva.setFechaSalida(fechaFinGlobal);
        reserva.setCantNoches(numeroDeNoches);
        String precioTotalStr = Double.toString(precioTotal);
        reserva.setMonto(precioTotalStr);
        reserva.setServiciosAdicionales(serviciosParaReserva);

        Intent intent = new Intent(getContext(), PasarellaDePago.class);
        intent.putExtra("reserva", reserva);
        startActivity(intent);

        Log.d("RESERVA", "Reserva creada:");
        Log.d("RESERVA", "ID: " + reserva.getIdReserva());
        Log.d("RESERVA", "Hotel ID: " + reserva.getIdHotel());
        Log.d("RESERVA", "Cliente ID: " + reserva.getIdCliente());
        Log.d("RESERVA", "Habitación ID: " + reserva.getIdHabitacion());
        Log.d("RESERVA", "Estado: " + reserva.getEstado());
        Log.d("RESERVA", "Fecha entrada: " + reserva.getFechaEntrada());
        Log.d("RESERVA", "Fecha salida: " + reserva.getFechaSalida());
        Log.d("RESERVA", "Cantidad de noches: " + reserva.getCantNoches());
        Log.d("RESERVA", "Monto total: S/. " + reserva.getMonto());
        Log.d("RESERVA", "Servicios adicionales seleccionados: " + serviciosParaReserva.size());
    }

    private void cargarDatosHotel(String hotelId) {
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