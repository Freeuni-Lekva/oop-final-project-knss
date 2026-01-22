package com.example.verification.TaskManagers;

import com.example.registration.dao.UserDAO;
import com.example.verification.DAO.VerificationDAO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VerificationTaskManager {

    private final ScheduledExecutorService schedular =
            Executors.newScheduledThreadPool(1);

    private final VerificationDAO verificationDAO;

    public VerificationTaskManager(VerificationDAO verificationDAO) {
        this.verificationDAO = verificationDAO;
    }

    public void start() {
        schedular.scheduleAtFixedRate(
                verificationDAO::removeTimedOutVerifications,
                10,
                10,
                TimeUnit.MINUTES
        );
    }

    public void stop() {
        schedular.shutdown();
    }

}
