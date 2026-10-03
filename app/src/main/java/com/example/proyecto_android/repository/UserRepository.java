package com.example.proyecto_android.repository;

import com.example.proyecto_android.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final FirebaseFirestore firestore;

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void saveUser(User user, UserCallback callback) {
        firestore.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUsers(String currentUserId, UsersCallback callback) {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<User> users = new ArrayList<>();

                    for (User user : queryDocumentSnapshots.toObjects(User.class)) {

                        if (!user.getUid().equals(currentUserId)) {
                            users.add(user);
                        }
                    }

                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface UserCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onError(String message);
    }
}