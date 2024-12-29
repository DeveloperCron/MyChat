package com.example.mychat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.classes.Chat;
import com.example.mychat.holders.ChatHolder;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {
    private final List<Chat> _storedChats;

    public ChatAdapter(List<Chat> _userCardsList) {
        this._storedChats = _userCardsList;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.getLastMessage().setText(_storedChats.get(position).getLastMessage());
        holder.getUsername().setText(_storedChats.get(position).getSender());
    }

    @Override
    public int getItemCount() {
        return this._storedChats.size();
    }
}
