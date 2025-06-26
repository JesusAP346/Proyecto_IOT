package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.RegistroServicioDesdeHabitacion;
import com.example.proyecto_iot.administradorHotel.entity.FotoItem;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.administradorHotel.viewmodel.HabitacionViewModel;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso3Binding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import androidx.lifecycle.ViewModelProvider;
import com.example.proyecto_iot.administradorHotel.viewmodel.HabitacionViewModel;

public class RegistroPaso3Fragment extends Fragment {

    private FragmentRegistroPaso3Binding binding;

    private boolean isMenuVisible = false;

    // Lista unificada para todas las fotos
    private List<FotoItem> todasLasFotos = new ArrayList<>();
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> archivoLauncher;
    private HabitacionViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(HabitacionViewModel.class);

        // 💥 LIMPIAR ANTES DE CARGAR
        binding.containerFotos.removeAllViews();
        todasLasFotos.clear();

        // Recuperar imágenes guardadas (si las hay)
        List<FotoItem> fotosGuardadas = viewModel.getListaFotosSeleccionadas();
        if (fotosGuardadas != null) {
            for (FotoItem item : fotosGuardadas) {
                todasLasFotos.add(item);
                crearVistaPreviaFoto(item, todasLasFotos.size() - 1);
            }
            if (!todasLasFotos.isEmpty()) {
                binding.containerFotos.setVisibility(View.VISIBLE);
            }
        }

        // Inicializar Launchers (galería, cámara, archivos)
        inicializarLaunchers();

        // Configurar eventos para mostrar/ocultar menú de fotos y opciones
        configurarEventos();

