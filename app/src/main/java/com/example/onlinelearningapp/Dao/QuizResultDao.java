package com.example.onlinelearningapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.QuizResult;

import java.util.List;

@Dao
public interface QuizResultDao {
    @Insert
    long insertQuizResult(QuizResult quizResult);

    @Update
    void updateQuizResult(QuizResult quizResult);

    @Query("SELECT * FROM QuizResults WHERE ResultID = :resultId")
    LiveData<QuizResult> getQuizResultById(int resultId);

    @Query("SELECT * FROM QuizResults WHERE UserID = :userId AND QuizID = :quizId")
    LiveData<QuizResult> getUserQuizResult(int userId, int quizId);

    @Query("SELECT * FROM QuizResults WHERE UserID = :userId")
    LiveData<List<QuizResult>> getQuizResultsByUserId(int userId);
}
