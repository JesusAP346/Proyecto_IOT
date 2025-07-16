package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PerfilClienteActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ImageView btnBack, ivFotoPerfil;
    private TextView tvNombreCompleto, tvTipoDocumento, tvNumeroDocumento, tvFechaNacimiento,
            tvDepartamento, tvProvincia, tvDistrito, tvDireccion, tvTelefono, tvCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cliente);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvTipoDocumento = findViewById(R.id.tvTipoDocumento);
        tvNumeroDocumento = findViewById(R.id.tvNumeroDocumento);
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        tvDepartamento = findViewById(R.id.tvDepartamento);
        tvProvincia = findViewById(R.id.tvProvincia);
        tvDistrito = findViewById(R.id.tvDistrito);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvCorreo = findViewById(R.id.tvCorreo);

        btnBack.setOnClickListener(v -> finish());

        escucharDatosUsuarioEnTiempoReal();
    }

    private void escucharDatosUsuarioEnTiempoReal() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DocumentReference docRef = db.collection("usuarios").document(uid);

        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error al escuchar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null) {
                    actualizarUIConDatosUsuario(usuario);
                }
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarUIConDatosUsuario(Usuario usuario) {
        String nombre = usuario.getNombres() != null ? usuario.getNombres() : "";
        String apellido = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        tvNombreCompleto.setText(nombre + " " + apellido);

        tvTipoDocumento.setText(usuario.getTipoDocumento() != null ? usuario.getTipoDocumento() : "");
        tvNumeroDocumento.setText(usuario.getNumDocumento() != null ? usuario.getNumDocumento() : "");
        tvFechaNacimiento.setText(usuario.getFechaNacimiento() != null ? usuario.getFechaNacimiento() : "");
        tvDepartamento.setText(usuario.getDepartamento() != null ? usuario.getDepartamento() : "");
        tvProvincia.setText(usuario.getProvincia() != null ? usuario.getProvincia() : "");
        tvDistrito.setText(usuario.getDistrito() != null ? usuario.getDistrito() : "");
        tvDireccion.setText(usuario.getDireccion() != null ? usuario.getDireccion() : "");
        tvTelefono.setText(usuario.getNumCelular() != null ? usuario.getNumCelular() : "");
        tvCorreo.setText(usuario.getEmail() != null ? usuario.getEmail() : "");

        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Picasso.get()
                    .load(usuario.getUrlFotoPerfil())
                    .placeholder(R.drawable.ic_generic_user)
                    .into(ivFotoPerfil);
        } else {
            ivFotoPerfil.setImageResource(R.drawable.ic_generic_user);
        }
    }
}
