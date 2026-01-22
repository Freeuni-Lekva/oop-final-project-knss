package com.example.admin.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AddProblemPageServlet", value = "/AddProblemPage")
public class AddProblemPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AdminAuthHelper.isAdmin(request, getServletContext())) {
            response.sendRedirect("/home");
            return;
        }

        request.getRequestDispatcher("/static/admin/add_problem.html")
                .forward(request, response);
    }
}
