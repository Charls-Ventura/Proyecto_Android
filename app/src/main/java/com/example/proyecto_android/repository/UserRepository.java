package com.example.proyecto_android.repository;

import com.example.proyecto_android.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserRepository {

    private final FirebaseFirestore firestore;

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public ListenerRegistration listenForUsers(
            String currentUserId,
            UsersListener listener
    ) {

        return firestore.collection("users")
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    List<User> users = new ArrayList<>();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        User user = document.toObject(User.class);

                        if (user == null) {
                            continue;
                        }

                        String userId = document.getId();
                        user.setUid(userId);

                        if (userId.equals(currentUserId)) {
                            continue;
                        }

                        users.add(user);
                    }

                    users.sort(
                            Comparator.comparing(
                                    user -> {
                                        String name = user.getName();

                                        if (name == null) {
                                            return "";
                                        }

                                        return name.toLowerCase();
                                    }
                            )
                    );

                    listener.onUsersChanged(users);
                });
    }

    public interface UsersListener {

        void onUsersChanged(List<User> users);

        void onError(String message);
    }
}