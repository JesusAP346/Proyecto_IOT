package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.DatosCheckout;
import com.example.proyecto_iot.administradorHotel.entity.DatosPago;
import com.example.proyecto_iot.administradorHotel.entity.EstadoReservaUI;
import com.example.proyecto_iot.administradorHotel.entity.ItemCosto;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ServicioAdicionalNombrePrecio;
import com.example.proyecto_iot.databinding.FragmentCheckoutHistorialBinding;

import java.util.List;

public class CheckoutHistorialFragment extends Fragment {

    private FragmentCheckoutHistorialBinding binding;
    private ReservaCompletaHotel reservaCompleta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutHistorialBinding.inflate(inflater, container, false);
        reservaCompleta = (ReservaCompletaHotel) getArguments().getSerializable("reservaCompleta");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (reservaCompleta == null) return;

        ReservaHotel reserva = reservaCompleta.getReserva();
        HabitacionHotel habitacion = reservaCompleta.getHabitacion();
        DatosCheckout datos = reserva.getDatosCheckout();

        // Datos de habitación
        binding.textNombreHabitacion.setText(habitacion.getTipo());
        int cantNoches = reserva.getCantNoches();
        binding.textCantNoches.setText("(por " + cantNoches + " noche" + (cantNoches > 1 ? "s" : "") + ")");
        double totalHabitacion = habitacion.getPrecioPorNoche() * cantNoches;
        binding.textPrecioHabitacion.setText(String.format("S/. %.2f", totalHabitacion));

        // Fechas
        binding.textCheckin.setText("( " + reserva.getFechaEntrada() + " - ");
        binding.textCheckout.setText(reserva.getFechaSalida() + " )");

        // Noches extra
        if (datos != null) {
            binding.textNochesExtra.setText(String.valueOf(datos.getNochesExtra()));
            binding.textPrecioNocheExtra.setText(String.format("S/. %.2f", datos.getPrecioNochesExtra()));

            // Subtotal, IGV, Total
            binding.textCobroSinIgv.setText(String.format("S/. %.2f", datos.getSubtotalSinIGV()));
            binding.textIgv.setText(String.format("S/. %.2f", datos.getIgv()));
            binding.textTotal.setText(String.format("S/. %.2f", datos.getTotal()));
        }

        // Servicios extra
        mostrarServiciosAdicionalesDesdeReserva();

        // Consumo y daños
        mostrarConsumosDesdeReserva();
        mostrarCargosDesdeReserva();

        // Datos de tarjeta
        mostrarDatosTarjeta();

        // Servicio de taxi habilitado
        if (Boolean.TRUE.equals(reserva.getServicioTaxiHabilitado())) {
            binding.btnIrServicioTaxi.setVisibility(View.VISIBLE);
            binding.textSinServicioTaxi.setVisibility(View.GONE);
        } else {
            binding.btnIrServicioTaxi.setVisibility(View.GONE);
            binding.textSinServicioTaxi.setVisibility(View.VISIBLE);
        }

        // Botón back
        binding.backdetallecheckout.setOnClickListener(v -> {
            // Restaurar pestaña "finalizadas"
            EstadoReservaUI.seccionSeleccionada = "finalizadas";

            // Ir directamente a ReservasFragment
            Fragment fragment = new ReservasFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
        });

        // Botón taxi con reservaCompleta enviada
        binding.btnIrServicioTaxi.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("reservaCompleta", reservaCompleta);

            Fragment f = new ServicioTaxiFragment();
            f.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, f)
                    .addToBackStack(null)
                    .commit();
        });
    }



    private void mostrarServiciosAdicionalesDesdeReserva() {
        List<ServicioAdicionalNombrePrecio> servicios = reservaCompleta.getServiciosAdicionalesInfo();
        binding.layoutServiciosDinamicos.removeAllViews();

        if (servicios == null || servicios.isEmpty()) {
            binding.textSinServicios.setVisibility(View.VISIBLE);
        } else {
            binding.textSinServicios.setVisibility(View.GONE);
            for (ServicioAdicionalNombrePrecio s : servicios) {
                agregarFilaCosto(binding.layoutServiciosDinamicos, s.getNombre(), s.getPrecio());
            }
        }
    }

    private void mostrarConsumosDesdeReserva() {
        DatosCheckout datos = reservaCompleta.getReserva().getDatosCheckout();
        binding.layoutConsumoDinamicos.removeAllViews();

        if (datos != null && datos.getConsumos() != null && !datos.getConsumos().isEmpty()) {
            binding.textSinConsumo.setVisibility(View.GONE);
            for (ItemCosto c : datos.getConsumos()) {
                agregarFilaCosto(binding.layoutConsumoDinamicos, c.getNombre(), c.getPrecio());
            }
        } else {
            binding.textSinConsumo.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarCargosDesdeReserva() {
        DatosCheckout datos = reservaCompleta.getReserva().getDatosCheckout();
        binding.layoutCargosDinamicos.removeAllViews();

        if (datos != null && datos.getCargos() != null && !datos.getCargos().isEmpty()) {
            binding.textSinCargosPorDanos.setVisibility(View.GONE);
            for (ItemCosto c : datos.getCargos()) {
                agregarFilaCosto(binding.layoutCargosDinamicos, c.getNombre(), c.getPrecio());
            }
        } else {
            binding.textSinCargosPorDanos.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDatosTarjeta() {
        DatosPago pago = reservaCompleta.getReserva().getDatosPago();
        if (pago != null) {
            binding.textMarcaSimple.setText(pago.getMarca());
            binding.textUltimosDigitos.setText(" " + pago.getNumeroTarjetaEnmascarado());
            binding.textTitularSimple.setText(pago.getTitular());
        }
    }

    private void agregarFilaCosto(LinearLayout layout, String nombre, double precio) {
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
        layout.addView(fila);

        View separador = new View(requireContext());
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2);
        sepParams.setMargins(16, 0, 16, 8);
        separador.setLayoutParams(sepParams);
        separador.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));

        layout.addView(separador);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
