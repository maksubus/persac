package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.persac.persistence.dao.RoleDao;
import org.persac.persistence.model.Role;

/**
 * Created by maks on 13.10.15.
 */
public class RoleDaoImpl implements RoleDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role getById(int id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Role loadById(int id) {
        return (Role) sessionFactory.getCurrentSession().load(Role.class, id);
    }
}
