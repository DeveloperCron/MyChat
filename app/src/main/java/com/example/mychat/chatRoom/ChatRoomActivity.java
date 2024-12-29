package com.example.mychat.chatRoom;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.databinding.ActivityChatRoomBinding;

public class ChatRoomActivity extends AppCompatActivity {
    ActivityChatRoomBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
    }
}