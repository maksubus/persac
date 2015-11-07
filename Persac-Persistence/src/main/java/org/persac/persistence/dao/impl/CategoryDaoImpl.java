package org.persac.persistence.dao.impl;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.persac.persistence.dao.CategoryDao;
import org.persac.persistence.model.Category;
import org.persac.persistence.model.IncomeCategory;
import org.persac.persistence.model.OutcomeCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mzhokha
 * @since 06.07.2014
 */
public class CategoryDaoImpl implements CategoryDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Category getById(Integer id) {
        return (Category) sessionFactory.getCurrentSession()
                .get(Category.class, id);
    }

    @Override
    public List<IncomeCategory> getAllIncomeCategories() {
        List<Category> categories = sessionFactory.getCurrentSession()
                .createQuery("from Category where parentCategory.id = 1")
                .list();

        List<IncomeCategory> incomeCategories = new ArrayList<IncomeCategory>();

        for (Category category : categories) {
            Hibernate.initialize(category);
            incomeCategories.add(new IncomeCategory(category));
        }

        return incomeCategories;
    }

    @Override
    public List<OutcomeCategory> getAllOutcomeCategories() {
        List<Category> categories = sessionFactory.getCurrentSession()
                .createQuery("from Category where parentCategory.id = 2")
                .list();

        List<OutcomeCategory> outCategories = new ArrayList<OutcomeCategory>();

        for (Category category : categories) {
            Hibernate.initialize(category);
            outCategories.add(new OutcomeCategory(category));
        }

        return outCategories;
    }

    @Override
    public List<IncomeCategory> getActiveIncomeCategories() {
        List<Category> categories = sessionFactory.getCurrentSession()
                .createQuery("from Category where parentCategory.id = 1 and active = :status")
                .setString("status", "Y")
                .list();

        List<IncomeCategory> inCategories = new ArrayList<IncomeCategory>();

        for (Category category : categories) {
            Hibernate.initialize(category);
            inCategories.add(new IncomeCategory(category));
        }

        return inCategories;
    }

    @Override
    public List<OutcomeCategory> getActiveOutcomeCategories() {
        List<Category> categories = sessionFactory.getCurrentSession()
                .createQuery("from Category where parentCategory.id = 2 and active = :status")
                .setString("status", "Y")
                .list();

        List<OutcomeCategory> outCategories = new ArrayList<OutcomeCategory>();

        for (Category category : categories) {
            Hibernate.initialize(category);
            outCategories.add(new OutcomeCategory(category));
        }

        return outCategories;
    }

    @Override
    public void save(Category category) {
        sessionFactory.getCurrentSession().save(category);
    }

    @Override
    public void update(Category category) {
        sessionFactory.getCurrentSession().update(category);
    }

    @Override
    public List<Integer> getAllIds() {
        return sessionFactory.getCurrentSession()
                .createQuery("select id from Category")
                .list();
    }

    @Override
    //TODO: do it for specific user
    public void updateCategoryStatusById(Integer id, Boolean active) {
        sessionFactory.getCurrentSession()
                .createQuery("update Category set active = :active where id = :id")
                .setParameter("active", active)
                .setParameter("id", id)
                .executeUpdate();
    }
}
