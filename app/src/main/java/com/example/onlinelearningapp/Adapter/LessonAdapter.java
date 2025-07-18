package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Entity.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClick listener;

    public interface OnLessonClick {
        void onLessonClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons, OnLessonClick listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvLessonTitle.setText(lesson.getTitle());
        holder.tvLessonContentPreview.setText(lesson.getContent());
        // For image, use placeholder for now
        holder.ivLessonImage.setImageResource(R.drawable.placeholder_lesson);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLessonClick(lesson);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLessonImage;
        TextView tvLessonTitle;
        TextView tvLessonContentPreview;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLessonImage = itemView.findViewById(R.id.iv_lesson_image);
            tvLessonTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvLessonContentPreview = itemView.findViewById(R.id.tv_lesson_content_preview);
        }
    }
}
