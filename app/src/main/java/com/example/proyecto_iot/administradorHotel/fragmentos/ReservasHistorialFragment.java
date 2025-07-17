package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.databinding.FragmentReservasHistorialBinding;

import java.util.Calendar;

public class ReservasHistorialFragment extends Fragment {

    private FragmentReservasHistorialBinding binding;

    public ReservasHistorialFragment() {
        // Constructor vacío requerido
    }


    public static ReservasHistorialFragment newInstance(String param1, String param2) {
        ReservasHistorialFragment fragment = new ReservasHistorialFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservasHistorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Listener para mostrar el calendario cuando se toca el EditText
        View.OnClickListener datePickerListener = v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedMonth += 1;
                        String selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                        binding.etSelectDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        };

        binding.etSelectDate.setOnClickListener(datePickerListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
