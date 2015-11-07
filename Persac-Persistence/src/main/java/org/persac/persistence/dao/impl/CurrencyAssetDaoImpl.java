package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.persac.persistence.dao.CurrencyAssetDao;
import org.persac.persistence.model.CurrencyAsset;
import org.persac.persistence.model.UserDetails;

import java.util.List;

/**
 * @author mzhokha
 * @since 27.01.2015
 */
public class CurrencyAssetDaoImpl implements CurrencyAssetDao {

    private SessionFactory sessionFactory;
    private UserDetails userDetails;

    public CurrencyAssetDaoImpl(SessionFactory sessionFactory, UserDetails userDetails) {
        this.sessionFactory = sessionFactory;
        this.userDetails = userDetails;
    }

    @Override
    public CurrencyAsset getById(Integer id) {
        return (CurrencyAsset) sessionFactory.getCurrentSession().get(CurrencyAsset.class, id);
    }

    @Override
    public List<CurrencyAsset> getAllCurrencyAssets() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    @Override
    public void save(CurrencyAsset currencyAsset) {

    }

    @Override
    public void update(CurrencyAsset currencyAsset) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}
