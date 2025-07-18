package com.example.onlinelearningapp.ViewModel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;

import java.util.List;

public class CourseViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Course>> allCourses;

    public CourseViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        allCourses = repository.getAllCourses(); // Khởi tạo LiveData từ Repository
    }

    // Phương thức để lấy tất cả các khóa học
    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    // Bạn có thể thêm các phương thức khác liên quan đến Course tại đây, ví dụ:
    // public LiveData<Course> getCourseDetails(int courseId) {
    //     return repository.getCourseById(courseId);
    // }
}
