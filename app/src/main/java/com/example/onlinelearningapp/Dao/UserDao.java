package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User WHERE Email = :email AND Password = :password")
    LiveData<User> getUserByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM User WHERE Email = :email")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM User WHERE UserID = :userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllUsers();

    @Query("DELETE FROM User")
    void deleteAllUsers();
}
