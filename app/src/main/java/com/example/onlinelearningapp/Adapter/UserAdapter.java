package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private final OnUserClickListener viewListener;
    private final OnUserClickListener deleteListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(OnUserClickListener viewListener, OnUserClickListener deleteListener) {
        this.viewListener = viewListener;
        this.deleteListener = deleteListener;
    }

    public void setUserList(List<User> users) {
        this.userList = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserId.setText("ID: " + user.getUserId());
        holder.tvName.setText("Name: " + (user.getName() != null ? user.getName() : "N/A"));
        holder.tvEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));


        holder.ivView.setOnClickListener(v -> viewListener.onUserClick(user));
        holder.ivChange.setOnClickListener(v -> deleteListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvName, tvEmail, tvStatus, tvRole, tvCreatedAt;
        ImageView ivView, ivChange;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);


            ivView = itemView.findViewById(R.id.iv_view);
            ivChange = itemView.findViewById(R.id.iv_delete);
        }
    }
}