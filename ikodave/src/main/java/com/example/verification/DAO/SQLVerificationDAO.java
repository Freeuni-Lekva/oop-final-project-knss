package com.example.verification.DAO;

import com.example.registration.DTO.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.problems.utils.ToDTO.toUser;
import static com.example.registration.Utils.ToSQL.toGetUserByVerificationCode;
import static com.example.registration.Utils.ToSQL.toUpdateUserByVerificationCode;
import static com.example.verification.utils.ToSQL.toRemoveTimedOutVerifications;
import static com.example.verification.utils.ToSQL.toUpdateUserVerificationCode;

public class SQLVerificationDAO implements VerificationDAO {

    private final BasicDataSource basicDataSource;

    public SQLVerificationDAO(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    @Override
    public void removeTimedOutVerifications() {
        try (Connection connection = basicDataSource.getConnection()) {
            String sqlStatement = toRemoveTimedOutVerifications();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByVerificationCode(String verificationCode) {
        String sqlStatement = toGetUserByVerificationCode();
        try (Connection connection = basicDataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, verificationCode);
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
    public User updateUserVerificationCode(User user, String verificationCode) {
        try (Connection connection = basicDataSource.getConnection()) {
            String sqlStatement = toUpdateUserVerificationCode();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, verificationCode);
            preparedStatement.setInt(2, user.getId());
            int updates = preparedStatement.executeUpdate();
            if (updates == 0) {
                return null;
            }
            return getUserByVerificationCode(verificationCode);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User updateUserByVerificationCode(String verificationCode) {
        try (Connection connection = basicDataSource.getConnection()) {
            User user = getUserByVerificationCode(verificationCode);
            if (user == null) {
                return null;
            }
            String updateSQL = toUpdateUserByVerificationCode();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, verificationCode);
            int update = preparedStatement.executeUpdate();
            if (update == 0) {
                return null;
            }
            user.setIsVerified(true);
            return user;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
