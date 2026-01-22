package com.example.registration.dao;

import com.example.registration.DTO.User;
import com.example.constants.DatabaseConstants.*;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.problems.utils.ToDTO.toUser;
import static com.example.registration.Utils.ToSQL.*;
import static java.lang.String.format;

public class SQLUserDAO implements UserDAO {
    private final BasicDataSource dataSource;

    public SQLUserDAO(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User getUserById(int userId) {
        String sqlStatement = format("SELECT * FROM %s WHERE %s.%s = ?;",
                    Users.TABLE_NAME,
                    Users.TABLE_NAME,
                    Users.COL_ID
        );

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return toUser(resultSet);

            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User addUser(User user) {
        try (Connection con = dataSource.getConnection()){
            String sql = format("INSERT INTO users " +
                            "(%s, %s, %s, %s, %s, %s, %s) " +
                            "VALUES (2, ?, ?, ?, ?, NOW() + INTERVAL 2 MINUTE, NOW())",
                    Users.COL_ROLE_ID,
                    Users.COL_MAIL,
                    Users.COL_USERNAME,
                    Users.COL_PASSWORD_HASH,
                    Users.COL_VERIFICATION_TOKEN,
                    Users.COL_VERIFICATION_TOKEN_EXPIRY,
                    Users.COL_REGISTER_DATE);
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getMail());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getVerificationCode());

            int updates = preparedStatement.executeUpdate();
            if (updates == 0) {
                return null;
            }
            return getUserByUsername(user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    @Override
    public boolean authenticate(User user) {
        try (Connection con = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error authenticating user", e);
        }
        return false;
    }

    @Override
    public void deleteUser(String username) {
        try(Connection con = dataSource.getConnection()) {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
    @Override
    public boolean usernameExists(String username) {
        try (Connection con = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if user exists", e);
        }
    }

    @Override
    public boolean verifiedMailExists(String mail) {
        try (Connection connection = dataSource.getConnection()) {
            String sqlStatement = toVerifiedMailExists();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, mail);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        String sqlStatement = format("SELECT * FROM %s WHERE %s.%s = ?;",
                    Users.TABLE_NAME,
                    Users.TABLE_NAME,
                    Users.COL_USERNAME
        );

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return toUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByMail(String mail) {
        String sqlStatement = format("SELECT * FROM %s WHERE %s.%s = ?;",
                Users.TABLE_NAME,
                Users.TABLE_NAME,
                Users.COL_MAIL
        );

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, mail);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return toUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
