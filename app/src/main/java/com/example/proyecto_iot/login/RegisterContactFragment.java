package com.example.proyecto_iot.login;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterContactFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Referencias a los campos de entrada
    private TextInputLayout numeroCelularLayout;
    private TextInputLayout emailLayout;
    private TextInputEditText numeroCelularEditText;
    private TextInputEditText emailEditText;

    public RegisterContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterContactFragment newInstance(String param1, String param2) {
        RegisterContactFragment fragment = new RegisterContactFragment();
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
        View view = inflater.inflate(R.layout.fragment_register_contact, container, false);

        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);


        // Inicializar referencias a los campos
        numeroCelularLayout = view.findViewById(R.id.numeroCelular);
        emailLayout = view.findViewById(R.id.email);
        numeroCelularEditText = view.findViewById(R.id.etNumeroCelular);
        emailEditText = view.findViewById(R.id.etEmail);

        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        botonRegresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getParentFragmentManager().popBackStack();
            }
        });

        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                            RegisterDireccionFragment registerDireccionFragment = new RegisterDireccionFragment();
                                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                            transaction.replace(R.id.fragment_container, registerDireccionFragment);
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
            }
        });


        return view;
    }

    /**
     * Valida todos los campos del formulario
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        boolean esValido = true;

        // Limpiar errores previos
        numeroCelularLayout.setError(null);
        emailLayout.setError(null);

        // Validar número de celular
        String numeroCelular = numeroCelularEditText.getText().toString().trim();
        if (!validarNumeroCelular(numeroCelular)) {
            esValido = false;
        }

        // Validar email
        String email = emailEditText.getText().toString().trim();
        if (!validarEmail(email)) {
            esValido = false;
        }

        return esValido;
    }

    /**
     * Valida el número de celular
     * @param numeroCelular El número a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarNumeroCelular(String numeroCelular) {
        if (numeroCelular.isEmpty()) {
            numeroCelularLayout.setError("El número de celular es obligatorio");
            numeroCelularLayout.setErrorEnabled(true);
            return false;
        }

        // Verificar que solo contenga números
        if (!numeroCelular.matches("\\d+")) {
            numeroCelularLayout.setError("El número de celular solo debe contener números");
            numeroCelularLayout.setErrorEnabled(true);
            return false;
        }

        // Verificar que tenga exactamente 9 dígitos
        if (numeroCelular.length() != 9) {
            numeroCelularLayout.setError("El número de celular debe tener exactamente 9 dígitos");
            numeroCelularLayout.setErrorEnabled(true);
            return false;
        }

        numeroCelularLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * Valida el email
     * @param email El email a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarEmail(String email) {
        if (email.isEmpty()) {
            emailLayout.setError("El correo electrónico es obligatorio");
            emailLayout.setErrorEnabled(true);
            return false;
        }

        // Validar formato de email usando Patterns de Android
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Ingrese un correo electrónico válido");
            emailLayout.setErrorEnabled(true);
            return false;
        }

        emailLayout.setErrorEnabled(false);
        return true;
    }
}