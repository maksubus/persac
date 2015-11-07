package org.persac.persistence.dao.impl;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.model.Month;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 06.07.2014
 */
public class MonthDaoImpl implements MonthDao{

    private SessionFactory sessionFactory;
    private UserDetails userDetails;


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public Integer save(Month month) {
        month.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        return ((Integer) sessionFactory.getCurrentSession().save(month));
    }

    @Override
    public Month getById(int id) {
        return (Month) sessionFactory.getCurrentSession()
                .createQuery("from Month where id = :id" +
                        " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId());
    }

    @Override
    public List<Month> getLast5Months() {
        List<Month> months = sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Month" +
                        " where user.id = :userId" +
                        " order by beginDate desc")
                .setInteger("userId", userDetails.getId())
                .setMaxResults(5)
                .list();

        return Lists.reverse(months);
    }

    @Override
    public Month getCurrentMonth() {
        Month month = (Month) sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Month " +
                        " where user.id = :userId " +
                        " and beginDate = :date")
                .setInteger("userId", userDetails.getId())
                .setDate("date", new DateTime().dayOfMonth().withMinimumValue().toDateMidnight().toDate())
                .uniqueResult();

        return month;
    }

    @Override
    public Month getLastMonth() {
        Integer id = (Integer) sessionFactory.getCurrentSession()
                .createSQLQuery("select id from month m " +
                        " where m.user_id = :userId " +
                        " and m.begin_date = " +
                        "(select max(begin_date) from month where user_id = :userId)")
                .setInteger("userId", userDetails.getId())
                .uniqueResult();

        if (id == null) {
            return null;
        }

        return (Month) sessionFactory.getCurrentSession()
                .get(Month.class, id);
    }

    @Override
    public List<Month> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Month" +
                        " where user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public Month getByFirstDayDate(Date date) {
        return (Month) sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Month" +
                        " where user.id = :userId" +
                        " and beginDate = :date")
                .setInteger("userId", userDetails.getId())
                .setDate("date", date)
                .uniqueResult();
    }

    @Override
    @Deprecated
    public Month getByYearAndMonthNumber(Date date) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Double getCurrentCapital() {
        return (Double) sessionFactory.getCurrentSession()
                .createQuery("select capital from Month " +
                        "where user.id = :userId " +
                        "and beginDate = (select max(beginDate) from Month)")
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public void update(Month month) {
        month.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().update(month);
    }

    @Override
    public void deleteById(int id) {
        sessionFactory.getCurrentSession()
                .createQuery("delete from org.persac.persistence.model.Month" +
                        " where user.id = :userId" +
                        " and id = :id")
                .setInteger("userId", userDetails.getId())
                .setInteger("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        sessionFactory.getCurrentSession()
                .createQuery("delete from org.persac.persistence.model.Month" +
                        " where user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }
}
