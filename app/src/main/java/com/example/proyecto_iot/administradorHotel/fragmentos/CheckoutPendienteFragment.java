package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.DatosPago;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.ItemCosto;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ServicioAdicionalNombrePrecio;
import com.example.proyecto_iot.databinding.FragmentCheckoutBinding;
import com.example.proyecto_iot.databinding.FragmentCheckoutPendienteBinding;

import java.util.ArrayList;
import java.util.List;


public class CheckoutPendienteFragment extends Fragment {

    private FragmentCheckoutPendienteBinding binding;
    private ReservaCompletaHotel reservaCompleta;
    private final List<ItemCosto> consumoList = new ArrayList<>();
    private final List<ItemCosto> cargosList = new ArrayList<>();
    private final List<ItemCosto> serviciosExtraList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutPendienteBinding.inflate(inflater, container, false);
        reservaCompleta = (ReservaCompletaHotel) getArguments().getSerializable("reservaCompleta");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (reservaCompleta != null) {
            HabitacionHotel habitacion = reservaCompleta.getHabitacion();
            binding.textNombreHabitacion.setText(habitacion.getTipo());
            int cantNoches = reservaCompleta.getReserva().getCantNoches();
            binding.textCantNoches.setText("(por " + cantNoches + " noche" + (cantNoches > 1 ? "s" : "") + ")");

            double precioPorNoche = habitacion.getPrecioPorNoche();
            double precioPorNocheXdias = precioPorNoche * cantNoches;

            binding.textPrecioHabitacion.setText(String.format("S/. %.2f", precioPorNocheXdias));
            binding.textCheckin.setText("( " + reservaCompleta.getReserva().getFechaEntrada() + " - ");
            binding.textCheckout.setText(reservaCompleta.getReserva().getFechaSalida() + " )");

            actualizarResumen();
        }

        mostrarServiciosAdicionalesDesdeReserva();

        // Datos de tarjeta (metodo de pago)
        mostrarDatosTarjeta();

// ⛔️ DESACTIVAR ACCIONES
        binding.btnAgregarConsumo.setEnabled(false);
        binding.btnAgregarConsumo.setAlpha(0.5f);

        binding.btnAgregarCargos.setEnabled(false);
        binding.btnAgregarCargos.setAlpha(0.5f);

        binding.editNochesExtras.setEnabled(false);
        binding.editNochesExtras.setFocusable(false);
        binding.editNochesExtras.setAlpha(0.5f);

