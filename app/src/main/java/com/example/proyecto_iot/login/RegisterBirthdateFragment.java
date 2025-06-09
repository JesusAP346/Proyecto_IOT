package com.example.proyecto_iot.login;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterBirthdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterBirthdateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterBirthdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterBirthdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterBirthdateFragment newInstance(String param1, String param2) {
        RegisterBirthdateFragment fragment = new RegisterBirthdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_birthdate, container, false);

        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);

        // Referencias a los elementos
        TextInputLayout inputLayout = view.findViewById(R.id.fechaNac);
        TextInputEditText editTextFecha = (TextInputEditText) inputLayout.getEditText();
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        Button botonRegresar = view.findViewById(R.id.botonRegresar);

        // Botón regresar
        botonRegresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getParentFragmentManager().popBackStack();
            }
        });

        // Botón siguiente con validaciones
        botonSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String fechaNacimiento = editTextFecha.getText() != null ? editTextFecha.getText().toString().trim() : "";

                boolean isValid = true;

                // Validar que no esté vacío
                if (fechaNacimiento.isEmpty()) {
                    inputLayout.setError("Debe seleccionar una fecha de nacimiento");
                    isValid = false;
                } else {
                    // Validar que sea mayor de 13 años
                    if (!esMayorDe13Anos(fechaNacimiento)) {
                        inputLayout.setError("Debe ser mayor de 13 años");
                        isValid = false;
                    } else {
                        inputLayout.setError(null);
                    }
                }

                // Si la validación pasa, continuar al siguiente fragmento
                if (isValid) {
                    viewModel.actualizarCampo("fechaNacimiento", fechaNacimiento);
                    RegisterContactFragment registerContactFragment = new RegisterContactFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, registerContactFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        // Configurar DatePicker
        if (editTextFecha != null) {
            editTextFecha.setOnClickListener(v -> {
                final Calendar calendario = Calendar.getInstance();
                int año = calendario.get(Calendar.YEAR);
                int mes = calendario.get(Calendar.MONTH);
                int dia = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog selectorFecha = new DatePickerDialog(
                        requireContext(),
                        (view1, year, month, dayOfMonth) -> {
                            // Formato: DD/MM/AAAA
                            String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                            editTextFecha.setText(fechaSeleccionada);
                            // Limpiar error cuando se selecciona una fecha
                            inputLayout.setError(null);
                        },
                        año, mes, dia
                );

                // Establecer fecha máxima (hoy) para evitar fechas futuras
                selectorFecha.getDatePicker().setMaxDate(System.currentTimeMillis());

                selectorFecha.show();
            });
        }

        return view;
    }

    /**
     * Método para validar si la persona es mayor de 13 años
     * @param fechaNacimiento fecha en formato DD/MM/AAAA
     * @return true si es mayor de 13 años, false en caso contrario
     */
    private boolean esMayorDe13Anos(String fechaNacimiento) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaNac = sdf.parse(fechaNacimiento);

            if (fechaNac == null) return false;

            Calendar fechaNacimiento_cal = Calendar.getInstance();
            fechaNacimiento_cal.setTime(fechaNac);

            Calendar fechaActual = Calendar.getInstance();

            // Calcular la edad
            int edad = fechaActual.get(Calendar.YEAR) - fechaNacimiento_cal.get(Calendar.YEAR);

            // Ajustar si aún no ha cumplido años este año
            if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaNacimiento_cal.get(Calendar.DAY_OF_YEAR)) {
                edad--;
            }

            return edad >= 13;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}