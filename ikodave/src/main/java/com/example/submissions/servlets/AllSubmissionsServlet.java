package com.example.submissions.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import com.example.submissions.DAO.CodeLanguageDAO;
import com.example.submissions.DAO.SubmissionDAO;
import com.example.submissions.DAO.VerdictDAO;
import com.example.submissions.DTO.Submission;
import com.example.submissions.Response.SubmissionResponse;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.AttributeConstants.CODE_LANGUAGE_DAO_KEY;
import static com.example.constants.AttributeConstants.VERDICT_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class AllSubmissionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);


        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized access\"}");
            return;
        }


        UserDAO userDAO = (UserDAO) getServletContext().getAttribute(USER_DAO_KEY);
        ProblemDAO problemDAO = (ProblemDAO) getServletContext().getAttribute(PROBLEM_DAO_KEY);
        VerdictDAO verdictDAO = (VerdictDAO) getServletContext().getAttribute(VERDICT_DAO_KEY);
        CodeLanguageDAO codeLanguageDAO = (CodeLanguageDAO) getServletContext().getAttribute(CODE_LANGUAGE_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        SubmissionDAO submissionDAO = (SubmissionDAO) getServletContext().getAttribute(SUBMISSION_DAO_KEY);

        List<Submission> submissions = submissionDAO.getAllSubmissionsByOrder();

        List<SubmissionResponse> submissionResponses =
                submissions.stream().map((submission) -> {

                            return new SubmissionResponse(
                                    submission.getId(),
                                    submission.getSubmitDate(),
                                    userDAO.getUserById(submission.getUserId()).getUsername(),
                                    submission.getSolutionCode(),
                                    problemDAO.getProblemTitle(submission.getProblemId()),
                                    codeLanguageDAO.getCodeLanguageById(submission.getCodeLanguageId()).getLanguage(),
                                    verdictDAO.getVerdictById(submission.getVerdictId()).getVerdict(),
                                    submission.getTime(),
                                    submission.getMemory(),
                                    submission.getLog()
                            );
                        }
                ).toList();


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, List<SubmissionResponse>> payload = new HashMap<>();
        payload.put("submissions", submissionResponses);
        response.getWriter().write(gson.toJson(payload));
    }

}