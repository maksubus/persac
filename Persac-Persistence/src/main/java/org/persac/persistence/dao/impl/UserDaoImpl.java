package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.persac.persistence.dao.UserDao;
import org.persac.persistence.model.User;

/**
 * @author mzhokha
 * @since 09.07.2014
 */
public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getUserByName(String name) {
        return (User) sessionFactory.getCurrentSession().
                createQuery("from org.persac.persistence.model.User where name = :name")
                .setString("name", name)
                .uniqueResult();
    }

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Override
    public void update(User user) {
        sessionFactory.getCurrentSession().update(user);
    }

    @Override
    public User findByEmail(String email) {
        return (User) sessionFactory
                .getCurrentSession()
                .createQuery("from org.persac.persistence.model.User where email = :email")
                .setString("email", email)
                .uniqueResult();
    }


}
