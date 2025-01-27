package com.example.mychat.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;

public class ChatRoomHolder extends RecyclerView.ViewHolder {
  private final TextView _messageText;

    public ChatRoomHolder(@NonNull View itemView) {
        super(itemView);

        _messageText = itemView.findViewById(R.id.text);
    }

    public TextView get_messageText() {
        return _messageText;
    }
}
