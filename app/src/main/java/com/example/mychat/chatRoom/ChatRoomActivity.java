package com.example.mychat.chatRoom;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychat.R;
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