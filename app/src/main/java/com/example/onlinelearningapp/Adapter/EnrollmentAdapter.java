package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Entity.Course;

import java.util.List;

public class EnrollmentAdapter extends RecyclerView.Adapter<EnrollmentAdapter.EnrolledCourseViewHolder> {

    private List<Course> enrolledCourses;
    private OnEnrolledCourseClickListener listener;

    public interface OnEnrolledCourseClickListener {
        void onEnrolledCourseClick(Course course);
    }

    public EnrollmentAdapter(List<Course> enrolledCourses, OnEnrolledCourseClickListener listener) {
        this.enrolledCourses = enrolledCourses;
        this.listener = listener;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EnrolledCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrolled_course, parent, false);
        return new EnrolledCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrolledCourseViewHolder holder, int position) {
        Course course = enrolledCourses.get(position);
        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvCourseDescription.setText(course.getDescription());

        // Xử lý hiển thị ảnh theo tên trong course.getImg()
        String imageName = course.getImg();
        if (imageName != null && imageName.contains(".")) {
            imageName = imageName.substring(0, imageName.lastIndexOf('.'));
        }

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                imageName,
                "drawable",
                holder.itemView.getContext().getPackageName()
        );

        if (imageResId != 0) {
            holder.ivCourseImage.setImageResource(imageResId);
        } else {
            holder.ivCourseImage.setImageResource(R.drawable.placeholder_course);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEnrolledCourseClick(course);
            }
        });
    }


    @Override
    public int getItemCount() {
        return enrolledCourses.size();
    }

    static class EnrolledCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvCourseTitle;
        TextView tvCourseDescription;

        public EnrolledCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvCourseDescription = itemView.findViewById(R.id.tv_course_description);
        }
    }
}
