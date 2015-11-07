package org.persac.persistence.dao.impl;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.persac.persistence.dao.WeekDao;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;
import org.persac.persistence.model.Week;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 06.07.2014
 */
public class WeekDaoImpl implements WeekDao {

    private SessionFactory sessionFactory;
    private UserDetails userDetails;


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public boolean save(Week week) {
        week.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        return ((Integer) sessionFactory.getCurrentSession().save(week)) != 0;
    }

    @Override
    public Week getById(int id) {
        return (Week) sessionFactory.getCurrentSession()
                .createQuery("from Week where id = :id" +
                        " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId());
    }

    @Override
    public List<Week> getLast5Weeks() {
        List<Week> weeks =  sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Week" +
                        " where user.id = :userId" +
                        " order by mondayDate desc")
                .setInteger("userId", userDetails.getId())
                .setMaxResults(5)
                .list();

        return Lists.reverse(weeks);
    }

    @Override
    public List<Week> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Week" +
                        " where user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public Week getByMondayDate(Date mondayDate) {
        return (Week) sessionFactory.getCurrentSession()
                .getNamedQuery("getWeekByMondayDate")
                .setInteger("userId", userDetails.getId())
                .setDate("date", mondayDate)
                .uniqueResult();
    }

    @Override
    public void update(Week week) {
        week.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().update(week);
    }

    @Override
    //TODO: return int or boolean
    public void deleteById(Integer id) {
        sessionFactory.getCurrentSession()
                .createQuery("delete from org.persac.persistence.model.Week" +
                        " where id = :id" +
                        " and userId = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        sessionFactory.getCurrentSession()
                .createQuery("delete from org.persac.persistence.model.Week" +
                    " where user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }
}
