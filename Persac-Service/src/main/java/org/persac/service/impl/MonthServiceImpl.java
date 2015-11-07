package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.model.Asset;
import org.persac.persistence.model.CurrencyAsset;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Month;
import org.persac.persistence.model.Outcome;
import org.persac.service.MonthService;
import org.persac.service.util.Constants;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author mzhokha
 * @since 11/30/13
 */
public class MonthServiceImpl extends DateServiceImpl implements MonthService {

    public static final String YOU_MUST_CREATE_ASSET_FIRST = "You must create asset first. And then you can record incomes and outcomes";
    public static final String CANT_FIND_YOUR_CURRENCY_ASSET_DATA = "Can't find your currency asset data";
    private ItemDao itemDao;
    private MonthDao monthDao;
    private AssetDao assetDao;

    public MonthServiceImpl() {
    }

    public MonthServiceImpl(MonthDao monthDao, ItemDao itemDao, AssetDao assetDao) {
        this.monthDao = monthDao;
        this.itemDao = itemDao;
        this.assetDao = assetDao;
    }

    @Override
    public List<Month> getAllMonths() {
        return monthDao.getAll();
    }

    @Override
    public List<Month> getLast5Months() {
        return monthDao.getLast5Months();
    }

    @Override
    public Month getCurrentMonth() {
        return monthDao.getCurrentMonth();
    }

    @Override
    public void createCurrentMonthIfNotExists() {
        Month month = monthDao.getCurrentMonth();

        if (month != null) {
            return;
        }

        month = new Month(new DateTime().dayOfMonth().withMinimumValue().toDateMidnight().toDate());
        Month lastMonth = monthDao.getLastMonth();

        //if last month is null
        //then user is new and create test asset and currency asset for him
        if (lastMonth == null) {
            month.addCurrencyAsset(new CurrencyAsset(month, Constants.USD));
            monthDao.save(month);
            return;
        }

        //if last month is not null
        //if it has currency assets create currency assets from them
        if (lastMonth.getCurrencyAssets().size() > 0) {
            createCurrencyAssetsBasedOnLastMonthCurrencyAssets(month, lastMonth);
        } else {
            //else create currency assets from assets of last month
            /*List<Asset> lastMonthAssets = assetDao.getAllLastMonthAssets();

            if (lastMonthAssets.size() > 0) {
                createCurrencyAssetsBasedOnLastMonthAssets(month, lastMonthAssets);
            } else {
                throw new RuntimeException("Last month must have assets");
            }*/

            throw new RuntimeException(CANT_FIND_YOUR_CURRENCY_ASSET_DATA);
        }

        monthDao.save(month);
    }

    private void createCurrencyAssetsBasedOnLastMonthCurrencyAssets(Month month, Month lastMonth) {
        //New month must have other objects of CurrencyAsset's cause CurrencyAsset is value type.
        // We must not share references of value type objects
        for (CurrencyAsset lastMonthCurrencyAsset : lastMonth.getCurrencyAssets()) {
            CurrencyAsset currencyAsset = new CurrencyAsset();
            currencyAsset.setAmount(lastMonthCurrencyAsset.getAmount());
            currencyAsset.setCurrencyCode(lastMonthCurrencyAsset.getCurrencyCode());

            month.addCurrencyAsset(currencyAsset);
        }
    }

    private void createCurrencyAssetsBasedOnLastMonthAssets(Month month, List<Asset> lastMonthAssets) {
        Set<String> currenciesOwned = new HashSet<String>();
        List<CurrencyAsset> currencyAssets = new ArrayList<CurrencyAsset>();

        for (Asset asset: lastMonthAssets) {
            currenciesOwned.add(asset.getAssetType().getCurrencyCode());
        }

        for (String currencyCode : currenciesOwned) {
            currencyAssets.add(new CurrencyAsset(currencyCode));
        }

        for (CurrencyAsset currencyAsset: currencyAssets) {
            for (Asset lastMonthAsset : lastMonthAssets) {
                if (currencyAsset.getCurrencyCode().equals(lastMonthAsset.getAssetType().getCurrencyCode())) {
                    currencyAsset.addAmount(lastMonthAsset.getAmount());
                }
            }

            month.addCurrencyAsset(currencyAsset);
        }
    }

    @Override
    public void updateMonthAfterCreatingItem(Item item) {
        DateTime itemDT = new DateTime(item.getActionDate());
        Date monthFirstDayDate = itemDT.dayOfMonth().withMinimumValue().toDateMidnight().toDate();

        Month month = monthDao.getByFirstDayDate(monthFirstDayDate);

        //todo: month must not be null at this point. Never.
        if (month == null) {
            month = new Month(monthFirstDayDate);
            month = addAmountToMonthAndUpdateCurrencyAsset(month, item);
            monthDao.save(month);
        } else {
            month = addAmountToMonthAndUpdateCurrencyAsset(month, item);
            monthDao.update(month);
        }
    }

