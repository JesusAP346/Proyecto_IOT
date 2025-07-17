package com.example.proyecto_iot.administradorHotel.chat;

import java.util.Date;

public class Chat {
    private String adminId;
    private String userId;
    private Date createdAt;
    private boolean isActive;
    private String lastMessage;
    private Date lastMessageTime;
    private String chatId;

    public Chat() {
        // Constructor vacío requerido para Firestore
    }

    public Chat(String adminId, String userId, Date createdAt, boolean isActive,
                String lastMessage, Date lastMessageTime, String chatId) {
        this.adminId = adminId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.chatId = chatId;
    }

    // Getters y Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}