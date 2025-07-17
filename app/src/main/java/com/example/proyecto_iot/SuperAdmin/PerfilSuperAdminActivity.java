package com.example.proyecto_iot.SuperAdmin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

public class PerfilSuperAdminActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;

    private ImageView btnBack, ivFotoPerfil;
    private TextView tvNombreCompleto, tvTipoDocumento, tvNumeroDocumento, tvFechaNacimiento,
            tvDepartamento, tvProvincia, tvDistrito, tvDireccion, tvTelefono, tvCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_super_admin);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = firestore.collection("usuarios").document(uid);
        listenerRegistration = docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null) {
                    actualizarVista(usuario);
                }
            }
        });
    }

    private void actualizarVista(Usuario usuario) {
        String nombreCompleto = (usuario.getNombres() != null ? usuario.getNombres() : "") +
                (usuario.getApellidos() != null ? " " + usuario.getApellidos() : "");
        tvNombreCompleto.setText(nombreCompleto.trim());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
