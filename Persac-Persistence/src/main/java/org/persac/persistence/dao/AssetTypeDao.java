package org.persac.persistence.dao;

import org.persac.persistence.model.AssetType;

import java.util.List;

/**
 * @author mzhokha
 * @since 11.08.2014
 */
public interface AssetTypeDao {

    public AssetType getById(Integer id);
    public List<AssetType> getAll();
    public List<AssetType> getAllActive();
    public AssetType save(AssetType assetType);
    public AssetType update(AssetType assetType);
}
