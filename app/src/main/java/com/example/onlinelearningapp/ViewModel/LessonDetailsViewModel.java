package com.example.onlinelearningapp.ViewModel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.Entity.Quiz;

import java.util.List;

public class LessonDetailsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Lesson> lesson;
    private LiveData<List<Quiz>> quizForLesson;

    public LessonDetailsViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void loadLessonDetails(int lessonId) {
        lesson = repository.getLessonById(lessonId);
        quizForLesson = repository.getQuizzesByLessonId(lessonId);
    }

    public LiveData<Lesson> getLesson() {
        return lesson;
    }

    public LiveData<List<Quiz>> getQuizForLesson() {
        return quizForLesson;
    }
}
