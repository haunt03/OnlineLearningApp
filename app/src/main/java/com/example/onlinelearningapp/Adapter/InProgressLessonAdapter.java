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

public class InProgressLessonAdapter extends RecyclerView.Adapter<InProgressLessonAdapter.LessonViewHolder> {

    private List<Lesson> lessonList = new ArrayList<>();

    public void setLessonList(List<Lesson> lessonList) {
        this.lessonList = lessonList != null ? lessonList : new ArrayList<>();
        Log.d("InProgressLessonAdapter", "Lesson list size: " + this.lessonList.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_progress_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.tvLessonTitle.setText(lesson.getTitle());
        holder.tvLessonDescription.setText(lesson.getContent());
        Log.d("InProgressLessonAdapter", "Binding lesson: " + lesson.getTitle());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvLessonTitle, tvLessonDescription;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLessonTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvLessonDescription = itemView.findViewById(R.id.tv_lesson_description);
        }
    }
}
