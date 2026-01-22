package com.example.problems.servlets;

import com.example.problems.DAO.TopicDAO;
import com.example.problems.DTO.Topic;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.example.constants.AttributeConstants.GSON_KEY;
import static com.example.constants.AttributeConstants.TOPIC_DAO_KEY;

public class ProblemTopicsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TopicDAO topicDAO = (TopicDAO) getServletContext().getAttribute(TOPIC_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        List<Topic> topics = topicDAO.getTopics();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(topics));
    }

}
