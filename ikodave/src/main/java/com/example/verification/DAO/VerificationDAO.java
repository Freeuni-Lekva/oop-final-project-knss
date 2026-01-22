package com.example.verification.DAO;

import com.example.registration.DTO.User;

public interface VerificationDAO {

    void removeTimedOutVerifications();

    User updateUserByVerificationCode(String verificationCode);

    User getUserByVerificationCode(String verificationCode);

    User updateUserVerificationCode(User user, String verificationCode);
}
