package org.persac.persistence.dao;

import org.persac.persistence.model.Asset;

import java.util.List;

/**
 * @author mzhokha
 * @since 09.08.2014
 */
public interface AssetDao {

    public Asset getById(Integer id);
    public Asset getAssetForCurrentMonthByAssetTypeId(Integer id);
    public Asset getAssetForLastMonthByAssetTypeId(Integer id);
    public List<Asset> getAllCurrentAssets();
    public List<Asset> getAllLastMonthAssets();
    public void save(Asset asset);
    public void update(Asset asset);
    public void deleteById(Integer id);
    public void deleteAll();
    public void updateActiveStatusOfAssetType(Integer id, Boolean activeStatus);

}
