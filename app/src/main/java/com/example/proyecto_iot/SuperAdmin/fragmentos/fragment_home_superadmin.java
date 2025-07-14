package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.viewpager2.widget.ViewPager2;
import com.example.proyecto_iot.SuperAdmin.adapter.CarruselAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Handler;

public class fragment_home_superadmin extends Fragment {

    private ViewPager2 imageSlider;
    private Handler sliderHandler = new Handler();
    private int currentPosition = 0;
    private List<String> imageList = new ArrayList<>();

    private TabLayout tabLayout;

    public fragment_home_superadmin() {
        // Required empty public constructor
    }

    public static fragment_home_superadmin newInstance(String param1, String param2) {
        fragment_home_superadmin fragment = new fragment_home_superadmin();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_superadmin, container, false);

        // 1) Usuarios nuevos
        LineChart chartUsuarios = view.findViewById(R.id.sparklineChart);
        List<Entry> usuarioEntries = new ArrayList<>();
        usuarioEntries.add(new Entry(0, 10));
        usuarioEntries.add(new Entry(1, 12));
        usuarioEntries.add(new Entry(2, 9));
        usuarioEntries.add(new Entry(3, 14));
        usuarioEntries.add(new Entry(4, 11));
        setupSparkline(chartUsuarios, usuarioEntries, "#4CAF50");

        // 2) Hoteles registrados
        LineChart chartHoteles = view.findViewById(R.id.chartHoteles);
        List<Entry> hotelEntries = new ArrayList<>();
        hotelEntries.add(new Entry(0, 20));
        hotelEntries.add(new Entry(1, 22));
        hotelEntries.add(new Entry(2, 25));
        hotelEntries.add(new Entry(3, 23));
        hotelEntries.add(new Entry(4, 26));
        setupSparkline(chartHoteles, hotelEntries, "#3F51B5");

        // 3) Reservas mensuales
        LineChart chartReservas = view.findViewById(R.id.chartReservas);
        List<Entry> reservaEntries = new ArrayList<>();
        reservaEntries.add(new Entry(0, 50));
        reservaEntries.add(new Entry(1, 70));
        reservaEntries.add(new Entry(2, 60));
        reservaEntries.add(new Entry(3, 80));
        reservaEntries.add(new Entry(4, 112));
        setupSparkline(chartReservas, reservaEntries, "#FF9800");

        // 4) Calificación promedio
        LineChart chartRating = view.findViewById(R.id.chartRating);
        List<Entry> ratingEntries = new ArrayList<>();
        ratingEntries.add(new Entry(0, 4.2f));
        ratingEntries.add(new Entry(1, 4.4f));
        ratingEntries.add(new Entry(2, 4.5f));
        ratingEntries.add(new Entry(3, 4.6f));
        ratingEntries.add(new Entry(4, 4.6f));
        setupSparkline(chartRating, ratingEntries, "#F44336");
        // 5) Carrusel
        imageSlider = view.findViewById(R.id.imageSlider);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Obtener IDs de hoteles
        db.collection("hoteles").get().addOnSuccessListener(hotelSnap -> {
            List<String> hotelIds = new ArrayList<>();
            for (DocumentSnapshot doc : hotelSnap.getDocuments()) {
                hotelIds.add(doc.getId());
            }

            // Obtener todas las reservas
            db.collection("reservas").get().addOnSuccessListener(reservaSnap -> {
                Map<String, Integer> conteoHoteles = new HashMap<>();

                for (DocumentSnapshot reserva : reservaSnap.getDocuments()) {
                    String idHotel = reserva.getString("idHotel");
                    if (idHotel != null && hotelIds.contains(idHotel)) {
                        conteoHoteles.put(idHotel, conteoHoteles.getOrDefault(idHotel, 0) + 1);
                    }
                }

                // Hotel con más reservas
                String hotelMasReservado = null;
                int maxReservas = 0;
                for (Map.Entry<String, Integer> entry : conteoHoteles.entrySet()) {
                    if (entry.getValue() > maxReservas) {
                        maxReservas = entry.getValue();
                        hotelMasReservado = entry.getKey();
                    }
                }

                // Obtener imágenes
                if (hotelMasReservado != null) {
                    db.collection("hoteles").document(hotelMasReservado).get().addOnSuccessListener(hotelDoc -> {
                        List<String> fotos = (List<String>) hotelDoc.get("fotosHotelUrls");
                        if (fotos != null && !fotos.isEmpty()) {
                            imageList = fotos;
                            if (!isAdded()) return;
                            CarruselAdapter adapter = new CarruselAdapter(requireContext(), imageList);
                            imageSlider.setAdapter(adapter);



                            // Auto-slide
                            sliderHandler.postDelayed(sliderRunnable, 10000);
                        }
                    });
                }
            });
        });



// Iniciar auto-slide cada 3 segundos
        sliderHandler.postDelayed(sliderRunnable, 3000);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    /**
     * Configura un LineChart en modo "sparkline" (sin ejes, sin leyenda,
     * sin interacción, curva suavizada y relleno degradado).
     *
     * @param chart   El LineChart a configurar.
     * @param entries Lista de Entry(xIndex, yValue) con los datos.
     * @param color   Color en hex (#RRGGBB) para la línea.
     */
    private void setupSparkline(LineChart chart, List<Entry> entries, String color) {
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor(color));
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Relleno degradado (crea res/drawable/circle_green.xml o similar)
        dataSet.setDrawFilled(true);
        Drawable fill = ContextCompat.getDrawable(requireContext(), R.drawable.circle_green);
        dataSet.setFillDrawable(fill);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Desactiva descripciones, ejes y leyendas
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(false);

        chart.invalidate(); // refresca el gráfico
    }
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (imageList == null || imageList.size() == 0) return;

            currentPosition = (currentPosition + 1) % imageList.size();
            imageSlider.setCurrentItem(currentPosition, true);
            sliderHandler.postDelayed(this, 3000); // 3 segundos
        }
    };




}


