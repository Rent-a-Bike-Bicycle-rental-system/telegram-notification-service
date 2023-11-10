package com.example.data.database;

import com.example.data.data.User;
import com.example.data.repositories.UserRepository;
import com.example.data.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostgreSqlDatabase implements Database {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public PostgreSqlDatabase(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public User getUserInfo(long chatId) {
        return userRepository.getUserByUserId(chatId);
    }

    @Override
    public List<User> getAdminList() {
        return userRepository.getUsersByIsAdmin(true);
    }

    @Override
    public void createNewUser(User newUser) {
        userService.addNewUser(newUser);
    }

    @Override
    public void setDialogPart(User user) {
        userService.changeUserDialog(user);
    }

    @Override
    public void deleteUser(User user) {
        userService.deleteUser(user);
    }

    @Override
    public void setUserLanguage(User user) {
        userService.changeUserLanguage(user);
    }

    @Override
    public void addAdmin(User user) {
        userService.addAdmin(user);
    }
}
