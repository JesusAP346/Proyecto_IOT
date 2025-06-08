package com.example.proyecto_iot.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.models.Departamento;
import com.example.proyecto_iot.models.Distrito;
import com.example.proyecto_iot.models.Provincia;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterDireccionFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Views
    private AutoCompleteTextView dropdownDepartamento;
    private AutoCompleteTextView dropdownProvincia;
    private AutoCompleteTextView dropdownDistrito;
    private TextInputEditText direccionEditText;
    private TextInputLayout idDepartamento;
    private TextInputLayout idProvincia;
    private TextInputLayout distrito;
    private TextInputLayout direccion;

    // Data lists
    private List<Departamento> departamentos;
    private List<Provincia> todasProvincias;
    private List<Distrito> todosDistritos;
    private List<Provincia> provinciasFiltradas;
    private List<Distrito> distritosFiltrados;

    // Selected items
    private Departamento departamentoSeleccionado;
    private Provincia provinciaSeleccionada;
    private Distrito distritoSeleccionado;

    // Adapters
    private ArrayAdapter<Departamento> departamentoAdapter;
    private ArrayAdapter<Provincia> provinciaAdapter;
    private ArrayAdapter<Distrito> distritoAdapter;

    // Pattern for address validation (no special characters except spaces, numbers, letters, and basic punctuation)
    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9\\s.,#-]+$");

    public RegisterDireccionFragment() {
        // Required empty public constructor
    }

    public static RegisterDireccionFragment newInstance(String param1, String param2) {
        RegisterDireccionFragment fragment = new RegisterDireccionFragment();
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
        View view = inflater.inflate(R.layout.fragment_register_direccion, container, false);

        initViews(view);
        loadDataFromJson();
        setupDropdowns();
        setupButtons(view);
        setupValidation();

        return view;
    }

    private void initViews(View view) {
        dropdownDepartamento = view.findViewById(R.id.dropdownDepartamento);
        dropdownProvincia = view.findViewById(R.id.dropdownProvincia);
        dropdownDistrito = view.findViewById(R.id.dropdownDistrito);
        direccionEditText = view.findViewById(R.id.direccionEditText);

        idDepartamento = view.findViewById(R.id.idDepartamento);
        idProvincia = view.findViewById(R.id.idProvincia);
        distrito = view.findViewById(R.id.distrito);
        direccion = view.findViewById(R.id.direccion);

        // Initially disable province and district dropdowns
        dropdownProvincia.setEnabled(false);
        dropdownDistrito.setEnabled(false);
        idProvincia.setAlpha(0.5f);
        distrito.setAlpha(0.5f);
    }

    private void loadDataFromJson() {
        departamentos = loadDepartamentosFromJson();
        todasProvincias = loadProvinciasFromJson();
        todosDistritos = loadDistritosFromJson();

        provinciasFiltradas = new ArrayList<>();
        distritosFiltrados = new ArrayList<>();
    }

    private List<Departamento> loadDepartamentosFromJson() {
        List<Departamento> list = new ArrayList<>();
        try {
            String json = loadJSONFromAsset("ubigeo_peru_2016_departamentos.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Departamento dept = new Departamento();
                dept.setId(obj.getString("id"));
                dept.setNombre(obj.getString("name"));
                list.add(dept);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error cargando departamentos", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private List<Provincia> loadProvinciasFromJson() {
        List<Provincia> list = new ArrayList<>();
        try {
            String json = loadJSONFromAsset("ubigeo_peru_2016_provincias.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Provincia prov = new Provincia();
                prov.setId(obj.getString("id"));
                prov.setNombre(obj.getString("name"));
                prov.setDepartment_id(obj.getString("department_id"));
                list.add(prov);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error cargando provincias", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private List<Distrito> loadDistritosFromJson() {
        List<Distrito> list = new ArrayList<>();
        try {
            String json = loadJSONFromAsset("ubigeo_peru_2016_distritos.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Distrito dist = new Distrito();
                dist.setId(obj.getString("id"));
                dist.setNombre(obj.getString("name"));
                dist.setProvince_id(obj.getString("province_id"));
                dist.setDepartment_id(obj.getString("department_id"));
                list.add(dist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error cargando distritos", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setupDropdowns() {
        // Setup departamento dropdown
        departamentoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, departamentos);
        dropdownDepartamento.setAdapter(departamentoAdapter);

        dropdownDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            departamentoSeleccionado = (Departamento) parent.getItemAtPosition(position);
            onDepartamentoSelected();
        });

        // Setup provincia dropdown
        provinciaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, provinciasFiltradas);
        dropdownProvincia.setAdapter(provinciaAdapter);

        dropdownProvincia.setOnItemClickListener((parent, view, position, id) -> {
            provinciaSeleccionada = (Provincia) parent.getItemAtPosition(position);
            onProvinciaSelected();
        });

        // Setup distrito dropdown
        distritoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, distritosFiltrados);
        dropdownDistrito.setAdapter(distritoAdapter);

        dropdownDistrito.setOnItemClickListener((parent, view, position, id) -> {
            distritoSeleccionado = (Distrito) parent.getItemAtPosition(position);
        });
    }

    private void onDepartamentoSelected() {
        // Clear previous selections
        dropdownProvincia.setText("");
        dropdownDistrito.setText("");
        provinciaSeleccionada = null;
        distritoSeleccionado = null;

        // Clear and filter provinces by selected department
        provinciasFiltradas.clear();
        for (Provincia provincia : todasProvincias) {
            if (provincia.getDepartment_id().equals(departamentoSeleccionado.getId())) {
                provinciasFiltradas.add(provincia);
            }
        }

        // Create new adapter for provinces (this is crucial for proper updating)
        provinciaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, provinciasFiltradas);
        dropdownProvincia.setAdapter(provinciaAdapter);

        // Enable provincia dropdown
        dropdownProvincia.setEnabled(true);
        idProvincia.setAlpha(1.0f);

        // Disable and clear distrito dropdown
        dropdownDistrito.setEnabled(false);
        distrito.setAlpha(0.5f);
        distritosFiltrados.clear();
        distritoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, distritosFiltrados);
        dropdownDistrito.setAdapter(distritoAdapter);
    }

    private void onProvinciaSelected() {
        // Clear previous distrito selection
        dropdownDistrito.setText("");
        distritoSeleccionado = null;

        // Clear and filter districts by selected province
        distritosFiltrados.clear();
        for (Distrito distrito : todosDistritos) {
            if (distrito.getProvince_id().equals(provinciaSeleccionada.getId())) {
                distritosFiltrados.add(distrito);
            }
        }

        // Create new adapter for districts (this is crucial for proper updating)
        distritoAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, distritosFiltrados);
        dropdownDistrito.setAdapter(distritoAdapter);

        // Enable distrito dropdown
        dropdownDistrito.setEnabled(true);
        distrito.setAlpha(1.0f);
    }

    private void setupValidation() {
        // Add text watcher for address validation
        direccionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateDireccion(s.toString().trim());
            }
        });
    }

    private void validateDireccion(String text) {
        if (!text.isEmpty() && !DIRECCION_PATTERN.matcher(text).matches()) {
            direccion.setError("Solo se permiten letras, números, espacios y los caracteres: . , # -");
        } else {
            direccion.setError(null);
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Validate departamento
        if (departamentoSeleccionado == null) {
            idDepartamento.setError("Selecciona un departamento");
            errorMessage.append("• Selecciona un departamento\n");
            isValid = false;
        } else {
            idDepartamento.setError(null);
        }

        // Validate provincia
        if (provinciaSeleccionada == null) {
            idProvincia.setError("Selecciona una provincia");
            errorMessage.append("• Selecciona una provincia\n");
            isValid = false;
        } else {
            idProvincia.setError(null);
        }

        // Validate distrito
        if (distritoSeleccionado == null) {
            distrito.setError("Selecciona un distrito");
            errorMessage.append("• Selecciona un distrito\n");
            isValid = false;
        } else {
            distrito.setError(null);
        }

        // Validate direccion
        String direccionText = direccionEditText.getText().toString().trim();
        if (direccionText.isEmpty()) {
            direccion.setError("Ingresa tu dirección");
            errorMessage.append("• Ingresa tu dirección\n");
            isValid = false;
        } else if (!DIRECCION_PATTERN.matcher(direccionText).matches()) {
            direccion.setError("La dirección contiene caracteres no válidos");
            errorMessage.append("• La dirección contiene caracteres no válidos (solo se permiten letras, números, espacios y .,#-)\n");
            isValid = false;
        } else {
            direccion.setError(null);
        }


        return isValid;
    }

    private void setupButtons(View view) {
        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        botonRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        botonSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                // All validations passed, proceed to next fragment
                // You can pass the selected data to the next fragment if needed
                Bundle bundle = new Bundle();
                bundle.putString("departamento_id", departamentoSeleccionado.getId());
                bundle.putString("departamento_nombre", departamentoSeleccionado.getNombre());
                bundle.putString("provincia_id", provinciaSeleccionada.getId());
                bundle.putString("provincia_nombre", provinciaSeleccionada.getNombre());
                bundle.putString("distrito_id", distritoSeleccionado.getId());
                bundle.putString("distrito_nombre", distritoSeleccionado.getNombre());
                bundle.putString("direccion", direccionEditText.getText().toString().trim());

                RegisterFotoFragment registerFotoFragment = new RegisterFotoFragment();
                registerFotoFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerFotoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            // Error message is now handled inside validateForm()
        });
    }
}