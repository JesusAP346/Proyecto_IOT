package com.example.proyecto_iot.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFotoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables para manejo de foto
    private ImageView fotoPerfil;
    private ImageButton botonSubirFoto;
    private Uri fotoUri;

    // Launchers para permisos y actividades
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;

    public RegisterFotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFotoFragment newInstance(String param1, String param2) {
        RegisterFotoFragment fragment = new RegisterFotoFragment();
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

        // Inicializar launchers
        inicializarLaunchers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_foto, container, false);

        // Inicializar vistas
        fotoPerfil = view.findViewById(R.id.fotoPerfil);
        botonSubirFoto = view.findViewById(R.id.botonSubirFoto);

        // Configurar botón de subir foto
        botonSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccionFoto();
            }
        });

        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        botonRegresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getParentFragmentManager().popBackStack();
            }
        });

        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        botonSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RegisterPasswordFragment registerPasswordFragment = new RegisterPasswordFragment();
                Bundle args = new Bundle();
                if (fotoUri != null) {
                    args.putString("fotoUri", fotoUri.toString());
                }
                registerPasswordFragment.setArguments(args);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerPasswordFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    /**
     * Inicializa los launchers para permisos y actividades
     */
    private void inicializarLaunchers() {
        // Launcher para solicitar permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );



        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            fotoUri = imageUri;
                            fotoPerfil.setImageURI(imageUri);
                            fotoPerfil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            // Hacer la imagen circular
                            hacerImagenCircular(fotoPerfil);
                        }
                    }
                }
        );

        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.get("data") != null) {
                            android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                            fotoPerfil.setImageBitmap(imageBitmap);
                            fotoPerfil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            // Hacer la imagen circular
                            hacerImagenCircular(fotoPerfil);
                        }
                    }
                }
        );
    }

    /**
     * Muestra un diálogo para seleccionar entre cámara o galería
     */
    private void mostrarDialogoSeleccionFoto() {
        String[] opciones = {"Tomar foto", "Seleccionar de galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar foto de perfil");
        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    verificarPermisosCamara();
                    break;
                case 1:
                    abrirGaleria();
                    break;
            }
        });
        builder.show();
    }

    /**
     * Verifica los permisos de la cámara antes de abrirla
     */
    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void hacerImagenCircular(ImageView imageView) {
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imageView.setClipToOutline(true);
    }

    /**
     * Abre la cámara para tomar una foto
     */
    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            camaraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(getContext(), "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Abre la galería para seleccionar una imagen
     */
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }

    /**
     * Obtiene la URI de la foto seleccionada
     * @return URI de la foto o null si no hay foto
     */
    public Uri getFotoUri() {
        return fotoUri;
    }
}