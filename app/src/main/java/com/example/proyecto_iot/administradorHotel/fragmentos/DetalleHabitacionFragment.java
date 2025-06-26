package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.Equipamiento;
import com.example.proyecto_iot.administradorHotel.entity.FotoItem;
import com.example.proyecto_iot.administradorHotel.entity.Habitacion;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.databinding.FragmentDetalleHabitacionBinding;

import com.google.android.material.button.MaterialButton;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetalleHabitacionFragment extends Fragment {

    private FragmentDetalleHabitacionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleHabitacionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener datos enviados
        HabitacionHotel habitacion = (HabitacionHotel) getArguments().getSerializable("habitacion");

        if (habitacion != null) {
            // Llenar campos con binding
            binding.textTipoHabitacion.setText(habitacion.getTipo());

            String capacidadTexto = habitacion.getCapacidadAdultos() + " Adultos, " + habitacion.getCapacidadNinos() + " Niño(s)";
            binding.textCapacidad.setText(capacidadTexto);

            binding.textTamano.setText(habitacion.getTamanho() + " m²");

            String precio = "S/ " + habitacion.getPrecioPorNoche();
            binding.textPrecio.setText(precio);

            String cantidad = habitacion.getCantidadHabitaciones() + " habitaciones";
            binding.textCantidadHabitaciones.setText(cantidad);

            // Mostrar equipamiento
            if (habitacion.getEquipamiento() != null && !habitacion.getEquipamiento().isEmpty()) {
                mostrarEquipamiento(binding.contenedorEquipamiento, habitacion.getEquipamiento());
            }

            // Mostrar servicios
            if (habitacion.getServicio() != null && !habitacion.getServicio().isEmpty()) {
                mostrarServicios(binding.contenedorServicios, habitacion.getServicio());
            }

            ImageCarousel carrusel = binding.carruselImagenes;

            if (habitacion.getFotosUrls() != null && !habitacion.getFotosUrls().isEmpty()) {
                List<CarouselItem> items = new ArrayList<>();

                for (String url : habitacion.getFotosUrls()) {
                    items.add(new CarouselItem(url));
                }
                carrusel.setCarouselType(CarouselType.BLOCK);
                carrusel.setAutoPlay(true);
                carrusel.setData(items);
            } else {
                carrusel.setVisibility(View.GONE);
            }
        }

        binding.btnActualizarInfo.setOnClickListener(v -> {
            EditarHabitacionfragment actualizarFragment = new EditarHabitacionfragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("habitacion", habitacion);
            actualizarFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, actualizarFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Acción de retroceso
        binding.backdetallehabitacion.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void mostrarEquipamiento(LinearLayout contenedor, List<String> items) {
        contenedor.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        LinearLayout fila = null;
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                fila = new LinearLayout(getContext());
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                fila.setPadding(0, 4, 0, 4);
                contenedor.addView(fila);
            }

            TextView tv = new TextView(getContext());
            tv.setText("• " + items.get(i));
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (i % 2 == 0) {
                ((LinearLayout.LayoutParams) tv.getLayoutParams()).setMarginEnd(8);
            }

            if (fila != null) fila.addView(tv);
        }
    }

    private void mostrarServicios(LinearLayout contenedor, List<String> servicios) {
        contenedor.removeAllViews();
        for (String servicio : servicios) {
            TextView tv = new TextView(getContext());
            tv.setText("• " + servicio);
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setPadding(0, 6, 0, 6);
            contenedor.addView(tv);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //para las fotos


}
