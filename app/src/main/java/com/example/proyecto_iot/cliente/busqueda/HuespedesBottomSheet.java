package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HuespedesBottomSheet extends BottomSheetDialogFragment {

    private int habitaciones = 1, adultos = 2, ninos = 0;
    private TextView txtHuespedes;

    public HuespedesBottomSheet(TextView txtHuespedes) {
        this.txtHuespedes = txtHuespedes;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_huespedes, container, false);

        TextView txtRoom = view.findViewById(R.id.txtRoomCount);
        TextView txtAdult = view.findViewById(R.id.txtAdultCount);
        TextView txtChild = view.findViewById(R.id.txtChildCount);

        view.findViewById(R.id.btnPlusRoom).setOnClickListener(v -> {
            habitaciones++;
            txtRoom.setText(String.valueOf(habitaciones));
        });

        view.findViewById(R.id.btnMinusRoom).setOnClickListener(v -> {
            if (habitaciones > 1) habitaciones--;
            txtRoom.setText(String.valueOf(habitaciones));
        });

        view.findViewById(R.id.btnPlusAdult).setOnClickListener(v -> {
            adultos++;
            txtAdult.setText(String.valueOf(adultos));
        });

        view.findViewById(R.id.btnMinusAdult).setOnClickListener(v -> {
            if (adultos > 1) adultos--;
            txtAdult.setText(String.valueOf(adultos));
        });

        view.findViewById(R.id.btnPlusChild).setOnClickListener(v -> {
            ninos++;
            txtChild.setText(String.valueOf(ninos));
        });

        view.findViewById(R.id.btnMinusChild).setOnClickListener(v -> {
            if (ninos > 0) ninos--;
            txtChild.setText(String.valueOf(ninos));
        });

        view.findViewById(R.id.btnConfirmar).setOnClickListener(v -> {
            String texto = adultos + ninos + " Huésp, " + habitaciones + " hab.";
            txtHuespedes.setText(texto);
            dismiss();
        });

        return view;
    }
}

