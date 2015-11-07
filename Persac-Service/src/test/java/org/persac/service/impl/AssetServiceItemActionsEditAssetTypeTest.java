/*
package org.persac.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.model.Asset;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.persac.service.AssetService;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.persac.service.TestConstants.INCOME_CATEGORY;
import static org.persac.service.TestConstants.OUTCOME_CATEGORY;
import static org.persac.service.TestConstants.UAH;
import static org.persac.service.TestConstants.USD;

*/
/**
 * Created by maks on 27.06.2015.
 *
 * All following tests are for testing editing item when asset type is changed
 * All tests in here are for case when item(income/outcome) is changed from current month date to current month date
 * All other cases are tested in AssetServiceItemActionsTest
 * The only method of AssetService which is tested in here is updateAssetAfterUpdatingItem() method
 *//*

public class AssetServiceItemActionsEditAssetTypeTest {

    public static final String ITEM_DESCRIPTION = "Item description";

    public static final AssetType IZMAIL_UAH = new AssetType(1, "Izmail UAH", UAH);
    public static final AssetType HOME_USD = new AssetType(2, "Home USD", USD);    

    private AssetDao assetDaoMock;
    private AssetService assetService;

    @Before
    public void setup() {
        assetDaoMock = mock(AssetDao.class);
        assetService = new AssetServiceImpl(assetDaoMock);
    }

    //EDITING IN CURRENT MONTH

    @Test
    public void whenUpdatingIncomeInCurrentMonth_thenSubtractOldAmountAndAddNewAmount() throws Exception {
        Item oldItem = new Income(150D, ITEM_DESCRIPTION, new Date(), INCOME_CATEGORY, IZMAIL_UAH);
        Item newItem = new Income(250D, ITEM_DESCRIPTION, new Date(), INCOME_CATEGORY, HOME_USD);

        Asset izmailUah = new Asset(40000D, IZMAIL_UAH);
        Asset homeUsd = new Asset(10040D, HOME_USD);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(izmailUah);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(homeUsd);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(izmailUah);
        verify(assetDaoMock).update(homeUsd);
        assertEquals(Double.valueOf(10290D), homeUsd.getAmount());
        assertEquals(Double.valueOf(39850D), izmailUah.getAmount());
    }

    @Test
    public void whenUpdatingOutcomeInCurrentMonth_thenAddOldAmountAndSubtractNewAmount() throws Exception {
        Item oldItem = new Outcome(150D, ITEM_DESCRIPTION, new Date(), OUTCOME_CATEGORY, IZMAIL_UAH);
        Item newItem = new Outcome(250D, ITEM_DESCRIPTION, new Date(), OUTCOME_CATEGORY, HOME_USD);

        Asset izmailUah = new Asset(40000D, IZMAIL_UAH);
        Asset homeUsd = new Asset(10040D, HOME_USD);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(izmailUah);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(homeUsd);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(izmailUah);
        verify(assetDaoMock).update(homeUsd);
        assertEquals(Double.valueOf(9790D), homeUsd.getAmount());
        assertEquals(Double.valueOf(40150D), izmailUah.getAmount());
    }

    @Test
    public void whenChangingFromIncomeToOutcomeInCurrentMonth_thenSubtractOldIncomeAmountAndSubtractNewOutcomeAmount() throws Exception {
        Item oldItem = new Income(150D, ITEM_DESCRIPTION, new Date(), INCOME_CATEGORY, IZMAIL_UAH);
        Item newItem = new Outcome(250D, ITEM_DESCRIPTION, new Date(), OUTCOME_CATEGORY, HOME_USD);

        Asset izmailUah = new Asset(40000D, IZMAIL_UAH);
        Asset homeUsd = new Asset(10040D, HOME_USD);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(izmailUah);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(homeUsd);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(izmailUah);
        verify(assetDaoMock).update(homeUsd);
        assertEquals(Double.valueOf(9790D), homeUsd.getAmount());
        assertEquals(Double.valueOf(39850D), izmailUah.getAmount());
    }

    @Test
    public void whenChangingFromOutcomeToIncomeInCurrentMonth_thenAddOldOutcomeAmountAndAddNewIncomeAmount() throws Exception {
        Item oldItem = new Outcome(250D, ITEM_DESCRIPTION, new Date(), INCOME_CATEGORY, IZMAIL_UAH);
        Item newItem = new Income(150D, ITEM_DESCRIPTION, new Date(), INCOME_CATEGORY, HOME_USD);

        Asset izmailUah = new Asset(40000D, IZMAIL_UAH);
        Asset homeUsd = new Asset(10040D, HOME_USD);

        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(1)).thenReturn(izmailUah);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(2)).thenReturn(homeUsd);

        assetService.updateAssetAfterUpdatingItem(oldItem, newItem);

        verify(assetDaoMock).update(izmailUah);
        verify(assetDaoMock).update(homeUsd);
        assertEquals(Double.valueOf(10190D), homeUsd.getAmount());
        assertEquals(Double.valueOf(40250D), izmailUah.getAmount());
    }
}
*/
