package com.example.onlinelearningapp.Adapter;

import android.graphics.Color; // Import Color
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;

import java.util.List;

public class QuestionNavigationAdapter extends RecyclerView.Adapter<QuestionNavigationAdapter.ViewHolder> {

    private int numberOfQuestions;
    private OnQuestionClickListener listener;
    private List<Boolean> answeredStatus; // True if answered, false if not
    private int currentQuestionIndex; // To highlight the current question

    public interface OnQuestionClickListener {
        void onQuestionClick(int position);
    }

    public QuestionNavigationAdapter(int numberOfQuestions, OnQuestionClickListener listener, List<Boolean> answeredStatus, int currentQuestionIndex) {
        this.numberOfQuestions = numberOfQuestions;
        this.listener = listener;
        this.answeredStatus = answeredStatus;
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public void updateAnsweredStatus(List<Boolean> newStatus) {
        this.answeredStatus = newStatus;
        notifyDataSetChanged();
    }

    public void updateCurrentQuestionIndex(int newIndex) {
        this.currentQuestionIndex = newIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_nav_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.btnQuestionNav.setText(String.valueOf(position + 1)); // Question numbers start from 1

        // Set background color based on answered status
        if (answeredStatus != null && position < answeredStatus.size()) {
            if (answeredStatus.get(position)) {
                // Sử dụng getResources().getColorStateList() cho các phiên bản Android mới hơn
                holder.btnQuestionNav.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.green_answered, null)); // Green for answered
            } else {
                holder.btnQuestionNav.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(android.R.color.white, null)); // White for not answered
            }
        } else {
            holder.btnQuestionNav.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(android.R.color.white, null)); // Default white
        }

        // Highlight current question
        if (position == currentQuestionIndex) {
            holder.btnQuestionNav.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.blue_current_question, null)); // Blue for current
        }


        holder.btnQuestionNav.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return numberOfQuestions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnQuestionNav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnQuestionNav = itemView.findViewById(R.id.btn_question_nav);
        }
    }
}
