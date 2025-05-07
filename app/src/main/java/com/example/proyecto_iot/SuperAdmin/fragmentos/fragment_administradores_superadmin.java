package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.AdministradoresAdapter;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;

import java.util.ArrayList;
import java.util.List;

public class fragment_administradores_superadmin extends Fragment {

    private List<AdministradoresDomain> adminsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdministradoresAdapter administradoresAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public fragment_administradores_superadmin() {
        // Constructor vacío requerido
    }

    public static fragment_administradores_superadmin newInstance(String param1, String param2) {
        fragment_administradores_superadmin fragment = new fragment_administradores_superadmin();
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

        initData();  // puedes dejarlo aquí
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout del fragmento
        View view = inflater.inflate(R.layout.fragment_administradores_superadmin, container, false);

        // Inicializa el RecyclerView aquí con `view.findViewById(...)`
        recyclerView = view.findViewById(R.id.rv_music);
        administradoresAdapter = new AdministradoresAdapter(adminsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(administradoresAdapter);

        return view;
    }

    private void initData() {
        adminsList.add(new AdministradoresDomain("Andrea Torres", "987654321", "https://randomuser.me/api/portraits/women/68.jpg", "andrea.torres@email.com", "Av. Los Álamos 123", "1990-03-15", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Luis Mendoza", "912345678", "https://randomuser.me/api/portraits/men/45.jpg", "luis.mendoza@email.com", "Jr. Lima 456", "1988-11-20", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("María Pérez", "956789123", "https://randomuser.me/api/portraits/women/12.jpg", "maria.perez@email.com", "Calle San Juan 789", "1992-07-08", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Carlos Ruiz", "998877665", "https://randomuser.me/api/portraits/men/34.jpg", "carlos.ruiz@email.com", "Av. Miraflores 321", "1985-06-23", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Fernanda Silva", "934567890", "https://randomuser.me/api/portraits/women/24.jpg", "fernanda.silva@email.com", "Jr. Arequipa 654", "1991-12-01", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Diego Ramírez", "976543210", "https://randomuser.me/api/portraits/men/53.jpg", "diego.ramirez@email.com", "Calle Los Olivos 987", "1987-05-12", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Lucía Romero", "923456781", "https://randomuser.me/api/portraits/women/30.jpg", "lucia.romero@email.com", "Av. Primavera 111", "1993-09-30", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Jorge Valdez", "987123654", "https://randomuser.me/api/portraits/men/22.jpg", "jorge.valdez@email.com", "Jr. Cusco 222", "1986-04-17", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Camila Torres", "965432178", "https://randomuser.me/api/portraits/women/15.jpg", "camila.torres@email.com", "Calle Amazonas 333", "1994-10-05", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Alonso Rivas", "976812345", "https://randomuser.me/api/portraits/men/28.jpg", "alonso.rivas@email.com", "Av. Grau 444", "1989-08-22", "Admin Hotel"));

    }
}
