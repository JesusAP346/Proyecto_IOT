package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.BuildConfig;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.login.UsuarioClienteViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Button btnBuscar;

    private SugerenciaItem destinoSeleccionado;
    private List<SugerenciaItem> sugerenciasConCoordenadas = new ArrayList<>();

    private MaterialAutoCompleteTextView etDestino;
    private RecyclerView recyclerSugerencias;
    private SugerenciaAdapter sugerenciaAdapter;
    private List<String> sugerenciasList = new ArrayList<>();
    private boolean isSelecting = false; // Flag para controlar la selección
    private View rootView;

    // Variables para validación
    private boolean destinoCompleto = false;
    private boolean fechasCompletas = false;
    private boolean huespedesCompletos = false;

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
        rootView = view;

        // Inicializar vistas
        txtFechas = view.findViewById(R.id.txtFechas);
        txtHuespedes = view.findViewById(R.id.txtHuespedes);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        etDestino = view.findViewById(R.id.etDestino);

        // Inicializar botón como deshabilitado
        btnBuscar.setEnabled(false);
        btnBuscar.setAlpha(0.5f);

        // Ajustar comportamiento cuando aparece/desaparece el teclado
        setupKeyboardListener();

        txtFechas.setOnClickListener(v -> {
            // Ocultar sugerencias al seleccionar fechas
            ocultarSugerencias();

            // Crear restricciones de calendario para no permitir fechas pasadas (permite hoy)
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setValidator(DateValidatorPointForward.from(yesterday.getTimeInMillis()));

            MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                    MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Selecciona rango de fechas");
            builder.setCalendarConstraints(constraintsBuilder.build());

            // Establecer fecha mínima como hoy
            Calendar today = Calendar.getInstance();
            builder.setSelection(Pair.create(
                    today.getTimeInMillis(),
                    today.getTimeInMillis() + (24 * 60 * 60 * 1000) // Mañana como fecha de salida por defecto
            ));

            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    Long startDate = selection.first;
                    Long endDate = selection.second;

                    // Validar que la fecha de salida sea posterior a la de entrada
                    if (endDate <= startDate) {
                        Toast.makeText(getContext(), "La fecha de salida debe ser posterior a la fecha de entrada", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validar que no sean fechas anteriores a hoy
                    Calendar todayValidation = Calendar.getInstance();
                    todayValidation.set(Calendar.HOUR_OF_DAY, 0);
                    todayValidation.set(Calendar.MINUTE, 0);
                    todayValidation.set(Calendar.SECOND, 0);
                    todayValidation.set(Calendar.MILLISECOND, 0);

                    if (startDate < todayValidation.getTimeInMillis()) {
                        Toast.makeText(getContext(), "No se pueden seleccionar fechas anteriores a hoy", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat formato = new SimpleDateFormat("dd MMM", Locale.getDefault());
                    String inicio = formato.format(new Date(startDate));
                    String fin = formato.format(new Date(endDate));

                    txtFechas.setText(inicio + " - " + fin);
                    fechasCompletas = true;
                    validarFormulario();
                }
            });

            picker.addOnNegativeButtonClickListener(v1 -> {
                // Si se cancela, no cambiar el estado
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        txtHuespedes.setOnClickListener(v -> {
            // Ocultar sugerencias al seleccionar huéspedes
            ocultarSugerencias();

            HuespedesBottomSheetDialogFragment dialog = new HuespedesBottomSheetDialogFragment();
            dialog.setOnHuespedesSelectedListener((adultos, ninos, habitaciones) -> {
                if (adultos > 0 || ninos > 0) { // Al menos debe haber un huésped
                    int totalHuespedes = adultos + ninos;
                    txtHuespedes.setText(totalHuespedes + " Huésp, " + habitaciones + " hab.");
                    huespedesCompletos = true;
                } else {
                    huespedesCompletos = false;
                }
                validarFormulario();
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

        btnBuscar.setOnClickListener(v -> {
            if (validarFormularioCompleto()) {
                Bundle bundle = new Bundle();

                // Datos básicos
                bundle.putString("destino", etDestino.getText().toString().trim());
                bundle.putString("fechas", txtFechas.getText().toString());
                bundle.putString("huespedes", txtHuespedes.getText().toString());

                // Coordenadas del destino (si están disponibles)
                if (destinoSeleccionado != null) {
                    bundle.putDouble("destinoLat", destinoSeleccionado.getLat());
                    bundle.putDouble("destinoLng", destinoSeleccionado.getLng());
                    bundle.putString("destinoPlaceId", destinoSeleccionado.getPlaceId());
                }

                // Crear fragment y pasar datos
                ResultadosDeBusquedaFragment resultadosFragment = new ResultadosDeBusquedaFragment();
                resultadosFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_busqueda, resultadosFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                mostrarCamposFaltantes();
            }
        });

        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);

        TextView txtSaludo = view.findViewById(R.id.textView8);

        viewModel.getUsuarioCliente().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null && usuario.getNombres() != null) {
                String nombre = usuario.getNombres();
                txtSaludo.setText("Hola " + nombre);
            }
        });

        //Sugerencias de búsqueda:
        recyclerSugerencias = view.findViewById(R.id.recyclerSugerencias);



        sugerenciaAdapter = new SugerenciaAdapter(sugerenciasList, lugar -> {
            isSelecting = true;
            etDestino.setText(lugar);
            etDestino.setSelection(lugar.length());

            // Buscar el item seleccionado para obtener sus coordenadas
            for (SugerenciaItem item : sugerenciasConCoordenadas) {
                if (item.getDescripcion().equals(lugar)) {
                    destinoSeleccionado = item;
                    break;
                }
            }

            ocultarSugerencias();
            isSelecting = false;
            destinoCompleto = true;
            validarFormulario();

            // Ocultar teclado
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etDestino.getWindowToken(), 0);
        });

        // Configurar RecyclerView con mejor scroll
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerSugerencias.setLayoutManager(layoutManager);
        recyclerSugerencias.setAdapter(sugerenciaAdapter);
        recyclerSugerencias.setNestedScrollingEnabled(true);

        etDestino.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No hacer nada si estamos en proceso de selección
                if (isSelecting) return;

                // Validar si el destino está completo
                if (s.length() > 0) {
                    // Si hay texto pero no se ha seleccionado de las sugerencias, considerar incompleto
                    destinoCompleto = false;
                    validarFormulario();
                } else {
                    destinoCompleto = false;
                    validarFormulario();
                }

                if (s.length() > 2) {
                    obtenerSugerenciasDesdeAPI(s.toString());
                } else {
                    ocultarSugerencias();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Ocultar sugerencias cuando el campo pierde el foco
        etDestino.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Usar un pequeño delay para permitir que el click en la sugerencia se complete
                etDestino.postDelayed(this::ocultarSugerencias, 150);
            }
        });

        return view;
    }

    private void validarFormulario() {
        boolean formularioCompleto = destinoCompleto && fechasCompletas && huespedesCompletos;

        btnBuscar.setEnabled(formularioCompleto);
        btnBuscar.setAlpha(formularioCompleto ? 1.0f : 0.5f);
    }

    private boolean validarFormularioCompleto() {
        return destinoCompleto && fechasCompletas && huespedesCompletos;
    }

    private void mostrarCamposFaltantes() {
        StringBuilder mensaje = new StringBuilder("Por favor, completa los siguientes campos:\n");

        if (!destinoCompleto) {
            mensaje.append("• Destino\n");
        }
        if (!fechasCompletas) {
            mensaje.append("• Fechas de entrada y salida\n");
        }
        if (!huespedesCompletos) {
            mensaje.append("• Huéspedes y habitaciones\n");
        }

        Toast.makeText(getContext(), mensaje.toString().trim(), Toast.LENGTH_LONG).show();
    }

    private void setupKeyboardListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Obtener la altura visible de la pantalla
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                // Si la diferencia es mayor a 200px, probablemente el teclado está visible
                boolean isKeyboardVisible = heightDiff > 200;

                if (isKeyboardVisible) {
                    // El teclado está visible, ajustar posición de sugerencias si es necesario
                    adjustSuggestionsPosition();
                }
            }
        });
    }

    private void adjustSuggestionsPosition() {
        if (recyclerSugerencias.getVisibility() == View.VISIBLE) {
            // Recalcular la posición de las sugerencias basado en la posición del EditText
            int[] location = new int[2];
            etDestino.getLocationOnScreen(location);

            // Ajustar márgenes si es necesario para que las sugerencias sean visibles
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerSugerencias.getLayoutParams();

            // Asegurar que el RecyclerView no exceda los límites de la pantalla
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int keyboardHeight = 250; // Altura estimada del teclado
            int availableHeight = screenHeight - location[1] - etDestino.getHeight() - keyboardHeight;
        }
    }

    private void ocultarSugerencias() {
        sugerenciasList.clear();
        sugerenciaAdapter.notifyDataSetChanged();
        recyclerSugerencias.setVisibility(View.GONE);
    }

    private void mostrarSugerencias() {
        if (!sugerenciasList.isEmpty()) {
            recyclerSugerencias.setVisibility(View.VISIBLE);
            adjustSuggestionsPosition();
        }
    }

    private void obtenerSugerenciasDesdeAPI(String input) {
        new Thread(() -> {
            try {
                String apiKey = BuildConfig.MAPS_API_KEY;
                String urlStr = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                        "?input=" + URLEncoder.encode(input, "UTF-8") +
                        "&types=geocode" +
                        "&components=country:pe" +
                        "&key=" + apiKey;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray predictions = jsonResponse.getJSONArray("predictions");

                    List<String> resultados = new ArrayList<>();
                    sugerenciasConCoordenadas.clear();

                    for (int i = 0; i < Math.min(predictions.length(), 8); i++) {
                        JSONObject prediction = predictions.getJSONObject(i);
                        String descripcion = prediction.getString("description");
                        String placeId = prediction.getString("place_id");

                        resultados.add(descripcion);

                        // Crear item con place_id para obtener coordenadas después
                        SugerenciaItem item = new SugerenciaItem(descripcion, placeId);
                        sugerenciasConCoordenadas.add(item);

                        // Obtener coordenadas usando Place Details API
                        obtenerCoordenadasDelLugar(item);
                    }

                    requireActivity().runOnUiThread(() -> {
                        if (!isSelecting && etDestino.hasFocus()) {
                            sugerenciasList.clear();
                            sugerenciasList.addAll(resultados);
                            sugerenciaAdapter.notifyDataSetChanged();
                            mostrarSugerencias();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("SUGERENCIAS", "Exception: " + e.getMessage(), e);
            }
        }).start();
    }

    private void obtenerCoordenadasDelLugar(SugerenciaItem item) {
        new Thread(() -> {
            try {
                String apiKey = BuildConfig.MAPS_API_KEY;
                String urlStr = "https://maps.googleapis.com/maps/api/place/details/json" +
                        "?place_id=" + item.getPlaceId() +
                        "&fields=geometry" +
                        "&key=" + apiKey;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject result = jsonResponse.getJSONObject("result");
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");

                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    item.setLat(lat);
                    item.setLng(lng);
                }
            } catch (Exception e) {
                Log.e("COORDENADAS", "Error obteniendo coordenadas: " + e.getMessage(), e);
            }
        }).start();
    }
}