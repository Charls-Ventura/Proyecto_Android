package com.example.proyecto_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_android.model.Message;
import com.example.proyecto_android.repository.ChatRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
public class ChatViewModel extends ViewModel{

    private final ChatRepository chatRepository;

    private final MutableLiveData<List<Message>> messages;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> messageSent;

    private ListenerRegistration messagesListener;

    public ChatViewModel() {
        chatRepository = new ChatRepository();

        messages = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        messageSent = new MutableLiveData<>();
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getMessageSent() {
        return messageSent;
    }

    public void sendMessage(
            String senderId,
            String receiverId,
            String text
    ) {

        if (senderId == null || senderId.trim().isEmpty()) {
            errorMessage.setValue("No se pudo identificar al usuario.");
            return;
        }

        if (receiverId == null || receiverId.trim().isEmpty()) {
            errorMessage.setValue("No se pudo identificar al destinatario.");
            return;
        }

        if (text == null || text.trim().isEmpty()) {
            errorMessage.setValue("El mensaje no puede estar vacío.");
            return;
        }

        Message message = new Message(
                senderId,
                receiverId,
                text.trim(),
                System.currentTimeMillis()
        );

        chatRepository.sendMessage(
                message,
                new ChatRepository.MessageCallback() {

                    @Override
                    public void onSuccess() {
                        messageSent.setValue(true);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                        messageSent.setValue(false);
                    }
                }
        );
    }

    public void startListeningForMessages(
            String currentUserId,
            String otherUserId
    ) {

        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            errorMessage.setValue("No se pudo identificar al usuario.");
            return;
        }

        if (otherUserId == null || otherUserId.trim().isEmpty()) {
            errorMessage.setValue("No se pudo identificar al destinatario.");
            return;
        }

        stopListeningForMessages();

        messagesListener = chatRepository.listenForMessages(
                currentUserId,
                otherUserId,
                new ChatRepository.MessagesListener() {

                    @Override
                    public void onMessagesChanged(List<Message> updatedMessages) {
                        messages.setValue(updatedMessages);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                }
        );
    }

    public void stopListeningForMessages() {

        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopListeningForMessages();
    }
}
