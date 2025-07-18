package com.example.onlinelearningapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    long insertQuestion(Question question);

    @Update
    void updateQuestion(Question question);

    @Query("SELECT * FROM Questions WHERE QuestionID = :questionId")
    LiveData<Question> getQuestionById(int questionId);

    @Query("SELECT * FROM Questions WHERE QuizID = :quizId")
    LiveData<List<Question>> getQuestionsByQuizId(int quizId);

    @Query("DELETE FROM Questions")
    void deleteAllQuestions();
}
