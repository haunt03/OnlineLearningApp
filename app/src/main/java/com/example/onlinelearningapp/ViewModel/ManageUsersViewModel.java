package com.example.onlinelearningapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.User;

import java.util.List;

public class ManageUsersViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<User>> allUsers;
    private LiveData<List<User>> activeUsers;
    private LiveData<List<User>> inactiveUsers;
    private MutableLiveData<List<User>> currentUsers;

    public ManageUsersViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allUsers = repository.getAllUsersByRole();
        activeUsers = repository.getActiveUsers();
        inactiveUsers = repository.getInactiveUsers();
        currentUsers = new MutableLiveData<>();
        currentUsers.setValue(allUsers.getValue());
    }

    public LiveData<List<User>> getAllUsersByRole() {
        return allUsers;
    }

    public void deleteUser(User user) {
        repository.deleteUser(user);
    }

    public void showActiveUsers() {
        currentUsers.setValue(activeUsers.getValue());
    }

    public void showInactiveUsers() {
        currentUsers.setValue(inactiveUsers.getValue());
    }

    public LiveData<User> getUserById(int userId) {
        return repository.getUserById(userId); // Fixed to return LiveData<User>
    }
}