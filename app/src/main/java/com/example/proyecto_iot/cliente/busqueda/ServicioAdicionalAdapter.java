package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import java.text.DecimalFormat;
import java.util.List;

public class ServicioAdicionalAdapter extends RecyclerView.Adapter<ServicioAdicionalAdapter.ViewHolder> {

    private List<ServicioAdicional> servicios;
    private Context context;
    private OnServicioSeleccionadoListener listener;

    public interface OnServicioSeleccionadoListener {
        void onServicioSeleccionado(ServicioAdicional servicio, boolean seleccionado);
    }

    public ServicioAdicionalAdapter(Context context, List<ServicioAdicional> servicios, OnServicioSeleccionadoListener listener) {
        this.context = context;
        this.servicios = servicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicio_adicional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServicioAdicional servicio = servicios.get(position);

        holder.textNombre.setText(servicio.getNombre());
        holder.textDescripcion.setText(servicio.getDescripcion());

        DecimalFormat df = new DecimalFormat("#,##0.00");
        holder.textPrecio.setText("S/. " + df.format(servicio.getPrecio()));

        // Configurar carrusel de imágenes
        if (servicio.getFotosUrls() != null && !servicio.getFotosUrls().isEmpty()) {
            // Si hay múltiples imágenes, mostrar el carrusel
            if (servicio.getFotosUrls().size() > 1) {
                holder.imageServicio.setVisibility(View.GONE);
                holder.viewPagerServicios.setVisibility(View.VISIBLE);
                holder.indicatorContainer.setVisibility(View.VISIBLE);

                // Configurar el ViewPager2
                ImageCarouselAdapter carouselAdapter = new ImageCarouselAdapter(servicio.getFotosUrls());
                holder.viewPagerServicios.setAdapter(carouselAdapter);

                // Configurar indicadores
                setupIndicators(holder, servicio.getFotosUrls().size());

                // Configurar listener para cambiar indicadores
                holder.viewPagerServicios.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateIndicators(holder, position);
                    }
                });

                // Click en el carrusel para ver en pantalla completa
                holder.viewPagerServicios.setOnClickListener(v -> {
                    int currentPosition = holder.viewPagerServicios.getCurrentItem();
                    showFullscreenImage(servicio.getFotosUrls(), currentPosition);
                });

            } else {
                // Si solo hay una imagen, mostrar el ImageView tradicional
                holder.imageServicio.setVisibility(View.VISIBLE);
                holder.viewPagerServicios.setVisibility(View.GONE);
                holder.indicatorContainer.setVisibility(View.GONE);

                Glide.with(context)
                        .load(servicio.getFotosUrls().get(0))
                        .placeholder(R.drawable.ic_circle)
                        .into(holder.imageServicio);

                // Click en la imagen para ver en pantalla completa
                holder.imageServicio.setOnClickListener(v -> {
                    showFullscreenImage(servicio.getFotosUrls(), 0);
                });
            }
        } else {
            // No hay imágenes, mostrar placeholder
            holder.imageServicio.setVisibility(View.VISIBLE);
            holder.viewPagerServicios.setVisibility(View.GONE);
            holder.indicatorContainer.setVisibility(View.GONE);
            holder.imageServicio.setImageResource(R.drawable.ic_circle);
        }

        // Configurar checkbox
        holder.checkBoxSeleccionar.setChecked(servicio.isSeleccionado());
        holder.checkBoxSeleccionar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            servicio.setSeleccionado(isChecked);
            if (listener != null) {
                listener.onServicioSeleccionado(servicio, isChecked);
            }
        });

        // Click en el item completo (evitar conflicto con click en imágenes)
        holder.textNombre.setOnClickListener(v -> toggleSelection(holder, servicio));
        holder.textDescripcion.setOnClickListener(v -> toggleSelection(holder, servicio));
        holder.textPrecio.setOnClickListener(v -> toggleSelection(holder, servicio));
    }

    private void toggleSelection(ViewHolder holder, ServicioAdicional servicio) {
        boolean nuevoEstado = !servicio.isSeleccionado();
        servicio.setSeleccionado(nuevoEstado);
        holder.checkBoxSeleccionar.setChecked(nuevoEstado);
        if (listener != null) {
            listener.onServicioSeleccionado(servicio, nuevoEstado);
        }
    }

    private void showFullscreenImage(List<String> imageUrls, int currentPosition) {
        ImageFullscreenDialog dialog = new ImageFullscreenDialog(context, imageUrls, currentPosition);
        dialog.show();
    }

    private void setupIndicators(ViewHolder holder, int count) {
        holder.indicatorContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            params.setMargins(4, 0, 4, 0);
            indicator.setLayoutParams(params);
            indicator.setImageResource(R.drawable.ic_indicator_inactive);
            holder.indicatorContainer.addView(indicator);
        }

        if (count > 0) {
            updateIndicators(holder, 0);
        }
    }

    private void updateIndicators(ViewHolder holder, int position) {
        for (int i = 0; i < holder.indicatorContainer.getChildCount(); i++) {
            ImageView indicator = (ImageView) holder.indicatorContainer.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.ic_indicator_active);
            } else {
                indicator.setImageResource(R.drawable.ic_indicator_inactive);
            }
        }
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageServicio;
        ViewPager2 viewPagerServicios;
        LinearLayout indicatorContainer;
        TextView textNombre;
        TextView textDescripcion;
        TextView textPrecio;
        CheckBox checkBoxSeleccionar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageServicio = itemView.findViewById(R.id.imageServicio);
            viewPagerServicios = itemView.findViewById(R.id.viewPagerServicios);
            indicatorContainer = itemView.findViewById(R.id.indicatorContainer);
            textNombre = itemView.findViewById(R.id.textNombre);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            checkBoxSeleccionar = itemView.findViewById(R.id.checkBoxSeleccionar);
        }
    }
}