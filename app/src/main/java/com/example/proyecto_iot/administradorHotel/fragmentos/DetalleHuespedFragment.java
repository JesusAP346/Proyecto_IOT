package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.CircleTransform;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.databinding.FragmentDetalleHuespedBinding;
import com.squareup.picasso.Picasso;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.util.ArrayList;
import java.util.List;

public class DetalleHuespedFragment extends Fragment {

    private FragmentDetalleHuespedBinding binding;
    private ReservaCompletaHotel reservaCompleta;
    private String idReserva = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleHuespedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reservaCompleta = (ReservaCompletaHotel) getArguments().getSerializable("reservaCompleta");
        if (reservaCompleta == null) return;

        idReserva = reservaCompleta.getReserva().getIdReserva();

        mostrarDatosHuesped();
        mostrarDatosHabitacion();
        mostrarFechas();
        configurarBotones();
    }

    private void mostrarDatosHuesped() {
        String nombreCompleto = reservaCompleta.getUsuario().getNombres().split(" ")[0] + " " +
                reservaCompleta.getUsuario().getApellidos().split(" ")[0];

        binding.textNombre.setText(nombreCompleto);
        binding.textDni.setText(reservaCompleta.getUsuario().getNumDocumento());
        binding.textCorreo.setText(reservaCompleta.getUsuario().getEmail());
        binding.textTelefono.setText(reservaCompleta.getUsuario().getNumCelular());

        if (reservaCompleta.getUsuario().getUrlFotoPerfil() != null &&
                !reservaCompleta.getUsuario().getUrlFotoPerfil().isEmpty()) {
            Picasso.get()
                    .load(reservaCompleta.getUsuario().getUrlFotoPerfil())
                    .placeholder(R.drawable.ic_person)
                    .transform(new CircleTransform())
                    .into(binding.imgPerfilHuesped);
        }
    }

    private void mostrarDatosHabitacion() {
        HabitacionHotel habitacion = reservaCompleta.getHabitacion();
        binding.textTipoHabitacion.setText(habitacion.getTipo());
        binding.textCapacidad.setText(habitacion.getCapacidadAdultos() + " Adultos, " + habitacion.getCapacidadNinos() + " Niño(s)");
        binding.textPrecio.setText("S/ " + String.format("%.2f", habitacion.getPrecioPorNoche()));
        binding.textTamano.setText(habitacion.getTamanho() + " m²");

        mostrarEquipamiento(binding.gridEquipamiento, habitacion.getEquipamiento());
        mostrarServicios(binding.layoutServiciosDinamicos, habitacion.getServicio());
        mostrarCarrusel(habitacion.getFotosUrls());
    }

    private void mostrarFechas() {
        binding.checkinText.setText(reservaCompleta.getReserva().getFechaEntrada());
        binding.checkoutText.setText(reservaCompleta.getReserva().getFechaSalida());
    }

    private void configurarBotones() {
        binding.backdetallehuesped.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        binding.headerHabitacion.setOnClickListener(v -> {
            int visibility = binding.contentExpandible.getVisibility();
            binding.contentExpandible.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.iconExpand.setRotation(visibility == View.VISIBLE ? 0 : 180);
        });

        // Siempre habilitado
        binding.btnCheckout.setEnabled(true);
        binding.btnCheckout.setAlpha(1f);

        binding.btnCheckout.setOnClickListener(v -> {
            CheckoutFragment fragment = new CheckoutFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("reservaCompleta", reservaCompleta);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void mostrarEquipamiento(GridLayout contenedor, List<String> items) {
        contenedor.removeAllViews();
        Context context = getContext();
        int padding = 12;

        for (String item : items) {
            TextView tv = new TextView(context);
            tv.setText("\u2022 " + item);
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setPadding(padding, padding, padding, padding);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            tv.setLayoutParams(params);
            contenedor.addView(tv);
        }
    }

    private void mostrarServicios(LinearLayout contenedor, List<String> servicios) {
        contenedor.removeAllViews();
        for (String servicio : servicios) {
            TextView tv = new TextView(getContext());
            tv.setText("\u2022 " + servicio);
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setPadding(0, 6, 0, 6);
            contenedor.addView(tv);
        }
    }

    private void mostrarCarrusel(List<String> urls) {
        ImageCarousel carrusel = binding.carruselImagenes;

        if (urls != null && !urls.isEmpty()) {
            List<CarouselItem> items = new ArrayList<>();
            for (String url : urls) {
                items.add(new CarouselItem(url));
            }
            carrusel.setCarouselType(CarouselType.BLOCK);
            carrusel.setAutoPlay(true);
            carrusel.setData(items);
        } else {
            carrusel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