    @Override
    public void updateMonthAfterUpdatingItem(Item newItem, Item oldItem) {
        DateTime newItemDT = new DateTime(newItem.getActionDate());
        DateTime newItemMonthFirstDayDT = newItemDT.dayOfMonth().withMinimumValue();

        DateTime oldItemDT = new DateTime(oldItem.getActionDate());
        DateTime oldItemMonthFirstDayDT = oldItemDT.dayOfMonth().withMinimumValue();

        if (newItemMonthFirstDayDT.equals(oldItemMonthFirstDayDT)) {
            Month month = monthDao.getByFirstDayDate(newItemMonthFirstDayDT.toDate());

            month = subtractAmountFromMonthAndUpdateCurrencyAsset(month, oldItem);
            month = addAmountToMonthAndUpdateCurrencyAsset(month, newItem);
            monthDao.update(month);
        } else {
            Month newItemMonth = monthDao.getByFirstDayDate(newItemMonthFirstDayDT.toDate());
            addAmountToMonthAndUpdateCurrencyAsset(newItemMonth, newItem);
            monthDao.update(newItemMonth);

            Month oldItemMonth = monthDao.getByFirstDayDate(oldItemMonthFirstDayDT.toDate());
            subtractAmountFromMonthAndUpdateCurrencyAsset(oldItemMonth, oldItem);
            monthDao.update(oldItemMonth);
        }
    }

    public void updateMonthBeforeDeletingItem(Item oldItem) {
        DateTime itemDT = new DateTime(oldItem.getActionDate());
        DateTime monthFirstDayDT = itemDT.dayOfMonth().withMinimumValue();

        Month month = monthDao.getByFirstDayDate(monthFirstDayDT.toDate());

        month = subtractAmountFromMonthAndUpdateCurrencyAsset(month, oldItem);

        monthDao.update(month);
    }

    private Month subtractAmountFromMonthAndUpdateCurrencyAsset(Month month, Item oldItem) {
        if (oldItem instanceof Income) {
            month.subtractIncomeAmount(oldItem.getAmount(), oldItem.getAssetType().getCurrencyCode());
            subtractAmountFromCurrencyAsset(month, oldItem);
        } else if (oldItem instanceof Outcome) {
            month.subtractOutcomeAmount(oldItem.getAmount(), oldItem.getAssetType().getCurrencyCode());
            addAmountToCurrencyAssetOrCreateNew(month, oldItem);
        }

        return month;
    }

    private Month addAmountToMonthAndUpdateCurrencyAsset(Month month, Item item) {
        if (item instanceof Income) {
            month.addIncomeAmount(item.getAmount(), item.getAssetType().getCurrencyCode());
            addAmountToCurrencyAssetOrCreateNew(month, item);
        } else if (item instanceof Outcome) {
            month.addOutcomeAmount(item.getAmount(), item.getAssetType().getCurrencyCode());
            subtractAmountFromCurrencyAsset(month, item);
        }

        return month;
    }

    private void addAmountToCurrencyAssetOrCreateNew(Month month, Item item) {
        boolean itemDateIsBeforeCurrentMonth = new DateTime(item.getActionDate()).isBefore(new DateTime().dayOfMonth().withMinimumValue());
        if (!itemDateIsBeforeCurrentMonth) {
            boolean currencyAssetExists = false;
            for (CurrencyAsset currencyAsset: month.getCurrencyAssets()) {
                if (currencyAsset.getCurrencyCode().equals(item.getAssetType().getCurrencyCode())) {
                    currencyAssetExists = true;
                    currencyAsset.addAmount(item.getAmount());
                    break;
                }
            }

            if (!currencyAssetExists) {
                CurrencyAsset currencyAsset = new CurrencyAsset(item.getAmount(), month, item.getAssetType().getCurrencyCode());
                month.getCurrencyAssets().add(currencyAsset);
            }
        }
    }

    private void subtractAmountFromCurrencyAsset(Month month, Item item) {
        boolean itemDateIsBeforeCurrentMonth = new DateTime(item.getActionDate()).isBefore(new DateTime().dayOfMonth().withMinimumValue());
        if (!itemDateIsBeforeCurrentMonth) {
            boolean currencyAssetExists = false;
            for (CurrencyAsset currencyAsset : month.getCurrencyAssets()) {
                if (currencyAsset.getCurrencyCode().equals(item.getAssetType().getCurrencyCode())) {
                    currencyAssetExists = true;
                    currencyAsset.subtractAmount(item.getAmount());
                    break;
                }
            }

            if (!currencyAssetExists) {
                throw new RuntimeException("Currency asset does not exists. How you can spend money if you don't have them.");
            }
        }
    }

