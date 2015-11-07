package org.persac.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.model.Asset;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 09.08.2014
 */
public class AssetDaoImpl implements AssetDao {

    private SessionFactory sessionFactory;
    private UserDetails userDetails;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public Asset getById(Integer id) {
        return (Asset) sessionFactory.getCurrentSession()
                .createQuery("from Asset where id = :id" +
                        " and assetType.user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public Asset getAssetForCurrentMonthByAssetTypeId(Integer id) {
/*        return (Asset) sessionFactory.getCurrentSession()
                .createQuery("from Asset " +
                    "where Asset.assetType.id = :id " +
                    "and user.id = :userId " +
                    "having Asset.recordDate = max(Asset.recordDate)")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();*/

        return (Asset) sessionFactory.getCurrentSession()
                .createSQLQuery("select * from asset a, asset_type at " +
                        "where a.asset_type_id = at.id " +
                        "and at.id = :id " +
                        "and at.user_id = :userId " +
                        "and month(a.record_date) = month(curdate())")
                .addEntity(Asset.class)
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    public Asset getAssetForLastMonthByAssetTypeId(Integer id) {
        return (Asset) sessionFactory.getCurrentSession()
                .createSQLQuery("select * from asset a, asset_type at " +
                        "where a.asset_type_id = at.id " +
                        "and at.id = :id " +
                        "and at.user_id = :userId " +
                        "and month(a.record_date) = month(curdate()) - 1")
                .addEntity(Asset.class)
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();
    }

    @Override
    public List<Asset> getAllCurrentAssets() {
        return sessionFactory.getCurrentSession()
                .createQuery("select asset " +
                        "from Asset asset " +
                        "join asset.assetType at " +
                        "where asset.assetType.user.id = :userId " +
                        "and month(asset.recordDate) = month(curdate())")
                .setInteger("userId", userDetails.getId())
                .list();
    }

    @Override
    public List<Asset> getAllLastMonthAssets() {
        //get current year
        //get current month
        //if month is january year -1 and month 12
        //else month - 1

        int todayYear = new DateTime().getYear();
        int todayMonth = new DateTime().getMonthOfYear();

        int lastAssetYear = todayYear;
        int lastAssetMonth = todayMonth - 1;

        if (todayMonth == 1) {
            lastAssetYear = todayYear - 1;
            lastAssetMonth = 12;
        }


        return sessionFactory.getCurrentSession()
                .createQuery("select asset " +
                        "from Asset asset " +
                        "join asset.assetType at " +
                        "where asset.assetType.user.id = :userId " +
                        "and month(asset.recordDate) = :lastAssetMonth " +
                        "and year(asset.recordDate) = :lastAssetYear")
                .setInteger("userId", userDetails.getId())
                .setInteger("lastAssetMonth", lastAssetMonth)
                .setInteger("lastAssetYear", lastAssetYear)
                .list();
    }

    @Override
    public void save(Asset asset) {
        if (asset.getRecordDate() == null) {
            asset.setRecordDate(new Date());
        }

        asset.getAssetType().setUser((User) sessionFactory.getCurrentSession().get(User.class, userDetails.getId()));
        sessionFactory.getCurrentSession().save(asset);
    }

    @Override
    public void update(Asset asset) {
        sessionFactory.getCurrentSession().update(asset);
    }

    @Override
    public void deleteById(Integer id) {
        //TODO: refactor, implement deletion by single query
        Integer userId = (Integer) sessionFactory.getCurrentSession().
                createSQLQuery("select user_id from asset_type" +
                        " where id = " +
                        "(select asset_type_id from asset " +
                        " where id = :id)")
                .setInteger("id", id)
                .uniqueResult();

        if (userId.equals(userDetails.getId())) {
            sessionFactory.getCurrentSession()
                    .createQuery("delete from Asset where id = :id")
                    .setInteger("id", id)
                    .executeUpdate();
        }
    }

    @Override
    public void deleteAll() {
        sessionFactory.getCurrentSession()
                .createQuery("delete from Asset where assetType.user.id = :userId")
                .setInteger("userId", userDetails.getId())
                .executeUpdate();
    }

    @Override
    public void updateActiveStatusOfAssetType(Integer id, Boolean activeStatus) {
        Asset asset = (Asset) sessionFactory.getCurrentSession()
                .createQuery("from Asset where id = :id" +
                        " and assetType.user.id = :userId")
                .setInteger("id", id)
                .setInteger("userId", userDetails.getId())
                .uniqueResult();

        AssetType assetType = asset.getAssetType();
        assetType.setActive(activeStatus);
    }
}
