package com.example.proyecto_iot.login;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);
        TextInputEditText etNombres = view.findViewById(R.id.etNombres);
        TextInputEditText etApellidos = view.findViewById(R.id.etApellidos);
        TextInputLayout layoutNombres = view.findViewById(R.id.textInputLayout2);
        TextInputLayout layoutApellidos = view.findViewById(R.id.textInputLayoutApellidos);

        botonSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
                String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";

                String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

                boolean isValid = true;

                if (nombres.isEmpty()) {
                    layoutNombres.setError("Este campo es obligatorio");
                    isValid = false;
                } else if (!nombres.matches(regex)) {
                    layoutNombres.setError("Solo se permiten letras y espacios");
                    isValid = false;
                } else {
                    layoutNombres.setError(null);
                }

                if (apellidos.isEmpty()) {
                    layoutApellidos.setError("Este campo es obligatorio");
                    isValid = false;
                } else if (!apellidos.matches(regex)) {
                    layoutApellidos.setError("Solo se permiten letras y espacios");
                    isValid = false;
                } else {
                    layoutApellidos.setError(null);
                }

                if (isValid) {
                    viewModel.actualizarCampo("nombres", nombres);
                    viewModel.actualizarCampo("apellidos", apellidos);
                    RegistroDniFragment registroDniFragment = new RegistroDniFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, registroDniFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });



        return view;
    }
}