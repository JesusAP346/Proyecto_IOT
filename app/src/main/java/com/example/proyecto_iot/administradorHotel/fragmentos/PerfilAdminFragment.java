package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.InfoPerfilAdminFragment;
import com.example.proyecto_iot.administradorHotel.InicioDeSesionAdminFragment;
import com.example.proyecto_iot.databinding.FragmentHomeBinding;
import com.example.proyecto_iot.databinding.FragmentPerfilAdminBinding;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilAdminFragment newInstance(String param1, String param2) {
        PerfilAdminFragment fragment = new PerfilAdminFragment();
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

    FragmentPerfilAdminBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate con ViewBinding
        binding = FragmentPerfilAdminBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.informacionPersonal.setOnClickListener(v -> {
            Fragment infoPerfilAdminFragment = new InfoPerfilAdminFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, infoPerfilAdminFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.seguridadPersonal.setOnClickListener(v -> {
            Fragment inicioDeSesionAdminFragment = new InicioDeSesionAdminFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, inicioDeSesionAdminFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No hay sesión iniciada", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}