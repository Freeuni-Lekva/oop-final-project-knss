package com.example.listener;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DAO.SQLProblemDAO;
import com.example.registration.dao.MySQLUserDao;
import com.example.registration.dao.UserDAO;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import static com.example.util.AttributeConstants.*;
import static com.example.util.DBConnectionConstants.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {
    private BasicDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUsername(DATABASE_USER);
        dataSource.setPassword(DATABASE_PASSWORD);

        UserDAO userDao = new MySQLUserDao(dataSource);
        sce.getServletContext().setAttribute(USER_DAO_KEY, userDao);

        ProblemDAO problemDAO = new SQLProblemDAO(dataSource);
        sce.getServletContext().setAttribute(PROBLEM_DAO_KEY, problemDAO);

        sce.getServletContext().setAttribute(BASIC_DATASOURCE_KEY, dataSource);
    }

    @Override
    public void contextDestroyed(javax.servlet.ServletContextEvent sce) {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error closing the database connection pool", e);
        }
    }

}
