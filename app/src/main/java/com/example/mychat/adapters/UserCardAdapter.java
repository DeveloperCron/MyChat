package com.example.mychat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.classes.StoredUser;
import com.example.mychat.holders.UserCardHolder;

import java.util.List;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardHolder> {
    private List<StoredUser> _storedUsers;
    @NonNull
    @Override
    public UserCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);

        return new UserCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCardHolder holder, int position) {
        holder.getUserEmail().setText(_storedUsers.get(position).getEmail());
        holder.getUsername().setText(_storedUsers.get(position).getName());
    }

    public UserCardAdapter(List<StoredUser> _userCardsList){
        this._storedUsers = _userCardsList;
    }

    @Override
    public int getItemCount() {
        return _storedUsers.size();
    }
}
