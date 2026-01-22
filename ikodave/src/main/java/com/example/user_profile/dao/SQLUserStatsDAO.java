package com.example.user_profile.dao;

import com.example.problems.DTO.Difficulty;
import com.example.registration.DTO.User;
import com.example.submissions.DTO.SubmissionVerdict;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.user_profile.utils.ToSQL.*;
import static com.example.constants.DatabaseConstants.ProblemVerdictElements.VERDICT_ACCEPTED;
import static java.lang.String.format;

public class SQLUserStatsDAO implements UserStatsDAO {

    private final BasicDataSource basicDataSource;

    public SQLUserStatsDAO(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    @Override
    public int getSubmittedProblemCountByVerdict(User user, SubmissionVerdict verdict) {
        String sqlStatement = getUserSubmittedProblemCountByVerdictSQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, verdict.getId());
            preparedStatement.setInt(2, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying problems by status", e);
        }
        return 0;
    }


    public int getSolvedProblemCount(User user) {
        String sqlStatement = getUserSolvedProblemCountSQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2,VERDICT_ACCEPTED);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error querying accepted problems", e);
        }
    }

    public int getSolvedProblemCountByDifficulty(User user, Difficulty difficulty) {
        String sqlStatement = getUserProblemCountByDifficultySQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, VERDICT_ACCEPTED);
            preparedStatement.setInt(2, difficulty.getId());
            preparedStatement.setInt(3, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error querying accepted problems by difficulty", e);
        }
    }

    public int getSubmissionsCount(User user) {
        String sqlStatement = getUserSubmissionsCountSQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error querying tried problems", e);
        }
    }

    @Override
    public int getSubmissionsCountByDays(User user) {
        String sqlStatement = getUserSubmissionCountByDays();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error querying tried problems", e);
        }
    }

    @Override
    public int getUserRank(User user) {
        String sqlStatement = getUserRankSQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error querying tried problems", e);
        }
    }

    @Override
    public List<Timestamp> getUserActivityByMonth(User user, int month, int year) {
        String sqlStatement = getUserActivityByMonthSQL();
        try (Connection connection = basicDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, user.getId());
            preparedStatement.setString(4, "Accepted");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Timestamp> activityTimestamps = new ArrayList<>();
            while(resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp(1);
                activityTimestamps.add(timestamp);
            }
            return activityTimestamps;

        } catch (SQLException e) {
            throw new RuntimeException("Error querying activity problems", e);
        }
    }

}