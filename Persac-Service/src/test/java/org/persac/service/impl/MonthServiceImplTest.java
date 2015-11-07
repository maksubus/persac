package org.persac.service.impl;

import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.model.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.persac.service.impl.MonthServiceImpl.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.persac.service.TestConstants.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MonthServiceImpl.class)
public class MonthServiceImplTest {

    public static final BigDecimal AMOUNT_1000 = new BigDecimal(1000);

    private MonthDao monthDaoMock;
    private AssetDao assetDaoMock;
    private MonthServiceImpl monthService;

    @Before
    public void setUp() throws Exception {
        monthDaoMock = mock(MonthDao.class);
        assetDaoMock = mock(AssetDao.class);

        monthService = new MonthServiceImpl();
        monthService.setMonthDao(monthDaoMock);
        monthService.setAssetDao(assetDaoMock);
    }

    @After
    public void tearDown() throws Exception {

    }

    //CREATE MONTH

    //Cur month exists, last month exists
    //New month must not be created
    //If cur month doesn't have currency assets throw an exception

    //Cur month exists, last month doesn't exist (almost the same as first case -> do not test)
    //New month must not be created
    //If cur month doesn't have currency assets throw an exception
    @Test
    public void testCreateCurrentMonthIfNotExists_whenMonthExists_doNotCreateMonth() throws Exception {
        Month month = new Month();
        month.addCurrencyAsset(new CurrencyAsset(AMOUNT_1000, month, USD));

        when(monthDaoMock.getCurrentMonth()).thenReturn(month);

        monthService.createCurrentMonthIfNotExists();

        verify(monthDaoMock, never()).save(any(Month.class));
    }

    @Test
    public void testCreateCurrentMonthIfNotExists_whenMonthExistsButNoCurrencyAssets_throwException()
            throws Exception {

        Month month = new Month();
        when(monthDaoMock.getCurrentMonth()).thenReturn(month);

        try {
            monthService.createCurrentMonthIfNotExists();
        } catch (Exception e) {
            assertEquals(CANT_FIND_YOUR_CURRENCY_ASSET_DATA, e.getMessage());
        }
    }

    //Cur month doesn't exist, last month exist
    //1 If last month doesn't have currency assets throw an exception
    //2 If has create current month and currency assets from last month

    @Test
    public void testCreateCurrentMonthIfNotExists_whenMonthNotExistsAndLastMonthHasNoCurrencyAssets_thenThrowException() throws Exception {
        Date lastMonthBeginDate = new DateTime().minusMonths(1).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month lastMonth = new Month(lastMonthBeginDate);
        when(monthDaoMock.getLastMonth()).thenReturn(lastMonth);

        when(assetDaoMock.getAllLastMonthAssets()).thenReturn(Collections.<Asset>emptyList());

        //TODO: fix here. If no exception thrown test doesn't fail but must
        try {
            monthService.createCurrentMonthIfNotExists();
        } catch (Exception e) {
            assertEquals(CANT_FIND_YOUR_CURRENCY_ASSET_DATA, e.getMessage());
        }
    }

