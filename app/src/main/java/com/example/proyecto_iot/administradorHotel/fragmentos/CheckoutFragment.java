package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
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

    private final Map<String, Double> precioHabitacionMap = new HashMap<String, Double>() {{
        put("Stándar", 500.0);
        put("Deluxe", 600.0);
        put("Suite Ejecutiva", 750.0);
        put("Familiar", 800.0);
        put("Suite Presidencial", 1000.0);
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
            binding.textCheckout.setText("(⚠ Check out: " + reserva.getCheckOut() + ")");
            actualizarResumen();
        }

        binding.btnAgregarConsumo.setOnClickListener(v -> showDialog("Agregar Consumo", consumoList));
        binding.btnAgregarCargos.setOnClickListener(v -> showDialog("Agregar Cargo", cargosList));

        binding.editNochesExtras.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                actualizarResumen();
            }
        });
        binding.spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String servicio = parent.getItemAtPosition(position).toString();
                if (!servicio.equals("Seleccionar servicio")) {
                    serviciosExtraList.add(new ItemCosto(servicio, 30.0));
                    actualizarResumen();
                    parent.setSelection(0);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnProcesarPago.setOnClickListener(v -> mostrarDialogoConfirmacionPago());

        binding.backdetallecheckout.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

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
        List<String> lista = Arrays.asList("Seleccionar servicio", "WiFi", "Desayuno", "Spa", "Lavandería");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_small, lista);
        binding.spinnerServicios.setAdapter(adapter);
    }

    private void actualizarResumen() {
        double precioHabitacion = precioHabitacionMap.getOrDefault(reserva.getTipoHabitacion(), 500.0);

        // Obtener número de noches extras desde el EditText
        int nochesExtras = 0;
        try {
            String texto = binding.editNochesExtras.getText().toString().trim();
            nochesExtras = texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException ignored) {}

        // Calcular precio por noche extra
        double precioNochesExtras = nochesExtras * precioHabitacion;
        binding.textPrecioNocheExtra.setText(String.format("S/. %.2f", precioNochesExtras));

        // Calcular total sin IGV
        double totalSinIGV = precioHabitacion + precioNochesExtras;

        // Agregar consumos, cargos, servicios extra
        LinearLayout layout = binding.layoutCostosDinamicos;
        totalSinIGV += agregarItemsADisplay(layout, R.id.btn_agregar_consumo, consumoList);
        totalSinIGV += agregarItemsADisplay(layout, R.id.btn_agregar_cargos, cargosList);
        totalSinIGV += agregarItemsADisplay(layout, R.id.spinner_servicios, serviciosExtraList);

        // Calcular IGV y total final
        double igv = totalSinIGV * 0.18;
        double totalFinal = totalSinIGV + igv;

        actualizarTextoEnLinea(layout, "Cobro sin IGV", String.format("S/. %.2f", totalSinIGV));
        actualizarTextoEnLinea(layout, "IGV (18%)", String.format("S/. %.2f", igv));
        actualizarTextoEnLinea(layout, "Pago total", String.format("S/. %.2f", totalFinal));
    }


    private double agregarItemsADisplay(ViewGroup layout, int belowViewId, List<ItemCosto> items) {
        double total = 0;
        int insertIndex = getInsertIndex(layout, belowViewId);

        for (ItemCosto item : items) {
            View fila = crearFilaCosto(item.nombre, item.precio);
            View separador = crearLinea();
            layout.addView(fila, insertIndex++);
            layout.addView(separador, insertIndex++);
            total += item.precio;
        }

        items.clear();
        return total;
    }

    private int getInsertIndex(ViewGroup layout, int referenceId) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view.findViewById(referenceId) != null) return i + 1;
        }
        return layout.getChildCount();
    }

    private View crearFilaCosto(String nombre, double costo) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvNombre.setText(nombre);
        tvNombre.setTextSize(14);

        TextView tvCosto = new TextView(requireContext());
        tvCosto.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvCosto.setText(String.format("S/. %.2f", costo));
        tvCosto.setTextSize(14);

        fila.addView(tvNombre);
        fila.addView(tvCosto);
        return fila;
    }

    private View crearLinea() {
        View line = new View(requireContext());
        line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
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

            boolean pagoExitoso = Math.random() < 0.8;
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
