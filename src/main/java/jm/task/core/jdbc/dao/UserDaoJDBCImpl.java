package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
            `id` BIGINT NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(45) NULL,
            `lastname` VARCHAR(45) NULL,
            `age` TINYINT(3) NULL,
            PRIMARY KEY (`id`),
            UNIQUE INDEX `idusers_UNIQUE` (`id` ASC) VISIBLE)
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8;""";
    private final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private final String INSERT_INTO = "INSERT INTO users (name,lastname,age) VALUES (?,?,?)";
    private final String DELETE_BY_ID = "DELETE FROM users WHERE id=?";
    private final String SELECT_ALL = "SELECT * FROM users";
    private final String DELETE_ALL = "DELETE FROM users";

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        statement(CREATE_TABLE);
    }


    public void dropUsersTable() {
        statement(DROP_TABLE);
    }

    public void saveUser(String name, String lastName, byte age) {
        createUsersTable();
        try (Connection connection = Util.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setByte(3, age);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void removeUserById(long id) {
        createUsersTable();
        try (Connection connection = Util.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)) {
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }


    public List<User> getAllUsers() {
        createUsersTable();
        List<User> users = new ArrayList<>();
        User user;
        try (Connection connection = Util.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                user = new User(resultSet.getString("name"), resultSet.getString("lastname"),
                        resultSet.getByte("age"));
                user.setId(resultSet.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return users;
    }

    public void cleanUsersTable() {
        createUsersTable();
        statement(DELETE_ALL);
    }

    private void statement(String action) {
        try (Connection connection = Util.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(action)) {
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
