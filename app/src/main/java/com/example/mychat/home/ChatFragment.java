package com.example.mychat.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.adapters.ChatAdapter;
import com.example.mychat.classes.Chat;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.google.android.material.search.SearchBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private FirebaseFirestore _firestore;
    private List<Chat> _storedChats;
    private ChatAdapter _adapter;
    private UserSingleton _userSingleton;
    private FirebaseDatabase _database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _firestore = FirebaseFirestore.getInstance();
        _database = FirebaseDatabase.getInstance();
        _userSingleton = UserSingleton.getInstance();
        _storedChats = new ArrayList<>();
        _adapter = new ChatAdapter(_storedChats);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Replace with your actual layout file
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views manually
        RecyclerView _chatsList = rootView.findViewById(R.id.chatsList);
        TextView _welcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        SearchBar _searchBar = rootView.findViewById(R.id.searchBar);
        ProgressBar _progressBar = rootView.findViewById(R.id.progress);

        // Set RecyclerView layout manager and adapter
        _chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        _chatsList.setAdapter(_adapter);

        // Set the welcome message
        _welcomeTextView.setText("Welcome back " + _userSingleton.getFirstName());
        _searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });


        // Set up Firebase listener
        setupFirebaseListener();
        _progressBar.setVisibility(View.GONE);

        return rootView;
    }

    private void setupFirebaseListener() {
        DatabaseReference _ref = _database.getReference(Tags.Firebase.CHATS);
        _ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                insertFromDatabase(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Tags.Debugger.KEY, String.valueOf(error));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void insertFromDatabase(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String chatKey = snapshot.child(Tags.Firebase.CHAT_KEY).getValue(String.class);
            if (chatKey != null && chatKey.contains(_userSingleton.getUid())) {
                String senderUid = getSenderUidFromChatKey(chatKey);
                fetchSender(senderUid, senderName -> {
                    Query latestMessageQuery = snapshot.child(Tags.Firebase.MESSAGES).getRef().orderByChild(Tags.Firebase.TIMESTAMP).limitToLast(1);
                    latestMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                String messageText = messageSnapshot.child("text").getValue(String.class);
                                if (messageText != null) {
                                    Chat chatInstance = new Chat(messageText, senderName);
                                    Log.d(Tags.Debugger.KEY, chatInstance.toString());
                                    _storedChats.add(chatInstance);
                                    _adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(Tags.Debugger.KEY, "Failed to fetch latest message: " + databaseError.getMessage());
                        }
                    });
                });
            }
        }
    }

    protected void fetchSender(String uid, OnSenderFetchedListener listener) {
        _firestore.collection(Tags.Firebase.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String firstName = documentSnapshot.getString(Tags.UserFields.FIRST_NAME);
                        String secondName = documentSnapshot.getString(Tags.UserFields.SECOND_NAME);
                        String senderName = firstName + " " + secondName;

                        listener.onSenderFetched(senderName); // Callback with sender name
                    } else {
                        Log.e(Tags.FirebaseErrors.UNAVAILABLE, "User not found");
                    }
                })
                .addOnFailureListener(e -> Log.e("SenderName", "Error: " + e.getMessage()));
    }

    // Interface to handle sender fetching callback
    public interface OnSenderFetchedListener {
        void onSenderFetched(String senderName);
    }

    protected String getSenderUidFromChatKey(String chatKey) {
        String[] parts = chatKey.split("_");
        String firstPart = parts[0];
        String secondPart = parts[1];

        if (_userSingleton.getUid().equals(firstPart)) {
            return secondPart;
        }

        return firstPart;
    };
}
