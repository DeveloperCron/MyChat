package com.example.mychat.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;

public class UserCardHolder extends RecyclerView.ViewHolder {
    private final TextView _userEmail;
    private final TextView _userName;

    public UserCardHolder(@NonNull View itemView) {
        super(itemView);

        _userEmail = itemView.findViewById(R.id.user_email);
        _userName = itemView.findViewById(R.id.user_name);
    }

    public TextView get_userEmail() {
        return _userEmail;
    }

    public TextView getUsername() {
        return this._userName;
    }

    public TextView getUserEmail() {
        return this._userEmail;
    }
}
