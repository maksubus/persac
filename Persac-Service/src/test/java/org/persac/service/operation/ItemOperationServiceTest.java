package org.persac.service.operation;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.dao.CategoryDao;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.dao.WeekDao;
import org.persac.persistence.model.*;
import org.persac.service.AssetService;
import org.persac.service.ItemService;
import org.persac.service.MonthService;
import org.persac.service.WeekService;
import org.persac.service.impl.AssetServiceImpl;
import org.persac.service.impl.ItemServiceImpl;
import org.persac.service.impl.MonthServiceImpl;
import org.persac.service.impl.WeekServiceImpl;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.persac.service.util.Constants.INCOME;
import static org.persac.service.TestConstants.*;

/**
 * @author mzhokha
 * @since 09.02.2015
 */
public class ItemOperationServiceTest {

    public static final int SALARY_SUB_CATEGORY_ID = 3;

    public static final int WALLET_ASSET_TYPE_ID = 1;

    private ItemOperationService itemOperationService;

    private ItemService itemService;

    private WeekService weekService;

    private MonthService monthService;

    private AssetService assetService;

    private ItemDao itemDaoMock;

    private CategoryDao categoryDaoMock;

    private WeekDao weekDaoMock;

    private MonthDao monthDaoMock;

    private AssetDao assetDaoMock;

    private AssetTypeDao assetTypeDaoMock;

    @Before
    public void setUp() {
        itemDaoMock = mock(ItemDao.class);
        categoryDaoMock = mock(CategoryDao.class);
        weekDaoMock = mock(WeekDao.class);
        monthDaoMock = mock(MonthDao.class);
        assetDaoMock = mock(AssetDao.class);
        assetTypeDaoMock = mock(AssetTypeDao.class);

        itemService = new ItemServiceImpl(itemDaoMock, categoryDaoMock, assetTypeDaoMock);
        weekService = new WeekServiceImpl(weekDaoMock, itemDaoMock);
        monthService = new MonthServiceImpl(monthDaoMock, itemDaoMock, assetDaoMock);
        assetService = new AssetServiceImpl(assetDaoMock);

        itemOperationService = new ItemOperationService(itemService, weekService, monthService, assetService);
    }

    @Test
    /**
     * When item is income and is of current date then save new item, recalculate current month incomes,
     * assets, currency assets, current week incomes
     *
     * Too heavy test. Tests all stuff in one test method.
     */
    public void testSaveNewItem_whenItemIsOfCurrentDate_thanSaveItemAndRecalculateWeekAndMonthIncomes() throws Exception {
        Date currentDate = new Date();

        IncomeCategory salaryIncomeCategory = new IncomeCategory("Salary");
        when(categoryDaoMock.getById(SALARY_SUB_CATEGORY_ID)).thenReturn(salaryIncomeCategory);

        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        when(assetTypeDaoMock.getById(WALLET_ASSET_TYPE_ID)).thenReturn(walletAssetType);

        Asset walletAsset = new Asset(new BigDecimal(10000), walletAssetType);
        when(assetDaoMock.getAssetForCurrentMonthByAssetTypeId(WALLET_ASSET_TYPE_ID)).thenReturn(walletAsset);

        Date currentWeekMondayDate = new DateTime(currentDate).dayOfWeek().withMinimumValue().toDate();
        Week currentWeek = new Week(currentWeekMondayDate);
        when(weekDaoMock.getByMondayDate(currentWeekMondayDate)).thenReturn(currentWeek);

        Date currentMonthBeginDate = new DateTime(currentDate).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month currentMonth = new Month(currentMonthBeginDate);
        currentMonth.getIncomes().add(new MonthIncome(currentMonth, PLN, new BigDecimal(200)));
        currentMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(10000), currentMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(currentMonthBeginDate)).thenReturn(currentMonth);

        itemOperationService.saveNewItem(INCOME, SALARY_SUB_CATEGORY_ID, WALLET_ASSET_TYPE_ID, new BigDecimal(6000), "My salary", currentDate);

        verify(itemDaoMock).save(any(Income.class));

        assertEquals(new BigDecimal(6200), currentMonth.getIncomes().iterator().next().getAmount().doubleValue());

        CurrencyAsset polishZlotyAsset = new CurrencyAsset(new BigDecimal(16000), currentMonth, PLN);
        assertEquals(polishZlotyAsset, currentMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(currentMonth);
    }
}
