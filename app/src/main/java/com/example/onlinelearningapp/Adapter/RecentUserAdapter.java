package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.R;

import java.util.ArrayList;
import java.util.List;

public class RecentUserAdapter extends RecyclerView.Adapter<RecentUserAdapter.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private UserAdapter.OnUserClickListener onUserClickListener;

    // Constructor with click listener
    public RecentUserAdapter(UserAdapter.OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList != null ? userList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if(onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
        }
    }
}
