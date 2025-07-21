package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.utils.EmailSender;

import java.security.SecureRandom;
import java.util.function.Consumer;

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

    private LiveData<User> userByIdLiveData;

    // private FirebaseAuth mAuth; // Remove if not using Firebase Auth at all

    public AuthViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        // mAuth = FirebaseAuth.getInstance(); // Remove if not using Firebase Auth at all
    }

    public LiveData<User> getUserById(int userId) {
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

    public void resetPassword(String email, Consumer<Boolean> callback) {
        new Thread(() -> {
            User user = repository.getUserByEmailSync(email); // FIXED

            if (user == null) {
                callback.accept(false);
                return;
            }

            String newPassword = generateRandomPassword(10); // FIXED: truyền length
            boolean sent = EmailSender.send(email, "Your New Password", "Your new password is: " + newPassword); // FIXED

            if (sent) {
                user.setPassword(newPassword);
                repository.updateUser(user); // FIXED
            }

            callback.accept(sent);
        }).start();
    }



    public void updateUser(User user) {
        repository.updateUser(user);
        if (_loggedInUser.getValue() != null && _loggedInUser.getValue().getUserId() == user.getUserId()) {
            _loggedInUser.postValue(user);
        }
    }

    // Helper method to generate a random password
    private String generateRandomPassword(int length) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String OTHER_CHAR = "!@#$%&*_+-="; // Optional special characters

        String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}