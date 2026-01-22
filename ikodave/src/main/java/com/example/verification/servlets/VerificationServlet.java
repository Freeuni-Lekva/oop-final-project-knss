package com.example.verification.servlets;

import com.example.registration.DTO.User;
import com.example.registration.dao.UserDAO;
import com.example.verification.DAO.VerificationDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.constants.AttributeConstants.USER_DAO_KEY;
import static com.example.constants.AttributeConstants.VERIFICATION_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class VerificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VerificationDAO verificationDAO = (VerificationDAO) getServletContext().getAttribute(VERIFICATION_DAO_KEY);
        String verificationCode = request.getParameter("code");
        User user = verificationDAO.updateUserByVerificationCode(verificationCode);

        if (user == null) {
            request.getRequestDispatcher("/static/verification/verification_error.html").forward(request, response);
        }
        else {
            request.getSession().invalidate();
            request.getRequestDispatcher("/static/verification/verification_success.html").forward(request, response);
        }

    }

}
