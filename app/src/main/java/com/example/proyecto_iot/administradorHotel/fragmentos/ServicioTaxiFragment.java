package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.DatosTaxi;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.databinding.FragmentServicioTaxiBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ServicioTaxiFragment extends Fragment {

    private FragmentServicioTaxiBinding binding;
    private ReservaCompletaHotel reservaCompleta;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reservaCompleta = (ReservaCompletaHotel) getArguments().getSerializable("reservaCompleta");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServicioTaxiBinding.inflate(inflater, container, false);

        binding.backserviciotaxi.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        if (reservaCompleta != null) {
            String idReserva = reservaCompleta.getReserva().getIdReserva();
            Log.d("ID_RESERVA_DEBUG", "ID de la reserva: " + idReserva);
            cargarDatosTaxi(idReserva);
        }

        return binding.getRoot();
    }

    private void cargarDatosTaxi(String idReserva) {
        db.collection("servicios_taxi")
                .whereEqualTo("idReserva", idReserva)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);

                        String estado = doc.getString("estado");
                        Log.d("DEBUG_TAXI", "Estado recibido: " + estado);

                        double latTaxi = doc.contains("latTaxista") ? doc.getDouble("latTaxista") : 0;
                        double longTaxi = doc.contains("longTaxista") ? doc.getDouble("longTaxista") : 0;
                        double latHotel = doc.contains("latitudHotel") ? doc.getDouble("latitudHotel") : 0;
                        double longHotel = doc.contains("longitudHotel") ? doc.getDouble("longitudHotel") : 0;

                        if (estado == null || estado.equals("pendiente") || estado.equals("rechazado")) {
                            Log.d("DEBUG_TAXI", "No hay taxista asignado. Estado: " + estado);
                            mostrarEstadoBuscandoConductor(estado);  // muestra layout "Buscando conductor..."
                        } else {
                            String idTaxista = doc.getString("idTaxista");
                            if (idTaxista != null && !idTaxista.isEmpty()) {
                                Log.d("DEBUG_TAXI", "Taxista asignado: " + idTaxista);
                                cargarDatosTaxista(idTaxista, latTaxi, longTaxi, latHotel, longHotel, estado);
                            } else {
                                Log.w("DEBUG_TAXI", "El estado es '" + estado + "' pero no se encontró idTaxista");
                                mostrarEstadoBuscandoConductor("pendiente");
                            }
                        }
                    } else {
                        Log.w("DEBUG_TAXI", "No se encontró ningún documento de taxi para esta reserva");
                    }
                })
                .addOnFailureListener(e -> Log.e("DEBUG_TAXI", "Error al consultar Firestore", e));
    }


    private void cargarDatosTaxista(String idTaxista, double latTaxi, double longTaxi,
                                    double latHotel, double longHotel, String estadoServicio) {

        db.collection("usuarios").document(idTaxista).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String nombres = document.getString("nombres");
                String apellidos = document.getString("apellidos");
                String modeloAuto = document.getString("modeloAuto");
                String placaAuto = document.getString("placaAuto");
                String fotourl = document.getString("urlFotoPerfil");
                String nombreCompleto = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");

                DatosTaxi datosTaxi = new DatosTaxi(
                        nombreCompleto.trim(), modeloAuto, placaAuto,
                        latTaxi, longTaxi, latHotel, longHotel,
                        estadoServicio, fotourl
                );

                mostrarDatosTaxi(datosTaxi);
            }
        });
    }

    private void mostrarEstadoBuscandoConductor(String estado) {
        binding.textNombreTaxista.setText("Buscando conductor...");
        binding.textModeloAuto.setVisibility(View.GONE);
        binding.textPlacaAuto.setVisibility(View.GONE);
        binding.imageFotoTaxista.setImageResource(R.drawable.driver_photo);
        binding.imageUbicacion.setEnabled(false);

        pintarPaso(binding.puntoPaso1, true);
        pintarLinea(binding.lineaPaso1, false);
        pintarPaso(binding.puntoPaso2, false);
        pintarLinea(binding.lineaPaso2, false);
        pintarPaso(binding.puntoPaso3, false);
    }

    private void mostrarDatosTaxi(DatosTaxi datos) {
        binding.textNombreTaxista.setText(datos.getNombreTaxista());
        binding.textModeloAuto.setText(datos.getModeloAuto());
        binding.textModeloAuto.setVisibility(View.VISIBLE);
        binding.textPlacaAuto.setText(datos.getPlacaAuto());
        binding.textPlacaAuto.setVisibility(View.VISIBLE);

        if (datos.getFotourl() != null && !datos.getFotourl().isEmpty()) {
            Picasso.get().load(datos.getFotourl())
                    .placeholder(R.drawable.driver_photo)
                    .into(binding.imageFotoTaxista);
        }

        binding.imageUbicacion.setEnabled(true);

        switch (datos.getEstado()) {
            case "aceptado":
                pintarPaso(binding.puntoPaso1, true);
                pintarLinea(binding.lineaPaso1, true);
                pintarPaso(binding.puntoPaso2, true);
                pintarLinea(binding.lineaPaso2, false);
                pintarPaso(binding.puntoPaso3, false);
                break;

            case "finalizado":
                pintarPaso(binding.puntoPaso1, true);
                pintarLinea(binding.lineaPaso1, true);
                pintarPaso(binding.puntoPaso2, true);
                pintarLinea(binding.lineaPaso2, true);
                pintarPaso(binding.puntoPaso3, true);
                break;
        }

        Log.d("COORDENADAS", "Taxi: (" + datos.getLatitudTaxista() + ", " + datos.getLongitudTaxista() + ")");
        Log.d("COORDENADAS", "Hotel: (" + datos.getLatitudHotel() + ", " + datos.getLongitudHotel() + ")");
    }

    private void pintarPaso(View view, boolean activo) {
        view.setBackgroundResource(activo ? R.drawable.circle_green : R.drawable.circle_gray);
    }

    private void pintarLinea(View view, boolean activo) {
        view.setBackgroundColor(getResources().getColor(activo ? R.color.verde_linea : R.color.gris_linea));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
