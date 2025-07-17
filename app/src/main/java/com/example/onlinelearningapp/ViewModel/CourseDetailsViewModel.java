package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Lesson;

import java.util.List;

public class CourseDetailsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Lesson>> lessons;

    public CourseDetailsViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void loadLessonsForCourse(int courseId) {
        lessons = repository.getLessonsByCourseId(courseId);
    }

    // CourseDetailsViewModel.java
    public LiveData<List<Lesson>> getLessons(int courseId) {
        return repository.getLessonsByCourseId(courseId);
    }

}
