package com.example.onlinelearningapp.Activity.Learner;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelearningapp.R;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvScore, tvPassFailStatus;
    private Button btnBackToHome, btnReviewQuiz;

    private int quizId; // To pass to review mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        tvScore = findViewById(R.id.tv_score);
        tvPassFailStatus = findViewById(R.id.tv_pass_fail_status);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnReviewQuiz = findViewById(R.id.btn_review_quiz);

        // Get score and quizId from Intent
        int score = getIntent().getIntExtra(TakeQuizActivity.EXTRA_QUIZ_SCORE, 0);
        int totalQuestions = getIntent().getIntExtra(TakeQuizActivity.EXTRA_TOTAL_QUESTIONS, 1);
        quizId = getIntent().getIntExtra(TakeQuizActivity.EXTRA_QUIZ_ID, -1); // Get quiz ID

        tvScore.setText("Your Score: " + score + "%");

        // Determine pass/fail status (70% passing score)
        if (score >= 70) {
            tvPassFailStatus.setText("Status: Pass!");
            tvPassFailStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvPassFailStatus.setText("Status: Fail!");
            tvPassFailStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnReviewQuiz.setOnClickListener(v -> {
            if (quizId != -1) {
                Intent reviewIntent = new Intent(QuizResultActivity.this, TakeQuizActivity.class);
                reviewIntent.putExtra(TakeQuizActivity.EXTRA_QUIZ_ID, quizId);
                reviewIntent.putExtra(TakeQuizActivity.EXTRA_REVIEW_MODE, true); // Indicate review mode
                startActivity(reviewIntent);
            } else {
                Toast.makeText(QuizResultActivity.this, "Cannot review quiz: Quiz ID not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
