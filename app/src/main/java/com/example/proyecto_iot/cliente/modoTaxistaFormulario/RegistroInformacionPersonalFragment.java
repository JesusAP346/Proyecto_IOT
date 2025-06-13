package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroInformacionPersonalFragment extends Fragment {
    private TextInputEditText etNombre, etApellido, etFechaNacimiento;
    private ImageView imageViewPerfil;

    public RegistroInformacionPersonalFragment() {
        super(R.layout.fragment_registro_informacion_personal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_informacion_personal, container, false);

        imageViewPerfil = view.findViewById(R.id.imageView4);
        etNombre = view.findViewById(R.id.etNombre);
        etApellido = view.findViewById(R.id.etApellido);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);

        // Deshabilitar edición
        etNombre.setEnabled(false);
        etApellido.setEnabled(false);
        etFechaNacimiento.setEnabled(false);

        // Obtener y mostrar datos del usuario actual
        cargarDatosUsuario();

        return view;
    }

    private void cargarDatosUsuario() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> mostrarDatos(documentSnapshot))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDatos(DocumentSnapshot doc) {
        if (doc.exists()) {
            etNombre.setText(doc.getString("nombres"));
            etApellido.setText(doc.getString("apellidos"));
            etFechaNacimiento.setText(doc.getString("fechaNacimiento"));
            // Puedes cargar imagen si la tienes en Firestore/Storage
        } else {
            Toast.makeText(getContext(), "Datos de usuario no encontrados", Toast.LENGTH_SHORT).show();
        }
    }
}
