package com.example.proyecto_iot.SuperAdmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.LogAdapter;
import com.example.proyecto_iot.SuperAdmin.adapter.UsuarioHistorialAdapter;
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsuarioHistorialAdapter usuariosAdapter;
    private List<Usuario> usuariosList = new ArrayList<>();
    private List<Usuario> usuariosFiltrados = new ArrayList<>();
    private String filtroRolActual = "Todos";
    private SearchView searchView;

    private MaterialButton btnFiltroRol, btnLimpiarFiltros;

    private final CollectionReference usuariosRef = FirebaseFirestore.getInstance().collection("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerView = findViewById(R.id.recyclerLogsHistorial);
        searchView = findViewById(R.id.svBuscarLogs);
        btnFiltroRol = findViewById(R.id.btnFiltroRol);
        btnLimpiarFiltros = findViewById(R.id.btnLimpiarFiltros);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usuariosAdapter = new UsuarioHistorialAdapter(usuariosFiltrados, usuario -> {
            //Toast.makeText(this, "Seleccionaste a " + usuario.getNombres(), Toast.LENGTH_SHORT).show();
            mostrarHistorialLogsEnDialog(usuario.getId());
        });

        recyclerView.setAdapter(usuariosAdapter);

        btnFiltroRol.setOnClickListener(v -> mostrarPopupFiltroRol());

        btnLimpiarFiltros.setOnClickListener(v -> {
            filtroRolActual = "Todos";
            btnFiltroRol.setText("Rol");  // Reset texto botón
            searchView.setQuery("", false);  // Limpiar texto búsqueda
            aplicarFiltro(filtroRolActual, "");  // Reset filtros
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                aplicarFiltro(filtroRolActual, newText);
                return true;
            }
        });
    }

    private void mostrarPopupFiltroRol() {
        PopupMenu popup = new PopupMenu(this, btnFiltroRol);

        popup.getMenu().add("Todos");
        popup.getMenu().add("Cliente");
        popup.getMenu().add("Administrador");
        popup.getMenu().add("Taxista");
        popup.getMenu().add("SuperAdmin");

        popup.setOnMenuItemClickListener(item -> {
            filtroRolActual = item.getTitle().toString();
            btnFiltroRol.setText(filtroRolActual);
            aplicarFiltro(filtroRolActual, searchView.getQuery().toString());
            return true;
        });

        popup.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuariosDesdeFirestore();
    }

    private void cargarUsuariosDesdeFirestore() {
        usuariosRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            usuariosList.clear();

            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null) {
                        usuario.setId(doc.getId());
                        usuariosList.add(usuario);
                    }
                }
            }

            aplicarFiltro(filtroRolActual, searchView.getQuery().toString());
        });
    }

    private void aplicarFiltro(String rolSeleccionado, String textoBusqueda) {
        usuariosFiltrados.clear();

        for (Usuario u : usuariosList) {
            boolean cumpleFiltroRol = rolSeleccionado.equals("Todos") || rolSeleccionado.equalsIgnoreCase(u.getIdRol());
            if (cumpleFiltroRol) {
                usuariosFiltrados.add(u);
            }
        }

        if (!textoBusqueda.isEmpty()) {
            String queryLower = textoBusqueda.toLowerCase(Locale.getDefault());
            List<Usuario> filtradosPorTexto = new ArrayList<>();
            for (Usuario usuario : usuariosFiltrados) {
                String nombreCompleto = (usuario.getNombres() + " " + usuario.getApellidos()).toLowerCase(Locale.getDefault());
                if (nombreCompleto.contains(queryLower)) {
                    filtradosPorTexto.add(usuario);
                }
            }
            usuariosAdapter.updateList(filtradosPorTexto);
        } else {
            usuariosAdapter.updateList(new ArrayList<>(usuariosFiltrados));
        }
    }

    private void mostrarHistorialLogsEnDialog(String uidUsuario) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_historial_logs, null);
        RecyclerView recyclerHistorial = dialogView.findViewById(R.id.recyclerHistorialLogs);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));
        LogAdapter logAdapter = new LogAdapter(new ArrayList<>());
        recyclerHistorial.setAdapter(logAdapter);

        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        dialog.show();

        // 🔥 NUEVO: traer todos los logs y filtrar manualmente por uidUsuario
        FirebaseFirestore.getInstance()
                .collection("logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<LogSA> logsUsuario = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        LogSA log = doc.toObject(LogSA.class);
                        if (log != null && uidUsuario.equals(log.getUidUsuario())) {
                            logsUsuario.add(log);
                        }
                    }
                    logAdapter.updateList(logsUsuario);
                });
    }


}
