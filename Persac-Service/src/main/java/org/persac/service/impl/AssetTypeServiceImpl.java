package org.persac.service.impl;

import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.model.AssetType;
import org.persac.service.AssetTypeService;

import java.util.List;

/**
 * @author mzhokha
 * @since 11.08.2014
 */
public class AssetTypeServiceImpl implements AssetTypeService {

    private AssetTypeDao assetTypeDao;

    public void setAssetTypeDao(AssetTypeDao assetTypeDao) {
        this.assetTypeDao = assetTypeDao;
    }

    @Override
    public List<AssetType> getAllAssetTypes() {
        return assetTypeDao.getAll();
    }

    @Override
    public List<AssetType> getAllActiveAssetTypes() {
        return assetTypeDao.getAllActive();
    }
}
