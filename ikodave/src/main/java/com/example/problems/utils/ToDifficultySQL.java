package com.example.problems.utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToDifficultySQL {

    public static String toProblemDifficultiesSQL() {
        return format("SELECT * FROM %s;", ProblemDifficulty.TABLE_NAME);
    }

    public static String toProblemDifficultiesByIdSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                ProblemDifficulty.TABLE_NAME,
                ProblemDifficulty.TABLE_NAME,
                ProblemDifficulty.COL_ID);
    }

    public static String toProblemDifficultiesByDifficultyNameSQL() {
        return format("SELECT * FROM %s WHERE LOWER(%s) = LOWER(?);",
                ProblemDifficulty.TABLE_NAME,
                ProblemDifficulty.COL_DIFFICULTY);
    }

}
