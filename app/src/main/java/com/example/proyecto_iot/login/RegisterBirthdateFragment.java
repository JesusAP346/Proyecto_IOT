package com.example.proyecto_iot.login;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

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
        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        botonRegresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getParentFragmentManager().popBackStack();
            }
        });
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        botonSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RegisterContactFragment registerContactFragment = new RegisterContactFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerContactFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        TextInputLayout inputLayout = view.findViewById(R.id.fechaNac);
        TextInputEditText editTextFecha = (TextInputEditText) inputLayout.getEditText();

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
                        },
                        año, mes, dia
                );
                selectorFecha.show();
            });
        }

        return view;
    }
}