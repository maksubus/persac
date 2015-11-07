package org.persac.service.impl;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.model.Asset;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.Month;
import org.persac.service.AssetService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.persac.service.impl.AssetServiceItemActionsTest.BANK_ACCOUNT;
import static org.persac.service.impl.AssetServiceItemActionsTest.WALLET;
import static org.persac.service.util.Constants.USD;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.persac.service.TestConstants.*;
import static org.persac.service.impl.AssetServiceImpl.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AssetServiceImpl.class)
public class AssetServiceAssetActionsTest {

    public static final String BANK_ACCOUNT_ASSET_TYPE_NAME = "Bank Account";

    private AssetDao assetDaoMock;
    private AssetTypeDao assetTypeDaoMock;
    private AssetService assetService;

    @Before
    public void setup() {
        assetDaoMock = mock(AssetDao.class);
        assetTypeDaoMock = mock(AssetTypeDao.class);
        assetService = new AssetServiceImpl(assetDaoMock, assetTypeDaoMock);
    }

    //Cur month exists, last month exists
    //New month must not be created
    //If cur month doesn't have currency assets throw an exception

    //Cur month exists, last month doesn't exist (almost the same as first case -> do not test)
    //New month must not be created
    //If cur month doesn't have currency assets throw an exception

    //Cur month doesn't exist, last month exist
    //1 If last month doesn't have currency assets throw an exception
    //2 If has create current month and currency assets from last month



    @Test
    public void testCreateCurrentAssetsIfNotExist_whenAssetsExist_thenDoNotCreateAssets() {
        List<Asset> currentAssets = new ArrayList<Asset>();
        currentAssets.add(new Asset(new BigDecimal(1000), WALLET));
        currentAssets.add(new Asset(new BigDecimal(5000), BANK_ACCOUNT));
        when(assetDaoMock.getAllCurrentAssets()).thenReturn(currentAssets);

        assetService.createCurrentAssetsIfNotExist();

        verify(assetDaoMock, never()).save(any(Asset.class));
    }

    @Test
    public void testCreateCurrentAssetsIfNotExist_whenAssetsNotExistButLastMonthAssetsExist_thenCreateNewAssets() {
        when(assetDaoMock.getAllCurrentAssets()).thenReturn(Collections.EMPTY_LIST);

        List<Asset> lastMonthAssets = new ArrayList<Asset>();
        lastMonthAssets.add(new Asset(new BigDecimal(1000), WALLET));
        lastMonthAssets.add(new Asset(new BigDecimal(5000), BANK_ACCOUNT));
        when(assetDaoMock.getAllLastMonthAssets()).thenReturn(lastMonthAssets);

        assetService.createCurrentAssetsIfNotExist();

        verify(assetDaoMock, times(2)).save(any(Asset.class));
    }

    //Cur month assets doesn't exist, last month assets doesn't exist
    //Create new test asset from scratch (new user)
    @Test
    public void testCreateCurrentAssetsIfNotExist_whenAssetsNotExistAndLastMonthAssetsNotExist_thenCreateNewTestAsset()
            throws Exception {
        when(assetDaoMock.getAllCurrentAssets()).thenReturn(Collections.<Asset>emptyList());
        when(assetDaoMock.getAllLastMonthAssets()).thenReturn(Collections.<Asset>emptyList());

        AssetType assetType = new AssetType("Test Asset");
        Asset asset = new Asset(new BigDecimal(0), assetType);

        whenNew(Asset.class).withAnyArguments().thenReturn(asset);
        whenNew(AssetType.class).withAnyArguments().thenReturn(assetType);

        assetService.createCurrentAssetsIfNotExist();

        verify(assetDaoMock, times(1)).save(asset);
        assertTrue(assetType.getActive());
        assertEquals(assetType.getCurrencyCode(), USD);
    }

    @Test
    public void testCreateAssetTypeAndAsset_createAssetTypeAndAssetAndRecalculateCurrencyAssets() throws Exception {
        AssetType assetType = new AssetType();
        whenNew(AssetType.class).withAnyArguments().thenReturn(assetType);

        Asset asset = new Asset();
        whenNew(Asset.class).withAnyArguments().thenReturn(asset);

        assetService.createAssetTypeAndAsset(BANK_ACCOUNT_ASSET_TYPE_NAME, new BigDecimal(5000), USD);

        verify(assetTypeDaoMock).save(assetType);
        assertEquals(BANK_ACCOUNT_ASSET_TYPE_NAME, assetType.getName());
        assertEquals(USD, assetType.getCurrencyCode());
        assertEquals(new BigDecimal(5000), asset.getAmount());
    }

    @Test
    public void testDeleteAsset() {}

