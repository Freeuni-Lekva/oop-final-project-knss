package com.example.submissions.Response;

import java.sql.Timestamp;

public class SubmissionResponse {
    private final int id;
    private final Timestamp submitDate;
    private final String username;
    private final String solutionCode;
    private final String problemTitle;
    private final String codeLanguage;
    private final String verdict;
    private final long time;
    private final long memory;
    private final String log;


    public SubmissionResponse(int id, Timestamp submitDate, String username, String solutionCode, String problemTitle, String codeLanguage, String verdict, long time, long memory, String log) {
        this.id = id;
        this.submitDate = submitDate;
        this.username = username;
        this.solutionCode = solutionCode;
        this.problemTitle = problemTitle;
        this.codeLanguage = codeLanguage;
        this.verdict = verdict;
        this.time = time;
        this.memory = memory;
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public Timestamp getSubmitDate() {
        return submitDate;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getSolutionCode() {
        return solutionCode;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public String getVerdict() {
        return verdict;
    }

    public long getTime() {
        return time;
    }

    public long getMemory() {
        return memory;
    }
}
