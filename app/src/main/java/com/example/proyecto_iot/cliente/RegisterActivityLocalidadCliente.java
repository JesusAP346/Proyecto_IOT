package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.models.Departamento;
import com.example.proyecto_iot.models.Provincia;
import com.example.proyecto_iot.models.Distrito;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivityLocalidadCliente extends AppCompatActivity {

    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9\\s.,#-]+$");

    private AutoCompleteTextView dropdownDepartamento, dropdownProvincia, dropdownDistrito;
    private TextInputEditText editTextDireccion;
    private TextInputLayout layoutDepartamento, layoutProvincia, layoutDistrito, layoutDireccion;

    private List<Departamento> departamentos;
    private List<Provincia> todasProvincias;
    private List<Distrito> todosDistritos;
    private List<Provincia> provinciasFiltradas = new ArrayList<>();
    private List<Distrito> distritosFiltrados = new ArrayList<>();

    private Departamento departamentoSeleccionado;
    private Provincia provinciaSeleccionada;
    private Distrito distritoSeleccionado;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_localidad_cliente);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userDocRef = firestore.collection("usuarios").document(currentUser.getUid());

        layoutDepartamento = findViewById(R.id.textInputLayoutDepartamento);
        layoutProvincia = findViewById(R.id.textInputLayoutProvincia);
        layoutDistrito = findViewById(R.id.textInputLayoutDistrito);
        layoutDireccion = findViewById(R.id.textInputLayoutDireccion);

        dropdownDepartamento = findViewById(R.id.dropdownDepartamento);
        dropdownProvincia = findViewById(R.id.dropdownProvincia);
        dropdownDistrito = findViewById(R.id.dropdownDistrito);
        editTextDireccion = findViewById(R.id.editTextDireccion);

        Button botonGuardar = findViewById(R.id.botonGuardar);
        Button botonCancelar = findViewById(R.id.botonCancelar);

        departamentos = loadDepartamentos();
        todasProvincias = loadProvincias();
        todosDistritos = loadDistritos();

        configurarAdapters();

        cargarDatosActuales();

        dropdownDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            departamentoSeleccionado = departamentos.get(position);
            filtrarProvincias();
        });

        dropdownProvincia.setOnItemClickListener((parent, view, position, id) -> {
            provinciaSeleccionada = provinciasFiltradas.get(position);
            filtrarDistritos();
        });

        dropdownDistrito.setOnItemClickListener((parent, view, position, id) -> {
            distritoSeleccionado = distritosFiltrados.get(position);
        });

        botonGuardar.setOnClickListener(v -> guardarCambios());
        botonCancelar.setOnClickListener(v -> finish());
    }

    private void configurarAdapters() {
        dropdownDepartamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departamentos));
        dropdownProvincia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinciasFiltradas));
        dropdownDistrito.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distritosFiltrados));
    }

    private void cargarDatosActuales() {
        userDocRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Usuario usuario = snapshot.toObject(Usuario.class);
                if (usuario != null) {
                    if (!TextUtils.isEmpty(usuario.getDepartamento())) {
                        dropdownDepartamento.setText(usuario.getDepartamento(), false);
                        for (Departamento dep : departamentos) {
                            if (dep.getNombre().equalsIgnoreCase(usuario.getDepartamento())) {
                                departamentoSeleccionado = dep;
                                filtrarProvincias();
                                break;
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(usuario.getProvincia())) {
                        dropdownProvincia.setText(usuario.getProvincia(), false);
                        for (Provincia prov : provinciasFiltradas) {
                            if (prov.getNombre().equalsIgnoreCase(usuario.getProvincia())) {
                                provinciaSeleccionada = prov;
                                filtrarDistritos();
                                break;
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(usuario.getDistrito())) {
                        dropdownDistrito.setText(usuario.getDistrito(), false);
                        for (Distrito dist : distritosFiltrados) {
                            if (dist.getNombre().equalsIgnoreCase(usuario.getDistrito())) {
                                distritoSeleccionado = dist;
                                break;
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(usuario.getDireccion())) {
                        editTextDireccion.setText(usuario.getDireccion());
                    }
                }
            }
        });
    }

    private void filtrarProvincias() {
        provinciasFiltradas.clear();
        provinciaSeleccionada = null;
        distritoSeleccionado = null;
        dropdownProvincia.setText("");
        dropdownDistrito.setText("");
        distritosFiltrados.clear();

        for (Provincia p : todasProvincias) {
            if (p.getDepartment_id().equals(departamentoSeleccionado.getId())) {
                provinciasFiltradas.add(p);
            }
        }

        dropdownProvincia.setEnabled(true);
        layoutProvincia.setAlpha(1f);
        dropdownProvincia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinciasFiltradas));
    }

    private void filtrarDistritos() {
        distritosFiltrados.clear();
        distritoSeleccionado = null;
        dropdownDistrito.setText("");

        for (Distrito d : todosDistritos) {
            if (d.getProvince_id().equals(provinciaSeleccionada.getId())) {
                distritosFiltrados.add(d);
            }
        }

        dropdownDistrito.setEnabled(true);
        layoutDistrito.setAlpha(1f);
        dropdownDistrito.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distritosFiltrados));
    }

    private void guardarCambios() {
        layoutDepartamento.setError(null);
        layoutProvincia.setError(null);
        layoutDistrito.setError(null);
        layoutDireccion.setError(null);

        boolean valido = true;

        String direccion = editTextDireccion.getText().toString().trim();

        if (departamentoSeleccionado == null) {
            layoutDepartamento.setError("Seleccione un departamento");
            valido = false;
        }
        if (provinciaSeleccionada == null) {
            layoutProvincia.setError("Seleccione una provincia");
            valido = false;
        }
        if (distritoSeleccionado == null) {
            layoutDistrito.setError("Seleccione un distrito");
            valido = false;
        }
        if (direccion.isEmpty()) {
            layoutDireccion.setError("Ingrese su dirección");
            valido = false;
        } else if (!DIRECCION_PATTERN.matcher(direccion).matches()) {
            layoutDireccion.setError("Dirección no válida");
            valido = false;
        }

        if (!valido) return;

        userDocRef.update(
                "departamento", departamentoSeleccionado.getNombre(),
                "provincia", provinciaSeleccionada.getNombre(),
                "distrito", distritoSeleccionado.getNombre(),
                "direccion", direccion
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Ubicación actualizada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al actualizar ubicación", Toast.LENGTH_SHORT).show();
        });
    }

    // 🔽 Métodos para cargar JSON
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
        try (InputStream is = getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
}
