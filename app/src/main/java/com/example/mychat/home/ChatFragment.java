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
import com.example.mychat.chatRoom.ChatRoomActivity;
import com.example.mychat.classes.ChatUser;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.utils.ChatUtils;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseFirestore firestoreInstance;
    private List<ChatUser> chatsList;
    private ChatAdapter chatAdapter;
    private UserSingleton userSingleton;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestoreInstance = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userSingleton = UserSingleton.getInstance();
        chatsList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatsList);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views
        RecyclerView chatsRecyclerView = rootView.findViewById(R.id.chatsList);
        TextView welcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        SearchBar searchBar = rootView.findViewById(R.id.searchBar);
        ProgressBar loadingProgressBar = rootView.findViewById(R.id.progress);

        // Set RecyclerView layout manager and adapter
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecyclerView.setAdapter(chatAdapter);

        // Set the welcome message
        welcomeTextView.setText("Welcome back " + userSingleton.getFirstName());
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        chatsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(chatsRecyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
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
                    ChatUser chatUser = chatsList.get(position);

//                    Intent startChatServiceIntent = new Intent(getActivity(), ChatService.class);
//                    startChatServiceIntent.putExtra(Tags.IntentKeys.UID, chatUser.getUid());
//                    getActivity().startService(startChatServiceIntent);

                    Intent openChatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
                    openChatRoomIntent.putExtra(Tags.IntentKeys.CHAT_USER, chatUser);
                    openChatRoomIntent.putExtra(Tags.IntentKeys.UID, chatUser.getUid());
                    startActivity(openChatRoomIntent);

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

        loadChatsFromFirebase();
        loadingProgressBar.setVisibility(View.GONE);

        return rootView;
    }

    protected void loadChatsFromFirebase() {
        DatabaseReference chatsReference = firebaseDatabase.getReference(Tags.Firebase.CHATS);
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                    String chatKey = documentSnapshot.getKey();

                    if (chatKey.contains(userSingleton.getUid())) {
                        String senderUid = ChatUtils.getSenderUidFromChatKey(chatKey);
                        retrieveSenderDetails(senderUid, chatUser -> {
                            Query lastMessage = documentSnapshot.child(Tags.Firebase.MESSAGES)
                                    .getRef()
                                    .orderByChild(Tags.Firebase.TIMESTAMP)
                                    .limitToLast(1);
                            lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                                        String text = messageSnapshot.child(Tags.MessageFields.TEXT).getValue(String.class);
                                        chatUser.setLastMessage(text);
                                        System.out.println("Last Message: " + text);
                                    }
                                    chatsList.clear();
                                    chatsList.add(chatUser);
                                    chatAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("ERR", error.getMessage());
                                }
                            });
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Tags.Debugger.KEY, "Failed to fetch latest message: " + error.getMessage());
            }
        });
    }

    protected void retrieveSenderDetails(String userId, OnSenderDetailsFetchedListener listener) {
        firestoreInstance.collection(Tags.Firebase.USERS_COLLECTION).document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        String firstName = snapshot.getString(Tags.UserFields.FIRST_NAME);
                        String lastName = snapshot.getString(Tags.UserFields.SECOND_NAME);
                        String email = snapshot.getString(Tags.UserFields.EMAIL);
                        String photo = snapshot.getString(Tags.UserFields.PROFILE_PICTURE);
                        ChatUser storedUser = new ChatUser(photo, firstName, lastName, email, userId);
                        listener.onSenderDetailsFetched(storedUser);
                    }
                });
    }

    public interface OnSenderDetailsFetchedListener {
        void onSenderDetailsFetched(ChatUser chatUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
