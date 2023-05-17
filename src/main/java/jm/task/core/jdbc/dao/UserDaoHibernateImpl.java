package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
            `id` BIGINT NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(45) NULL,
            `lastname` VARCHAR(45) NULL,
            `age` TINYINT(3) NULL,
            PRIMARY KEY (`id`),
            UNIQUE INDEX `idusers_UNIQUE` (`id` ASC) VISIBLE)
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8;""";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private static final String DELETE_ALL = "DELETE FROM users";
    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        sqlQuery(CREATE_TABLE);
    }

    @Override
    public void dropUsersTable() {
        sqlQuery(DROP_TABLE);
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        createUsersTable();
        User user = new User(name, lastName, age);
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }


    @Override
    public void removeUserById(long id) {
        createUsersTable();
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        createUsersTable();
        List<User> users;
        try (Session session = Util.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            criteriaQuery.from(User.class);
            users = session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        sqlQuery(DELETE_ALL);
    }

    private void sqlQuery(String sql) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery(sql).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
