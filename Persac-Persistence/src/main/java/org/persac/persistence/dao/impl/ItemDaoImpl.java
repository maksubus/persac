package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 06.07.2014
 */
public class ItemDaoImpl implements ItemDao {

    private SessionFactory sessionFactory;
    private UserDetails userDetails;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public void save(Item item) {
        item.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().save(item);
    }

    @Override
    public Income getIncomeByID(Integer id) {
        return (Income) sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 1" +
                        " and id = :id" +
                        " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public Outcome getOutcomeByID(Integer id) {
        return (Outcome) sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 2" +
                        " and id = :id" +
                        " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public List<Income> getIncomeByActionDate(Date date) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 1" +
                        " and user.id = :userId" +
                        " and actionDate = : date")
                .setInteger("userId", userDetails.getId())
                .setDate("date", date)
                .list();
    }

    @Override
    public List<Outcome> getOutcomeByActionDate(Date date) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 2" +
                        " and user.id = :userId" +
                        " and actionDate = : date")
                .setInteger("userId", userDetails.getId())
                .setDate("date", date)
                .list();
    }

    @Override
    public List<Income> getAllIncomes() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 1" +
                        " and user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public List<Outcome> getAllOutcomes() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Item where subCategory.parentCategory.id = 2" +
                        " and user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public void update(Item item) {
        item.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().update(item);
    }

    @Override
    public void deleteById(Integer id) {
        sessionFactory.getCurrentSession()
                .createQuery("delete from Item where id = :id" +
                        " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        sessionFactory.getCurrentSession()
                .createQuery("delete from Item where user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }

    @Override
    public List<Income> getIncomesBetweenDates(Date startDate, Date endDate) {
        return sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Item" +
                        " where subCategory.parentCategory.id = 1" +
                        " and actionDate >= :startDate and actionDate <= :endDate" +
                        " and user.id = :userId")
                .setDate("startDate", startDate)
                .setDate("endDate", endDate)
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public List<Outcome> getOutcomesBetweenDates(Date startDate, Date endDate) {
        return sessionFactory.getCurrentSession()
                .createQuery("from org.persac.persistence.model.Item" +
                        " where subCategory.parentCategory.id = 2" +
                        " and actionDate >= :startDate and actionDate <= :endDate" +
                        " and user.id = :userId")
                .setDate("startDate", startDate)
                .setDate("endDate", endDate)
                .setInteger("userId", userDetails.getId())
                .list();
    }

//    @Override
//    public List<Income> getIncomesByMonthDate(Date monthDate) {
//        return null;
//    }
//
//    @Override
//    public List<Outcome> getOutcomesByMonthDate(Date monthDate) {
//        return null;
//    }
//
//    @Override
//    public List<Income> getAllIncomesByMonthAndYear(Integer month, Integer year) {
//        return null;
//    }
//
//    @Override
//    public List<Outcome> getAllOutcomesByMonthAndYear(Integer month, Integer year) {
//        return null;
//    }
//
//    @Override
//    public List<Income> getIncomesAfterDate(String date) {
//        return null;
//    }
//
//    @Override
//    public List<Outcome> getOutcomesAfterDate(String date) {
//        return null;
//    }
}
