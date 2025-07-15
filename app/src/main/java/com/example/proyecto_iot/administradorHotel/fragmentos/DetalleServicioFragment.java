package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.administradorHotel.entity.ServicioHotel;
import com.example.proyecto_iot.databinding.FragmentDetalleServicioBinding;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.util.ArrayList;
import java.util.List;

public class DetalleServicioFragment extends Fragment {

    private FragmentDetalleServicioBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleServicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener datos enviados
        ServicioHotel servicio = (ServicioHotel) getArguments().getSerializable("servicio");

        if (servicio != null) {
            // Llenar campos

            binding.textTipoNombre.setText(servicio.getNombre());

            double precioValor = servicio.getPrecio(); // asegúrate que sea double o float
            String precio = "S/ " + String.format("%.2f", precioValor);
            binding.textPrecio.setText(precio);

            binding.textTipoDescripcion.setText(servicio.getDescripcion());

            ImageCarousel carrusel = binding.carruselImagenes;

            if (servicio.getFotosUrls() != null && !servicio.getFotosUrls().isEmpty()) {
                List<CarouselItem> items = new ArrayList<>();

                for (String url : servicio.getFotosUrls()) {
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
            EditarServicioFragment actualizarFragment = new EditarServicioFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("servicio", servicio);
            actualizarFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, actualizarFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Acción del botón de retroceso
        binding.backdetalleservicio.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
