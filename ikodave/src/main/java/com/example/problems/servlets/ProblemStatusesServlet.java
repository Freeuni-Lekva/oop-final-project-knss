package com.example.problems.servlets;

import com.example.problems.DTO.Status;
import com.example.submissions.DAO.StatusDAO;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.constants.AttributeConstants.*;

public class ProblemStatusesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatusDAO statusDAO = (StatusDAO) getServletContext().getAttribute(STATUS_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        List<Status> statuses = statusDAO.getStatuses();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(statuses));
    }
}
