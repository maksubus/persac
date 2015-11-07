package org.persac.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.model.*;
import org.persac.service.AssetService;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

public class AssetServiceItemActionsTest {

    public static final String ITEM_DESCRIPTION = "Item description";

    public static final AssetType BANK_ACCOUNT = new AssetType(1, "Bank account");
    public static final AssetType WALLET = new AssetType(2, "Wallet");

    public static final IncomeCategory INCOME_SUB_CATEGORY = new IncomeCategory();

    public static final OutcomeCategory OUTCOME_SUB_CATEGORY = new OutcomeCategory();

    private AssetDao assetDaoMock;
    private AssetService assetService;

    @Before
    public void setup() {
        assetDaoMock = mock(AssetDao.class);
        assetService = new AssetServiceImpl(assetDaoMock);
    }

    //SAVING NEW ITEM

    //TODO: add check for currency assets updating

    @Test
    public void testUpdateAssetAfterCreatingItem_whenSaveIncomeAndThereIsNoAsset_thenSaveNewAssetAndAddIncomeAmount() throws Exception {
        Item item = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset lastMonthAsset = new Asset(new BigDecimal(1000), BANK_ACCOUNT);
        Asset currentMonthAsset = new Asset(lastMonthAsset.getAmount(), BANK_ACCOUNT);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(null);
        when(assetDaoMock.getAssetForLastMonthByAssetTypeId(1)).thenReturn(lastMonthAsset);

        AssetServiceImpl assetServiceImpl = new AssetServiceImpl(assetDaoMock);
        AssetServiceImpl assetServiceSpy = spy(assetServiceImpl);
        when(assetServiceSpy.makeAsset(any(BigDecimal.class), any(AssetType.class))).thenReturn(currentMonthAsset);

        assetServiceSpy.updateAssetAfterCreatingItem(item);

        verify(assetDaoMock).save(currentMonthAsset);
        assertEquals(item.getAmount().add(lastMonthAsset.getAmount()), currentMonthAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterCreatingItem_whenSaveOutcomeAndThereIsNoAsset_thenSaveNewAssetAndSubtractOutcomeAmount() throws Exception {
        Item item = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset lastMonthAsset = new Asset(new BigDecimal(1000), BANK_ACCOUNT);
        Asset currentMonthAsset = new Asset(lastMonthAsset.getAmount(), BANK_ACCOUNT);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(null);
        when(assetDaoMock.getAssetForLastMonthByAssetTypeId(1)).thenReturn(lastMonthAsset);

        AssetServiceImpl assetServiceImpl = new AssetServiceImpl(assetDaoMock);
        AssetServiceImpl assetServiceSpy = spy(assetServiceImpl);
        when(assetServiceSpy.makeAsset(any(BigDecimal.class), any(AssetType.class))).thenReturn(currentMonthAsset);

        assetServiceSpy.updateAssetAfterCreatingItem(item);

        verify(assetDaoMock).save(currentMonthAsset);
        assertEquals(lastMonthAsset.getAmount().subtract(item.getAmount()), currentMonthAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterCreatingItem_whenSaveIncomeAndAssetAlreadyExists_thenAddIncomeAmountAndUpdateAsset() throws Exception {
        Item item = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset currentMonthAsset = new Asset(new BigDecimal(1000), BANK_ACCOUNT);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(currentMonthAsset);

        assetService.updateAssetAfterCreatingItem(item);

        verify(assetDaoMock).update(currentMonthAsset);
        assertEquals(new BigDecimal(1150), currentMonthAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterCreatingItem_whenSaveOutcomeAndAssetAlreadyExists_thenSubtractOutcomeAmountAndUpdateAsset() throws Exception {
        Item item = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset currentMonthAsset = new Asset(new BigDecimal(1000), BANK_ACCOUNT);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(currentMonthAsset);

        assetService.updateAssetAfterCreatingItem(item);

        verify(assetDaoMock).update(currentMonthAsset);
        assertEquals(currentMonthAsset.getAmount(), new BigDecimal(850D));
    }

    //EDITING IN CURRENT MONTH

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenUpdatingIncomeInCurrentMonth_thenSubtractOldAmountAndAddNewAmount() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, WALLET);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, WALLET);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1250), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenUpdatingOutcomeInCurrentMonth_thenAddOldAmountAndSubtractNewAmount() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1050), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingFromIncomeToOutcomeInCurrentMonth_thenSubtractOldIncomeAmountAndSubtractNewOutcomeAmount() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(750), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingFromOutcomeToIncomeInCurrentMonth_thenAddOldOutcomeAmountAndAddNewIncomeAmount() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1550), walletAsset.getAmount());
    }

    //EDITING FROM CURRENT MONTH TO PAST MONTH

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInCurrentMonthToIncomeInPastMonth_thenSubtractOldIncomeAmountFromCurrentMonthAsset() throws Exception {
        Item oldItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(900), walletAsset.getAmount());
    }

    //TODO: need to add test when changing from current month to past year

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInCurrentMonthToOutcomeInPastMonth_thenAddOldOutcomeAmountToCurrentMonthAsset() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1400), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInCurrentMonthToOutcomeInPastMonth_thenSubtractOldIncomeAmountFromCurrentMonthAsset() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1000), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInCurrentMonthToIncomeInPastMonth_thenAddOldIncomeAmountToCurrentMonthAsset() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1300D), walletAsset.getAmount());
    }

    //EDITING FROM PAST MONTH TO CURRENT MONTH

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInPastMonthToIncomeInCurrentMonth_thenAddNewIncomeAmountToCurrentMonthAsset() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1400D), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInPastMonthToOutcomeInCurrentMonth_thenSubtractNewOutcomeAmountFromCurrentMonthAsset() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, WALLET);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, WALLET);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(900D), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInPastMonthToOutcomeInCurrentMonth_thenSubtractNewOutcomeAmountFromCurrentMonthAsset() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), OUTCOME_SUB_CATEGORY, WALLET);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, WALLET);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(900D), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInPastMonthToIncomeInCurrentMonth_thenAddNewIncomeAmountToCurrentMonthAsset() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), OUTCOME_SUB_CATEGORY, WALLET);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, WALLET);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1400D), walletAsset.getAmount());
    }
    
    //EDITING FROM PAST MONTH TO PAST MONTH

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInPastMonthToIncomeInPastMonth_thenDoNothing() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, WALLET);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), INCOME_SUB_CATEGORY, WALLET);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock, never()).update(walletAsset);
        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingIncomeInPastMonthToOutcomeInPastMonth_thenDoNothing() throws Exception {
        Item oldItem = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock, never()).update(walletAsset);
        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInPastMonthToOutcomeInPastMonth_thenDoNothing() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Outcome(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);

        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock, never()).update(walletAsset);
        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetAfterUpdatingItem_whenChangingOutcomeInPastMonthToIncomeInPastMonth_thenDoNothing() throws Exception {
        Item oldItem = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(2).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Item newItem = new Income(new BigDecimal(250), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock, never()).update(walletAsset);
        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }

    //DELETING ITEM

    @Test
    public void testUpdateAssetBeforeDeletingItemIfCurrentMonthItem_whenDeletingIncomeInCurrentMonth_thenSubtractAmount() throws Exception {
        Item item = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetBeforeDeletingItemIfCurrentMonthItem(item);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1000), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetBeforeDeletingItemIfCurrentMonthItem_whenDeletingOutcomeInCurrentMonth_thenAddAmount() throws Exception {
        Item item = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new Date(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetBeforeDeletingItemIfCurrentMonthItem(item);

        verify(assetDaoMock).update(walletAsset);
        assertEquals(new BigDecimal(1300D), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetBeforeDeletingItemIfCurrentMonthItem_whenDeletingIncomeInPastMonth_thenDoNothing() throws Exception {
        Item item = new Income(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), INCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetBeforeDeletingItemIfCurrentMonthItem(item);

        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }

    @Test
    public void testUpdateAssetBeforeDeletingItemIfCurrentMonthItem_whenDeletingOutcomeInPastMonth_thenDoNothing() throws Exception {
        Item item = new Outcome(new BigDecimal(150), ITEM_DESCRIPTION, new DateTime().minusMonths(3).toDate(), OUTCOME_SUB_CATEGORY, BANK_ACCOUNT);
        Asset walletAsset = new Asset(new BigDecimal(1150), WALLET);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(walletAsset);

        assetService.updateAssetBeforeDeletingItemIfCurrentMonthItem(item);

        assertEquals(new BigDecimal(1150), walletAsset.getAmount());
    }
}