        // Botón siguiente: guarda la lista de imágenes seleccionadas (aún no subidas)
        binding.btnSiguientePaso3.setOnClickListener(v -> {
            if (!validarCampos()) return;

            viewModel.setListaFotosSeleccionadas(new ArrayList<>(todasLasFotos));
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerHabitacion, new RegistroPaso4Fragment())
                    .addToBackStack(null)
                    .commit();
        });

    }


    ////////////////////////Para lanzar la calería, cámara o archivos////////////////////////////
    private void inicializarLaunchers() {
        // Launcher para galería
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                agregarFoto(new FotoItem(imageUri));
                            }
                        }
                        ocultarMenu();
                    }
                });

        // Launcher para cámara
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                if (imageBitmap != null) {
                                    agregarFoto(new FotoItem(imageBitmap));
                                }
                            }
                        }
                        ocultarMenu();
                    }
                });

        // Launcher para archivos
        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri fileUri = result.getData().getData();
                            if (fileUri != null) {
                                agregarFoto(new FotoItem(fileUri));
                            }
                        }
                        ocultarMenu();
                    }
                });
    }

    //Para mostrar/ocultar el menu de para la selccion de fotos
    private void configurarEventos() {
        // Click en el botón principal para mostrar/ocultar menú
        binding.btnAgregarFoto.setOnClickListener(v -> {
            if (isMenuVisible) {
                ocultarMenu();
            } else {
                mostrarMenu();
            }
        });

        // Opciones del menú
        binding.opcionGaleria.setOnClickListener(v -> abrirGaleria());
        binding.opcionTomarFoto.setOnClickListener(v -> abrirCamara());
        binding.opcionSeleccionarArchivo.setOnClickListener(v -> abrirSelectorArchivos());
    }


    // Menú de Selección de Fotos //////////////////////////////////////////////////7

    private void mostrarMenu() {
        // Esperar a que el layout esté completamente dibujado
        binding.btnAgregarFoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.btnAgregarFoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                posicionarMenuEncima();
            }
        });

        // Si el layout ya está dibujado, posicionar inmediatamente
        if (binding.btnAgregarFoto.getHeight() > 0) {
            posicionarMenuEncima();
        }
        // MOSTRAR FONDO PARA INTERCEPTAR TOQUES
        binding.fondoToque.setVisibility(View.VISIBLE);
        binding.fondoToque.setOnClickListener(v -> ocultarMenu());
    }

    private void ocultarMenu() {
        if (isMenuVisible) {
            // Animación de salida
            binding.menuOpciones.animate()
                    .alpha(0f)
                    .scaleY(0.8f)
                    .setDuration(150)
                    .withEndAction(() -> {

                        binding.menuOpciones.setVisibility(View.GONE);
                        binding.fondoToque.setVisibility(View.GONE);
                    })
                    .start();

            isMenuVisible = false;
        }
    }

    //Menú de opciones para la seleccion  de fotos
    private void posicionarMenuEncima() {
        // Obtener las dimensiones de la pantalla desde el Activity
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calcular la posición del botón en la pantalla
        int[] buttonLocation = new int[2];
        binding.btnAgregarFoto.getLocationOnScreen(buttonLocation);

        // Medir el menú para obtener su altura
        binding.menuOpciones.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int menuHeight = binding.menuOpciones.getMeasuredHeight();

        // Calcular la posición Y del menú (por encima del botón)
        int buttonTopY = buttonLocation[1];
        int statusBarHeight = getStatusBarHeight(); // esto no cambia, funciona igual
        int menuTopY = buttonTopY - menuHeight;

        // Asegurar que el menú no se salga de la pantalla por arriba
        if (menuTopY < statusBarHeight + 50) {
            menuTopY = statusBarHeight + 50;
        }

        // Configurar la posición del menú
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.menuOpciones.getLayoutParams();
        params.topMargin = menuTopY;
        binding.menuOpciones.setLayoutParams(params);

        // Mostrar el overlay y el menú con animación suave
        binding.menuOpciones.setVisibility(View.VISIBLE);
        binding.menuOpciones.setAlpha(0f);
        binding.menuOpciones.setScaleY(0.8f);
        binding.menuOpciones.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        isMenuVisible = true;
    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //Abrir Galería, Cámara y Archivos //////////////////////////////////////////////
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(), // ← CORREGIDO
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) { // ← CORREGIDO
                camaraLauncher.launch(intent);
            } else {
                Toast.makeText(requireContext(), "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void abrirSelectorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        archivoLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara(); // vuelve a lanzar
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /////////Lógica para Agregar, eliminar y mostrar (previsualizar) las fotos/////////////////////

    // Metodo unificado para agregar fotos
    private void agregarFoto(FotoItem fotoItem) {
        todasLasFotos.add(fotoItem);
        viewModel.setListaFotosSeleccionadas(new ArrayList<>(todasLasFotos)); // ✅ CORRECTO
        crearVistaPreviaFoto(fotoItem, todasLasFotos.size() - 1);
        binding.containerFotos.setVisibility(View.VISIBLE);

        // Validación en tiempo real
        if (todasLasFotos.size() >= 2 && binding.errorTipoFotos.getVisibility() == View.VISIBLE) {
            binding.errorTipoFotos.setVisibility(View.GONE);
        }
    }


    // Metodo unificado para crear vista previa
    private void crearVistaPreviaFoto(FotoItem fotoItem, int index) {
        // Crear CardView como contenedor principal
        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (50 * getResources().getDisplayMetrics().density));
        cardParams.setMargins(0, 2, 0, 2);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2);
        cardView.setRadius(6);

        // Asignar un tag con el índice para identificar la vista
        cardView.setTag(index);

        // Crear LinearLayout horizontal dentro del CardView
        LinearLayout container = new LinearLayout(requireContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.setPadding(6, 6, 6, 6);

        // ImageView para mostrar la foto
        ImageView imageView = new ImageView(requireContext());
        int imageSize = (int) (38 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, 8, 0);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.image_border));

        // Cargar imagen según el tipo
        if (fotoItem.bitmap != null) {
            imageView.setImageBitmap(fotoItem.bitmap);
        } else if (fotoItem.uri != null) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(fotoItem.uri);
                Bitmap bitmapFromUri = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmapFromUri);
            } catch (FileNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_gallery);
            }
        }

        // Click en la imagen para abrir en pantalla completa
        imageView.setOnClickListener(v -> mostrarImagenCompleta(fotoItem.uri, fotoItem.bitmap));

        // Espaciador para empujar el botón hacia la derecha
        View spacer = new View(requireContext());
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        spacer.setLayoutParams(spacerParams);

        // Botón eliminar
        MaterialButton btnEliminar = new MaterialButton(requireContext());
        btnEliminar.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete));
        btnEliminar.setIconSize(42);
        btnEliminar.setIconTint(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        btnEliminar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        btnEliminar.setCornerRadius(8);
        btnEliminar.setStrokeWidth(0);
        btnEliminar.setMinWidth(0);
        btnEliminar.setMinHeight(0);
        btnEliminar.setText("");

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        btnEliminar.setLayoutParams(btnParams);
        btnEliminar.setPadding(0, 0, 0, 0);

        // Evento para eliminar foto - CORREGIDO
        btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));

        // Agregar vistas al contenedor
        container.addView(imageView);
        container.addView(spacer);
        container.addView(btnEliminar);
        cardView.addView(container);

        // Agregar al contenedor principal
        binding.containerFotos.addView(cardView);
    }
    //metodo para mostrar la imagen en la vista previa
    private void mostrarImagenCompleta(Uri imageUri, Bitmap bitmap) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

        // 🔧 Corrección para Fragment
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics); // ← cambio aquí

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        RelativeLayout layout = new RelativeLayout(requireContext()); // ← corrección de this
        layout.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
        layout.setBackgroundColor(Color.parseColor("#CC000000"));
        layout.setPadding(-50, -50, -50, -50);

        ImageView imageView = new ImageView(requireContext()); // ← corrección de this
        int maxWidth = (int) (screenWidth * 0.8);
        int maxHeight = (int) (screenHeight * 0.6);

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(maxWidth, maxHeight);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageParams.setMargins(50, 50, 50, 50);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);

        if (bitmap != null) {
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, maxWidth, maxHeight);
            imageView.setImageBitmap(resizedBitmap);
        } else if (imageUri != null) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri); // ← corrección de this
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = redimensionarBitmap(originalBitmap, maxWidth, maxHeight);
                imageView.setImageBitmap(resizedBitmap);
            } catch (FileNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_gallery);
            }
        }

        MaterialButton btnCerrar = new MaterialButton(requireContext()); // ← corrección de this
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                (int) (48 * getResources().getDisplayMetrics().density),
                (int) (48 * getResources().getDisplayMetrics().density));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.setMargins(66, 98, 16, 66);
        btnCerrar.setLayoutParams(btnParams);

        btnCerrar.setIcon(ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_close_clear_cancel));
        btnCerrar.setIconSize((int) (32 * getResources().getDisplayMetrics().density));
        btnCerrar.setIconTint(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        btnCerrar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        btnCerrar.setCornerRadius((int) (24 * getResources().getDisplayMetrics().density));
        btnCerrar.setText("");
        btnCerrar.setPadding(0, 0, 0, 0);
        btnCerrar.setMinWidth(0);
        btnCerrar.setMinHeight(0);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        layout.setOnClickListener(v -> dialog.dismiss());

        layout.addView(imageView);
        layout.addView(btnCerrar);

        dialog.setContentView(layout);
        dialog.show();
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calcular ratio para mantener proporción
        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // Metodo  para eliminar fotos
    private void eliminarFoto(CardView cardView) {
        // Encontrar el índice de la vista en el contenedor
        int indexEnContenedor = binding.containerFotos.indexOfChild(cardView);

        if (indexEnContenedor >= 0 && indexEnContenedor < todasLasFotos.size()) {
            // Eliminar de la lista
            todasLasFotos.remove(indexEnContenedor);

            // Eliminar la vista del contenedor
            binding.containerFotos.removeView(cardView);

            // Si no quedan fotos, ocultar el contenedor
            if (todasLasFotos.isEmpty()) {
                binding.containerFotos.setVisibility(View.GONE);
            }

            // Actualizar los listeners de los botones restantes
            actualizarListenersEliminacion();
        }
        viewModel.setListaFotosSeleccionadas(new ArrayList<>(todasLasFotos)); // mantener sincronizado con el ViewModel

        if (todasLasFotos.size() >= 2 && binding.errorTipoFotos.getVisibility() == View.VISIBLE) {
            binding.errorTipoFotos.setVisibility(View.GONE);
        }
    }

    // Metodo para actualizar los listeners de eliminación después de eliminar una foto
    private void actualizarListenersEliminacion() {
        for (int i = 0; i < binding.containerFotos.getChildCount(); i++) {
            View childView = binding.containerFotos.getChildAt(i);
            if (childView instanceof CardView) {
                CardView cardView = (CardView) childView;
                LinearLayout container = (LinearLayout) cardView.getChildAt(0);

                // Encontrar el botón eliminar
                MaterialButton btnEliminar = null;
                for (int j = 0; j < container.getChildCount(); j++) {
                    View view = container.getChildAt(j);
                    if (view instanceof MaterialButton) {
                        btnEliminar = (MaterialButton) view;
                        break;
                    }
                }

                if (btnEliminar != null) {
                    // Actualizar el listener con la referencia correcta
                    btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));
                }
            }
        }
    }



    private boolean validarCampos() {
        boolean esValido = true;

        if (todasLasFotos.size() < 2) {
            binding.errorTipoFotos.setText("Debe agregar al menos 2 fotos del servicio");
            binding.errorTipoFotos.setVisibility(View.VISIBLE);
            esValido = false;
        }

        return esValido;
    }
    private void mostrarError(TextView errorView, String mensaje) {
        errorView.setText(mensaje);
        errorView.setVisibility(View.VISIBLE);

        // Opcional: hacer scroll hacia el error
        errorView.getParent().requestChildFocus(errorView, errorView);
    }
    // Validación en tiempo real (opcional)




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
