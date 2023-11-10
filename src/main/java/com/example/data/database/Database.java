package com.example.data.database;

import com.example.data.data.User;

import java.util.List;

public interface Database {
    User getUserInfo(long chatId);

    List<User> getAdminList();

    void createNewUser(User newUser);

    void setDialogPart(User user);

    void deleteUser(User user);

    void setUserLanguage(User user);

    void addAdmin(User user);
}
