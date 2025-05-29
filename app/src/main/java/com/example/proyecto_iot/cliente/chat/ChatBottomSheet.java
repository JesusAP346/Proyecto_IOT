package com.example.proyecto_iot.cliente.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatBottomSheet extends BottomSheetDialogFragment {

    private EditText etMensaje;
    private ImageButton btnEnviarMicrofono;
    private ImageButton btnAdjuntar;
    private ImageButton btnEmoji;
    private ImageButton btnCerrarChat;
    private RecyclerView recyclerViewMensajes;
    private TextView tvEstado;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> mensajes;
    private boolean isRecording = false;
    private PopupWindow emojiPopup;

    // SharedPreferences para persistencia
    private static final String PREF_NAME = "chat_messages";
    private static final String KEY_MESSAGES = "messages_list";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    // Request codes para intents
    private static final int REQUEST_PICK_FILE = 100;
    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_CAMERA = 102;

    // Emojis disponibles
    private static final String[] EMOJI_LIST = {
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
            "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩",
            "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣",
            "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬",
            "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗",
            "👍", "👎", "👌", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉",
            "👆", "👇", "☝️", "✋", "🤚", "🖐️", "🖖", "👋", "🤏", "💪",
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_chat, container, false);

        initStorage();
        initViews(view);
        loadMessages();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Forzar BottomSheet a pantalla completa
        View parent = (View) view.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parent);

        bottomSheetBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setDraggable(false);

        updateEstado("En línea");
    }

    private void initStorage() {
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    private void initViews(View view) {
        etMensaje = view.findViewById(R.id.etMensaje);
        btnEnviarMicrofono = view.findViewById(R.id.btnEnviarMicrofono);
        btnAdjuntar = view.findViewById(R.id.btnAdjuntar);
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnCerrarChat = view.findViewById(R.id.btnCerrarChat);
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        tvEstado = view.findViewById(R.id.tvEstado);
    }

    private void loadMessages() {
        String messagesJson = sharedPreferences.getString(KEY_MESSAGES, null);

        if (messagesJson != null) {
            Type type = new TypeToken<List<ChatMessage>>(){}.getType();
            mensajes = gson.fromJson(messagesJson, type);

            if (mensajes == null) {
                mensajes = new ArrayList<>();
            }
        } else {
            mensajes = new ArrayList<>();
            agregarMensajeSoporte("¡Hola! ¿En qué puedo ayudarte hoy?");
        }
    }

    private void saveMessages() {
        String messagesJson = gson.toJson(mensajes);
        sharedPreferences.edit()
                .putString(KEY_MESSAGES, messagesJson)
                .apply();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(mensajes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        recyclerViewMensajes.setLayoutManager(layoutManager);
        recyclerViewMensajes.setAdapter(chatAdapter);

        if (!mensajes.isEmpty()) {
            recyclerViewMensajes.scrollToPosition(mensajes.size() - 1);
        }
    }

    private void setupListeners() {
        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendButton(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEnviarMicrofono.setOnClickListener(v -> {
            String mensaje = etMensaje.getText().toString().trim();

            if (!mensaje.isEmpty()) {
                enviarMensaje(mensaje);
            } else {
                toggleRecording();
            }
        });

        btnAdjuntar.setOnClickListener(v -> mostrarOpcionesAdjuntar());
        btnEmoji.setOnClickListener(v -> mostrarSelectorEmoji());
        btnCerrarChat.setOnClickListener(v -> dismiss());
    }

    private void updateSendButton(boolean hasText) {
        if (hasText) {
            btnEnviarMicrofono.setImageResource(R.drawable.ic_send);
            btnEnviarMicrofono.setContentDescription("Enviar mensaje");
        } else {
            btnEnviarMicrofono.setImageResource(R.drawable.ic_mic);
            btnEnviarMicrofono.setContentDescription("Grabar mensaje de voz");
        }
    }

    private void enviarMensaje(String texto) {
        ChatMessage mensaje = new ChatMessage(
                texto,
                getCurrentTime(),
                true,
                ChatMessage.MessageStatus.SENT
        );

        mensajes.add(mensaje);
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);

        saveMessages();
        etMensaje.setText("");
    }

    private void agregarMensajeSoporte(String texto) {
        ChatMessage mensaje = new ChatMessage(
                texto,
                getCurrentTime(),
                false,
                ChatMessage.MessageStatus.DELIVERED
        );

        mensajes.add(mensaje);
        saveMessages();

        if (chatAdapter != null) {
            chatAdapter.notifyItemInserted(mensajes.size() - 1);
            recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);
        }
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        btnEnviarMicrofono.setImageResource(R.drawable.ic_send);
    }

    private void stopRecording() {
        isRecording = false;
        btnEnviarMicrofono.setImageResource(R.drawable.ic_mic);
        updateEstado("En línea");

        enviarMensajeAudio();
    }

    private void enviarMensajeAudio() {
        ChatMessage mensaje = new ChatMessage(
                "🎵 Mensaje de audio",
                getCurrentTime(),
                true,
                ChatMessage.MessageStatus.SENT
        );

        mensajes.add(mensaje);
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);

        saveMessages();
    }

    private void mostrarOpcionesAdjuntar() {
        String[] opciones = {"Cámara", "Galería", "Archivo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Adjuntar archivo")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Cámara
                            abrirCamara();
                            break;
                        case 1: // Galería
                            abrirGaleria();
                            break;
                        case 2: // Archivo
                            abrirExploradorArchivos();
                            break;
                    }
                })
                .show();
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void abrirExploradorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), REQUEST_PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String mensajeArchivo = "";

            switch (requestCode) {
                case REQUEST_CAMERA:
                    mensajeArchivo = "📷 Foto de cámara";
                    break;
                case REQUEST_PICK_IMAGE:
                    mensajeArchivo = "🖼️ Imagen de galería";
                    break;
                case REQUEST_PICK_FILE:
                    if (data != null && data.getData() != null) {
                        String fileName = getFileName(data.getData());
                        mensajeArchivo = "📎 " + (fileName != null ? fileName : "Archivo adjunto");
                    } else {
                        mensajeArchivo = "📎 Archivo adjunto";
                    }
                    break;
            }

            enviarMensajeArchivo(mensajeArchivo);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void enviarMensajeArchivo(String textoArchivo) {
        ChatMessage mensaje = new ChatMessage(
                textoArchivo,
                getCurrentTime(),
                true,
                ChatMessage.MessageStatus.SENT
        );

        mensajes.add(mensaje);
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);

        saveMessages();
    }

    private void mostrarSelectorEmoji() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
            return;
        }

        // Crear vista del popup
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.emoji_selector, null);
        GridLayout gridEmojis = popupView.findViewById(R.id.gridEmojis);

        // Configurar grid de emojis
        gridEmojis.setColumnCount(10);
        gridEmojis.setRowCount((int) Math.ceil(EMOJI_LIST.length / 10.0));

        // Agregar emojis al grid
        for (String emoji : EMOJI_LIST) {
            TextView tvEmoji = new TextView(getContext());
            tvEmoji.setText(emoji);
            tvEmoji.setTextSize(24);
            tvEmoji.setPadding(16, 16, 16, 16);
            tvEmoji.setOnClickListener(v -> {
                insertarEmoji(emoji);
                emojiPopup.dismiss();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(4, 4, 4, 4);
            tvEmoji.setLayoutParams(params);

            gridEmojis.addView(tvEmoji);
        }

        // Crear y mostrar popup
        emojiPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                600,
                true);
        emojiPopup.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        emojiPopup.showAsDropDown(btnEmoji, 0, -610);
    }

    private void insertarEmoji(String emoji) {
        String textoActual = etMensaje.getText().toString();
        int cursorPos = etMensaje.getSelectionStart();

        String nuevoTexto = textoActual.substring(0, cursorPos) + emoji + textoActual.substring(cursorPos);
        etMensaje.setText(nuevoTexto);
        etMensaje.setSelection(cursorPos + emoji.length());
    }

    private void updateEstado(String estado) {
        if (tvEstado != null) {
            tvEstado.setText(estado);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void limpiarChat() {
        mensajes.clear();
        chatAdapter.notifyDataSetChanged();
        saveMessages();
        agregarMensajeSoporte("¡Hola! ¿En qué puedo ayudarte hoy?");
    }

    public static class ChatMessage {
        private String texto;
        private String hora;
        private boolean isEnviado;
        private MessageStatus status;

        public enum MessageStatus {
            SENDING, SENT, DELIVERED, READ, ERROR
        }

        public ChatMessage(String texto, String hora, boolean isEnviado, MessageStatus status) {
            this.texto = texto;
            this.hora = hora;
            this.isEnviado = isEnviado;
            this.status = status;
        }

        public String getTexto() { return texto; }
        public String getHora() { return hora; }
        public boolean isEnviado() { return isEnviado; }
        public MessageStatus getStatus() { return status; }
        public void setStatus(MessageStatus status) { this.status = status; }
    }
}