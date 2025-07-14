package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.util.Patterns;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterContactFragmentSuperAdmin extends Fragment {

    private TextInputLayout numeroCelularLayout;
    private TextInputLayout emailLayout;
    private TextInputEditText numeroCelularEditText;
    private TextInputEditText emailEditText;

    public RegisterContactFragmentSuperAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_contact_super_admin, container, false);

        UsuarioAdminViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        numeroCelularLayout = view.findViewById(R.id.numeroCelular);
        emailLayout = view.findViewById(R.id.email);
        numeroCelularEditText = view.findViewById(R.id.etNumeroCelular);
        emailEditText = view.findViewById(R.id.etEmail);

        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);

        botonRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        botonSiguiente.setOnClickListener(v -> {
            if (validarCampos()) {
                String numeroCelular = numeroCelularEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usuariosRef = db.collection("usuarios");

                usuariosRef
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener(querySnapshot1 -> {
                            if (!querySnapshot1.isEmpty()) {
                                emailLayout.setError("Este correo ya está registrado");
                                return;
                            }

                            usuariosRef
                                    .whereEqualTo("numCelular", numeroCelular)
                                    .get()
                                    .addOnSuccessListener(querySnapshot2 -> {
                                        if (!querySnapshot2.isEmpty()) {
                                            numeroCelularLayout.setError("Este número ya está registrado");
                                            return;
                                        }

                                        viewModel.actualizarCampo("numCelular", numeroCelular);
                                        viewModel.actualizarCampo("email", email);

                                        RegisterDireccionFragmentSuperAdmin siguienteFragment = new RegisterDireccionFragmentSuperAdmin();
                                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, siguienteFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al validar número: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al validar email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }

    private boolean validarCampos() {
        boolean esValido = true;

        numeroCelularLayout.setError(null);
        emailLayout.setError(null);

        String numeroCelular = numeroCelularEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (!validarNumeroCelular(numeroCelular)) {
            esValido = false;
        }

        if (!validarEmail(email)) {
            esValido = false;
        }

        return esValido;
    }

    private boolean validarNumeroCelular(String numeroCelular) {
        if (numeroCelular.isEmpty()) {
            numeroCelularLayout.setError("El número de celular es obligatorio");
            return false;
        }

        if (!numeroCelular.matches("\\d+")) {
            numeroCelularLayout.setError("El número de celular solo debe contener números");
            return false;
        }

        if (numeroCelular.length() != 9) {
            numeroCelularLayout.setError("El número de celular debe tener 9 dígitos");
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        if (email.isEmpty()) {
            emailLayout.setError("El correo electrónico es obligatorio");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Formato de correo inválido");
            return false;
        }

        return true;
    }
}
