package com.example.submissions.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.Problem;
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
import static com.example.constants.SessionConstants.USER_KEY;


public class SubmissionsListServlet extends HttpServlet  {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        String problemTitle = pathParts[pathParts.length - 1];

        User user = (User) request.getSession().getAttribute(USER_KEY);
        ProblemDAO problemDAO = (ProblemDAO) getServletContext().getAttribute(PROBLEM_DAO_KEY);
        SubmissionDAO submissionDAO = (SubmissionDAO) getServletContext().getAttribute(SUBMISSION_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        VerdictDAO verdictDAO = (VerdictDAO) getServletContext().getAttribute(VERDICT_DAO_KEY);
        CodeLanguageDAO codeLanguageDAO = (CodeLanguageDAO) getServletContext().getAttribute(CODE_LANGUAGE_DAO_KEY);

        if (user == null) {
            response.setContentType("application/json");
            Map<String, Object> payload = new HashMap<>();
            payload.put("submissions", null);
            response.getWriter().write(gson.toJson(payload));
            return;
        }


        Problem problem = problemDAO.getProblemByTitle(problemTitle);
        List<Submission> submissions = submissionDAO.getSubmissionsByOrder(user.getId(), problem.getId());

        List<SubmissionResponse> submissionResponses =
                submissions.stream().map((submission) -> {


                            return new SubmissionResponse(
                                    submission.getId(),
                                    submission.getSubmitDate(),
                                    user.getUsername(),
                                    submission.getSolutionCode(),
                                    problemTitle,
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
