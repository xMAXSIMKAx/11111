package org.example.app.repository.impl;

import org.example.app.entity.User;
import org.example.app.repository.AppRepository;
import org.example.app.utils.Constants;
import org.example.app.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.util.List;
import java.util.Optional;

public class UserRepository implements AppRepository<User> {

    @Override
    public String create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "INSERT INTO User (name, email) VALUES (:name, :email)";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("name", user.getName());
            query.setParameter("email", user.getEmail());
            query.executeUpdate();
            transaction.commit();
            return Constants.DATA_INSERT_MSG;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
            }
            return e.getMessage();
        }
    }

    @Override
    public Optional<List<User>> read() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            List<User> list = session.createQuery("FROM User", User.class).list();
            transaction.commit();
            return Optional.of(list);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String update(User contact) {
        if (readById(contact.getId()).isEmpty()) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                String hql = "UPDATE User SET name = :name, email = :email WHERE id = :id";
                MutationQuery query = session.createMutationQuery(hql);
                query.setParameter("name", contact.getName());
                query.setParameter("email", contact.getEmail());
                query.setParameter("id", contact.getId());
                query.executeUpdate();
                transaction.commit();
                return Constants.DATA_UPDATE_MSG;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                    transaction.rollback();
                }
                return e.getMessage();
            }
        }
    }

    @Override
    public String delete(Long id) {
        if (readById(id).isEmpty()) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                String hql = "DELETE FROM User WHERE id = :id";
                MutationQuery query = session.createMutationQuery(hql);
                query.setParameter("id", id);
                query.executeUpdate();
                transaction.commit();
                return Constants.DATA_DELETE_MSG;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                    transaction.rollback();
                }
                return e.getMessage();
            }
        }
    }

    @Override
    public Optional<User> readById(Long id) {
        Transaction transaction = null;
        Optional<User> optional;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = " FROM User c WHERE c.id = :id";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("id", id);
            optional = query.uniqueResultOptional();
            transaction.commit();
            return optional;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
            }
            return Optional.empty();
        }
    }


}