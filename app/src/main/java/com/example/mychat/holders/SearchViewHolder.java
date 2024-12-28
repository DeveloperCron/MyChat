package com.example.mychat.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    private final TextView _username;
    private final TextView _useremail;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        _username = itemView.findViewById(R.id.user_name);
        _useremail = itemView.findViewById(R.id.user_email);
    }

    public TextView getUsername(){
        return this._username;
    }

    public TextView getEmail(){
        return this._useremail;
    }
}
