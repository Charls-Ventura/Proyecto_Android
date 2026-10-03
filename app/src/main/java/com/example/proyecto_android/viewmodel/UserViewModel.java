package com.example.proyecto_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_android.model.User;
import com.example.proyecto_android.repository.AuthRepository;
import com.example.proyecto_android.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
    }

    public void loadUsers() {

        String currentUserId = authRepository.getCurrentUserId();

        if (currentUserId == null) {
            errorMessage.setValue("No hay una sesión activa");
            return;
        }

        userRepository.getUsers(
                currentUserId,
                new UserRepository.UsersCallback() {

                    @Override
                    public void onSuccess(List<User> userList) {
                        users.setValue(userList);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                }
        );
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}