package org.persac.service;

import org.persac.persistence.model.Asset;
import org.persac.persistence.model.Item;
import org.persac.service.exception.AssetDeleteException;
import org.persac.service.exception.AssetTransferException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mzhokha
 * @since 10.08.2014
 */
public interface AssetService {

    @Transactional
    public Asset getById(Integer id);

    @Transactional
    public void createCurrentAssetsIfNotExist();

    @Transactional
    public List<Asset> getAllCurrentAssets();

    public void updateAssetAfterCreatingItem(Item item);
    public void updateAssetAfterUpdatingItem(Item oldItem, Item newItem);
    public void updateAssetBeforeDeletingItemIfCurrentMonthItem(Item oldItem);

    @Transactional
    public void updateAssetAfterEditing(Asset asset);

    @Transactional
    public void transfer(Integer fromAssetTypeId, Integer toAssetTypeId, BigDecimal amount) throws AssetTransferException;

    @Transactional
    public void createAssetTypeAndAsset(String assetTypeName, BigDecimal amount, String currencyCode);

    @Transactional
    public void deleteAsset(Integer id) throws AssetDeleteException;

    @Transactional
    public void deactivateAsset(Integer id);

    @Transactional
    public void activateAsset(Integer id);

    @Transactional
    public void exchange(Integer sourceAssetTypeIdForExchange,
                         BigDecimal amount,
                         BigDecimal rate,
                         boolean isBuyingOperation,
                         String currencyCode,
                         Integer destAssetTypeIdForExchange);
}
