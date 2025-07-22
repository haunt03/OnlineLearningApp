package com.example.onlinelearningapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.R;

import java.util.ArrayList;
import java.util.List;

public class LessonAdminAdapter extends RecyclerView.Adapter<LessonAdminAdapter.LessonViewHolder> {
    private static final String TAG = "LessonAdminAdapter";
    private List<Lesson> lessons = new ArrayList<>();

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        if (holder.tvTitle != null) {
            holder.tvTitle.setText(lesson.getTitle());
        } else {
            Log.e(TAG, "tvTitle is null for position: " + position);
        }
        if (holder.tvDescription != null) {
            holder.tvDescription.setText(lesson.getContent());
        } else {
            Log.e(TAG, "tvDescription is null for position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons != null ? lessons : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvDescription = itemView.findViewById(R.id.tv_lesson_description);
            Log.d(TAG, "LessonViewHolder initialized: tvTitle=" + (tvTitle != null) + ", tvDescription=" + (tvDescription != null));
        }
    }
}