    @Override
    public void recalculateAndSaveMonths() {
        List<Income> incomes = itemDao.getAllIncomes();
        List<Outcome> outcomes = itemDao.getAllOutcomes();

        Map<String, DateTime> minMaxDates = getMinAndMaxDate(incomes, outcomes);

        DateTime minDT = minMaxDates.get(MIN);
        DateTime maxDT = minMaxDates.get(MAX);

        Integer minYear = minDT.getYear();
        Integer minMonth = minDT.getMonthOfYear();

        Integer maxYear = maxDT.getYear();
        Integer maxMonth = maxDT.getMonthOfYear();

        List<Month> months = getMonthsList(minYear, minMonth, maxYear, maxMonth);
        months =  fillMonthData(incomes, outcomes, months);

        //TODO: constraint fails, cause incomes and outcomes not deleted but refers to the month
        monthDao.deleteAll();

        for (Month month: months) {
            monthDao.save(month);
        }
    }

    private List<Month> getMonthsList(Integer minYear, Integer minMonth, Integer maxYear, Integer maxMonth) {
        List<Month> months = new ArrayList<Month>();
        Integer yearsCount = maxYear - minYear;

        if (minYear.equals(maxYear)) {
            int currentMonth = minMonth;
            while (currentMonth <= maxMonth) {
                months.add(new Month(minYear, currentMonth));
                currentMonth++;
            }

            return months;
        }

        for (int i = 0; i <= yearsCount; i++) {
            if (i == 0) {
                int currentMonth = minMonth;
                while (currentMonth <= 12) {
                    months.add(new Month(minYear, currentMonth));
                    currentMonth++;
                }
            } else if (i < yearsCount) {
                int currentMonth = 1;
                while (currentMonth <= 12) {
                    months.add(new Month(minYear + i, currentMonth));
                    currentMonth++;
                }
            } else {
                int currentMonth = 1;
                while (currentMonth <= maxMonth) {
                    months.add(new Month(minYear + i, currentMonth));
                    currentMonth++;
                }
            }
        }

        return months;
    }

    private List<Month> fillMonthData(List<Income> incomes, List<Outcome> outcomes, List<Month> months) {
        for (Month month: months) {
            for (Income income : incomes) {
                if (month.getYear().equals(income.getYear()) && month.getMonthOfYear().equals(income.getMonthOfYear())) {
                    month.addIncomeAmount(income.getAmount(), income.getAssetType().getCurrencyCode());
                }
            }

            for (Outcome outcome : outcomes) {
                if (month.getYear().equals(outcome.getYear()) && month.getMonthOfYear().equals(outcome.getMonthOfYear())) {
                    month.addOutcomeAmount(outcome.getAmount(), outcome.getAssetType().getCurrencyCode());
                }
            }
        }

        return months;
    }

    @Override
    public Double getCurrentCapital() {
        return monthDao.getCurrentCapital();
    }

    @Override
    public void recalculateCapital() {
        List<Asset> assets = assetDao.getAllCurrentAssets();

        BigDecimal capital = new BigDecimal(0);
        for(Asset asset: assets) {
            //TODO: implement
        }

        //TODO: refactor cause there is method getCurrentMonth
        Date currentMonthFirstDayDate = new DateTime(new Date()).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Month month = monthDao.getByFirstDayDate(currentMonthFirstDayDate);

        if (month == null) {
            month = new Month(currentMonthFirstDayDate);
            monthDao.save(month);
        }

        month.setCapital(capital);
        monthDao.update(month);
    }

    public void recalculateCurrencyAssets(List<Asset> assets) {
        Set<String> currencies = new LinkedHashSet<String>();

        for (Asset asset: assets) {
            currencies.add(asset.getAssetType().getCurrencyCode());
        }

        Month month = monthDao.getCurrentMonth();
        month.getCurrencyAssets().clear();
        for (String currency: currencies) {
            BigDecimal amount = new BigDecimal(0);

            for (Asset asset: assets) {
                if (asset.getAssetType().getCurrencyCode().equals(currency)) {
                    amount = amount.add(asset.getAmount());
                }
            }

            month.getCurrencyAssets().add(new CurrencyAsset(amount, month, currency));
        }
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public MonthDao getMonthDao() {
        return monthDao;
    }

    public void setMonthDao(MonthDao monthDao) {
        this.monthDao = monthDao;
    }

    public AssetDao getAssetDao() {
        return assetDao;
    }

    public void setAssetDao(AssetDao assetDao) {
        this.assetDao = assetDao;
    }
}
