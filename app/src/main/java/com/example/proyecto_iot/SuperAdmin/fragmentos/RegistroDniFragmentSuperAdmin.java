package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistroDniFragmentSuperAdmin extends Fragment {

    public RegistroDniFragmentSuperAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registro_dni_super_admin, container, false);

        UsuarioAdminViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        // Referencias UI
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView2);
        TextInputLayout inputTipoDoc = view.findViewById(R.id.inputTipoDoc);
        TextInputEditText etDocumento = view.findViewById(R.id.etDocumento);
        TextInputLayout layoutDocumento = view.findViewById(R.id.textInputLayout3);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        Button botonRegresar = view.findViewById(R.id.botonRegresar);

        // Adapter para tipos de documento
        String[] tiposDocumento = {"DNI", "Carnet de Extranjería", "Pasaporte"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, tiposDocumento);
        autoCompleteTextView.setAdapter(adapter);

        botonRegresar.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        botonSiguiente.setOnClickListener(v -> {
            String tipoDocumento = autoCompleteTextView.getText() != null ? autoCompleteTextView.getText().toString().trim() : "";
            String documento = etDocumento.getText() != null ? etDocumento.getText().toString().trim() : "";

            boolean isValid = true;

            // Validar tipo de documento
            if (tipoDocumento.isEmpty()) {
                inputTipoDoc.setError("Debe seleccionar un tipo de documento");
                isValid = false;
            } else {
                inputTipoDoc.setError(null);
            }

            // Validar número de documento
            if (documento.isEmpty()) {
                layoutDocumento.setError("Este campo es obligatorio");
                isValid = false;
            } else if (!documento.matches("\\d+")) {
                layoutDocumento.setError("Solo se permiten números");
                isValid = false;
            } else if (tipoDocumento.equals("DNI") && documento.length() != 8) {
                layoutDocumento.setError("DNI debe tener 8 dígitos");
                isValid = false;
            } else {
                layoutDocumento.setError(null);
            }

            if (isValid) {
                // Guardar en ViewModel
                viewModel.actualizarCampo("tipoDocumento", tipoDocumento);
                viewModel.actualizarCampo("numDocumento", documento);

                // Ir al siguiente paso: Asignación de hotel
                RegisterBirthdateFragmentSuperAdmin siguienteFragment = new RegisterBirthdateFragmentSuperAdmin();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, siguienteFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
