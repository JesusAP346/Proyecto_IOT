package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.DatosPago;
import com.example.proyecto_iot.administradorHotel.entity.EstadoReservaUI;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.ItemCosto;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ServicioAdicionalNombrePrecio;
import com.example.proyecto_iot.cliente.busqueda.ServicioAdicionalReserva;
import com.example.proyecto_iot.databinding.FragmentCheckoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private ReservaCompletaHotel reservaCompleta;

    private final List<ItemCosto> consumoList = new ArrayList<>();
    private final List<ItemCosto> cargosList = new ArrayList<>();
    private final List<ItemCosto> serviciosExtraList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
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

            String estado = reservaCompleta.getReserva().getEstado();
            if (estado != null && estado.equalsIgnoreCase("ACTIVO")) {
                binding.errorTipoCheckout.setVisibility(View.VISIBLE);
                binding.errorTipoCheckout.setText("El huésped no ha realizado su checkout, no podrá continuar con el cobro.");

                binding.btnProcesarPago.setEnabled(false);
                binding.btnProcesarPago.setAlpha(0.4f);

                binding.btnAgregarConsumo.setEnabled(false);
                binding.btnAgregarConsumo.setAlpha(0.4f);

                binding.btnAgregarCargos.setEnabled(false);
                binding.btnAgregarCargos.setAlpha(0.4f);

                binding.editNochesExtras.setEnabled(false);
                binding.editNochesExtras.setAlpha(0.4f);

            } else {
                binding.errorTipoCheckout.setVisibility(View.GONE);

                binding.btnProcesarPago.setEnabled(true);
                binding.btnProcesarPago.setAlpha(1f);

                binding.btnAgregarConsumo.setEnabled(true);
                binding.btnAgregarConsumo.setAlpha(1f);

                binding.btnAgregarCargos.setEnabled(true);
                binding.btnAgregarCargos.setAlpha(1f);

                binding.editNochesExtras.setEnabled(true);
                binding.editNochesExtras.setAlpha(1f);
            }
        }

        binding.btnAgregarConsumo.setOnClickListener(v -> showDialog("Agregar Consumo", consumoList));
        binding.btnAgregarCargos.setOnClickListener(v -> showDialog("Agregar Cargo", cargosList));

        binding.editNochesExtras.setOnEditorActionListener((v, actionId, event) -> {
            actualizarResumen();
            ocultarTecladoYQuitarFoco(binding.editNochesExtras);
            return true;
        });

        binding.getRoot().setOnTouchListener((v, event) -> {
            if (binding.editNochesExtras.isFocused()) {
                ocultarTecladoYQuitarFoco(binding.editNochesExtras);
                actualizarResumen();
            }
            return false;
        });

        mostrarServiciosAdicionalesDesdeReserva();
        mostrarDatosTarjeta();
        binding.btnProcesarPago.setOnClickListener(v -> mostrarDialogoConfirmacionPago());

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
    private void ocultarTecladoYQuitarFoco(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

            if (pagoExitoso) {
                guardarCheckoutEnFirestore();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle(pagoExitoso ? "✅ Pago Exitoso" : "❌ Error en el Pago")
                    .setMessage(pagoExitoso ? "El pago se procesó correctamente." : "Hubo un error al procesar el pago.")
                    .setPositiveButton("OK", null)
                    .show();
        }, 2000);
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

    // Agrega este metodo al final de tu clase CheckoutFragment
    private void guardarCheckoutEnFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (reservaCompleta == null) return;

        Map<String, Object> datos = new HashMap<>();

        HabitacionHotel hab = reservaCompleta.getHabitacion();
        datos.put("tipoHabitacion", hab.getTipo());
        datos.put("precioPorNoche", hab.getPrecioPorNoche());
        datos.put("cantNoches", reservaCompleta.getReserva().getCantNoches());
        datos.put("precioHabitacionPorNNoches", hab.getPrecioPorNoche() * reservaCompleta.getReserva().getCantNoches());
        datos.put("fechaCheckIn", reservaCompleta.getReserva().getFechaEntrada());
        datos.put("fechaCheckOut", reservaCompleta.getReserva().getFechaSalida());

        // Noches extra
        int nochesExtra = 0;
        try {
            String texto = binding.editNochesExtras.getText().toString().trim();
            nochesExtra = texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException ignored) {}
        datos.put("nochesExtra", nochesExtra);
        datos.put("precioNochesExtra", nochesExtra * hab.getPrecioPorNoche());

        // Servicios adicionales
        List<Map<String, Object>> serviciosMap = new ArrayList<>();
        for (ServicioAdicionalNombrePrecio s : reservaCompleta.getServiciosAdicionalesInfo()) {
            Map<String, Object> m = new HashMap<>();
            m.put("nombre", s.getNombre());
            m.put("precio", s.getPrecio());
            serviciosMap.add(m);
        }
        datos.put("serviciosAdicionales", serviciosMap);

        // Consumos
        List<Map<String, Object>> consumoMap = new ArrayList<>();
        for (ItemCosto item : consumoList) {
            Map<String, Object> m = new HashMap<>();
            m.put("nombre", item.getNombre());
            m.put("precio", item.getPrecio());
            consumoMap.add(m);
        }
        datos.put("consumos", consumoMap);

        // Cargos
        List<Map<String, Object>> cargosMap = new ArrayList<>();
        for (ItemCosto item : cargosList) {
            Map<String, Object> m = new HashMap<>();
            m.put("nombre", item.getNombre());
            m.put("precio", item.getPrecio());
            cargosMap.add(m);
        }
        datos.put("cargos", cargosMap);

        // Subtotal, IGV y total
        double subtotal = 0;
        subtotal += hab.getPrecioPorNoche() * reservaCompleta.getReserva().getCantNoches();
        subtotal += nochesExtra * hab.getPrecioPorNoche();
        for (ServicioAdicionalNombrePrecio s : reservaCompleta.getServiciosAdicionalesInfo()) subtotal += s.getPrecio();
        for (ItemCosto item : consumoList) subtotal += item.getPrecio();
        for (ItemCosto item : cargosList) subtotal += item.getPrecio();

        double igv = subtotal * 0.18 / 1.18;
        double sinIgv = subtotal - igv;

        datos.put("subtotalSinIGV", sinIgv);
        datos.put("igv", igv);
        datos.put("total", subtotal);
        datos.put("timestamp", new Date());
        String idReserva = reservaCompleta.getReserva().getIdReserva();

        db.collection("reservas")
                .document(idReserva)
                .update("datosCheckout", datos)
                .addOnSuccessListener(aVoid -> {
                    // Ahora actualiza también el estado
                    db.collection("reservas")
                            .document(idReserva)
                            .update("estado", "FINALIZADO")
                            .addOnSuccessListener(unused -> {

                                // Luego de guardar todo, volver a obtener los datos actualizados
                                db.collection("reservas")
                                        .document(idReserva)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            ReservaHotel reservaActualizada = documentSnapshot.toObject(ReservaHotel.class);
                                            if (reservaActualizada != null) {
                                                reservaActualizada.setIdReserva(idReserva);
                                                reservaCompleta.setReserva(reservaActualizada); // actualizar reserva interna

                                                CheckoutHistorialFragment fragment = new CheckoutHistorialFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("reservaCompleta", reservaCompleta);
                                                fragment.setArguments(bundle);

                                                requireActivity().getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .replace(R.id.frame_layout, fragment)
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                        });
                            });
                })

                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
