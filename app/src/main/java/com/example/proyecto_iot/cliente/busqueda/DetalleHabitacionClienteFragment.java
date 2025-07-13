package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.chat.ChatBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.List;

public class DetalleHabitacionClienteFragment extends Fragment {

    private static final String ARG_HABITACION = "habitacion";
    private Habitacion2 habitacion;

    private ViewPager2 viewPagerHabitacion;
    private LinearLayout indicatorContainer;
    private TextView textTitulo, textPrecio, textCapacidad, textTamanho, textCantidadHabitaciones;
    private LinearLayout equipamientoContainer, serviciosContainer;

    public DetalleHabitacionClienteFragment() {
        // Required empty public constructor
    }

    public static DetalleHabitacionClienteFragment newInstance(Habitacion2 habitacion) {
        DetalleHabitacionClienteFragment fragment = new DetalleHabitacionClienteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HABITACION, habitacion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            habitacion = (Habitacion2) getArguments().getSerializable(ARG_HABITACION);
            if (habitacion != null) {
                android.util.Log.d("DETALLE_HABITACION", "Habitación recibida: ID = " + habitacion.getId());
            } else {
                android.util.Log.w("DETALLE_HABITACION", "Habitación es null");
            }
        } else {
            android.util.Log.w("DETALLE_HABITACION", "getArguments() es null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_habitacion_cliente, container, false);

        initializeViews(view);
        setupImageCarousel();
        populateHabitacionData();
        setupClickListeners(view);

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
        equipamientoContainer = view.findViewById(R.id.equipamientoContainer);
        serviciosContainer = view.findViewById(R.id.serviciosContainer);
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
            ChatBottomSheet chatBottomSheet = new ChatBottomSheet();
            chatBottomSheet.show(getParentFragmentManager(), "ChatBottomSheet");
        });
    }
}