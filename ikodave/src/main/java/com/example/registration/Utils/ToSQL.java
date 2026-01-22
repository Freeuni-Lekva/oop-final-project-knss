package com.example.registration.Utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToSQL {


    public static String toVerifiedMailExists() {
        return format("SELECT COUNT(*) FROM %s WHERE %s.%s = TRUE AND %s.%s = ?",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_IS_VERIFIED,
                Users.TABLE_NAME,
                Users.COL_MAIL
        );
    }

    public static String toUpdateUserByVerificationCode() {
        return format("UPDATE %s " +
                    "SET %s.%s = TRUE " +
                    "WHERE %s.%s = ? AND %s.%s > NOW();",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_IS_VERIFIED,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN_EXPIRY
        );
    }


    public static String toGetUserByVerificationCode() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN
        );
    }
}
