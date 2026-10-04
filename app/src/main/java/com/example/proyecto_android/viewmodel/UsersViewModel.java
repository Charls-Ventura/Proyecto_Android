package com.example.proyecto_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_android.model.Message;
import com.example.proyecto_android.model.User;
import com.example.proyecto_android.repository.ChatRepository;
import com.example.proyecto_android.repository.UserRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class UsersViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    private final MutableLiveData<List<User>> users;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Message> incomingMessage;

    private ListenerRegistration usersListener;
    private ListenerRegistration incomingMessagesListener;

    public UsersViewModel() {

        userRepository = new UserRepository();
        chatRepository = new ChatRepository();

        users = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        incomingMessage = new MutableLiveData<>();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Message> getIncomingMessage() {
        return incomingMessage;
    }

    public void startListeningForUsers(String currentUserId) {

        if (currentUserId == null ||
                currentUserId.trim().isEmpty()) {

            errorMessage.setValue(
                    "No se pudo identificar al usuario."
            );

            return;
        }

        stopListeningForUsers();

        usersListener = userRepository.listenForUsers(
                currentUserId,
                new UserRepository.UsersListener() {

                    @Override
                    public void onUsersChanged(
                            List<User> updatedUsers
                    ) {
                        users.setValue(updatedUsers);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                }
        );
    }

    public void stopListeningForUsers() {

        if (usersListener != null) {
            usersListener.remove();
            usersListener = null;
        }
    }

    public void startListeningForIncomingMessages(
            String currentUserId) {

        if (currentUserId == null ||
                currentUserId.trim().isEmpty()) {

            errorMessage.setValue(
                    "No se pudo activar las notificaciones de mensajes."
            );

            return;
        }

        stopListeningForIncomingMessages();

        incomingMessagesListener =
                chatRepository.listenForIncomingMessages(
                        currentUserId,
                        new ChatRepository.IncomingMessageListener() {

                            @Override
                            public void onNewMessage(
                                    Message message) {

                                incomingMessage.setValue(message);
                            }

                            @Override
                            public void onError(String message) {

                                errorMessage.setValue(message);
                            }
                        }
                );
    }

    public void stopListeningForIncomingMessages() {

        if (incomingMessagesListener != null) {

            incomingMessagesListener.remove();
            incomingMessagesListener = null;
        }
    }

    public void syncFcmToken(String currentUserId) {

        if (currentUserId == null ||
                currentUserId.trim().isEmpty()) {

            errorMessage.setValue(
                    "No se pudo registrar el dispositivo para notificaciones."
            );

            return;
        }

        userRepository.syncFcmToken(
                currentUserId,
                new UserRepository.OperationCallback() {

                    @Override
                    public void onSuccess() {
                        // Token guardado correctamente
                    }

                    @Override
                    public void onError(String message) {

                        errorMessage.setValue(message);
                    }
                }
        );
    }

    @Override
    protected void onCleared() {

        super.onCleared();

        stopListeningForUsers();
        stopListeningForIncomingMessages();
    }
}