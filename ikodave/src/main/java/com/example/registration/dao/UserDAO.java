package com.example.registration.dao;

import com.example.registration.DTO.User;

public interface UserDAO {

    User getUserById(int userId);

    User addUser(User user);

    boolean authenticate(User user);

    void deleteUser(String username);

    boolean usernameExists(String username);

    boolean verifiedMailExists(String mail);

    User getUserByUsername(String username);

    User getUserByMail(String username);

}
