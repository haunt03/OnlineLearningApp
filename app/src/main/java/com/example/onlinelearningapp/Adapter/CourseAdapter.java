package com.example.onlinelearningapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Entity.Course;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseClickListener listener;
    private OnCourseActionButtonClickListener actionButtonListener;
    private int currentUserId;
    private Set<Integer> enrolledCourseIds;
    private AdapterMode mode;

    public enum AdapterMode {
        ALL_COURSES,
        MY_COURSES
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public interface OnCourseActionButtonClickListener {
        void onActionButtonClick(Course course, AdapterMode mode);
    }

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener, OnCourseActionButtonClickListener actionButtonListener, int currentUserId, AdapterMode mode) {
        this.courses = courses;
        this.listener = listener;
        this.actionButtonListener = actionButtonListener;
        this.currentUserId = currentUserId;
        this.enrolledCourseIds = new HashSet<>();
        this.mode = mode;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public void setEnrolledCourseIds(List<Enrollment> enrollments) {
        enrolledCourseIds.clear();
        for (Enrollment enrollment : enrollments) {
            enrolledCourseIds.add(enrollment.getCourseId());
        }
        notifyDataSetChanged();
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
        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvCourseDescription.setText(course.getDescription());

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                course.getImg().split("\\.")[0],
                "drawable",
                holder.itemView.getContext().getPackageName()
        );
        if (imageResId != 0) {
            holder.ivCourseImage.setImageResource(imageResId);
        } else {
            holder.ivCourseImage.setImageResource(R.drawable.placeholder_course);
        }

        // Logic for the action button (Enroll or Drop Out)
        if (currentUserId != -1) { // Only show button if user is logged in
            if (mode == AdapterMode.ALL_COURSES) {
                if (!enrolledCourseIds.contains(course.getCourseId())) {
                    holder.btnCourseAction.setVisibility(View.VISIBLE);
                    holder.btnCourseAction.setText("Enroll");
                    holder.btnCourseAction.setOnClickListener(v -> {
                        if (actionButtonListener != null) {
                            actionButtonListener.onActionButtonClick(course, mode);
                        }
                    });
                } else {
                    holder.btnCourseAction.setVisibility(View.GONE);
                }
            } else if (mode == AdapterMode.MY_COURSES) {
                // In MyCoursesActivity, always show "Drop Out" for enrolled courses
                holder.btnCourseAction.setVisibility(View.VISIBLE);
                holder.btnCourseAction.setText("Drop Out");
                holder.btnCourseAction.setOnClickListener(v -> {
                    if (actionButtonListener != null) {
                        actionButtonListener.onActionButtonClick(course, mode);
                    }
                });
            }
        } else {
            holder.btnCourseAction.setVisibility(View.GONE); // Hide button if no user logged in
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvCourseTitle;
        TextView tvCourseDescription;
        Button btnCourseAction;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvCourseDescription = itemView.findViewById(R.id.tv_course_description);
            btnCourseAction = itemView.findViewById(R.id.btn_course_action);
        }
    }
}
