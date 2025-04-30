package com.example.proyecto_iot.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;

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
                RegisterDireccionFragment registerDireccionFragment = new RegisterDireccionFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerDireccionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}