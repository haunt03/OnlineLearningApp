package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.QuestionNavigationAdapter;
import com.example.onlinelearningapp.Entity.Answer;
import com.example.onlinelearningapp.Entity.Option;
import com.example.onlinelearningapp.Entity.Question;
import com.example.onlinelearningapp.Entity.Quiz;
import com.example.onlinelearningapp.Entity.QuizResult;
import com.example.onlinelearningapp.ViewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeQuizActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";
    public static final String EXTRA_QUIZ_SCORE = "extra_quiz_score";
    public static final String EXTRA_TOTAL_QUESTIONS = "extra_total_questions";
    public static final String EXTRA_REVIEW_MODE = "extra_review_mode";

    private TextView tvQuizTitle, tvTimer, tvQuestionNumber, tvQuestionText;
    private RadioGroup rgOptions;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private Button btnNextQuestion, btnPreviousQuestion, btnSubmitQuiz, btnExitQuiz;
    private RecyclerView rvQuestionNavigation;
    private QuestionNavigationAdapter questionNavigationAdapter;

    private QuizViewModel quizViewModel;
    private int quizId;
    private int currentUserId;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    // Store user's selected option ID for each question
    // Key: QuestionID, Value: Selected OptionID
    private Map<Integer, Integer> userSelectedOptionIds = new HashMap<>();
    // Store answered status for navigation buttons
    private List<Boolean> answeredStatus;

    private boolean isReviewMode = false;
    private RadioGroup.OnCheckedChangeListener radioGroupListener; // Store the listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        // Initialize views
        tvQuizTitle = findViewById(R.id.tv_quiz_title);
        tvTimer = findViewById(R.id.tv_timer);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvQuestionText = findViewById(R.id.tv_question_text);
        rgOptions = findViewById(R.id.rg_options);
        rbOptionA = findViewById(R.id.rb_option_a);
        rbOptionB = findViewById(R.id.rb_option_b);
        rbOptionC = findViewById(R.id.rb_option_c);
        rbOptionD = findViewById(R.id.rb_option_d);
        btnNextQuestion = findViewById(R.id.btn_next_question);
        btnPreviousQuestion = findViewById(R.id.btn_previous_question);
        btnSubmitQuiz = findViewById(R.id.btn_submit_quiz);
        btnExitQuiz = findViewById(R.id.btn_exit_quiz);
        rvQuestionNavigation = findViewById(R.id.rv_question_navigation);

        // Check if in review mode
        isReviewMode = getIntent().getBooleanExtra(EXTRA_REVIEW_MODE, false);

        // Get quiz ID from Intent
        if (getIntent().hasExtra(EXTRA_QUIZ_ID)) {
            quizId = getIntent().getIntExtra(EXTRA_QUIZ_ID, -1);
        } else {
            Toast.makeText(this, "Quiz not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(HomePageActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(HomePageActivity.KEY_LOGGED_IN_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in. Please login to take quiz.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.loadQuizData(quizId); // Load quiz and questions initially

        setupListeners();
        observeViewModel(); // Observe LiveData

        if (isReviewMode) {
            setupReviewMode();
        } else {
            setupQuizMode();
        }
    }

    private void setupListeners() {
        // Store the listener to be able to remove/add it
        radioGroupListener = (group, checkedId) -> {
            // Save selected answer immediately when user clicks
            saveCurrentAnswer();
            // Update answered status for navigation buttons
            if (answeredStatus != null && questions != null && currentQuestionIndex < questions.size()) {
                answeredStatus.set(currentQuestionIndex, true);
                questionNavigationAdapter.updateAnsweredStatus(answeredStatus);
            }
            updateRadioButtonColors(); // Update colors based on selection
        };

        btnNextQuestion.setOnClickListener(v -> goToNextQuestion());
        btnPreviousQuestion.setOnClickListener(v -> goToPreviousQuestion());
        btnSubmitQuiz.setOnClickListener(v -> submitQuiz());
        btnExitQuiz.setOnClickListener(v -> finish());
    }

    private void setupQuizMode() {
        tvTimer.setVisibility(View.VISIBLE);
        btnSubmitQuiz.setVisibility(View.VISIBLE); // Initially visible, will be controlled by displayQuestion
        btnExitQuiz.setVisibility(View.GONE);

        // Set the listener for RadioGroup
        rgOptions.setOnCheckedChangeListener(radioGroupListener);
    }

    private void setupReviewMode() {
        tvTimer.setVisibility(View.GONE); // Hide timer in review mode
        btnSubmitQuiz.setVisibility(View.GONE); // Hide submit button in review mode
        btnExitQuiz.setVisibility(View.VISIBLE); // Show exit button in review mode

        // Disable radio buttons in review mode
        rbOptionA.setEnabled(false);
        rbOptionB.setEnabled(false);
        rbOptionC.setEnabled(false);
        rbOptionD.setEnabled(false);

        // Load user's answers for review
        quizViewModel.loadUserAnswersForQuiz(currentUserId, quizId);
        quizViewModel.getUserAnswers().observe(this, answers -> {
            if (answers != null && !answers.isEmpty()) {
                if (questions != null) { // Ensure questions are loaded before processing answers
                    for (Answer answer : answers) {
                        List<Option> optionsForQuestion = quizViewModel.getOptionsSync(answer.getQuestionId());
                        if (optionsForQuestion != null) {
                            for (Option opt : optionsForQuestion) {
                                if (opt.getOptionText() != null && opt.getOptionText().equals(answer.getSelectedOption())) {
                                    userSelectedOptionIds.put(answer.getQuestionId(), opt.getOptionID());
                                    break;
                                }
                            }
                        }
                    }

                    answeredStatus = new ArrayList<>(Collections.nCopies(questions.size(), false));
                    for (Answer answer : answers) {
                        for (int i = 0; i < questions.size(); i++) {
                            if (questions.get(i).getQuestionId() == answer.getQuestionId()) {
                                answeredStatus.set(i, true);
                                break;
                            }
                        }
                    }
                    questionNavigationAdapter.updateAnsweredStatus(answeredStatus);
                }
                displayQuestion(); // Re-display current question with review colors
            } else {
                Toast.makeText(this, "No answers found for review.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        quizViewModel.getQuiz().observe(this, quiz -> {
            if (quiz != null) {
                tvQuizTitle.setText(quiz.getTitle());
                if (!isReviewMode) { // Only start timer if not in review mode
                    timeLeftInMillis = (long) quiz.getDuration() * 60 * 1000;
                    startTimer();
                }
            } else {
                Toast.makeText(TakeQuizActivity.this, "Failed to load quiz.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        quizViewModel.getQuestions().observe(this, questionsList -> {
            if (questionsList != null && !questionsList.isEmpty()) {
                questions = questionsList;
                // Initialize answeredStatus AFTER questions are loaded
                answeredStatus = new ArrayList<>(Collections.nCopies(questions.size(), false));
                setupQuestionNavigation();
                displayQuestion(); // Display the first question
            } else {
                Toast.makeText(TakeQuizActivity.this, "No questions found for this quiz.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Observe options LiveData once here. This callback will be triggered whenever
        // quizViewModel.loadOptionsForQuestion posts new options.
        quizViewModel.getOptions().observe(this, options -> {
            if (options != null && questions != null && currentQuestionIndex < questions.size()) {
                Question currentQuestion = questions.get(currentQuestionIndex);
                // Verify these options are for the current question
                if (!options.isEmpty() && options.get(0).getQuestionID() == currentQuestion.getQuestionId()) {
                    updateQuestionOptionsUI(options);
                }
            }
        });
    }

    private void setupQuestionNavigation() {
        rvQuestionNavigation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        questionNavigationAdapter = new QuestionNavigationAdapter(questions.size(), position -> {
            if (!isReviewMode) {
                saveCurrentAnswer(); // Save current answer before navigating
            }
            currentQuestionIndex = position;
            displayQuestion(); // This will trigger loading options for the new question
        }, answeredStatus, currentQuestionIndex);
        rvQuestionNavigation.setAdapter(questionNavigationAdapter);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                Toast.makeText(TakeQuizActivity.this, "Time's up! Submitting quiz...", Toast.LENGTH_SHORT).show();
                submitQuiz();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "Time Left: %02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormatted);
    }

    /**
     * Hiển thị câu hỏi hiện tại và các lựa chọn của nó.
     * Cập nhật trạng thái của các RadioButton và nút điều hướng.
     */
    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "No questions to display.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đảm bảo chỉ số câu hỏi nằm trong giới hạn
        if (currentQuestionIndex < 0) currentQuestionIndex = questions.size() - 1;
        if (currentQuestionIndex >= questions.size()) currentQuestionIndex = 0;

        // Temporarily remove the listener to prevent unintended calls to saveCurrentAnswer()
        // when clearing or setting radio button states programmatically.
        rgOptions.setOnCheckedChangeListener(null);
        rgOptions.clearCheck(); // Clear previous selection
        resetRadioButtonColors(); // Reset radio button colors to default (white)

        Question currentQuestion = questions.get(currentQuestionIndex);
        tvQuestionNumber.setText(String.format(Locale.getDefault(), "Question %d/%d", currentQuestionIndex + 1, questions.size()));
        tvQuestionText.setText(currentQuestion.getQuestionText());

        // Trigger loading options for the current question.
        // The UI update for options and selection will happen in the quizViewModel.getOptions() observer.
        quizViewModel.loadOptionsForQuestion(currentQuestion.getQuestionId());

        // Re-attach the listener after UI updates are done in updateQuestionOptionsUI()
        // This is crucial to ensure user interactions are captured correctly.
        // However, we need to re-attach it AFTER the options are set and selection is restored.
        // So, it will be re-attached inside updateQuestionOptionsUI().

        // Update navigation adapter to highlight the current question
        questionNavigationAdapter.updateCurrentQuestionIndex(currentQuestionIndex);

        // Adjust button visibility based on mode and current question index
        if (!isReviewMode) {
            // Quiz Mode
            btnPreviousQuestion.setVisibility(currentQuestionIndex == 0 ? View.GONE : View.VISIBLE); // Hide Previous on first question
            btnNextQuestion.setVisibility(currentQuestionIndex == questions.size() - 1 ? View.GONE : View.VISIBLE); // Hide Next on last question
            btnSubmitQuiz.setVisibility(currentQuestionIndex == questions.size() - 1 ? View.VISIBLE : View.GONE); // Show Submit only on last question
            btnExitQuiz.setVisibility(View.GONE); // Hide Exit in quiz mode
        } else {
            // Review Mode
            btnPreviousQuestion.setVisibility(View.VISIBLE); // Always show Previous in review
            btnNextQuestion.setVisibility(View.VISIBLE); // Always show Next in review
            btnSubmitQuiz.setVisibility(View.GONE); // Hide Submit in review mode
            btnExitQuiz.setVisibility(View.VISIBLE); // Show Exit in review mode
        }
    }

    /**
     * Updates the UI for question options (text, tags, and selection/colors).
     * This method is called from the quizViewModel.getOptions() observer.
     */
    private void updateQuestionOptionsUI(List<Option> options) {
        if (options != null && options.size() >= 4) {
            rbOptionA.setText(options.get(0).getOptionText());
            rbOptionB.setText(options.get(1).getOptionText());
            rbOptionC.setText(options.get(2).getOptionText());
            rbOptionD.setText(options.get(3).getOptionText());

            rbOptionA.setTag(options.get(0).getOptionID());
            rbOptionB.setTag(options.get(1).getOptionID());
            rbOptionC.setTag(options.get(2).getOptionID());
            rbOptionD.setTag(options.get(3).getOptionID());

            if (isReviewMode) {
                showReviewAnswers(questions.get(currentQuestionIndex).getQuestionId(), options);
            } else {
                // In quiz mode, re-select user's previous answer if available
                if (userSelectedOptionIds.containsKey(questions.get(currentQuestionIndex).getQuestionId())) {
                    int selectedOptionId = userSelectedOptionIds.get(questions.get(currentQuestionIndex).getQuestionId());
                    if (rbOptionA.getTag() != null && (int) rbOptionA.getTag() == selectedOptionId) rgOptions.check(R.id.rb_option_a);
                    else if (rbOptionB.getTag() != null && (int) rbOptionB.getTag() == selectedOptionId) rgOptions.check(R.id.rb_option_b);
                    else if (rbOptionC.getTag() != null && (int) rbOptionC.getTag() == selectedOptionId) rgOptions.check(R.id.rb_option_c);
                    else if (rbOptionD.getTag() != null && (int) rbOptionD.getTag() == selectedOptionId) rgOptions.check(R.id.rb_option_d);
                }
                updateRadioButtonColors(); // Update colors based on selection
            }
        } else {
            rbOptionA.setText("N/A"); rbOptionB.setText("N/A");
            rbOptionC.setText("N/A"); rbOptionD.setText("N/A");
            Toast.makeText(this, "Not enough options for question " + (currentQuestionIndex + 1), Toast.LENGTH_SHORT).show();
        }
        // Re-attach the listener after all programmatic UI updates are done
        rgOptions.setOnCheckedChangeListener(radioGroupListener);
    }


    /**
     * Chuyển đến câu hỏi tiếp theo.
     * Nếu đang ở câu cuối cùng, sẽ quay về câu đầu tiên.
     */
    private void goToNextQuestion() {
        if (!isReviewMode) { // Only save answer if not in review mode
            saveCurrentAnswer();
        }
        currentQuestionIndex++;
        if (currentQuestionIndex >= questions.size()) {
            currentQuestionIndex = 0; // Loop back to the first question
        }
        displayQuestion();
    }

    /**
     * Chuyển đến câu hỏi trước đó.
     * Nếu đang ở câu đầu tiên, sẽ quay về câu cuối cùng.
     */
    private void goToPreviousQuestion() {
        if (!isReviewMode) { // Only save answer if not in review mode
            saveCurrentAnswer();
        }
        currentQuestionIndex--;
        if (currentQuestionIndex < 0) {
            currentQuestionIndex = questions.size() - 1; // Loop back to the last question
        }
        displayQuestion();
    }

    /**
     * Lưu câu trả lời hiện tại của người dùng.
     */
    private void saveCurrentAnswer() {
        int selectedRadioButtonId = rgOptions.getCheckedRadioButtonId();
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            int selectedOptionId = (int) selectedRadioButton.getTag();
            userSelectedOptionIds.put(currentQuestion.getQuestionId(), selectedOptionId);

            // Mark as answered for navigation buttons
            if (answeredStatus != null && currentQuestionIndex < answeredStatus.size()) {
                answeredStatus.set(currentQuestionIndex, true);
                questionNavigationAdapter.updateAnsweredStatus(answeredStatus);
            }
        } else {
            // If no answer selected, ensure it's marked as not answered and remove from map
            if (answeredStatus != null && currentQuestionIndex < answeredStatus.size()) {
                answeredStatus.set(currentQuestionIndex, false);
                questionNavigationAdapter.updateAnsweredStatus(answeredStatus);
            }
            userSelectedOptionIds.remove(currentQuestion.getQuestionId());
        }
    }

    private void resetRadioButtonColors() {
        rbOptionA.setBackgroundColor(Color.WHITE);
        rbOptionB.setBackgroundColor(Color.WHITE);
        rbOptionC.setBackgroundColor(Color.WHITE);
        rbOptionD.setBackgroundColor(Color.WHITE);
    }

    private void updateRadioButtonColors() {
        // This is for quiz mode, to highlight the selected answer
        resetRadioButtonColors(); // First reset all to white
        int selectedRadioButtonId = rgOptions.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            selectedRadioButton.setBackgroundColor(getResources().getColor(R.color.blue_current_question, null));
        }
    }

    private void showReviewAnswers(int questionId, List<Option> options) {
        // Disable radio buttons for review
        rbOptionA.setEnabled(false);
        rbOptionB.setEnabled(false);
        rbOptionC.setEnabled(false);
        rbOptionD.setEnabled(false);

        // Reset colors first
        resetRadioButtonColors();

        // Find the correct option
        int correctOptionId = -1;
        for (Option opt : options) {
            if (opt.isCorrect()) {
                correctOptionId = opt.getOptionID();
                break;
            }
        }

        // Get user's selected option for this question from stored answers
        int userSelectedOptionId = -1;
        if (quizViewModel.getUserAnswers().getValue() != null) {
            for (Answer answer : quizViewModel.getUserAnswers().getValue()) {
                if (answer.getQuestionId() == questionId) {
                    // Map selectedOptionText back to OptionID for comparison
                    for (Option opt : options) {
                        if (opt.getOptionText() != null && opt.getOptionText().equals(answer.getSelectedOption())) {
                            userSelectedOptionId = opt.getOptionID();
                            break;
                        }
                    }
                    break;
                }
            }
        }

        // Highlight correct answer in green
        if (rbOptionA.getTag() != null && (int) rbOptionA.getTag() == correctOptionId) {
            rbOptionA.setBackgroundColor(getResources().getColor(R.color.green_answered, null));
        } else if (rbOptionB.getTag() != null && (int) rbOptionB.getTag() == correctOptionId) {
            rbOptionB.setBackgroundColor(getResources().getColor(R.color.green_answered, null));
        } else if (rbOptionC.getTag() != null && (int) rbOptionC.getTag() == correctOptionId) {
            rbOptionC.setBackgroundColor(getResources().getColor(R.color.green_answered, null));
        } else if (rbOptionD.getTag() != null && (int) rbOptionD.getTag() == correctOptionId) {
            rbOptionD.setBackgroundColor(getResources().getColor(R.color.green_answered, null));
        }

        // Highlight user's incorrect answer in red (if different from correct)
        if (userSelectedOptionId != -1 && userSelectedOptionId != correctOptionId) {
            if (rbOptionA.getTag() != null && (int) rbOptionA.getTag() == userSelectedOptionId) {
                rbOptionA.setBackgroundColor(getResources().getColor(R.color.red_incorrect, null));
            } else if (rbOptionB.getTag() != null && (int) rbOptionB.getTag() == userSelectedOptionId) {
                rbOptionB.setBackgroundColor(getResources().getColor(R.color.red_incorrect, null));
            } else if (rbOptionC.getTag() != null && (int) rbOptionC.getTag() == userSelectedOptionId) {
                rbOptionC.setBackgroundColor(getResources().getColor(R.color.red_incorrect, null));
            } else if (rbOptionD.getTag() != null && (int) rbOptionD.getTag() == userSelectedOptionId) {
                rbOptionD.setBackgroundColor(getResources().getColor(R.color.red_incorrect, null));
            }
        }

        // Check the user's selected radio button (without changing its background color if it's incorrect)
        if (userSelectedOptionId != -1) {
            if (rbOptionA.getTag() != null && (int) rbOptionA.getTag() == userSelectedOptionId) rgOptions.check(R.id.rb_option_a);
            else if (rbOptionB.getTag() != null && (int) rbOptionB.getTag() == userSelectedOptionId) rgOptions.check(R.id.rb_option_b);
            else if (rbOptionC.getTag() != null && (int) rbOptionC.getTag() == userSelectedOptionId) rgOptions.check(R.id.rb_option_c);
            else if (rbOptionD.getTag() != null && (int) rbOptionD.getTag() == userSelectedOptionId) rgOptions.check(R.id.rb_option_d);
        }
    }


    private void submitQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Save the last question's answer if not already saved
        saveCurrentAnswer();

        int correctAnswersCount = 0;
        // Iterate through all questions and check correctness based on userSelectedOptionIds
        for (Question question : questions) {
            if (userSelectedOptionIds.containsKey(question.getQuestionId())) {
                int selectedOptionId = userSelectedOptionIds.get(question.getQuestionId());
                // Find the correct option for this question
                List<Option> optionsForQuestion = quizViewModel.getOptionsSync(question.getQuestionId()); // Synchronous fetch
                if (optionsForQuestion != null) {
                    for (Option opt : optionsForQuestion) {
                        if (opt.getOptionID() == selectedOptionId && opt.isCorrect()) {
                            correctAnswersCount++;
                            break;
                        }
                    }
                }
            }
        }

        double scorePercentage = (double) correctAnswersCount / questions.size() * 100;
        int finalScore = (int) scorePercentage;

        // Save quiz result to database
        QuizResult quizResult = new QuizResult(currentUserId, quizId, finalScore);
        quizViewModel.insertQuizResult(quizResult);

        // Save individual answers to database
        for (Question question : questions) {
            Integer selectedOptionId = userSelectedOptionIds.get(question.getQuestionId());
            String selectedOptionText = null;
            Boolean isCorrect = false;

            if (selectedOptionId != null) {
                List<Option> optionsForQuestion = quizViewModel.getOptionsSync(question.getQuestionId());
                if (optionsForQuestion != null) {
                    for (Option opt : optionsForQuestion) {
                        if (opt.getOptionID() == selectedOptionId) {
                            selectedOptionText = opt.getOptionText();
                            isCorrect = opt.isCorrect();
                            break;
                        }
                    }
                }
            }
            quizViewModel.insertAnswer(new Answer(currentUserId, question.getQuestionId(), selectedOptionText, isCorrect));
        }


        // Navigate to QuizResultActivity
        Intent resultIntent = new Intent(TakeQuizActivity.this, QuizResultActivity.class);
        resultIntent.putExtra(EXTRA_QUIZ_SCORE, finalScore);
        resultIntent.putExtra(EXTRA_TOTAL_QUESTIONS, questions.size());
        resultIntent.putExtra(EXTRA_QUIZ_ID, quizId); // Pass quiz ID for review
        startActivity(resultIntent);
        finish(); // Close TakeQuizActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
