package com.example.onlinelearningapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Answer;

import java.util.List;

@Dao
public interface AnswerDao {
    @Insert
    long insertAnswer(Answer answer);

    @Update
    void updateAnswer(Answer answer);

    @Query("SELECT * FROM Answers WHERE AnswerID = :answerId")
    LiveData<Answer> getAnswerById(int answerId);

    @Query("SELECT * FROM Answers WHERE UserID = :userId AND QuestionID = :questionId")
    LiveData<Answer> getUserAnswerForQuestion(int userId, int questionId);

    @Query("SELECT * FROM Answers WHERE UserID = :userId")
    LiveData<List<Answer>> getAnswersByUserId(int userId);
}
