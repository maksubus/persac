package org.persac.service;

import org.persac.persistence.model.AssetType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author mzhokha
 * @since 11.08.2014
 */
public interface AssetTypeService {

    @Transactional
    public List<AssetType> getAllAssetTypes();

    @Transactional
    public List<AssetType> getAllActiveAssetTypes();
}
