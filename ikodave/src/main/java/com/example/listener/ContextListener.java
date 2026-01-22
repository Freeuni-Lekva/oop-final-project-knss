package com.example.listener;

import com.example.admin.dao.ProblemTopicRelationDAO;
import com.example.admin.dao.RoleDAO;
import com.example.admin.dao.SQLProblemTopicRelationDAO;
import com.example.admin.dao.SQLRoleDAO;
import com.example.leaderboard.dao.LeaderboardDAO;
import com.example.leaderboard.dao.SQLLeaderboardDAO;
import com.example.problems.DAO.*;
import com.example.registration.dao.SQLUserDAO;
import com.example.registration.dao.UserDAO;
import com.example.registration.mail.MailSender;
import com.example.submissions.CodeRunner.DockerCodeRunner;
import com.example.submissions.DAO.*;
import com.example.user_profile.dao.ProblemStatsDAO;
import com.example.user_profile.dao.SQLProblemStatsDAO;
import com.example.user_profile.dao.SQLUserStatsDAO;
import com.example.user_profile.dao.UserStatsDAO;
import com.example.verification.DAO.SQLVerificationDAO;
import com.example.verification.DAO.VerificationDAO;
import com.example.verification.TaskManagers.VerificationTaskManager;
import com.google.gson.Gson;
import org.apache.commons.dbcp2.BasicDataSource;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.DBConnectionConstants.*;
import static com.example.constants.MailConstants.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContextListener implements ServletContextListener {

    private BasicDataSource dataSource;
    private VerificationTaskManager taskManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUsername(DATABASE_USER);
        dataSource.setPassword(DATABASE_PASSWORD);

        ProblemTopicRelationDAO problemTopicRelationDAO = new SQLProblemTopicRelationDAO(dataSource);
        sce.getServletContext().setAttribute(PROBLEM_TOPIC_RELATION_DAO_KEY, problemTopicRelationDAO);

        UserDAO userDao = new SQLUserDAO(dataSource);
        sce.getServletContext().setAttribute(USER_DAO_KEY, userDao);

        ProblemDAO problemDAO = new SQLProblemDAO(dataSource);
        sce.getServletContext().setAttribute(PROBLEM_DAO_KEY, problemDAO);

        UserDAO userDAO = new SQLUserDAO(dataSource);
        sce.getServletContext().setAttribute(USER_DAO_KEY, userDAO);

        TestDAO testDAO = new SQLTestDAO(dataSource);
        sce.getServletContext().setAttribute(TEST_DAO_KEY, testDAO);

        SubmissionDAO submissionDAO = new SQLSubmissionDAO(dataSource);
        sce.getServletContext().setAttribute(SUBMISSION_DAO_KEY, submissionDAO);

        CodeLanguageDAO codeLanguageDAO = new SQLCodeLanguageDAO(dataSource);
        sce.getServletContext().setAttribute(CODE_LANGUAGE_DAO_KEY, codeLanguageDAO);

        VerdictDAO verdictDAO = new SQLVerdictDAO(dataSource);
        sce.getServletContext().setAttribute(VERDICT_DAO_KEY, verdictDAO);

        TopicDAO topicDAO = new SQLTopicDAO(dataSource);
        sce.getServletContext().setAttribute(TOPIC_DAO_KEY, topicDAO);

        DifficultyDAO difficultyDAO = new SQLDifficultyDAO(dataSource);
        sce.getServletContext().setAttribute(DIFFICULTY_DAO_KEY, difficultyDAO);

        LeaderboardDAO leaderboardDAO = new SQLLeaderboardDAO(dataSource);
        sce.getServletContext().setAttribute(LEADERBOARD_DAO_KEY, leaderboardDAO);

        UserStatsDAO userStatsDAO = new SQLUserStatsDAO(dataSource);
        sce.getServletContext().setAttribute(USER_STATS_DAO, userStatsDAO);

        ProblemStatsDAO problemStatsDAO = new SQLProblemStatsDAO(dataSource);
        sce.getServletContext().setAttribute(PROBLEM_STATS_DAO, problemStatsDAO);

        StatusDAO statusDAO = new SQLStatusDAO(dataSource);
        sce.getServletContext().setAttribute(STATUS_DAO_KEY, statusDAO);

        RoleDAO roleDAO = new SQLRoleDAO(dataSource);
        sce.getServletContext().setAttribute(ROLE_DAO_KEY, roleDAO);

        DockerCodeRunner dockerCodeRunner = new DockerCodeRunner();
//        dockerCodeRunner.startContainers();
        sce.getServletContext().setAttribute(DOCKER_CODE_RUNNER_KEY, dockerCodeRunner);

        Gson gson = new Gson();
        sce.getServletContext().setAttribute(GSON_KEY, gson);

        MailSender mailSender = new MailSender(smtpHost, smtpPort, username, appPassword, fromAddress);
        sce.getServletContext().setAttribute(MAIL_SENDER_KEY, mailSender);

        ExecutorService mailExec = Executors.newFixedThreadPool(5);
        sce.getServletContext().setAttribute(MAIL_EXEC_KEY, mailExec);

        VerificationDAO verificationDAO = new SQLVerificationDAO(dataSource);
        sce.getServletContext().setAttribute(VERIFICATION_DAO_KEY, verificationDAO);

        taskManager = new VerificationTaskManager(verificationDAO);
        taskManager.start();

        sce.getServletContext().setAttribute(BASIC_DATASOURCE_KEY, dataSource);
    }

    @Override
    public void contextDestroyed(javax.servlet.ServletContextEvent sce) {
        DockerCodeRunner dockerCodeRunner = (DockerCodeRunner) sce.getServletContext().getAttribute(DOCKER_CODE_RUNNER_KEY);
        try {
            if (dockerCodeRunner != null) {
                dockerCodeRunner.destroyContainers();
            }

            if (dataSource != null) {
                dataSource.close();
            }

            if (taskManager != null) {
                taskManager.stop();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error closing the database connection pool", e);
        }
    }

}
