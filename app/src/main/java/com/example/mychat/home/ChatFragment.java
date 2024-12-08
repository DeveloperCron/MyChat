package com.example.mychat.home;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mychat.R;
import com.example.mychat.adapters.UserCardAdapter;
import com.example.mychat.classes.StoredUser;
import com.example.mychat.constants.Tags;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private FirebaseFirestore _firestore;
    private List<StoredUser> _storedUsers;
    private UserCardAdapter _adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerViewContainer = view.findViewById(R.id.recycler_view_container);
        recyclerViewContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewContainer.setAdapter(_adapter);

        fetchUsers();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _firestore = FirebaseFirestore.getInstance();
        _storedUsers = new ArrayList<>();
        _adapter = new UserCardAdapter(_storedUsers);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    protected void fetchUsers() {
        _firestore.collection(Tags.FirebaseTags.USERS_COLLECTION)
                .limit(20) // Adjust the limit based on your requirements
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        // Clear the existing list before adding new data
                        _storedUsers.clear();

                        // Iterate through the documents and add them to the list
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                // Log the document data for debugging
                                Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Document data: " + documentSnapshot.getData());

                                // Deserialize the document snapshot into a StoredUser object
                                StoredUser storedUser = new StoredUser();
                                storedUser.setPhoto(documentSnapshot.getString("photo"));
                                storedUser.setFirst_name(documentSnapshot.getString("first_name"));
                                storedUser.setSecond_name(documentSnapshot.getString("second_name"));
                                storedUser.setEmail(documentSnapshot.getString("email"));
//                                storedUser.setFriends(documentSnapshot.("friends"));

                                if (storedUser != null) {
                                    _storedUsers.add(storedUser);
                                } else {
                                    Log.e(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Failed to map document to StoredUser");
                                }
                            } catch (Exception e) {
                                // Log any exceptions that happen during the mapping
                                Log.e(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Error mapping document to StoredUser", e);
                            }
                        }

                        // Notify the adapter that the data has changed
                        _adapter.notifyDataSetChanged();
                    } else {
                        Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "No users found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Error fetching users", e);
                });
    }

}
