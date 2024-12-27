package com.example.mychat.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;

public class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView _lastMessage;
    private final TextView _userName;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);

        _lastMessage = itemView.findViewById(R.id.last_message);
        _userName = itemView.findViewById(R.id.user_name);
    }

    public TextView getUsername() {
        return this._userName;
    }

    public TextView getLastMessage() {
        return this._lastMessage;
    }
}
