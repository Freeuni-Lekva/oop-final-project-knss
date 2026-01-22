package com.example.verification.utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToSQL {

    public static String toRemoveTimedOutVerifications() {
        return format("DELETE FROM %s WHERE %s.%s = FALSE AND %s.%s < NOW()  LIMIT 100",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_IS_VERIFIED,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN_EXPIRY
        );
    }

    public static String toUpdateUserVerificationCode() {
        return format("UPDATE %s SET %s.%s = ?, %s.%s = NOW() + INTERVAL 2 MINUTE WHERE %s.%s = ?",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN,
                Users.TABLE_NAME,
                Users.COL_VERIFICATION_TOKEN_EXPIRY,
                Users.TABLE_NAME,
                Users.COL_ID
        );
    }


}
