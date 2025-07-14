package com.example.onlinelearningapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Option;

import java.util.List;

@Dao
public interface OptionDao {
    @Insert
    long insertOption(Option option);

    @Update
    void updateOption(Option option);

    @Query("SELECT * FROM Options WHERE OptionID = :optionId")
    LiveData<Option> getOptionById(int optionId);

    @Query("SELECT * FROM Options WHERE QuestionID = :questionId")
    LiveData<List<Option>> getOptionsByQuestionId(int questionId);
}
