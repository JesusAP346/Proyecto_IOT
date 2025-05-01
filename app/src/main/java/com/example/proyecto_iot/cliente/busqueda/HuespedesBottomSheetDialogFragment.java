package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HuespedesBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private int adultos = 2;
    private int ninos = 0;
    private int habitaciones = 1;

    private TextView txtAdult, txtChild, txtRoom;
    private OnHuespedesSelectedListener listener;

    public interface OnHuespedesSelectedListener {
        void onHuespedesSelected(int adultos, int ninos, int habitaciones);
    }

    public void setOnHuespedesSelectedListener(OnHuespedesSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_huespedes, container, false);

        txtAdult = view.findViewById(R.id.txtAdultCount);
        txtChild = view.findViewById(R.id.txtChildCount);
        txtRoom = view.findViewById(R.id.txtRoomCount);

        // Botones Adultos
        view.findViewById(R.id.btnPlusAdult).setOnClickListener(v -> updateCount(txtAdult, ++adultos));
        view.findViewById(R.id.btnMinusAdult).setOnClickListener(v -> {
            if (adultos > 1) updateCount(txtAdult, --adultos);
        });

        // Botones Niños
        view.findViewById(R.id.btnPlusChild).setOnClickListener(v -> updateCount(txtChild, ++ninos));
        view.findViewById(R.id.btnMinusChild).setOnClickListener(v -> {
            if (ninos > 0) updateCount(txtChild, --ninos);
        });

        // Botones Habitaciones
        view.findViewById(R.id.btnPlusRoom).setOnClickListener(v -> updateCount(txtRoom, ++habitaciones));
        view.findViewById(R.id.btnMinusRoom).setOnClickListener(v -> {
            if (habitaciones > 1) updateCount(txtRoom, --habitaciones);
        });

        // Confirmar
        view.findViewById(R.id.btnConfirmar).setOnClickListener(v -> {
            if (listener != null) {
                listener.onHuespedesSelected(adultos, ninos, habitaciones);
            }
            dismiss();
        });

        return view;
    }

    private void updateCount(TextView view, int value) {
        view.setText(String.valueOf(value));
    }
}

