package com.example.proyecto_iot.administradorHotel.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private TextView tvEmptyChat;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration messageListener;

    private String chatId;
    private String userId;
    private String currentAdminId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getIntentData();
        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        setupToolbar();
        setupSendButton();
        loadUserName();
        loadMessages();
    }

    private void getIntentData() {
        chatId = getIntent().getStringExtra("chatId");
        userId = getIntent().getStringExtra("userId");

        if (TextUtils.isEmpty(chatId) || TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Error: Datos del chat no válidos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyChat = findViewById(R.id.tv_empty_chat);
        toolbar = findViewById(R.id.toolbar);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            currentAdminId = auth.getCurrentUser().getUid();
        } else {
            Log.e(TAG, "No hay usuario autenticado");
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentAdminId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Los mensajes más recientes al final

        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cargando...");
        }
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadUserName() {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            userName = usuario.getNombres() + " " + usuario.getApellidos();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(userName);
                            }
                        } else {
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle("Usuario desconocido");
                            }
                        }
                    } else {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Usuario no encontrado");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar nombre de usuario", e);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Error al cargar usuario");
                    }
                });
    }

    private void loadMessages() {
        showLoading(true);

        messageListener = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    showLoading(false);

                    if (error != null) {
                        Log.e(TAG, "Error al cargar mensajes: ", error);
                        Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        messageList.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                message.setMessageId(doc.getId());
                                messageList.add(message);
                            }
                        }

                        messageAdapter.updateMessageList(messageList);

                        // Mostrar mensaje si no hay conversación
                        if (messageList.isEmpty()) {
                            tvEmptyChat.setVisibility(View.VISIBLE);
                            recyclerViewMessages.setVisibility(View.GONE);
                        } else {
                            tvEmptyChat.setVisibility(View.GONE);
                            recyclerViewMessages.setVisibility(View.VISIBLE);

                            // Scroll al último mensaje
                            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                        }

                        // Marcar mensajes como leídos
                        markMessagesAsRead();

                        Log.d(TAG, "Mensajes cargados: " + messageList.size());
                    }
                });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto mensaje
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentAdminId);
        messageData.put("receiverId", userId);
        messageData.put("text", messageText);
        messageData.put("timestamp", new Date());
        messageData.put("type", "text");
        messageData.put("isRead", false);

        // Guardar el mensaje en Firestore
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Mensaje enviado: " + documentReference.getId());

                    // Actualizar el último mensaje del chat
                    updateLastMessage(messageText);

                    // Limpiar el campo de texto
                    etMessage.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al enviar mensaje", e);
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateLastMessage(String messageText) {
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage", messageText);
        chatUpdate.put("lastMessageTime", new Date());

        db.collection("chats")
                .document(chatId)
                .update(chatUpdate)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Último mensaje actualizado");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar último mensaje", e);
                });
    }

    private void markMessagesAsRead() {
        // Marcar como leídos todos los mensajes que no sean del administrador actual
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("isRead", false)
                .whereNotEqualTo("senderId", currentAdminId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().update("isRead", true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al marcar mensajes como leídos", e);
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerViewMessages.setVisibility(View.GONE);
            tvEmptyChat.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}