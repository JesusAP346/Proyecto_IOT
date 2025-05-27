package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.dto.ReservaDto;
import com.example.proyecto_iot.administradorHotel.entity.Reserva;
import com.example.proyecto_iot.administradorHotel.fragmentos.DetalleHuespedFragment;
import com.example.proyecto_iot.databinding.ItemReservaAdminBinding;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private final List<ReservaDto> dtoList;
    private final List<Reserva> fullList;
    private final Context context;

    public ReservaAdapter(List<ReservaDto> dtoList, List<Reserva> fullList, Context context) {
        this.dtoList = dtoList;
        this.fullList = fullList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemReservaAdminBinding binding = ItemReservaAdminBinding.inflate(inflater, parent, false);
        return new ReservaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        ReservaDto dto = dtoList.get(position);
        Reserva reserva = fullList.get(position);

        holder.binding.nombreHuesped.setText(dto.getNombre());
        holder.binding.tipoHabitacion.setText(dto.getTipo());
        holder.binding.checkin.setText(dto.getCheckIn());
        holder.binding.checkout.setText(dto.getCheckOut());

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("reserva", reserva);

            DetalleHuespedFragment fragment = new DetalleHuespedFragment();
            fragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return dtoList.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ItemReservaAdminBinding binding;

        public ReservaViewHolder(ItemReservaAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
