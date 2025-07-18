package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Answer;
import com.example.onlinelearningapp.Entity.Option;
import com.example.onlinelearningapp.Entity.Question;
import com.example.onlinelearningapp.Entity.Quiz;
import com.example.onlinelearningapp.Entity.QuizResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations; // Import Transformations
import androidx.core.util.Pair; // Import Pair (for the trigger)

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Answer;
import com.example.onlinelearningapp.Entity.Option;
import com.example.onlinelearningapp.Entity.Question;
import com.example.onlinelearningapp.Entity.Quiz;
import com.example.onlinelearningapp.Entity.QuizResult;

import java.util.List;

public class QuizViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Quiz> quiz;
    private LiveData<List<Question>> questions;

    // New: MutableLiveData to trigger loading options for a specific question
    private MutableLiveData<Integer> optionsLoadTrigger = new MutableLiveData<>();
    // New: LiveData that holds the options for the current question, using switchMap
    private LiveData<List<Option>> liveOptions;

    private LiveData<List<Answer>> userAnswers; // For review mode

    public QuizViewModel(Application application) {
        super(application);
        repository = new Repository(application);

        // Initialize liveOptions using Transformations.switchMap
        // This LiveData will react to changes in optionsLoadTrigger
        liveOptions = Transformations.switchMap(optionsLoadTrigger, questionId -> {
            if (questionId != null && questionId != -1) {
                return repository.getOptionsByQuestionId(questionId);
            }
            // Always return a non-null LiveData, even if empty
            return new MutableLiveData<>(null);
        });
    }

    public void loadQuizData(int quizId) {
        quiz = repository.getQuizById(quizId);
        questions = repository.getQuestionsByQuizId(quizId);
    }

    public LiveData<Quiz> getQuiz() {
        return quiz;
    }

    public LiveData<List<Question>> getQuestions() {
        return questions;
    }

    // This method now updates the trigger LiveData, which in turn updates liveOptions
    public void loadOptionsForQuestion(int questionId) {
        optionsLoadTrigger.setValue(questionId);
    }

    // This returns the LiveData for options (which is updated via switchMap)
    public LiveData<List<Option>> getOptions() {
        return liveOptions;
    }

    // Synchronous fetch for options (used in submitQuiz and setupReviewMode for immediate access)
    public List<Option> getOptionsSync(int questionId) {
        try {
            return repository.getOptionsByQuestionIdSync(questionId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertQuizResult(QuizResult quizResult) {
        repository.insertQuizResult(quizResult);
    }

    public void insertAnswer(Answer answer) {
        repository.insertAnswer(answer);
    }

    public void loadUserAnswersForQuiz(int userId, int quizId) {
        userAnswers = repository.getAnswersByUserIdAndQuizId(userId, quizId);
    }

    public LiveData<List<Answer>> getUserAnswers() {
        return userAnswers;
    }
}

