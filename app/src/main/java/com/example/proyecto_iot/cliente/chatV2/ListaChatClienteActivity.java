package com.example.proyecto_iot.cliente.chatV2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.proyecto_iot.administradorHotel.chat.Chat;
import com.example.proyecto_iot.administradorHotel.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ListaChatClienteActivity extends AppCompatActivity implements ChatClienteAdapter.OnChatClickListener {

    private static final String TAG = "ListaChatClienteActivity";

    private RecyclerView recyclerViewChats;
    private ChatClienteAdapter chatAdapter;
    private List<Chat> chatList;
    private ProgressBar progressBar;
    private TextView tvNoChats;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration chatListener;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_chat_cliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        setupToolbar();
        loadChats();
    }

    private void initializeViews() {
        recyclerViewChats = findViewById(R.id.recycler_view_chats);
        progressBar = findViewById(R.id.progress_bar);
        tvNoChats = findViewById(R.id.tv_no_chats);
        toolbar = findViewById(R.id.toolbar);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Obtener el ID del usuario actual
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        } else {
            Log.e(TAG, "No hay usuario autenticado");
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void setupRecyclerView() {
        chatList = new ArrayList<>();
        chatAdapter = new ChatClienteAdapter(chatList, this);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChats.setAdapter(chatAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Chats");
        }
    }

    private void loadChats() {
        showLoading(true);

        // Consultar chats donde el userId sea igual al usuario actual
        chatListener = db.collection("chats")
                .whereEqualTo("userId", currentUserId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    showLoading(false);

                    if (error != null) {
                        Log.e(TAG, "Error al cargar chats: ", error);
                        Toast.makeText(this, "Error al cargar chats", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        chatList.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Chat chat = doc.toObject(Chat.class);
                            if (chat != null) {
                                chat.setChatId(doc.getId());
                                chatList.add(chat);
                            }
                        }

                        chatAdapter.updateChatList(chatList);

                        // Mostrar mensaje si no hay chats
                        if (chatList.isEmpty()) {
                            tvNoChats.setVisibility(View.VISIBLE);
                            recyclerViewChats.setVisibility(View.GONE);
                        } else {
                            tvNoChats.setVisibility(View.GONE);
                            recyclerViewChats.setVisibility(View.VISIBLE);
                        }

                        Log.d(TAG, "Chats cargados: " + chatList.size());
                    }
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerViewChats.setVisibility(View.GONE);
            tvNoChats.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChatClick(Chat chat) {
        Log.d(TAG, "Chat clickeado - ID: " + chat.getChatId() + ", Admin: " + chat.getAdminId());

        Intent intent = new Intent(this, ChatClienteActivity.class);
        intent.putExtra("chatId", chat.getChatId());
        intent.putExtra("adminId", chat.getAdminId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) {
            chatListener.remove();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}