    @Test
    public void testCreateCurrentMonthIfNotExists_whenMonthNotExists_thenCreateMonthAndCurrencyAssets() throws Exception {
        Date currentMonthBeginDate = new DateTime().dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        when(monthDaoMock.getCurrentMonth()).thenReturn(null);

        Date lastMonthBeginDate = new DateTime().minusMonths(1).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month lastMonth = new Month(lastMonthBeginDate);
        lastMonth.getCurrencyAssets().add(new CurrencyAsset(AMOUNT_1000, lastMonth, EUR));
        lastMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(7000), lastMonth, USD));
        lastMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(3560), lastMonth, PLN));
        when(monthDaoMock.getLastMonth()).thenReturn(lastMonth);

        Month newMonth = new Month(currentMonthBeginDate);
        whenNew(Month.class).withAnyArguments().thenReturn(newMonth);

        monthService.createCurrentMonthIfNotExists();

        assertEquals(3, newMonth.getCurrencyAssets().size());
        verify(monthDaoMock).save(newMonth);
    }

    /*@Test
    public void testCreateCurrentMonthIfNotExists_whenMonthNotExistsAndLastMonthDoesntHaveCurrencyAssets_thenCreateMonthAndCurrencyAssets() throws Exception{
        Date currentMonthBeginDate = new DateTime().dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        when(monthDaoMock.getCurrentMonth()).thenReturn(null);

        Date lastMonthBeginDate = new DateTime().minusMonths(1).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month lastMonth = new Month(lastMonthBeginDate);
        when(monthDaoMock.getLastMonth()).thenReturn(lastMonth);

        List<Asset> lastMonthAssets = new ArrayList<Asset>();
        lastMonthAssets.add(new Asset(new BigDecimal(2000), new AssetType(1, "Wallet", PLN)));
        lastMonthAssets.add(new Asset(new BigDecimal(4500), new AssetType(2, "Wallet1", PLN)));
        lastMonthAssets.add(new Asset(new BigDecimal(7000), new AssetType(3, "Bank account", USD)));
        lastMonthAssets.add(new Asset(new BigDecimal(1111), new AssetType(4, "Home", EUR)));
        lastMonthAssets.add(new Asset(new BigDecimal(3444), new AssetType(5, "Home1", EUR)));
        when(assetDaoMock.getAllLastMonthAssets()).thenReturn(lastMonthAssets);

        Month newMonth = new Month(currentMonthBeginDate);
        whenNew(Month.class).withAnyArguments().thenReturn(newMonth);

        monthService.createCurrentMonthIfNotExists();

        assertEquals(3, newMonth.getCurrencyAssets().size());

        for (CurrencyAsset currencyAsset: newMonth.getCurrencyAssets()) {
            if (currencyAsset.getCurrencyCode().equals(PLN)) {
                assertEquals(new BigDecimal(6500D), currencyAsset.getAmount());
            } else if (currencyAsset.getCurrencyCode().equals(EUR)) {
                assertEquals(new BigDecimal(4555D), currencyAsset.getAmount());
            }
        }
        verify(monthDaoMock).save(newMonth);
    }*/

    //Cur month doesn't exist, last month doesn't exist
    //Create new month and currency asset from scratch (new user)
    @Test
    public void testCreateCurrentMonthIfNotExists_whenMonthNotExistAndLastMonthNotExist_thenCreateMonthAndCurrencyAsset() throws Exception {
        when(monthDaoMock.getCurrentMonth()).thenReturn(null);
        when(monthDaoMock.getLastMonth()).thenReturn(null);

        Date currentMonthBeginDate = new DateTime().dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month newMonth = new Month(currentMonthBeginDate);
        CurrencyAsset currencyAsset = new CurrencyAsset(newMonth, USD);

        whenNew(Month.class).withAnyArguments().thenReturn(newMonth);
        whenNew(CurrencyAsset.class).withAnyArguments().thenReturn(currencyAsset);

        monthService.createCurrentMonthIfNotExists();

        assertTrue(newMonth.getCurrencyAssets().size() == 1);
        assertEquals(newMonth.getCurrencyAssets().iterator().next(), currencyAsset);
        verify(monthDaoMock).save(newMonth);
    }

    //UPDATE MONTH

    @Test
    public void testUpdateMonthAfterCreatingItem_createIncome_whenItemInCurrentMonth_thenRecalculateIncomesAndCurrencyAssets() {
        Date todayDate = new Date();
        Date firstDayOfCurrentMonthDT = new DateTime(todayDate).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month currentMonth = new Month(firstDayOfCurrentMonthDT);
        currentMonth.getIncomes().add(new MonthIncome(currentMonth, PLN, new BigDecimal(200)));
        currentMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), currentMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(firstDayOfCurrentMonthDT)).thenReturn(currentMonth);

        Income income = new Income(new BigDecimal(6000), "My salary", todayDate, new IncomeCategory("Salary"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        income.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(income);

        TestCase.assertEquals(new BigDecimal(6200), currentMonth.getIncomes().iterator().next().getAmount());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(7000), currentMonth, PLN), currentMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(currentMonth);
    }

    @Test
    public void testUpdateMonthAfterCreatingItem_createIncome_whenItemInCurrentMonthAndThereAreNoIncomesInCurrentMonth_thenCreateIncomeAndRecalculateCurrencyAssets() {
        Date todayDate = new Date();
        Date firstDayOfCurrentMonthDT = new DateTime(todayDate).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month currentMonth = new Month(firstDayOfCurrentMonthDT);
        currentMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), currentMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(firstDayOfCurrentMonthDT)).thenReturn(currentMonth);

        Income income = new Income(new BigDecimal(6000), "My salary", todayDate, new IncomeCategory("Salary"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        income.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(income);

        TestCase.assertEquals(new BigDecimal(6000), currentMonth.getIncomes().iterator().next().getAmount());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(7000), currentMonth, PLN), currentMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(currentMonth);
    }

    @Test
    public void testUpdateMonthAfterCreatingItem_createOutcome_whenItemInCurrentMonth_thenRecalculateIncomesAndCurrencyAssets() {
        Date todayDate = new Date();
        Date firstDayOfCurrentMonthDT = new DateTime(todayDate).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month currentMonth = new Month(firstDayOfCurrentMonthDT);
        currentMonth.getOutcomes().add(new MonthOutcome(currentMonth, PLN, new BigDecimal(200)));
        currentMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), currentMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(firstDayOfCurrentMonthDT)).thenReturn(currentMonth);

        Outcome outcome = new Outcome(new BigDecimal(100), "Bought a monthly tram ticket", todayDate, new OutcomeCategory("Transport"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        outcome.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(outcome);

        TestCase.assertEquals(300D, currentMonth.getOutcomes().iterator().next().getAmount().doubleValue());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(900), currentMonth, PLN), currentMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(currentMonth);
    }

    @Test
    public void testUpdateMonthAfterCreatingItem_createOutcome_whenItemInCurrentMonthAndThereAreNoOutcomesInCurrentMonth_thenCreateOutcomeAndRecalculateCurrencyAssets() {
        Date todayDate = new Date();
        Date firstDayOfCurrentMonthDT = new DateTime(todayDate).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month currentMonth = new Month(firstDayOfCurrentMonthDT);
        currentMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), currentMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(firstDayOfCurrentMonthDT)).thenReturn(currentMonth);

        Outcome outcome = new Outcome(new BigDecimal(100), "Bought a monthly tram ticket", todayDate, new OutcomeCategory("Transport"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        outcome.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(outcome);

        TestCase.assertEquals(new BigDecimal(100), currentMonth.getOutcomes().iterator().next().getAmount());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(900), currentMonth, PLN), currentMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(currentMonth);
    }


        @Test
    public void testUpdateMonthAfterCreatingItem_createIncome_whenItemInLastMonth_thenRecalculateIncomesAndIgnoreCurrencyAssets() {
        DateTime todayDT = new DateTime();
        Date previousMonthBeginDate = todayDT.minusMonths(1).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month previousMonth = new Month(previousMonthBeginDate);
        previousMonth.getIncomes().add(new MonthIncome(previousMonth, PLN, new BigDecimal(200)));
        previousMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), previousMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(previousMonthBeginDate)).thenReturn(previousMonth);

        DateTime previousMonthOccasionalDay;
        if (todayDT.equals(todayDT.dayOfMonth().withMaximumValue())) {
            previousMonthOccasionalDay = todayDT.minusMonths(1).dayOfMonth().getDateTime().minusDays(2);
        } else if (todayDT.equals(todayDT.dayOfMonth().withMinimumValue())) {
            previousMonthOccasionalDay = todayDT.minusMonths(1).dayOfMonth().getDateTime().plusDays(2);
        } else {
            previousMonthOccasionalDay = todayDT.minusMonths(1);
        }

        Income income = new Income(new BigDecimal(6000), "My salary", previousMonthOccasionalDay.toDate(), new IncomeCategory("Salary"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        income.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(income);

        TestCase.assertEquals(new BigDecimal(6200), previousMonth.getIncomes().iterator().next().getAmount());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(1000), previousMonth, PLN), previousMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(previousMonth);
    }

    @Test
    public void testUpdateMonthAfterCreatingItem_createOutcome_whenItemInLastMonth_thenRecalculateOutcomesAndIgnoreCurrencyAssets() {
        DateTime todayDT = new DateTime();
        Date previousMonthBeginDate = todayDT.minusMonths(1).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month previousMonth = new Month(previousMonthBeginDate);
        previousMonth.getOutcomes().add(new MonthOutcome(previousMonth, PLN, new BigDecimal(200)));
        previousMonth.getCurrencyAssets().add(new CurrencyAsset(new BigDecimal(1000), previousMonth, PLN));
        when(monthDaoMock.getByFirstDayDate(previousMonthBeginDate)).thenReturn(previousMonth);

        DateTime previousMonthOccasionalDay;
        if (todayDT.equals(todayDT.dayOfMonth().withMaximumValue())) {
            previousMonthOccasionalDay = todayDT.minusMonths(1).dayOfMonth().getDateTime().minusDays(2);
        } else if (todayDT.equals(todayDT.dayOfMonth().withMinimumValue())) {
            previousMonthOccasionalDay = todayDT.minusMonths(1).dayOfMonth().getDateTime().plusDays(2);
        } else {
            previousMonthOccasionalDay = todayDT.minusMonths(1);
        }

        Outcome outcome = new Outcome(new BigDecimal(100), "Bought a monthly tram ticket", previousMonthOccasionalDay.toDate(), new OutcomeCategory("Transport"));
        AssetType walletAssetType = new AssetType(1, "Wallet");
        walletAssetType.setCurrencyCode(PLN);
        outcome.setAssetType(walletAssetType);

        monthService.updateMonthAfterCreatingItem(outcome);

        TestCase.assertEquals(300D, previousMonth.getOutcomes().iterator().next().getAmount().doubleValue());

        TestCase.assertEquals(new CurrencyAsset(new BigDecimal(1000), previousMonth, PLN), previousMonth.getCurrencyAssets().iterator().next());
        verify(monthDaoMock).update(previousMonth);
    }
}