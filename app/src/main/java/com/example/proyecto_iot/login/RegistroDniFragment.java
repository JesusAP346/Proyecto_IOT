package com.example.proyecto_iot.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroDniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroDniFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistroDniFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistroDniFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistroDniFragment newInstance(String param1, String param2) {
        RegistroDniFragment fragment = new RegistroDniFragment();
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
        View view = inflater.inflate(R.layout.fragment_registro_dni, container, false);

        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);

        // Referencias a los elementos del layout
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView2);
        TextInputLayout inputTipoDoc = view.findViewById(R.id.inputTipoDoc);
        TextInputEditText etDocumento = view.findViewById(R.id.textInputLayout3).findViewById(R.id.etDocumento);
        TextInputLayout layoutDocumento = view.findViewById(R.id.textInputLayout3);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        Button botonRegresar = view.findViewById(R.id.botonRegresar);

        // Configurar adapter para el dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.tipo_documento_array,
                android.R.layout.simple_dropdown_item_1line
        );
        autoCompleteTextView.setAdapter(adapter);

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
                // Obtener valores
                String tipoDocumento = autoCompleteTextView.getText().toString().trim();
                String documento = etDocumento.getText() != null ? etDocumento.getText().toString().trim() : "";

                boolean isValid = true;

                // Validar tipo de documento
                if (tipoDocumento.isEmpty()) {
                    inputTipoDoc.setError("Debe seleccionar un tipo de documento");
                    isValid = false;
                } else {
                    inputTipoDoc.setError(null);
                }

                // Validar documento
                if (documento.isEmpty()) {
                    layoutDocumento.setError("Este campo es obligatorio");
                    isValid = false;
                } else if (!documento.matches("\\d+")) {
                    layoutDocumento.setError("Solo se permiten números");
                    isValid = false;
                } else {
                    layoutDocumento.setError(null);
                }

                // Si todas las validaciones pasan, continuar al siguiente fragmento
                if (isValid) {
                    RegisterBirthdateFragment registerBirthdateFragment = new RegisterBirthdateFragment();

                    viewModel.actualizarCampo("tipoDocumento", tipoDocumento);
                    viewModel.actualizarCampo("numDocumento", documento);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, registerBirthdateFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return view;
    }
}