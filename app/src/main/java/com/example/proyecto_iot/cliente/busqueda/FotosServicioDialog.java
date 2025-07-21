package com.example.proyecto_iot.cliente.busqueda;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_iot.R;

import java.util.List;

public class FotosServicioDialog extends DialogFragment {

    private static final String ARG_SERVICIO = "servicio";
    private Servicio servicio;
    private ViewPager2 viewPagerFotos;
    private LinearLayout layoutIndicadores;
    private TextView textContadorFotos;

    public static FotosServicioDialog newInstance(Servicio servicio) {
        FotosServicioDialog fragment = new FotosServicioDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICIO, servicio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            servicio = (Servicio) getArguments().getSerializable(ARG_SERVICIO);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fotos_servicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupDialog();
        setupViewPager();
    }

    private void initViews(View view) {
        viewPagerFotos = view.findViewById(R.id.viewPagerFotosServicio);
        layoutIndicadores = view.findViewById(R.id.layoutIndicadoresFotos);
        textContadorFotos = view.findViewById(R.id.textContadorFotos);

        TextView textNombreServicio = view.findViewById(R.id.textNombreServicioDialog);
        ImageButton btnCerrar = view.findViewById(R.id.btnCerrarDialog);

        if (servicio != null) {
            textNombreServicio.setText(servicio.getNombre());
        }

        btnCerrar.setOnClickListener(v -> dismiss());
    }

    private void setupDialog() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void setupViewPager() {
        if (servicio != null && servicio.getFotosUrls() != null && !servicio.getFotosUrls().isEmpty()) {
            List<String> fotosUrls = servicio.getFotosUrls();

            // Configurar adapter para las fotos
            ImagenHotelAdapter adapter = new ImagenHotelAdapter(getContext(), fotosUrls);
            viewPagerFotos.setAdapter(adapter);

            // Si hay más de una foto, mostrar indicadores
            if (fotosUrls.size() > 1) {
                setupIndicadores(fotosUrls.size());
                setupPageChangeListener();
            } else {
                // Solo una foto, ocultar indicadores
                layoutIndicadores.setVisibility(View.GONE);
                textContadorFotos.setVisibility(View.GONE);
            }
        }
    }

    private void setupIndicadores(int cantidadFotos) {
        layoutIndicadores.removeAllViews();

        // Crear dots para cada foto
        for (int i = 0; i < cantidadFotos; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(R.drawable.dot_indicador);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            dot.setLayoutParams(params);
            dot.setSelected(i == 0); // Primer dot activo

            layoutIndicadores.addView(dot);
        }

        // Mostrar indicadores y contador
        layoutIndicadores.setVisibility(View.VISIBLE);
        textContadorFotos.setVisibility(View.VISIBLE);

        // Actualizar contador inicial
        updateContador(0, cantidadFotos);
    }

    private void setupPageChangeListener() {
        viewPagerFotos.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicadores(position);
                updateContador(position, layoutIndicadores.getChildCount());
            }
        });
    }

    private void updateIndicadores(int position) {
        for (int i = 0; i < layoutIndicadores.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutIndicadores.getChildAt(i);
            dot.setSelected(i == position);
        }
    }

    private void updateContador(int position, int total) {
        String contador = (position + 1) + "/" + total;
        textContadorFotos.setText(contador);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}