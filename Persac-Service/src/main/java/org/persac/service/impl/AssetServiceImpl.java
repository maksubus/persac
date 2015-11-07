package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.model.*;
import org.persac.service.AssetService;
import org.persac.service.exception.AssetDeleteException;
import org.persac.service.exception.AssetTransferException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.persac.service.util.Constants.USD;

/**
 * @author mzhokha
 * @since 10.08.2014
 */
public class AssetServiceImpl implements AssetService {

    public static final String SOURCE_ASSET_HAS_NO_SUFFICIENT_FUNDS = "Source asset has no sufficient funds.";

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    public AssetServiceImpl() {
    }

    public AssetServiceImpl(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    public AssetServiceImpl(AssetDao assetDao, AssetTypeDao assetTypeDao) {
        this.assetDao = assetDao;
        this.assetTypeDao = assetTypeDao;
    }

    @Override
    public Asset getById(Integer id) {
        return assetDao.getById(id);
    }

    @Override //todo: write tests for this method
    public void createCurrentAssetsIfNotExist() {
        if (assetDao.getAllCurrentAssets().size() == 0) {
            List<Asset> lastMonthAssets = assetDao.getAllLastMonthAssets();

            Date todayDate = new Date();

            //TODO: can be improved by saving in hibernate batch mode (by one DB hit)
            if (lastMonthAssets.size() > 0) {
                for (Asset lastMonthAsset : lastMonthAssets) {
                    Asset newAsset = new Asset();
                    newAsset.setAmount(lastMonthAsset.getAmount());
                    newAsset.setRecordDate(todayDate);
                    newAsset.setAssetType(lastMonthAsset.getAssetType());

                    assetDao.save(newAsset);
                }
            } else {
                //new user doesn't have any asset
                AssetType assetType = new AssetType("Test Asset");
                assetType.setCurrencyCode(USD);
                assetType.setActive(true);
                Asset newAsset = new Asset(new BigDecimal(0), assetType);
                assetDao.save(newAsset);
            }

        }
    }

    @Override
    public List<Asset> getAllCurrentAssets() {
        return assetDao.getAllCurrentAssets();
    }

    @Override
    public void updateAssetAfterCreatingItem(Item item) {
        Asset asset = assetDao.getAssetForCurrentMonthByAssetTypeId(item.getAssetType().getId());

        DateTime itemDT = new DateTime(item.getActionDate());
        Integer itemMonthOfYear = itemDT.getMonthOfYear();
        Integer itemYear = itemDT.getYear();

        DateTime todayDT = new DateTime();
        Integer currentMonthOfYear = todayDT.getMonthOfYear();
        Integer currentYear = todayDT.getYear();

        if (currentMonthOfYear.equals(itemMonthOfYear) && currentYear.equals(itemYear)) {
            //Item in this month
            //todo: asset must not be null at this point. Never.
            if (asset == null) {
                Asset lastMonthAsset = assetDao.getAssetForLastMonthByAssetTypeId(item.getAssetType().getId());

                asset = makeAsset(lastMonthAsset.getAmount(), item.getAssetType());
                changeAssetAmountAccordingToItemType(asset, item);
                assetDao.save(asset);
            } else {
                asset = changeAssetAmountAccordingToItemType(asset, item);
                assetDao.update(asset);
            }
        } else if ((itemMonthOfYear < currentMonthOfYear && currentYear.equals(itemYear))
                || (itemYear < currentYear)) {
            //TODO: add unit tests for new cases
            //Item in past month
            //Do nothing
        } else {
            throw new IllegalStateException("Date of item has invalid value. It is after current month");
        }
    }

    private Asset changeAssetAmountAccordingToItemType(Asset asset, Item item) {
        if (item instanceof Income) {
            asset.addAmount(item.getAmount());
        } else {
            asset.subtractAmount(item.getAmount());
        }

        return asset;
    }

    public Asset makeAsset(BigDecimal amount, AssetType assetType) {
        return new Asset(amount, assetType);
    }

    public void updateAssetAfterUpdatingItem(Item oldItem, Item newItem) {
        AssetType oldItemAssetType = oldItem.getAssetType();
        AssetType newItemAssetType = newItem.getAssetType();

        Asset oldItemAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(oldItemAssetType.getId());
        Asset newItemAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(newItemAssetType.getId());

        DateTime oldItemActionDateDT = new DateTime(oldItem.getActionDate());
        DateTime newItemActionDateDT = new DateTime(newItem.getActionDate());
        DateTime currentDT = new DateTime();

        Integer oldItemMonthOfYear = oldItemActionDateDT.getMonthOfYear();
        Integer newItemMonthOfYear = newItemActionDateDT.getMonthOfYear();
        Integer currentMonthOfYear = currentDT.getMonthOfYear();

        Integer oldItemYear = oldItemActionDateDT.getYear();
        Integer newItemYear = newItemActionDateDT.getYear();
        Integer currentYear = currentDT.getYear();

        Boolean oldItemIsBeforeCurrentMonth = oldItemYear < currentYear ||
                (oldItemMonthOfYear < currentMonthOfYear && oldItemYear.equals(currentYear));

        Boolean oldItemIsInCurrentMonth = oldItemYear.equals(currentYear) &&
                oldItemMonthOfYear.equals(currentMonthOfYear);


        Boolean newItemIsBeforeCurrentMonth = newItemYear < currentYear ||
                (newItemMonthOfYear < currentMonthOfYear && newItemYear.equals(currentYear));

        Boolean newItemIsInCurrentMonth = newItemYear.equals(currentYear) &&
                newItemMonthOfYear.equals(currentMonthOfYear);

        if (oldItemIsInCurrentMonth && newItemIsInCurrentMonth) {
            //Both item is in current month
            if (oldItem instanceof Income) {
                oldItemAsset.subtractAmount(oldItem.getAmount());
            } else {
                oldItemAsset.addAmount(oldItem.getAmount());
            }

            changeAssetAmountAccordingToItemType(newItemAsset, newItem);

            assetDao.update(oldItemAsset);

            //if asset is not changed during editing (oldItemAsset and newItemAsset are the same)
            //do not need to update second time the same instance of asset
            if (oldItemAsset.equals(newItemAsset)) {
                return;
            }

            assetDao.update(newItemAsset);

        } else if (oldItemIsInCurrentMonth && newItemIsBeforeCurrentMonth) {
            //Item changed from current month to past month
            if (oldItem instanceof Income) {
                oldItemAsset.subtractAmount(oldItem.getAmount());
            } else {
                oldItemAsset.addAmount(oldItem.getAmount());
            }

            assetDao.update(oldItemAsset);

        } else if (oldItemIsBeforeCurrentMonth && newItemIsInCurrentMonth) {
            //Item changed from past month to current month
            changeAssetAmountAccordingToItemType(newItemAsset, newItem);

            assetDao.update(newItemAsset);

        } else if (oldItemIsBeforeCurrentMonth && newItemIsBeforeCurrentMonth) {
            //Item changed from past month to past month
            //Do nothing
        } else {
            throw new IllegalStateException("Dates of items has invalid values");
        }
    }

    public void updateAssetBeforeDeletingItemIfCurrentMonthItem(Item oldItem) {
        DateTime oldItemActionDateDT = new DateTime(oldItem.getActionDate());

        Integer oldItemMonthOfYear = oldItemActionDateDT.getMonthOfYear();
        Integer currentMonthOfYear = new DateTime().getMonthOfYear();

        if (currentMonthOfYear.equals(oldItemMonthOfYear)) {
            Asset oldItemAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(oldItem.getAssetType().getId());

            if (oldItem instanceof Income) {
                oldItemAsset.subtractAmount(oldItem.getAmount());
            } else {
                oldItemAsset.addAmount(oldItem.getAmount());
            }

            //TODO: seems to be this call is redundant. Hibernate updates entity automatically
            assetDao.update(oldItemAsset);
        }
    }

    @Override
    public void updateAssetAfterEditing(Asset asset) {
        assetDao.update(asset);
    }

    @Override
    public void transfer(Integer fromAssetTypeId, Integer toAssetTypeId, BigDecimal amount) throws AssetTransferException {
        Asset sourceAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(fromAssetTypeId);
        Asset destinationAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(toAssetTypeId);

        if (sourceAsset.getAmount().subtract(amount).doubleValue() < 0) {
            throw new AssetTransferException(SOURCE_ASSET_HAS_NO_SUFFICIENT_FUNDS);
        }

        sourceAsset.subtractAmount(amount);
        destinationAsset.addAmount(amount);

        assetDao.update(sourceAsset);
        assetDao.update(destinationAsset);
    }

    @Override
    public void createAssetTypeAndAsset(String assetTypeName, BigDecimal amount, String currencyCode) {
        //asset type name must be unique or not

        AssetType assetType = new AssetType();
        assetType.setName(assetTypeName);
        assetType.setCurrencyCode(currencyCode);
        assetType.setActive(true);

        assetType = assetTypeDao.save(assetType);

        Asset asset;
        if (amount == null) {
            asset = new Asset();
            asset.setAssetType(assetType);

        } else {
            asset = new Asset();
            asset.setAssetType(assetType);
            asset.setAmount(amount);
        }
        asset.setRecordDate(new Date());

        assetDao.save(asset);
    }

    public void deleteAsset(Integer id) throws AssetDeleteException {
        Asset asset = assetDao.getById(id);

        if (asset.getAmount().doubleValue() != 0) {
            throw new AssetDeleteException("Cannot delete asset with non zero amount");
        }

        assetDao.deleteById(id);
    }

    @Override
    public void deactivateAsset(Integer id) {
        assetDao.updateActiveStatusOfAssetType(id, false);
    }

    @Override
    public void activateAsset(Integer id) {
        assetDao.updateActiveStatusOfAssetType(id, true);
    }

    @Override
    public void exchange(Integer sourceAssetTypeIdForExchange,
                         BigDecimal amount,
                         BigDecimal rate,
                         boolean isBuyingOperation,
                         String currencyCode,
                         Integer destAssetTypeIdForExchange) {
          //get asset by id
        Asset sourceAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(sourceAssetTypeIdForExchange);
         //check if it has enough money
        if (sourceAsset.getAmount().subtract(amount).doubleValue() < 0) {
            throw new RuntimeException(SOURCE_ASSET_HAS_NO_SUFFICIENT_FUNDS);
        }

        //multiply amount to rate (depending on currencies --> can be division)
        BigDecimal resultAmount;
        if (isBuyingOperation) {
            resultAmount = amount.divide(rate, BigDecimal.ROUND_HALF_UP);
        } else {
            resultAmount = amount.multiply(rate);
        }
        resultAmount = resultAmount.setScale(2, BigDecimal.ROUND_HALF_UP);

        //if destination asset type id is not null then transfer money to it
        if (destAssetTypeIdForExchange != null) {
            Asset destinationAsset = assetDao.getAssetForCurrentMonthByAssetTypeId(destAssetTypeIdForExchange);
            destinationAsset.addAmount(resultAmount);
            //TODO: maybe redundant
            assetDao.update(destinationAsset);
        } else {
            createAssetTypeAndAsset("Money(" + currencyCode + ") after exchange ", resultAmount, currencyCode);
        }

        sourceAsset.subtractAmount(amount);
        //TODO: maybe redundant
        assetDao.update(sourceAsset);
    }

    public AssetDao getAssetDao() {
        return assetDao;
    }

    public void setAssetDao(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    public AssetTypeDao getAssetTypeDao() {
        return assetTypeDao;
    }

    public void setAssetTypeDao(AssetTypeDao assetTypeDao) {
        this.assetTypeDao = assetTypeDao;
    }
}
