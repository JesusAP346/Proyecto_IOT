package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterBirthdateFragmentSuperAdmin extends Fragment {

    public RegisterBirthdateFragmentSuperAdmin() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_birthdate_super_admin, container, false);

        UsuarioAdminViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        // Referencias UI
        TextInputLayout inputLayout = view.findViewById(R.id.fechaNac);
        TextInputEditText editTextFecha = view.findViewById(R.id.etFechaNacimiento);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        Button botonRegresar = view.findViewById(R.id.botonRegresar);

        // Botón regresar
        botonRegresar.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Botón siguiente
        botonSiguiente.setOnClickListener(v -> {
            String fechaNacimiento = editTextFecha.getText() != null ? editTextFecha.getText().toString().trim() : "";

            boolean isValid = true;

            if (fechaNacimiento.isEmpty()) {
                inputLayout.setError("Debe seleccionar una fecha de nacimiento");
                isValid = false;
            } else {
                if (!esMayorDe18Anos(fechaNacimiento)) {
                    inputLayout.setError("El administrador debe ser mayor de 18 años");
                    isValid = false;
                } else {
                    inputLayout.setError(null);
                }
            }

            if (isValid) {
                viewModel.actualizarCampo("fechaNacimiento", fechaNacimiento);

                // Ir al siguiente paso: Asignar hotel
                RegisterContactFragmentSuperAdmin pasoSiguiente = new RegisterContactFragmentSuperAdmin();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, pasoSiguiente);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // DatePicker al hacer clic
        editTextFecha.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog selectorFecha = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        editTextFecha.setText(fechaSeleccionada);
                        inputLayout.setError(null);
                    },
                    año, mes, dia
            );

            selectorFecha.getDatePicker().setMaxDate(System.currentTimeMillis());
            selectorFecha.show();
        });

        return view;
    }

    private boolean esMayorDe18Anos(String fechaNacimiento) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaNac = sdf.parse(fechaNacimiento);
            if (fechaNac == null) return false;

            Calendar calNacimiento = Calendar.getInstance();
            calNacimiento.setTime(fechaNac);

            Calendar calActual = Calendar.getInstance();
            int edad = calActual.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR);

            if (calActual.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--;
            }

            return edad >= 18;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
