package com.example.verification.servlets;

import com.example.registration.DTO.User;
import com.example.registration.mail.MailSender;
import com.example.verification.DAO.VerificationDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.MailConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;
import static java.lang.String.format;

public class VerificationResendServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        VerificationDAO verificationDAO = (VerificationDAO) getServletContext().getAttribute(VERIFICATION_DAO_KEY);
        MailSender mailSender = (MailSender) getServletContext().getAttribute(MAIL_SENDER_KEY);
        Executor mailExec = (Executor) getServletContext().getAttribute(MAIL_EXEC_KEY);

        if (user == null || user.isVerified() || user.getVerificationCodeExpiry().isAfter(LocalDateTime.now())) {
            System.out.println("verification error1");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String newVerificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(newVerificationCode);
        User updateUser = verificationDAO.updateUserVerificationCode(user, newVerificationCode);
        request.getSession().setAttribute(USER_KEY, updateUser);

        if (updateUser == null) {
            System.out.println("verification error2");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        mailExec.execute(() -> {
            String verifyUrl = format("%s/verify?code=%s", System.getenv("DOMAIN"), user.getVerificationCode());
            String text = TEXT.formatted(verifyUrl);
            String html = HTML.formatted(user.getUsername(), verifyUrl, verifyUrl, verifyUrl);
            mailSender.send(user.getMail(), SUBJECT, text, html);
        });

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":true}");
    }

}
