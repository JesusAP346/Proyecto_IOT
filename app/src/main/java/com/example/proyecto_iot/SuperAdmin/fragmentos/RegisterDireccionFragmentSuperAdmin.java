package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
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

public class RegisterDireccionFragmentSuperAdmin extends Fragment {

    private AutoCompleteTextView dropdownDepartamento, dropdownProvincia, dropdownDistrito;
    private TextInputEditText direccionEditText;
    private TextInputLayout idDepartamento, idProvincia, distritoLayout, direccionLayout;

    private List<Departamento> departamentos;
    private List<Provincia> todasProvincias;
    private List<Distrito> todosDistritos;
    private List<Provincia> provinciasFiltradas;
    private List<Distrito> distritosFiltrados;

    private Departamento departamentoSeleccionado;
    private Provincia provinciaSeleccionada;
    private Distrito distritoSeleccionado;

    private ArrayAdapter<Departamento> departamentoAdapter;
    private ArrayAdapter<Provincia> provinciaAdapter;
    private ArrayAdapter<Distrito> distritoAdapter;

    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9\\s.,#-]+$");

    public RegisterDireccionFragmentSuperAdmin() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_direccion_super_admin, container, false);

        UsuarioAdminViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        // Referencias UI
        dropdownDepartamento = view.findViewById(R.id.dropdownDepartamento);
        dropdownProvincia = view.findViewById(R.id.dropdownProvincia);
        dropdownDistrito = view.findViewById(R.id.dropdownDistrito);
        direccionEditText = view.findViewById(R.id.direccionEditText);

        idDepartamento = view.findViewById(R.id.idDepartamento);
        idProvincia = view.findViewById(R.id.idProvincia);
        distritoLayout = view.findViewById(R.id.distrito);
        direccionLayout = view.findViewById(R.id.direccion);

        // Inicializar listas
        departamentos = loadDepartamentos();
        todasProvincias = loadProvincias();
        todosDistritos = loadDistritos();

        provinciasFiltradas = new ArrayList<>();
        distritosFiltrados = new ArrayList<>();

        // Inicialmente deshabilitar provincia y distrito
        dropdownProvincia.setEnabled(false);
        dropdownDistrito.setEnabled(false);
        idProvincia.setAlpha(0.5f);
        distritoLayout.setAlpha(0.5f);

        setupAdapters();
        setupListeners();

        Button btnRegresar = view.findViewById(R.id.botonRegresar);
        btnRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Button btnSiguiente = view.findViewById(R.id.botonSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            if (validarCampos()) {
                String dep = dropdownDepartamento.getText().toString().trim();
                String prov = dropdownProvincia.getText().toString().trim();
                String dist = dropdownDistrito.getText().toString().trim();
                String dir = direccionEditText.getText().toString().trim();

                viewModel.actualizarCampo("departamento", dep);
                viewModel.actualizarCampo("provincia", prov);
                viewModel.actualizarCampo("distrito", dist);
                viewModel.actualizarCampo("direccion", dir);

                RegisterFotoFragmentSuperAdmin siguiente = new RegisterFotoFragmentSuperAdmin();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, siguiente);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void setupAdapters() {
        departamentoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, departamentos);
        dropdownDepartamento.setAdapter(departamentoAdapter);

        provinciaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, provinciasFiltradas);
        dropdownProvincia.setAdapter(provinciaAdapter);

        distritoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, distritosFiltrados);
        dropdownDistrito.setAdapter(distritoAdapter);
    }

    private void setupListeners() {
        dropdownDepartamento.setOnItemClickListener((parent, view, pos, id) -> {
            departamentoSeleccionado = (Departamento) parent.getItemAtPosition(pos);
            onDepartamentoSeleccionado();
        });

        dropdownProvincia.setOnItemClickListener((parent, view, pos, id) -> {
            provinciaSeleccionada = (Provincia) parent.getItemAtPosition(pos);
            onProvinciaSeleccionada();
        });

        dropdownDistrito.setOnItemClickListener((parent, view, pos, id) -> {
            distritoSeleccionado = (Distrito) parent.getItemAtPosition(pos);
        });

        direccionEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { validateDireccion(s.toString().trim()); }
        });
    }

    private void onDepartamentoSeleccionado() {
        provinciasFiltradas.clear();
        dropdownProvincia.setText("");
        dropdownDistrito.setText("");
        provinciaSeleccionada = null;
        distritoSeleccionado = null;

        for (Provincia prov : todasProvincias) {
            if (prov.getDepartment_id().equals(departamentoSeleccionado.getId())) {
                provinciasFiltradas.add(prov);
            }
        }

        provinciaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, provinciasFiltradas);
        dropdownProvincia.setAdapter(provinciaAdapter);
        dropdownProvincia.setEnabled(true);
        idProvincia.setAlpha(1.0f);

        dropdownDistrito.setEnabled(false);
        distritoLayout.setAlpha(0.5f);
        distritosFiltrados.clear();
    }

    private void onProvinciaSeleccionada() {
        distritosFiltrados.clear();
        dropdownDistrito.setText("");
        distritoSeleccionado = null;

        for (Distrito dist : todosDistritos) {
            if (dist.getProvince_id().equals(provinciaSeleccionada.getId())) {
                distritosFiltrados.add(dist);
            }
        }

        distritoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, distritosFiltrados);
        dropdownDistrito.setAdapter(distritoAdapter);
        dropdownDistrito.setEnabled(true);
        distritoLayout.setAlpha(1.0f);
    }

    private void validateDireccion(String text) {
        if (!text.isEmpty() && !DIRECCION_PATTERN.matcher(text).matches()) {
            direccionLayout.setError("Solo letras, números, espacios y . , # -");
        } else {
            direccionLayout.setError(null);
        }
    }

    private boolean validarCampos() {
        boolean esValido = true;

        if (departamentoSeleccionado == null) {
            idDepartamento.setError("Selecciona un departamento");
            esValido = false;
        } else { idDepartamento.setError(null); }

        if (provinciaSeleccionada == null) {
            idProvincia.setError("Selecciona una provincia");
            esValido = false;
        } else { idProvincia.setError(null); }

        if (distritoSeleccionado == null) {
            distritoLayout.setError("Selecciona un distrito");
            esValido = false;
        } else { distritoLayout.setError(null); }

        String dir = direccionEditText.getText().toString().trim();
        if (dir.isEmpty()) {
            direccionLayout.setError("Ingresa tu dirección");
            esValido = false;
        } else if (!DIRECCION_PATTERN.matcher(dir).matches()) {
            direccionLayout.setError("Dirección no válida");
            esValido = false;
        } else { direccionLayout.setError(null); }

        return esValido;
    }

    private List<Departamento> loadDepartamentos() {
        List<Departamento> list = new ArrayList<>();
        try {
            String json = loadJsonAsset("ubigeo_peru_2016_departamentos.json");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new Departamento(obj.getString("id"), obj.getString("name")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private List<Provincia> loadProvincias() {
        List<Provincia> list = new ArrayList<>();
        try {
            String json = loadJsonAsset("ubigeo_peru_2016_provincias.json");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new Provincia(obj.getString("id"), obj.getString("name"), obj.getString("department_id")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private List<Distrito> loadDistritos() {
        List<Distrito> list = new ArrayList<>();
        try {
            String json = loadJsonAsset("ubigeo_peru_2016_distritos.json");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new Distrito(obj.getString("id"), obj.getString("name"), obj.getString("province_id"), obj.getString("department_id")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private String loadJsonAsset(String filename) {
        try (InputStream is = requireActivity().getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (IOException ex) { ex.printStackTrace(); return null; }
    }
}
