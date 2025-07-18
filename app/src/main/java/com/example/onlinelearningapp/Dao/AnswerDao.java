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

    @Query("SELECT * FROM Answers WHERE UserID = :userId AND QuestionID = :questionId")
    LiveData<Answer> getAnswer(int userId, int questionId);

    // ĐÃ THÊM: Phương thức để lấy tất cả câu trả lời của người dùng cho một quiz cụ thể
    @Query("SELECT A.* FROM Answers A JOIN Questions Q ON A.QuestionID = Q.QuestionID WHERE A.UserID = :userId AND Q.QuizID = :quizId")
    LiveData<List<Answer>> getAnswersByUserIdAndQuizId(int userId, int quizId);

    @Query("DELETE FROM Answers")
    void deleteAllAnswers();
}



