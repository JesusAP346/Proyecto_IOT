package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.MapaCompletoActivity;
import com.example.proyecto_iot.administradorHotel.entity.Hotel;
import com.example.proyecto_iot.databinding.FragmentHotelInfoBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.util.ArrayList;
import java.util.List;



public class HotelInfoFragment extends Fragment {

    FragmentHotelInfoBinding binding;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    private MapView mapViewPreview;
    private GoogleMap miniMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Inicializa el MapView
        mapViewPreview = binding.mapViewPreview;
        mapViewPreview.onCreate(savedInstanceState);
        mapViewPreview.onResume();

        if (currentUser != null) {
            String idAdmin = currentUser.getUid();

            CollectionReference hotelesRef = db.collection("hoteles");
            hotelesRef.whereEqualTo("idAdministrador", idAdmin)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            Hotel hotel = documentSnapshot.toObject(Hotel.class);

                            if (isAdded() && binding != null && hotel != null) {
                                binding.btnVerUbicacion.setOnClickListener(v -> {
                                    if (hotel.getUbicacionLat() != null && hotel.getUbicacionLng() != null) {
                                        Intent intent = new Intent(requireContext(), MapaCompletoActivity.class);
                                        intent.putExtra("latitud", hotel.getUbicacionLat());
                                        intent.putExtra("longitud", hotel.getUbicacionLng());
                                        intent.putExtra("nombre", hotel.getNombre());
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(requireContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Mover mostrarDatosHotel DENTRO de este if
                                mostrarDatosHotel(hotel);

                                // Asignar mapa al hotel
                                mapViewPreview.getMapAsync(googleMap -> {
                                    miniMap = googleMap;
                                    configurarMiniMapa(hotel);
                                });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error al cargar los datos del hotel", Toast.LENGTH_SHORT).show();
                    });
        }

        // También protege esto
        if (binding != null) {
            binding.btnActualizarInfo.setOnClickListener(v -> {
                com.example.proyecto_iot.administradorHotel.EstadoHotelUI.seccionSeleccionada = "info";
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new HotelActualizarFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }



    private void mostrarDatosHotel(Hotel hotel) {

        if (binding == null || !isAdded()) return;
        // Nombre
        binding.textNombreHotel.setText(hotel.getNombre() != null ? hotel.getNombre() : "Nombre no disponible");

        // Dirección
        binding.textDireccionHotel.setText(hotel.getDireccion() != null ? hotel.getDireccion() : "Dirección no registrada");

        // Referencias
        binding.containerLugaresHistoricos.removeAllViews();
        if (hotel.getReferencias() != null && !hotel.getReferencias().isEmpty()) {
            for (String ref : hotel.getReferencias()) {
                agregarReferencia(ref);
            }
        }

        // Carrusel de Imágenes
        ImageCarousel carrusel = binding.carruselImagenes;

        if (hotel.getFotosHotelUrls() != null && !hotel.getFotosHotelUrls().isEmpty()) {
            List<CarouselItem> items = new ArrayList<>();

            for (String url : hotel.getFotosHotelUrls()) {
                items.add(new CarouselItem(url));
            }
            carrusel.setCarouselType(CarouselType.BLOCK);
            carrusel.setAutoPlay(true);
            carrusel.setData(items);
        } else {
            carrusel.setVisibility(View.GONE);
        }


    }

    private void agregarReferencia(String referencia) {
        TextView referenciaText = new TextView(requireContext());
        referenciaText.setText("• " + referencia);
        referenciaText.setTextSize(14);
        referenciaText.setTextColor(getResources().getColor(android.R.color.black));
        referenciaText.setPadding(0, 4, 0, 4);
        binding.containerLugaresHistoricos.addView(referenciaText);
    }

    private void configurarMiniMapa(Hotel hotel) {
        if (miniMap != null && hotel.getUbicacionLat() != null && hotel.getUbicacionLng() != null) {
            LatLng ubicacionHotel = new LatLng(hotel.getUbicacionLat(), hotel.getUbicacionLng());

            miniMap.getUiSettings().setAllGesturesEnabled(false);
            miniMap.getUiSettings().setMapToolbarEnabled(false);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(ubicacionHotel)
                    .title(hotel.getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            miniMap.addMarker(markerOptions);
            miniMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionHotel, 15f));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapViewPreview != null) mapViewPreview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapViewPreview != null) mapViewPreview.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapViewPreview != null) mapViewPreview.onDestroy();
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapViewPreview != null) mapViewPreview.onLowMemory();
    }

}
