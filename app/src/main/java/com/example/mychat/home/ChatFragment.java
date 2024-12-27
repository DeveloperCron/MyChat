package com.example.mychat.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ChatFragment extends Fragment {
    private FirebaseFirestore _firestore;
    private List<Chat> _storedChats;
    private ChatAdapter _adapter;
    private UserSingleton _userSingleton;
    private FirebaseDatabase _database;
    private RecyclerView _chatsList; // Use RecyclerView directly
    private TextView _welcomeTextView; // Replace with the actual welcome TextView

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
        _chatsList = rootView.findViewById(R.id.chatsList);
        _welcomeTextView = rootView.findViewById(R.id.welcomeTextView);

        // Set RecyclerView layout manager and adapter
        _chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        _chatsList.setAdapter(_adapter);

        // Set the welcome message
        _welcomeTextView.setText("Welcome back " + _userSingleton.getFirstName());

        // Set up Firebase listener
        setupFirebaseListener();

        return rootView;
    }

    private void setupFirebaseListener() {
        DatabaseReference _ref = _database.getReference(Tags.FIREBASE_DATABASE.CHATS_KEY);
        _ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                insertFromDatabase(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Tags.DEBUGGER.DEBUG_KEY, String.valueOf(error));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void insertFromDatabase(DataSnapshot _dataSnapshot) {
        for (DataSnapshot _snapshot : _dataSnapshot.getChildren()) {
            final AtomicReference<DataSnapshot>[] _lastMessage = new AtomicReference[]{new AtomicReference<>()};
            final long[] latestTimestamp = {0};

            String chatKey = _snapshot.child("chat_key").getValue(String.class);
            String senderUid = getSenderUidFromChatKey(chatKey);
            if (chatKey.contains(_userSingleton.getUid())) {
                fetchSender(senderUid, senderName -> {
                    DataSnapshot documentSnapshot = _snapshot.child("messages");
                    for (DataSnapshot messageSnapshot : documentSnapshot.getChildren()) {
                        Long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                        if (timestamp != null && timestamp > latestTimestamp[0]) {
                            latestTimestamp[0] = timestamp;
                            _lastMessage[0].set(messageSnapshot);
                        }
                    }
                    if (_lastMessage[0].get() != null) {
                        Chat chatInstance = new Chat(_lastMessage[0].get().child("text").getValue(String.class), senderName);
                        Log.d(Tags.DEBUGGER.DEBUG_KEY, chatInstance.toString());
                        _storedChats.add(chatInstance);
                        _adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    protected void fetchSender(String uid, OnSenderFetchedListener listener) {
        _firestore.collection(Tags.FirebaseTags.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String firstName = documentSnapshot.getString(Tags.USER_FIELDS.FIRST_NAME);
                        String secondName = documentSnapshot.getString(Tags.USER_FIELDS.SECOND_NAME);
                        String senderName = firstName + " " + secondName;

                        listener.onSenderFetched(senderName); // Callback with sender name
                    } else {
                        Log.e("SenderName", "User not found");
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