        binding.backdetallecheckout.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

    }
    private void mostrarDatosTarjeta() {
        DatosPago pago = reservaCompleta.getReserva().getDatosPago();
        if (pago != null) {
            binding.textMarcaSimple.setText(pago.getMarca());
            binding.textUltimosDigitos.setText(" " + pago.getNumeroTarjetaEnmascarado());
            binding.textTitularSimple.setText(pago.getTitular());
        }
    }

    private void mostrarServiciosAdicionalesDesdeReserva() {
        binding.layoutServiciosDinamicos.removeAllViews();

        List<ServicioAdicionalNombrePrecio> servicios = reservaCompleta.getServiciosAdicionalesInfo();

        if (servicios == null || servicios.isEmpty()) {
            binding.textSinServicios.setText("Sin servicios extra agregados");
            binding.textSinServicios.setVisibility(View.VISIBLE);
            return;
        }

        binding.textSinServicios.setVisibility(View.GONE);

        for (ServicioAdicionalNombrePrecio s : servicios) {
            agregarFilaServicio(s.getNombre(), s.getPrecio());
        }
    }

    private void agregarFilaServicio(String nombre, double precio) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        filaParams.setMargins(16, 8, 16, 8);
        fila.setLayoutParams(filaParams);

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvNombre.setText(nombre);
        tvNombre.setTextSize(14);
        tvNombre.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvPrecio = new TextView(requireContext());
        tvPrecio.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvPrecio.setText(String.format("S/. %.2f", precio));
        tvPrecio.setTextSize(14);
        tvPrecio.setTextColor(getResources().getColor(android.R.color.black));

        fila.addView(tvNombre);
        fila.addView(tvPrecio);

        binding.layoutServiciosDinamicos.addView(fila);

        View separador = new View(requireContext());
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2);
        sepParams.setMargins(16, 0, 16, 8);
        separador.setLayoutParams(sepParams);
        separador.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));

        binding.layoutServiciosDinamicos.addView(separador);
    }

    private void actualizarResumen() {
        if (reservaCompleta == null) return;

        double precioPorNoche = reservaCompleta.getHabitacion().getPrecioPorNoche();
        double costoTotalReserva = precioPorNoche * reservaCompleta.getReserva().getCantNoches();

        int nochesExtras = 0;
        try {
            String texto = binding.editNochesExtras.getText().toString().trim();
            nochesExtras = texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException ignored) {}

        double precioNochesExtras = nochesExtras * precioPorNoche;
        binding.textPrecioNocheExtra.setText(String.format("S/. %.2f", precioNochesExtras));

        binding.layoutCostosDinamicos.removeAllViews();
        binding.layoutCargosDinamicos.removeAllViews();


        double totalSinIGV = costoTotalReserva + precioNochesExtras;
        // Sumar precio de servicios adicionales que vienen desde reservaCompleta
        List<ServicioAdicionalNombrePrecio> servicios = reservaCompleta.getServiciosAdicionalesInfo();
        if (servicios != null) {
            for (ServicioAdicionalNombrePrecio s : servicios) {
                totalSinIGV += s.getPrecio();
            }
        }
        totalSinIGV += pintarItems(binding.layoutCostosDinamicos, consumoList, consumoList, binding.textSinConsumo);
        totalSinIGV += pintarItems(binding.layoutCargosDinamicos, cargosList, cargosList, binding.textSinCargos);
        for (ItemCosto s : serviciosExtraList) {
            totalSinIGV += s.getPrecio(); // si es que en el futuro agregas más servicios manualmente
        }

        double igv = totalSinIGV * 0.18 / 1.18;
        double sinIGV = totalSinIGV - igv;

        binding.textCobroSinIgv.setText(String.format("S/. %.2f", sinIGV));
        binding.textIgv.setText(String.format("S/. %.2f", igv));
        binding.textPagoTotal.setText(String.format("S/. %.2f", totalSinIGV));
    }

    private double pintarItems(LinearLayout layout, List<ItemCosto> lista, List<ItemCosto> listaEditable, TextView mensajeVacio) {
        double total = 0;
        if (mensajeVacio != null) {
            mensajeVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }
        for (ItemCosto item : lista) {
            View fila = crearFilaCosto(item, listaEditable);
            layout.addView(fila);
            total += item.getPrecio();
        }
        return total;
    }

    private View crearFilaCosto(ItemCosto item, List<ItemCosto> listaEditable) {
        LinearLayout contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        filaParams.setMargins(16, 8, 16, 8);
        fila.setLayoutParams(filaParams);

        ImageView icono = new ImageView(requireContext());
        icono.setImageResource(R.drawable.ic_delete);
        icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
        iconParams.setMargins(0, 0, 16, 0);
        icono.setLayoutParams(iconParams);

        if (listaEditable != null) {
            icono.setOnClickListener(v -> {
                listaEditable.remove(item);
                actualizarResumen();
            });
        } else {
            icono.setVisibility(View.GONE);
        }

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvNombre.setText(item.getNombre());
        tvNombre.setTextSize(14);
        tvNombre.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvCosto = new TextView(requireContext());
        tvCosto.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tvCosto.setText(String.format("S/. %.2f", item.getPrecio()));
        tvCosto.setTextSize(14);
        tvCosto.setTextColor(getResources().getColor(android.R.color.black));

        fila.addView(icono);
        fila.addView(tvNombre);
        fila.addView(tvCosto);

        contenedor.addView(fila);

        View separador = new View(requireContext());
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
        );
        sepParams.setMargins(16, 0, 16, 8);
        separador.setLayoutParams(sepParams);
        separador.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));

        contenedor.addView(separador);

        return contenedor;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}