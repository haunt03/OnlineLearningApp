package com.example.onlinelearningapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.R;

import java.util.ArrayList;
import java.util.List;

public class CourseAdminAdapter extends RecyclerView.Adapter<CourseAdminAdapter.CourseViewHolder> {
    private static final String TAG = "CourseAdminAdapter";
    private List<Course> courses = new ArrayList<>();
    private final OnCourseClickListener clickListener;
    private final OnCourseClickListener deleteListener;
    private final OnCourseLongClickListener longClickListener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public interface OnCourseLongClickListener {
        void onCourseLongClick(Course course, View view);
    }

    public CourseAdminAdapter(OnCourseClickListener clickListener, OnCourseClickListener deleteListener, OnCourseLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvDescription.setText(course.getDescription());

        // Load image
        try {
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    course.getImg().replace(".png", ""), "drawable", holder.itemView.getContext().getPackageName());
            holder.ivCourseImage.setImageResource(resId);
        } catch (Exception e) {
            holder.ivCourseImage.setImageResource(R.drawable.imgvector2);
            Log.e(TAG, "Failed to load course image: " + course.getImg(), e);
        }

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Course item clicked: " + course.getTitle());
            clickListener.onCourseClick(course);
        });

        // Set delete click listener
        if (holder.ivDelete != null) {
            holder.ivDelete.setOnClickListener(v -> {
                Log.d(TAG, "Delete icon clicked for course: " + course.getTitle());
                deleteListener.onCourseClick(course);
            });
        } else {
            Log.e(TAG, "ivDelete is null for position: " + position);
        }

        // Set long-click listener on the entire item view
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onCourseLongClick(course, v);
            return true; // Consume the long click
        });
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            tvTitle = itemView.findViewById(R.id.tv_course_title);
            tvDescription = itemView.findViewById(R.id.tv_course_description);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            Log.d(TAG, "CourseViewHolder initialized: ivDelete=" + (ivDelete != null));
        }
    }
}