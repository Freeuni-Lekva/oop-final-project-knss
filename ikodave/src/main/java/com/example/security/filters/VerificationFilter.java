package com.example.security.filters;

import com.example.registration.DTO.User;
import com.example.registration.dao.UserDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.example.constants.AttributeConstants.USER_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

@WebFilter(value = "*", asyncSupported = true)
public class VerificationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute(USER_DAO_KEY);
        HttpSession session = ((HttpServletRequest) request).getSession(false);

        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        User user = (User) session.getAttribute(USER_KEY);

        if (user == null || userDAO.getUserById(user.getId()) == null) {
            session.invalidate();
        }
        chain.doFilter(request, response);
    }

}
