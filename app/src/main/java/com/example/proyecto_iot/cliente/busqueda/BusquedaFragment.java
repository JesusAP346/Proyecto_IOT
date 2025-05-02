package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proyecto_iot.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusquedaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusquedaFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView txtFechas;
    private TextView txtHuespedes;

    public BusquedaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusquedaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusquedaFragment newInstance(String param1, String param2) {
        BusquedaFragment fragment = new BusquedaFragment();
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
        View view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        TextView txtFechas = view.findViewById(R.id.txtFechas);
        TextView txtHuespedes = view.findViewById(R.id.txtHuespedes);

        txtFechas.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                    MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Selecciona rango de fechas");

            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    Long startDate = selection.first;
                    Long endDate = selection.second;

                    SimpleDateFormat formato = new SimpleDateFormat("dd MMM", Locale.getDefault());
                    String inicio = formato.format(new Date(startDate));
                    String fin = formato.format(new Date(endDate));

                    txtFechas.setText(inicio + " - " + fin);
                }
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        txtHuespedes.setOnClickListener(v -> {
            HuespedesBottomSheetDialogFragment dialog = new HuespedesBottomSheetDialogFragment();
            dialog.setOnHuespedesSelectedListener((adultos, ninos, habitaciones) -> {
                int totalHuespedes = adultos + ninos;
                txtHuespedes.setText(totalHuespedes + " Huésp, " + habitaciones + " hab.");
            });
            dialog.show(getParentFragmentManager(), "HuespedesBottomSheet");
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerRecientes);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<RecienteItem> recientes = new ArrayList<>();
        recientes.add(new RecienteItem("Hotel a seleccionar 1", "Bellavista", "18 abr. - 19 abr.", R.drawable.hotel1));
        recientes.add(new RecienteItem("Hotel a seleccionar 2", "San Miguel", "18 abr. - 19 abr.", R.drawable.hotel2));

        RecientesAdapter adapter = new RecientesAdapter(recientes);
        recycler.setAdapter(adapter);

        TextView txtFavoritos = view.findViewById(R.id.txtFavoritos);
        txtFavoritos.setOnClickListener(v -> {
            ClienteFavoritosFragment favoritosFragment = new ClienteFavoritosFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_busqueda, favoritosFragment)
                    .addToBackStack(null)
                    .commit();
        });

        Button btnBuscar = view.findViewById(R.id.btnBuscar);

        btnBuscar.setOnClickListener(v -> {
            ResultadosDeBusquedaFragment resultadosDeBusquedaFragment = new ResultadosDeBusquedaFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_busqueda, resultadosDeBusquedaFragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

}