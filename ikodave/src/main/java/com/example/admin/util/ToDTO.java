package com.example.admin.util;

import com.example.admin.dto.Role;
import com.example.constants.DatabaseConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ToDTO {
    public static Role toRole(ResultSet rs) {
        try {
            Role role = new Role();
            role.setId(rs.getInt(DatabaseConstants.UserRole.COL_ID));
            role.setRole(rs.getString(DatabaseConstants.UserRole.COL_ROLE));
            return role;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