    @Test
    public void testTransfer() {

    }

    @Test
    public void testExchange_whenNoSufficientFundsOnAsset_thenThrowException() throws Exception {
        Asset sourceAsset = new Asset(new BigDecimal(100), WALLET);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET.getId())).thenReturn(sourceAsset);

        try {
            assetService.exchange(WALLET.getId(), new BigDecimal(500), new BigDecimal(3.7), true, USD, null);
        } catch (Exception e) {
            assertEquals(SOURCE_ASSET_HAS_NO_SUFFICIENT_FUNDS, e.getMessage());
        }
    }

    @Test
    public void testExchange_whenOperationSellAndNoDestinationAsset_thenAmountMultiplyRateAndCreateNewAsset() throws Exception {
        Asset sourceAsset = new Asset(new BigDecimal(200), WALLET);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET.getId())).thenReturn(sourceAsset);

        //assert that new asset created (because of null destination asset
        AssetType newAssetType = new AssetType();
        whenNew(AssetType.class).withAnyArguments().thenReturn(newAssetType);

        Asset newAsset = new Asset();
        whenNew(Asset.class).withAnyArguments().thenReturn(newAsset);

        when(assetTypeDaoMock.save(newAssetType)).thenReturn(newAssetType);

        assetService.exchange(WALLET.getId(), new BigDecimal(100), new BigDecimal(3.7), false, PLN, null);

        assertTrue(new BigDecimal(370).compareTo(newAsset.getAmount()) == 0);
        assertTrue(new BigDecimal(100).compareTo(sourceAsset.getAmount()) == 0);
        assertEquals(PLN, newAsset.getAssetType().getCurrencyCode());
        verify(assetDaoMock).save(newAsset);
    }

    @Test
    public void testExchange_whenOperationBuyAndNoDestinationAsset_thenAmountDivideRateAndCreateNewAsset() throws Exception {
        Asset sourceAsset = new Asset(new BigDecimal(500), WALLET);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET.getId())).thenReturn(sourceAsset);

        //assert that new asset created (because of null destination asset
        AssetType newAssetType = new AssetType();
        whenNew(AssetType.class).withAnyArguments().thenReturn(newAssetType);

        Asset newAsset = new Asset();
        whenNew(Asset.class).withAnyArguments().thenReturn(newAsset);

        when(assetTypeDaoMock.save(newAssetType)).thenReturn(newAssetType);

        assetService.exchange(WALLET.getId(), new BigDecimal(370), new BigDecimal(3.7), true, USD, null);

        assertTrue(new BigDecimal(100).compareTo(newAsset.getAmount()) == 0);
        assertTrue(new BigDecimal(130).compareTo(sourceAsset.getAmount()) == 0);
        verify(assetDaoMock).save(newAsset);
    }

    @Test
    public void testExchange_whenOperationSellAndDestinationAssetPresent_thenAmountMultiplyRateAndChangeDestinationAsset() throws Exception {
        Asset sourceAsset = new Asset(new BigDecimal(200), WALLET);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET.getId())).thenReturn(sourceAsset);

        Asset destinationAsset = new Asset(new BigDecimal(600), BANK_ACCOUNT);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(BANK_ACCOUNT.getId())).thenReturn(destinationAsset);

        assetService.exchange(WALLET.getId(), new BigDecimal(100), new BigDecimal(3.7), false, USD, BANK_ACCOUNT.getId());

        assertTrue(new BigDecimal(100).compareTo(sourceAsset.getAmount()) == 0);
        assertTrue(new BigDecimal(970).compareTo(destinationAsset.getAmount()) == 0);
        verify(assetDaoMock).update(sourceAsset);
        verify(assetDaoMock).update(destinationAsset);
    }

    @Test
    public void testExchange_whenOperationBuyAndDestinationAssetPresent_thenAmountDivideRateAndChangeAsset() throws Exception {
        Asset sourceAsset = new Asset(new BigDecimal(1000), WALLET);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET.getId())).thenReturn(sourceAsset);

        Asset destinationAsset = new Asset(new BigDecimal(700), BANK_ACCOUNT);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(BANK_ACCOUNT.getId())).thenReturn(destinationAsset);

        assetService.exchange(WALLET.getId(), new BigDecimal(555), new BigDecimal(3.7), true, USD, BANK_ACCOUNT.getId());

        assertTrue(new BigDecimal(445D).compareTo(sourceAsset.getAmount()) == 0);
        assertTrue(new BigDecimal(850).compareTo(destinationAsset.getAmount()) == 0);
        verify(assetDaoMock).update(sourceAsset);
        verify(assetDaoMock).update(destinationAsset);
    }
}
