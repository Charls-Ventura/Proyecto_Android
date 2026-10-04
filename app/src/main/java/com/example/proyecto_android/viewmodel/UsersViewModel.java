package com.example.proyecto_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_android.model.User;
import com.example.proyecto_android.repository.UserRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class UsersViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<List<User>> users;
    private final MutableLiveData<String> errorMessage;

    private ListenerRegistration usersListener;

    public UsersViewModel() {

        userRepository = new UserRepository();

        users = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
    }
}