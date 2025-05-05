package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentCheckoutBinding;

import java.util.Arrays;
import java.util.List;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        binding.backdetallecheckout.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        binding.btnIrServicioTaxi.setOnClickListener(v -> {

            Fragment servicioTaxiFragment = new ServicioTaxiFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, servicioTaxiFragment)
                    .addToBackStack(null)
                    .commit();
        });
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAgregarConsumo.setOnClickListener(v -> showAgregarConsumoDialog());
        binding.btnAgregarCargos.setOnClickListener(v -> showAgregarCargoDialog());
        binding.btnProcesarPago.setOnClickListener(v -> mostrarDialogoConfirmacionPago());
        setupSpinnerServicios();
    }

    private void showAgregarConsumoDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_consumo, null);

        EditText editItem = dialogView.findViewById(R.id.edit_item);
        EditText editCosto = dialogView.findViewById(R.id.edit_costo);

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Consumo")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    // Aquí iría la lógica más adelante
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void showAgregarCargoDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_cargo, null);

        EditText editCargo = dialogView.findViewById(R.id.edit_item);
        EditText editCosto = dialogView.findViewById(R.id.edit_costo);

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Cargo")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    // Aquí iría la lógica más adelante
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupSpinnerServicios() {
        List<String> listaServicios = Arrays.asList("Seleccionar servicio", "WiFi", "Desayuno", "Spa", "Lavandería");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_small,   // <-- Este es el cambio importante
                listaServicios
        );

        adapter.setDropDownViewResource(R.layout.spinner_item_small);  // <-- Opcional: también para el dropdown
        binding.spinnerServicios.setAdapter(adapter);
    }
    private void mostrarDialogoConfirmacionPago() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmación de Pago")
                .setMessage("¿Deseas confirmar el pago?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    simularProcesamientoDePago();
                })
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

        new Handler().postDelayed(() -> {
            loadingDialog.dismiss();

            boolean pagoExitoso = Math.random() < 0.7; // 70% de éxito

            new AlertDialog.Builder(getContext())
                    .setTitle(pagoExitoso ? "✅ Pago Exitoso" : "❌ Error en el Pago")
                    .setMessage(pagoExitoso ? "El pago se procesó correctamente." : "Hubo un error al procesar el pago.")
                    .setPositiveButton("OK", null)
                    .show();

        }, 2000); // Simula espera de 2 segundos
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
