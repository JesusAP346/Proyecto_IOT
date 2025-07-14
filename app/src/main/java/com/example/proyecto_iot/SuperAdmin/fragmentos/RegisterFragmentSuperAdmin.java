package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragmentSuperAdmin extends Fragment {

    public RegisterFragmentSuperAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_super_admin, container, false);

        // Referencias UI
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        TextInputEditText etNombres = view.findViewById(R.id.etNombres);
        TextInputEditText etApellidos = view.findViewById(R.id.etApellidos);
        TextInputLayout layoutNombres = view.findViewById(R.id.textInputLayout2);
        TextInputLayout layoutApellidos = view.findViewById(R.id.textInputLayoutApellidos);

        // ViewModel compartido para mantener los datos entre pasos
        UsuarioAdminViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        botonSiguiente.setOnClickListener(v -> {
            String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
            String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";

            String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
            boolean isValid = true;

            if (nombres.isEmpty()) {
                layoutNombres.setError("Este campo es obligatorio");
                isValid = false;
            } else if (!nombres.matches(regex)) {
                layoutNombres.setError("Solo letras y espacios");
                isValid = false;
            } else {
                layoutNombres.setError(null);
            }

            if (apellidos.isEmpty()) {
                layoutApellidos.setError("Este campo es obligatorio");
                isValid = false;
            } else if (!apellidos.matches(regex)) {
                layoutApellidos.setError("Solo letras y espacios");
                isValid = false;
            } else {
                layoutApellidos.setError(null);
            }

            if (isValid) {
                // Guardamos en ViewModel
                viewModel.actualizarCampo("nombres", nombres);
                viewModel.actualizarCampo("apellidos", apellidos);

                // Navegamos al siguiente paso (Fragment de contacto por ejemplo)
                RegistroDniFragmentSuperAdmin paso2Fragment = new RegistroDniFragmentSuperAdmin();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, paso2Fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
