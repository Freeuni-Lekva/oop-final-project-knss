package com.example.admin.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AdminPageServlet", value = "/AdminPage")
public class AdminPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AdminAuthHelper.isAdmin(request, getServletContext())) {
            response.sendRedirect("/home");
            return;
        }

        request.getRequestDispatcher("/static/admin/admin_page.html")
                .forward(request, response);
    }
}
