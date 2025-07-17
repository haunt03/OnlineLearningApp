package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Quiz;

import java.util.List;

@Dao
public interface QuizDao {
    @Insert
    long insertQuiz(Quiz quiz);

    @Update
    void updateQuiz(Quiz quiz);

    @Query("SELECT * FROM Quizzes WHERE QuizID = :quizId")
    LiveData<Quiz> getQuizById(int quizId);

    @Query("SELECT * FROM Quizzes WHERE LessonID = :lessonId")
    LiveData<List<Quiz>> getQuizzesByLessonId(int lessonId);

    @Query("DELETE FROM Quizzes")
    void deleteAllQuizzes();
}
