package com.example.data.repositories;

import com.example.data.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByUserId(long id);

    @Query("SELECT u FROM User u WHERE u.isAdmin = :isAdmin")
    List<User> getUsersByIsAdmin(boolean isAdmin);
}
