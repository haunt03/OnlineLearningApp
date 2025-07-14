package com.example.onlinelearningapp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Answers",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "UserID",
                        childColumns = "UserID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "QuestionID",
                        childColumns = "QuestionID",
                        onDelete = ForeignKey.CASCADE)
        })
public class Answer {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "AnswerID")
    private int answerId;

    @ColumnInfo(name = "UserID")
    private int userId;

    @ColumnInfo(name = "QuestionID")
    private int questionId;

    @ColumnInfo(name = "SelectedOption")
    private String selectedOption; // Storing 'A','B','C','D'

    @ColumnInfo(name = "IsCorrect")
    private Boolean isCorrect; // Can be null if not yet evaluated

    // Constructor
    public Answer(int userId, int questionId, String selectedOption, Boolean isCorrect) {
        this.userId = userId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}

