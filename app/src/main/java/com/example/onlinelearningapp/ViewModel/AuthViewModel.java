package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.User;

public class AuthViewModel extends AndroidViewModel {
    private Repository repository;
    private MutableLiveData<User> _loggedInUser = new MutableLiveData<>();
    public LiveData<User> loggedInUser = _loggedInUser;

    private MutableLiveData<String> _loginMessage = new MutableLiveData<>();
    public LiveData<String> loginMessage = _loginMessage;

    private MutableLiveData<String> _registerMessage = new MutableLiveData<>();
    public LiveData<String> registerMessage = _registerMessage;

    private MutableLiveData<String> _resetPasswordMessage = new MutableLiveData<>();
    public LiveData<String> resetPasswordMessage = _resetPasswordMessage;

    // LiveData to observe a specific user by ID (for ChangeProfileActivity)
    private LiveData<User> userByIdLiveData;

    public AuthViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public LiveData<User> getUserById(int userId) {
        // This method will be observed by ChangeProfileActivity to get current user data
        if (userByIdLiveData == null || userByIdLiveData.getValue() == null || userByIdLiveData.getValue().getUserId() != userId) {
            userByIdLiveData = repository.getUserById(userId);
        }
        return userByIdLiveData;
    }

    public void login(String email, String password) {
        repository.getUserByEmailAndPassword(email, password).observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    _loggedInUser.postValue(user);
                    _loginMessage.postValue("Login successful!");
                } else {
                    _loggedInUser.postValue(null);
                    _loginMessage.postValue("Invalid email or password.");
                }
                // Remove the observer to prevent multiple calls
                repository.getUserByEmailAndPassword(email, password).removeObserver(this);
            }
        });
    }

    public void register(String name, String email, String password) {
        repository.getUserByEmail(email).observeForever(new Observer<User>() {
            @Override
            public void onChanged(User existingUser) {
                if (existingUser != null) {
                    _registerMessage.postValue("Email already registered.");
                } else {
                    User newUser = new User(name, email, password);
                    repository.insertUser(newUser);
                    _registerMessage.postValue("Registration successful! Please login.");
                }
                repository.getUserByEmail(email).removeObserver(this);
            }
        });
    }

    public void resetPassword(String email) {
        repository.getUserByEmail(email).observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    _resetPasswordMessage.postValue("Password reset link sent to your email (simulated).");
                } else {
                    _resetPasswordMessage.postValue("Email not found.");
                }
                repository.getUserByEmail(email).removeObserver(this);
            }
        });
    }

    // This method will be called from ChangeProfileActivity to update user details
    public void updateUser(User user) {
        repository.updateUser(user);
        // After update, if the updated user is the logged-in user, update _loggedInUser
        if (_loggedInUser.getValue() != null && _loggedInUser.getValue().getUserId() == user.getUserId()) {
            _loggedInUser.postValue(user);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers if necessary, though LiveData generally handles this with LifecycleOwner
    }
}