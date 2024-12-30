package com.example.mychat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.classes.ChatUser;
import com.example.mychat.holders.SearchViewHolder;

import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private final List<ChatUser> _searchItems;

    public SearchViewAdapter(List<ChatUser> _searchItems) {
        this._searchItems = _searchItems;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.getUsername().setText(_searchItems.get(position).getUsername());
        holder.getEmail().setText(_searchItems.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return _searchItems.size();
    }
}
