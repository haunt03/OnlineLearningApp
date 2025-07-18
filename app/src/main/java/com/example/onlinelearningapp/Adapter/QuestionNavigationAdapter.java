// app/src/main/java/com/example/onlinelearningapp/Adapter/QuestionNavigationAdapter.java
package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Đã đổi lại thành TextView

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;

import java.util.List;

public class QuestionNavigationAdapter extends RecyclerView.Adapter<QuestionNavigationAdapter.ViewHolder> {

    private int totalQuestions;
    private OnQuestionClickListener listener;
    private List<Boolean> answeredStatus; // true if answered, false if not
    private int currentQuestionIndex; // Index of the currently displayed question

    public interface OnQuestionClickListener {
        void onQuestionClick(int position);
    }

    public QuestionNavigationAdapter(int totalQuestions, OnQuestionClickListener listener, List<Boolean> answeredStatus, int currentQuestionIndex) {
        this.totalQuestions = totalQuestions;
        this.listener = listener;
        this.answeredStatus = answeredStatus;
        this.currentQuestionIndex = currentQuestionIndex;
    }

    // Call this method from TakeQuizActivity to update answered status
    public void updateAnsweredStatus(List<Boolean> newStatus) {
        this.answeredStatus = newStatus;
        notifyDataSetChanged(); // Refresh the RecyclerView to reflect changes
    }

    // Call this method from TakeQuizActivity to update the current question highlight
    public void updateCurrentQuestionIndex(int newIndex) {
        int oldIndex = this.currentQuestionIndex;
        this.currentQuestionIndex = newIndex;
        // Optimize updates: only notify changed items
        if (oldIndex != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldIndex); // Update old position (to remove highlight)
        }
        if (newIndex != RecyclerView.NO_POSITION) {
            notifyItemChanged(newIndex); // Update new position (to add highlight)
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item_question_navigation.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_navigation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvQuestionNavNumber.setText(String.valueOf(position + 1)); // Display question number (1-based)

        // Apply the custom background drawable to the TextView's background
        holder.tvQuestionNavNumber.setBackgroundResource(R.drawable.question_navigation_item_background);

        // Set state for the TextView's background drawable
        // These states will trigger the corresponding item in the selector
        holder.tvQuestionNavNumber.setSelected(position == currentQuestionIndex); // Highlight current question
        holder.tvQuestionNavNumber.setActivated(answeredStatus.get(position)); // Mark as answered

        // Adjust text color based on state for better visibility
        // Text color should be white for selected/answered, black for unanswered
        if (position == currentQuestionIndex || answeredStatus.get(position)) {
            holder.tvQuestionNavNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white, null));
        } else {
            holder.tvQuestionNavNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black, null));
        }

        holder.itemView.setOnClickListener(v -> listener.onQuestionClick(position));
    }

    @Override
    public int getItemCount() {
        return totalQuestions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNavNumber; // Đã đổi lại thành TextView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNavNumber = itemView.findViewById(R.id.tv_question_nav_number); // Tìm TextView
        }
    }
}
