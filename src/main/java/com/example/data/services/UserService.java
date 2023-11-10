package com.example.data.services;

import com.example.data.data.User;
import com.example.data.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNewUser(User user) {
        Optional<User> excitingUser = userRepository.findById(user.getId());

        if(excitingUser.isEmpty()) {
            userRepository.save(user);
        }
    }

    public void changeUserDialog(User user) {
        Optional<User> excitingUser = userRepository.findById(user.getId());

        if(excitingUser.isPresent()) {
            User inDb = excitingUser.get();
            inDb.setDialogPart(user.getDialogPart());

            userRepository.save(inDb);
        }
    }

    public void addAdmin(User user) {
        Optional<User> excitingUser = userRepository.findById(user.getId());

        if(excitingUser.isPresent()) {
            User inDb = excitingUser.get();
            inDb.setAdmin(true);

            userRepository.save(inDb);
        }
    }

    public void deleteUser(User user) {
        Optional<User> excitingUser = userRepository.findById(user.getId());

        if(excitingUser.isPresent()) {
            userRepository.delete(user);
        }
    }

    public void changeUserLanguage(User user) {
        Optional<User> excitingUser = userRepository.findById(user.getId());

        if(excitingUser.isPresent()) {
            User inDb = excitingUser.get();
            inDb.setLanguage(user.getLanguage());

            userRepository.save(inDb);
        }
    }
}
