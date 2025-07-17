package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.Entity.Quiz;
import com.example.onlinelearningapp.ViewModel.LessonDetailsViewModel; // New ViewModel

import java.util.List;

public class LessonDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_LESSON_ID = "extra_lesson_id";
    public static final String EXTRA_LESSON_TITLE = "extra_lesson_title";

    private TextView tvLessonDetailsTitle, tvLessonDetailsContent;
    private ImageView ivLessonDetailsImage;
    private Button btnTakeQuiz;
    private LessonDetailsViewModel lessonDetailsViewModel;

    private int lessonId;
    private int quizId = -1; // To store the quiz ID for this lesson

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_details);

        tvLessonDetailsTitle = findViewById(R.id.tv_lesson_details_title);
        tvLessonDetailsContent = findViewById(R.id.tv_lesson_details_content);
        ivLessonDetailsImage = findViewById(R.id.iv_lesson_details_image);
        btnTakeQuiz = findViewById(R.id.btn_take_quiz);

        if (getIntent().hasExtra(EXTRA_LESSON_ID)) {
            lessonId = getIntent().getIntExtra(EXTRA_LESSON_ID, -1);
            String lessonTitle = getIntent().getStringExtra(EXTRA_LESSON_TITLE);
            tvLessonDetailsTitle.setText(lessonTitle);
        } else {
            Toast.makeText(this, "Lesson not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lessonDetailsViewModel = new ViewModelProvider(this).get(LessonDetailsViewModel.class);
        lessonDetailsViewModel.loadLessonDetails(lessonId);

        lessonDetailsViewModel.getLesson().observe(this, lesson -> {
            if (lesson != null) {
                tvLessonDetailsContent.setText(lesson.getContent());
                // Set image, use placeholder for now
                ivLessonDetailsImage.setImageResource(R.drawable.placeholder_lesson);
            } else {
                Toast.makeText(LessonDetailsActivity.this, "Failed to load lesson details.", Toast.LENGTH_SHORT).show();
            }
        });

        lessonDetailsViewModel.getQuizForLesson().observe(this, quizzes -> {
            if (quizzes != null && !quizzes.isEmpty()) {
                quizId = quizzes.get(0).getQuizId(); // Assuming one quiz per lesson for simplicity
                btnTakeQuiz.setEnabled(true); // Enable button if quiz exists
            } else {
                btnTakeQuiz.setEnabled(false); // Disable if no quiz
                btnTakeQuiz.setText("No Quiz Available");
            }
        });

        btnTakeQuiz.setOnClickListener(v -> {
            if (quizId != -1) {
                Intent intent = new Intent(LessonDetailsActivity.this, TakeQuizActivity.class);
                intent.putExtra(TakeQuizActivity.EXTRA_QUIZ_ID, quizId);
                startActivity(intent);
            } else {
                Toast.makeText(LessonDetailsActivity.this, "No quiz available for this lesson.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
