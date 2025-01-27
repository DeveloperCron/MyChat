package com.example.mychat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.chatRoom.Message;
import com.example.mychat.holders.ChatRoomHolder;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomHolder>{
   private List<Message> _messageList;

   public ChatRoomAdapter(List<Message> _messageList){
       this._messageList = _messageList;
   }

   public int getItemViewType(int position){
       Message message = _messageList.get(position);
       return message.getMessageType() == Message.ModelType.SENDER ? R.layout.sender_message : R.layout.receiver_message;
   }

    @NonNull
    @Override
    public ChatRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
       return new ChatRoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomHolder holder, int position) {
        Message message = _messageList.get(position);
        holder.get_messageText().setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return _messageList.size();
    }


    public void addMessage(Message message) {
        _messageList.add(message);
        notifyItemInserted(_messageList.size() - 1);
    }

    public void updateMessages(List<Message> messages) {
        _messageList.clear();
        _messageList.addAll(messages);
        notifyDataSetChanged();
    }
}
