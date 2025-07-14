package com.example.onlinelearningapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Progress;

import java.util.List;

@Dao
public interface ProgressDao {
    @Insert
    long insertProgress(Progress progress);

    @Update
    void updateProgress(Progress progress);

    @Query("SELECT * FROM Progress WHERE ProgressID = :progressId")
    LiveData<Progress> getProgressById(int progressId);

    @Query("SELECT * FROM Progress WHERE UserID = :userId AND LessonID = :lessonId")
    LiveData<Progress> getUserProgressForLesson(int userId, int lessonId);

    @Query("SELECT * FROM Progress WHERE UserID = :userId")
    LiveData<List<Progress>> getProgressByUserId(int userId);
}
