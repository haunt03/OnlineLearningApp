package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    long insertLesson(Lesson lesson);

    @Update
    void updateLesson(Lesson lesson);

    @Query("SELECT * FROM Lessons WHERE LessonID = :lessonId")
    LiveData<Lesson> getLessonById(int lessonId);

    @Query("SELECT * FROM Lessons WHERE CourseID = :courseId")
    LiveData<List<Lesson>> getLessonsByCourseId(int courseId);

    @Query("SELECT * FROM Lessons ORDER BY LessonID DESC LIMIT 5") // Assuming LessonID reflects creation order for "newest"
    LiveData<List<Lesson>> getTop5NewestLessons();

    @Query("SELECT * FROM Lessons")
    LiveData<List<Lesson>> getAllLessons();

    @Query("DELETE FROM Lessons")
    void deleteAllLessons();
}

