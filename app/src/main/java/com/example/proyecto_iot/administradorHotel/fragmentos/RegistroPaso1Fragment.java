package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.viewmodel.HabitacionViewModel;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso1Binding;


public class RegistroPaso1Fragment extends Fragment {

    FragmentRegistroPaso1Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso1Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setupTextWatchers();
        setupClickListeners();
        loadExistingData();

        return view;
    }

    private void setupTextWatchers() {
        // TextWatcher para tipo de habitación
        binding.textTipoHabitacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ocultarError(binding.errorTipoHabitacion);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher para tamaño
        binding.textTamanoHabitacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ocultarError(binding.errorTamanoHabitacion);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher para precio
        binding.textPrecioPorNoche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ocultarError(binding.errorPrecioPorNoche);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher para cantidad
        binding.textCantidadHabitaciones.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ocultarError(binding.errorCantidadHabitaciones);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        binding.btnMasAdultos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
            if (count < 20) {
                binding.textCantidadAdultos.setText(String.valueOf(count + 1));
                ocultarError(binding.errorCapacidad);
            }
        });

        binding.btnMenosAdultos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
            if (count > 0) {
                binding.textCantidadAdultos.setText(String.valueOf(count - 1));
                ocultarError(binding.errorCapacidad);
            }
        });

        binding.btnMasNinos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
            if (count < 10) {
                binding.textCantidadNinos.setText(String.valueOf(count + 1));
                ocultarError(binding.errorCapacidad);
            }
        });

        binding.btnMenosNinos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
            if (count > 0) {
                binding.textCantidadNinos.setText(String.valueOf(count - 1));
                ocultarError(binding.errorCapacidad);
            }
        });

        binding.btnSiguientePaso.setOnClickListener(v -> {
            if (validarFormulario()) {
                String tipo = binding.textTipoHabitacion.getText().toString().trim();
                int adultos = Integer.parseInt(binding.textCantidadAdultos.getText().toString().trim());
                int ninos = Integer.parseInt(binding.textCantidadNinos.getText().toString().trim());
                int tamanho = Integer.parseInt(binding.textTamanoHabitacion.getText().toString().trim());
                double precio = Double.parseDouble(binding.textPrecioPorNoche.getText().toString().trim());
                int cantidad = Integer.parseInt(binding.textCantidadHabitaciones.getText().toString().trim());

                HabitacionViewModel viewModel = new ViewModelProvider(requireActivity()).get(HabitacionViewModel.class);
                viewModel.actualizarCampo("tipo", tipo);
                viewModel.actualizarCampo("tamanho", tamanho);
                viewModel.actualizarCampo("precioPorNoche", precio);
                viewModel.actualizarCampo("cantidadHabitaciones", cantidad);
                viewModel.actualizarCampo("capacidadAdultos", adultos);
                viewModel.actualizarCampo("capacidadNinos", ninos);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerHabitacion, new RegistroPaso2Fragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void loadExistingData() {
        HabitacionViewModel viewModel = new ViewModelProvider(requireActivity()).get(HabitacionViewModel.class);
        HabitacionHotel habitacionn = viewModel.getHabitacion().getValue();

        if (habitacionn != null) {
            if (habitacionn.getTipo() != null)
                binding.textTipoHabitacion.setText(habitacionn.getTipo());

            if (habitacionn.getTamanho() > 0)
                binding.textTamanoHabitacion.setText(String.valueOf(habitacionn.getTamanho()));

            if (habitacionn.getPrecioPorNoche() > 0)
                binding.textPrecioPorNoche.setText(String.valueOf(habitacionn.getPrecioPorNoche()));

            if (habitacionn.getCantidadHabitaciones() > 0)
                binding.textCantidadHabitaciones.setText(String.valueOf(habitacionn.getCantidadHabitaciones()));

            binding.textCantidadAdultos.setText(String.valueOf(habitacionn.getCapacidadAdultos()));
            binding.textCantidadNinos.setText(String.valueOf(habitacionn.getCapacidadNinos()));
        }
    }

    private boolean validarFormulario() {
        boolean esValido = true;

        // Limpiar todos los errores primero
        ocultarTodosLosErrores();

        // Validar tipo de habitación
        String tipo = binding.textTipoHabitacion.getText().toString().trim();
        if (tipo.isEmpty()) {
            mostrarError(binding.errorTipoHabitacion, "Por favor, ingrese el tipo de habitación");
            binding.textTipoHabitacion.requestFocus();
            esValido = false;
        } else if (tipo.length() < 3) {
            mostrarError(binding.errorTipoHabitacion, "El tipo debe tener al menos 3 caracteres");
            binding.textTipoHabitacion.requestFocus();
            esValido = false;
        } else if (tipo.length() > 50) {
            mostrarError(binding.errorTipoHabitacion, "El tipo no puede exceder 50 caracteres");
            binding.textTipoHabitacion.requestFocus();
            esValido = false;
        }

        // Validar capacidad
        int adultos = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
        int ninos = Integer.parseInt(binding.textCantidadNinos.getText().toString());

        if (adultos == 0 && ninos == 0) {
            mostrarError(binding.errorCapacidad, "Debe haber capacidad para al menos 1 persona");
            esValido = false;
        } else if (adultos == 0 && ninos > 0) {
            mostrarError(binding.errorCapacidad, "Debe haber al menos 1 adulto si hay niños");
            esValido = false;
        }

        // Validar tamaño de habitación
        String tamanhoTexto = binding.textTamanoHabitacion.getText().toString().trim();
        if (tamanhoTexto.isEmpty()) {
            mostrarError(binding.errorTamanoHabitacion, "Por favor, ingrese el tamaño de la habitación");
            if (esValido) binding.textTamanoHabitacion.requestFocus();
            esValido = false;
        } else {
            try {
                int tamanho = Integer.parseInt(tamanhoTexto);
                if (tamanho <= 0) {
                    mostrarError(binding.errorTamanoHabitacion, "El tamaño debe ser mayor a 0 m²");
                    if (esValido) binding.textTamanoHabitacion.requestFocus();
                    esValido = false;
                } else if (tamanho > 500) {
                    mostrarError(binding.errorTamanoHabitacion, "El tamaño no puede exceder 500 m²");
                    if (esValido) binding.textTamanoHabitacion.requestFocus();
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                mostrarError(binding.errorTamanoHabitacion, "Ingrese un número válido");
                if (esValido) binding.textTamanoHabitacion.requestFocus();
                esValido = false;
            }
        }

        // Validar precio por noche
        String precioTexto = binding.textPrecioPorNoche.getText().toString().trim();
        if (precioTexto.isEmpty()) {
            mostrarError(binding.errorPrecioPorNoche, "Por favor, ingrese el precio por noche");
            if (esValido) binding.textPrecioPorNoche.requestFocus();
            esValido = false;
        } else {
            try {
                double precio = Double.parseDouble(precioTexto);
                if (precio <= 0) {
                    mostrarError(binding.errorPrecioPorNoche, "El precio debe ser mayor a 0");
                    if (esValido) binding.textPrecioPorNoche.requestFocus();
                    esValido = false;
                } else if (precio > 10000) {
                    mostrarError(binding.errorPrecioPorNoche, "El precio no puede exceder S/. 10,000");
                    if (esValido) binding.textPrecioPorNoche.requestFocus();
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                mostrarError(binding.errorPrecioPorNoche, "Ingrese un precio válido");
                if (esValido) binding.textPrecioPorNoche.requestFocus();
                esValido = false;
            }
        }

        // Validar cantidad de habitaciones
        String cantidadTexto = binding.textCantidadHabitaciones.getText().toString().trim();
        if (cantidadTexto.isEmpty()) {
            mostrarError(binding.errorCantidadHabitaciones, "Por favor, ingrese la cantidad de habitaciones");
            if (esValido) binding.textCantidadHabitaciones.requestFocus();
            esValido = false;
        } else {
            try {
                int cantidad = Integer.parseInt(cantidadTexto);
                if (cantidad <= 0) {
                    mostrarError(binding.errorCantidadHabitaciones, "La cantidad debe ser mayor a 0");
                    if (esValido) binding.textCantidadHabitaciones.requestFocus();
                    esValido = false;
                } else if (cantidad > 100) {
                    mostrarError(binding.errorCantidadHabitaciones, "La cantidad no puede exceder 100");
                    if (esValido) binding.textCantidadHabitaciones.requestFocus();
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                mostrarError(binding.errorCantidadHabitaciones, "Ingrese un número válido");
                if (esValido) binding.textCantidadHabitaciones.requestFocus();
                esValido = false;
            }
        }

        return esValido;
    }

    private void mostrarError(View errorView, String mensaje) {
        if (errorView instanceof android.widget.TextView) {
            ((android.widget.TextView) errorView).setText(mensaje);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarError(View errorView) {
        errorView.setVisibility(View.GONE);
    }

    private void ocultarTodosLosErrores() {
        binding.errorTipoHabitacion.setVisibility(View.GONE);
        binding.errorCapacidad.setVisibility(View.GONE);
        binding.errorTamanoHabitacion.setVisibility(View.GONE);
        binding.errorPrecioPorNoche.setVisibility(View.GONE);
        binding.errorCantidadHabitaciones.setVisibility(View.GONE);
    }
}