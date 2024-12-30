package com.example.mychat.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.mychat.classes.ChatUser;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.utils.ChatUtils;
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
    private List<ChatUser> _storedChats;
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

        _chatsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(_chatsList.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    // Handle item click here
                    ChatUser chatUser = _storedChats.get(position);
                    Log.d(Tags.Debugger.KEY, "Item clicked at position: " + chatUser.getUsername());
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Optional: Handle other touch events if needed
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Optional: Handle disallow intercept if needed
            }
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
                String senderUid = ChatUtils.getSenderUidFromChatKey(chatKey);
                fetchSender(senderUid, storedUser -> {
                    Query latestMessageQuery = snapshot.child(Tags.Firebase.MESSAGES).getRef().orderByChild(Tags.Firebase.TIMESTAMP).limitToLast(1);
                    latestMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                String messageText = messageSnapshot.child("text").getValue(String.class);
                                if (messageText != null) {
                                    storedUser.setLastMessage(messageText);
                                    _storedChats.add(storedUser);
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
                        String email = documentSnapshot.getString(Tags.UserFields.EMAIL);
                        String photo = documentSnapshot.getString(Tags.UserFields.PROFILE_PICTURE);
                        ChatUser storedUser = new ChatUser(photo, firstName, secondName, email, uid);

                        listener.onSenderFetched(storedUser);
                    } else {
                        Log.e(Tags.FirebaseErrors.USER_NOT_FOUND, "User not found");
                    }
                }).addOnFailureListener(e -> Log.e(Tags.FirebaseErrors.DEADLINE_EXCEEDED, "Error: " + e.getMessage()));
    }

    public interface OnSenderFetchedListener {
        void onSenderFetched(ChatUser storedUser);
    }
}
