package com.example.onlinelearningapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Lesson;

import java.util.List;

public class LessonOfCourseViewModel extends AndroidViewModel {
    private Repository repository;

    public LessonOfCourseViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public LiveData<List<Lesson>> getLessonsByCourseId(int courseId) {
        return repository.getLessonsByCourseId(courseId);
    }
}
