package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.Reserva;
import com.example.proyecto_iot.databinding.FragmentCheckoutBinding;

import java.util.*;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private Reserva reserva;

    private final List<ItemCosto> consumoList = new ArrayList<>();
    private final List<ItemCosto> cargosList = new ArrayList<>();
    private final List<ItemCosto> serviciosExtraList = new ArrayList<>();

    private final Map<String, Double> mapaPreciosServicios = new HashMap<String, Double>() {{
        put("WiFi", 15.0);
        put("Desayuno", 20.0);
        put("Spa", 45.0);
        put("Lavandería", 25.0);
        put("Gimnasio", 30.0);
        put("Trampa", 150.0);
    }};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        reserva = (Reserva) getArguments().getSerializable("reserva");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupSpinnerServicios();

        if (reserva != null) {
            binding.textNombreHabitacion.setText(reserva.getTipoHabitacion());
            binding.textPrecioHabitacion.setText(String.format("S/. %.2f", reserva.getCostoReserva()));
            binding.textCheckin.setText("( " + reserva.getCheckIn() + " - ");
            binding.textCheckout.setText(reserva.getCheckOut() + " )");
            actualizarResumen();
        }

        binding.btnAgregarConsumo.setOnClickListener(v -> showDialog("Agregar Consumo", consumoList));
        binding.btnAgregarCargos.setOnClickListener(v -> showDialog("Agregar Cargo", cargosList));

        binding.editNochesExtras.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) actualizarResumen();
        });

        binding.spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String servicio = parent.getItemAtPosition(position).toString();
                if (!servicio.equals("Seleccionar servicio")) {
                    // No agregar si ya fue seleccionado
                    boolean yaExiste = false;
                    for (ItemCosto item : serviciosExtraList) {
                        if (item.nombre.equals(servicio)) {
                            yaExiste = true;
                            break;
                        }
                    }

                    if (!yaExiste) {
                        double precio = mapaPreciosServicios.getOrDefault(servicio, 0.0);
                        serviciosExtraList.add(new ItemCosto(servicio, precio));
                        actualizarResumen();
                    }

                    parent.setSelection(0);
                }

            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnProcesarPago.setOnClickListener(v -> mostrarDialogoConfirmacionPago());

        binding.backdetallecheckout.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnIrServicioTaxi.setOnClickListener(v -> {
            Fragment f = new ServicioTaxiFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, f)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showDialog(String titulo, List<ItemCosto> destino) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_consumo, null);
        EditText editItem = dialogView.findViewById(R.id.edit_item);
        EditText editCosto = dialogView.findViewById(R.id.edit_costo);
        editCosto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(getContext())
                .setTitle(titulo)
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = editItem.getText().toString().trim();
                    String costoStr = editCosto.getText().toString().trim();
                    if (!nombre.isEmpty() && !costoStr.isEmpty()) {
                        try {
                            double precio = Double.parseDouble(costoStr);
                            destino.add(new ItemCosto(nombre, precio));
                            actualizarResumen();
                        } catch (NumberFormatException ignored) {}
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupSpinnerServicios() {
        List<String> todosLosServicios = new ArrayList<>(mapaPreciosServicios.keySet());
        List<String> serviciosIncluidos = reserva != null ? reserva.getServiciosAdicionales() : new ArrayList<>();

        List<String> filtrados = new ArrayList<>();
        filtrados.add("Seleccionar servicio"); // opción inicial

        for (String servicio : todosLosServicios) {
            if (!serviciosIncluidos.contains(servicio)) {
                filtrados.add(servicio);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_small, filtrados);
        binding.spinnerServicios.setAdapter(adapter);
    }



    private void actualizarResumen() {
        if (reserva == null) return;

        double costoTotalReserva = reserva.getCostoReserva();
        double precioPorNoche = 0;

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date checkInDate = sdf.parse(reserva.getCheckIn());
            Date checkOutDate = sdf.parse(reserva.getCheckOut());

            if (checkInDate != null && checkOutDate != null) {
                long diffMillis = checkOutDate.getTime() - checkInDate.getTime();
                int diasHospedaje = (int) (diffMillis / (1000 * 60 * 60 * 24));
                if (diasHospedaje > 0) {
                    precioPorNoche = costoTotalReserva / diasHospedaje;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int nochesExtras = 0;
        try {
            String texto = binding.editNochesExtras.getText().toString().trim();
            nochesExtras = texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException ignored) {}

        double precioNochesExtras = nochesExtras * precioPorNoche;
        binding.textPrecioNocheExtra.setText(String.format("S/. %.2f", precioNochesExtras));

        // 🔁 LIMPIAR layouts antes de volver a pintar
        binding.layoutCostosDinamicos.removeAllViews();
        binding.layoutCargosDinamicos.removeAllViews();
        binding.layoutServiciosDinamicos.removeAllViews();

        // Suma todo
        double totalSinIGV = 0;
        totalSinIGV += costoTotalReserva + precioNochesExtras;

        totalSinIGV += pintarItems(binding.layoutCostosDinamicos, consumoList, consumoList, binding.textSinConsumo);
        totalSinIGV += pintarItems(binding.layoutCargosDinamicos, cargosList, cargosList, binding.textSinCargos);
        totalSinIGV += pintarItems(binding.layoutServiciosDinamicos, serviciosExtraList, serviciosExtraList, binding.textSinServicios);

        double totalFinal = totalSinIGV; // Este es el total que ya incluye el IGV

        double igv = totalFinal * 0.18 / 1.18;
        double sinIGV = totalFinal - igv;

        binding.textCobroSinIgv.setText(String.format("S/. %.2f", sinIGV));
        binding.textIgv.setText(String.format("S/. %.2f", igv));
        binding.textPagoTotal.setText(String.format("S/. %.2f", totalFinal));
    }


    private View crearLineaResumen(String label, double valor) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        filaParams.setMargins(16, 8, 16, 8);
        fila.setLayoutParams(filaParams);

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvLabel.setText(label);
        tvLabel.setTextSize(14);
        tvLabel.setTextColor(getResources().getColor(android.R.color.black));
        if (label.equals("Pago total")) {
            tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        TextView tvValor = new TextView(requireContext());
        tvValor.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tvValor.setText(String.format("S/. %.2f", valor));
        tvValor.setTextSize(14);
        tvValor.setTextColor(getResources().getColor(android.R.color.black));
        if (label.equals("Pago total")) {
            tvValor.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        fila.addView(tvLabel);
        fila.addView(tvValor);

        return fila;
    }

    private double pintarItems(LinearLayout layout, List<ItemCosto> lista, List<ItemCosto> listaEditable, TextView mensajeVacio) {
        double total = 0;

        if (mensajeVacio != null) {
            mensajeVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }

        for (ItemCosto item : lista) {
            View fila = crearFilaCosto(item, listaEditable);
            layout.addView(fila);

            total += item.precio;
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
            icono.setVisibility(View.GONE); // No editable
        }

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvNombre.setText(item.nombre);
        tvNombre.setTextSize(14);
        tvNombre.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvCosto = new TextView(requireContext());
        tvCosto.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tvCosto.setText(String.format("S/. %.2f", item.precio));
        tvCosto.setTextSize(14);
        tvCosto.setTextColor(getResources().getColor(android.R.color.black));

        fila.addView(icono);
        fila.addView(tvNombre);
        fila.addView(tvCosto);

        // Agrega la fila
        contenedor.addView(fila);

        // 🔻 Separador DENTRO del contenedor (mejorado con grosor + margen inferior)
        View separador = new View(requireContext());
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 👈 grosor: 1dp
        );
        sepParams.setMargins(16, 0, 16, 8); // 👈 margen inferior más notorio
        separador.setLayoutParams(sepParams);
        separador.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC")); // gris claro

        contenedor.addView(separador);

        return contenedor;
    }


    private View crearSeparador() {
        View line = new View(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        );
        params.setMargins(16, 0, 16, 0);
        line.setLayoutParams(params);
        line.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
        return line;
    }

    private void actualizarTextoEnLinea(ViewGroup layout, String label, String valor) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) v;
                if (row.getChildCount() == 2 && row.getChildAt(0) instanceof TextView) {
                    TextView tv = (TextView) row.getChildAt(0);
                    if (tv.getText().toString().equals(label)) {
                        TextView tvValor = (TextView) row.getChildAt(1);
                        tvValor.setText(valor);
                        break;
                    }
                }
            }
        }
    }

    static class ItemCosto {
        String nombre;
        double precio;
        ItemCosto(String nombre, double precio) {
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    private void mostrarDialogoConfirmacionPago() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmación de Pago")
                .setMessage("¿Deseas confirmar el pago?")
                .setPositiveButton("Confirmar", (dialog, which) -> simularProcesamientoDePago())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void simularProcesamientoDePago() {
        AlertDialog loadingDialog = new AlertDialog.Builder(getContext())
                .setTitle("Procesando")
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .create();

        loadingDialog.show();

        new android.os.Handler().postDelayed(() -> {
            loadingDialog.dismiss();
            boolean pagoExitoso = Math.random() < 0.9;
            new AlertDialog.Builder(getContext())
                    .setTitle(pagoExitoso ? "✅ Pago Exitoso" : "❌ Error en el Pago")
                    .setMessage(pagoExitoso ? "El pago se procesó correctamente." : "Hubo un error al procesar el pago.")
                    .setPositiveButton("OK", null)
                    .show();
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
