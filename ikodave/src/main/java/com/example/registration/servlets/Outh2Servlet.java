package com.example.registration.servlets;

import com.example.registration.DTO.User;
import com.example.registration.dao.UserDAO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.constants.AttributeConstants.GSON_KEY;
import static com.example.constants.AttributeConstants.USER_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class Outh2Servlet extends HttpServlet {
    
    private static final String CLIENT_ID = "143211445052-tql6as8lmt5samgbpf16errprt1j62hb.apps.googleusercontent.com";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute(USER_DAO_KEY);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        JsonObject jsonReq = gson.fromJson(sb.toString(), JsonObject.class);
        String idTokenString = jsonReq.get("id_token").getAsString();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        if (idToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("Invalid ID token");
            return;
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        User user = userDAO.getUserByMail(email);
        if (user == null) {
            response.setContentType("application/json");
            response.getWriter().print(
                    new JSONObject()
                            .put("auth", false)
                            .put("email", email)
                            .toString()
            );
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(USER_KEY, user);

        response.setContentType("application/json");
        response.getWriter().print(
                new JSONObject()
                        .put("auth", true)
                        .put("email", email)
                        .toString()
        );
    }
}
