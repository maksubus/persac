package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;

import java.util.List;

/**
 * @author mzhokha
 * @since 11.08.2014
 */
public class AssetTypeDaoImpl implements AssetTypeDao {

    private SessionFactory sessionFactory;
    private UserDetails userDetails;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public AssetType getById(Integer id) {
        return (AssetType) sessionFactory.getCurrentSession()
                .createQuery("from AssetType " +
                    "where id = :id " +
                    " and user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public List<AssetType> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from AssetType " +
                    "where user.id = :userId")
                 .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public List<AssetType> getAllActive() {
        return sessionFactory.getCurrentSession()
                .createQuery("from AssetType " +
                        "where active = 'Y'" +
                        "and user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public AssetType save(AssetType assetType) {
        assetType.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().save(assetType);
        return assetType;
    }

    @Override
    public AssetType update(AssetType assetType) {
        assetType.setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().update(assetType);
        return assetType;
    }
}
