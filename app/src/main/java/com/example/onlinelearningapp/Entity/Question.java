package com.example.onlinelearningapp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Questions",
        foreignKeys = @ForeignKey(entity = Quiz.class,
                parentColumns = "QuizID",
                childColumns = "QuizID",
                onDelete = ForeignKey.CASCADE))
public class Question {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "QuestionID")
    private int questionId;

    @ColumnInfo(name = "QuizID")
    private int quizId;

    @ColumnInfo(name = "QuestionText")
    private String questionText;

    // Constructor
    public Question(int quizId, String questionText) {
        this.quizId = quizId;
        this.questionText = questionText;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}