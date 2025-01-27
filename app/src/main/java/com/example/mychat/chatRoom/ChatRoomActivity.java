//package com.example.mychat.chatRoom;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.mychat.adapters.ChatRoomAdapter;
//import com.example.mychat.classes.ChatUser;
//import com.example.mychat.classes.UserSingleton;
//import com.example.mychat.constants.Tags;
//import com.example.mychat.databinding.ActivityChatRoomBinding;
//import com.example.mychat.services.ChatService;
//import com.example.mychat.utils.GetTextUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//import io.reactivex.rxjava3.disposables.Disposable;
//
//public class ChatRoomActivity extends AppCompatActivity {
//    private ActivityChatRoomBinding binding;
//    private boolean isBound = false;
//    private List<Message> messageList = new ArrayList<>();
//    private ChatService chatService;
//    private ChatRoomAdapter chatRoomAdapter;
//    private final CompositeDisposable disposables = new CompositeDisposable();
//    private final UserSingleton userSingleton = UserSingleton.getInstance();
//
//    private final ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
//            chatService = binder.getService();
//            isBound = true;
//
//            messageList = chatService.getMessagesList();
//            Log.d("ChatRoomActivity", "Messages: " + messageList);
//
//            initializeChatRoom();
//            subscribeToMessageUpdates();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isBound = false;
//            chatService = null;
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        initializeChatRoom();
//    }
//
//    private void initializeChatRoom() {
//        Intent intent = getIntent();
//        ChatUser chatUser = (ChatUser) intent.getSerializableExtra(Tags.IntentKeys.CHAT_USER);
//
//        if (chatUser != null) {
//            binding.senderNameTextView.setText(chatUser.getUsername());
//        }
//        binding.backButton.setOnClickListener(v -> finish());
//
//        chatRoomAdapter = new ChatRoomAdapter(messageList);
//        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.chatRecyclerView.setAdapter(chatRoomAdapter);
//
//        binding.sendFab.setOnClickListener(v -> {
//            String inputText = GetTextUtils.getTextFromInput(binding.messageInputLayout);
//            if (!inputText.isEmpty() && chatUser != null && chatService != null) {
//                chatService.addMessage(chatUser.getUid(), inputText);
//                binding.messageEditText.setText("");
//            }
//        });
//    }
//
//    private void subscribeToMessageUpdates() {
//        if (chatService == null) return;
//
//        Disposable disposable = chatService.observeMessages()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        message -> {
//                            messageList.add(message);
//                            chatRoomAdapter.notifyItemInserted(messageList.size() - 1);
//                            binding.chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
//                            Log.d("ChatRoomActivity", "Message received and added.");
//                        },
//                        throwable -> Log.e("ChatRoomActivity", "Error receiving message", throwable)
//                );
//
//        disposables.add(disposable);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent serviceIntent = new Intent(this, ChatService.class);
//        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (isBound) {
//            unbindService(serviceConnection);
//            isBound = false;
//        }
//        disposables.clear();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        messageList.clear();
//    }
//}

package com.example.mychat.chatRoom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mychat.adapters.ChatRoomAdapter;
import com.example.mychat.classes.ChatUser;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.databinding.ActivityChatRoomBinding;
import com.example.mychat.utils.GetTextUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    ActivityChatRoomBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private List<Message> messages;
    private ChatRoomAdapter chatRoomAdapter;
    private UserSingleton userSingleton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
        userSingleton = UserSingleton.getInstance();
        messages = new ArrayList<>();

        chatRoomAdapter = new ChatRoomAdapter(messages);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        ChatUser chatUser = (ChatUser) intent.getSerializableExtra(Tags.IntentKeys.CHAT_USER);

        if (chatUser != null) {
            binding.senderNameTextView.setText(chatUser.getUsername());
        }
        binding.backButton.setOnClickListener(v -> finish());

        chatRoomAdapter = new ChatRoomAdapter(messages);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(chatRoomAdapter);

        binding.sendFab.setOnClickListener(v -> {
            String inputText = GetTextUtils.getTextFromInput(binding.messageInputLayout);
            if (!inputText.isEmpty() && chatUser != null) {
                sendMessage(inputText, chatUser.getUid());
                binding.messageEditText.setText("");
            }
        });
    }

    protected void sendMessage(String messageText, String recipientUid){
        Map<String, Object> message = new HashMap<>();
        message.put(Tags.MessageFields.SENDER, userSingleton.getUid());
        message.put(Tags.MessageFields.TEXT, messageText);
        message.put(Tags.MessageFields.TIMESTAMP, System.currentTimeMillis());

        String chatKey1 = getChatKey(userSingleton.getUid(), recipientUid);
//        String chatKey2 = getChatKey(recipientUid, userSingleton.getUid());

        DatabaseReference chatRef1 = firebaseDatabase.getReference(Tags.Firebase.CHATS).child(chatKey1).getRef();
//        DatabaseReference chatRef2 = firebaseDatabase.getReference(Tags.Firebase.CHATS).child(chatKey2).getRef();

        chatRef1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    chatRef1.child(Tags.Firebase.MESSAGES).push().setValue(message);
                } else {
                    Log.d(Tags.FirebaseErrors.DOCUMENT_NOT_FOUND, "Document not found");
                }
            } else {
                Log.d(Tags.FirebaseErrors.NETWORK_ERROR, "Network error occurred");
            }
        });

//        chatRef2.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DataSnapshot snapshot = task.getResult();
//                if (snapshot.exists()) {
//                    chatRef2.child(Tags.Firebase.MESSAGES).push().setValue(message);
//                } else {
//                    Log.d(Tags.FirebaseErrors.DOCUMENT_NOT_FOUND, "Document not found");
//                }
//            } else {
//                Log.d(Tags.FirebaseErrors.NETWORK_ERROR, "Network error occurred");
//            }
//        });
    }

    private String getChatKey(String uid1, String uid2) {
        String[] sortedUids = {uid1, uid2};
        Arrays.sort(sortedUids);
        return sortedUids[0] + "_" + sortedUids[1];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}