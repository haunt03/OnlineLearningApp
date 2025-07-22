package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void deleteUser(User user);

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

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User getUserByEmailSync(String email); // Hàm đồng bộ để dùng trong thread

    @Query("SELECT * FROM User WHERE Role = 0")
    LiveData<List<User>> getAllUsersByRole();

    @Query("SELECT COUNT(UserID) FROM User")
    LiveData<Integer> getUserCount();

    @Query("SELECT * FROM User ORDER BY CreatedAt DESC LIMIT 5")
    LiveData<List<User>> getRecentUsers();

    @Query("SELECT * FROM User WHERE Role = 0 AND Status = 'active'")
    LiveData<List<User>> getActiveUsers();

    @Query("SELECT * FROM User WHERE Role = 0 AND Status = 'inactive'")
    LiveData<List<User>> getInactiveUsers();
}
