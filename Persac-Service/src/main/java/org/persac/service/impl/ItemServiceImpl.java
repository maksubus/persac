package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.dao.AssetTypeDao;
import org.persac.persistence.dao.CategoryDao;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.Category;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.persac.service.ItemService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author mzhokha
 * @since 11/19/13
 */
public class ItemServiceImpl implements ItemService {

    private ItemDao itemDao;

    private CategoryDao categoryDao;

    private AssetTypeDao assetTypeDao;

    public ItemServiceImpl(ItemDao itemDao, CategoryDao categoryDao, AssetTypeDao assetTypeDao) {
        this.itemDao = itemDao;
        this.categoryDao = categoryDao;
        this.assetTypeDao = assetTypeDao;
    }

    @Override
    public List<Income> getAllIncomes() {
        return itemDao.getAllIncomes();
    }

    @Override
    public List<Outcome> getAllOutcomes() {
        return itemDao.getAllOutcomes();
    }

    @Override
    public void save(Item item) {
        checkNotNull(item, "Item must not be null");
        itemDao.save(item);
    }

    @Override
    public Item save(BigDecimal amount, String description, Date actionDate, Integer categoryID, Integer subCategoryID) {
        checkArgument(categoryID.equals(1) || categoryID.equals(2));

        Category category = categoryDao.getById(subCategoryID);

        Item item = null;

        if (categoryID.equals(1)) {
            item = new Income(amount, description, actionDate, category);
        } else if (categoryID.equals(2)) {
            item = new Outcome(amount, description, actionDate, category);
        }

        itemDao.save(item);

        return item;
    }

    @Override
    public Item save(BigDecimal amount,
                     String description,
                     Date actionDate,
                     String categoryName,
                     Integer subCategoryID,
                     Integer assetTypeId) {

        Category category = categoryDao.getById(subCategoryID);
        AssetType assetType = assetTypeDao.getById(assetTypeId);

        Item item = null;

        if (categoryName.equals("Income")) {
            item = new Income(amount, description, actionDate, category, assetType);
        } else if (categoryName.equals("Outcome")) {
            item = new Outcome(amount, description, actionDate, category, assetType);
        }

        itemDao.save(item);

        return item;
    }

    @Override
    public Income getIncomeById(Integer id) {
        checkNotNull(id, "Id must not be null");
        return itemDao.getIncomeByID(id);
    }

    @Override
    public Outcome getOutcomeById(Integer id) {
        checkNotNull(id, "Id must not be null");
        return itemDao.getOutcomeByID(id);
    }

    @Override
    public List<Income> getIncomeByDate(Date date) {
        checkNotNull(date, "Date must not be null");
        return itemDao.getIncomeByActionDate(date);
    }

    @Override
    public List<Outcome> getOutcomeByDate(Date date) {
        checkNotNull(date, "Date must not be null");
        return itemDao.getOutcomeByActionDate(date);
    }

    @Override
    public void update(Item item) {
        checkNotNull(item, "Item must not be null");
        itemDao.update(item);
    }

    @Override
    public Item update(Integer id, BigDecimal amount, String description, Date actionDate, Integer categoryID, Integer subCategoryID) {
        checkNotNull(id);
        checkArgument(categoryID.equals(1) || categoryID.equals(2));

        Item item = null;

        if (categoryID.equals(1)) {
            item = itemDao.getIncomeByID(id);
        } else if (categoryID.equals(2)) {
            item = itemDao.getOutcomeByID(id);
        }

        item.setAmount(amount);
        item.setDescription(description);
        item.setActionDate(actionDate);

        itemDao.update(item);

        return item;
    }

    @Override
    public Item update(Integer id, BigDecimal amount, String description, Date actionDate, String categoryName, Integer subCategoryID, Integer assetTypeId) {
        checkNotNull(id);
        checkNotNull(amount);
        checkNotNull(description);
        checkNotNull(actionDate);
        checkNotNull(categoryName);
        checkNotNull(subCategoryID);
        checkArgument(categoryName.equals("Income") || categoryName.equals("Outcome"));
        checkState(categoryName.equals(categoryDao.getById(subCategoryID).getParentCategory().getName()));

        Item item = null;

        if (categoryName.equals("Income")) {
            item = itemDao.getIncomeByID(id);
        } else if (categoryName.equals("Outcome")) {
            item = itemDao.getOutcomeByID(id);
        }

        Boolean isNewItem = false;
        // if we change base category
        if (item == null) {
            isNewItem = true;
            if (categoryName.equals("Income")) {
                item = new Income();
            } else if (categoryName.equals("Outcome")) {
                item = new Outcome();
            }

            itemDao.deleteById(id);
        }

        item.setAmount(amount);
        item.setDescription(description);
        item.setActionDate(actionDate);
        item.setSubCategory(categoryDao.getById(subCategoryID));
        item.setAssetType(assetTypeDao.getById(assetTypeId));

        if (isNewItem) {
            itemDao.save(item);
        } else {
            itemDao.update(item);
        }

        return item;
    }

    @Override
    public void deleteById(int id) {
        checkNotNull(id, "Id must not be null");
        itemDao.deleteById(id);
    }

    @Override
    public void deleteAll() {
        itemDao.deleteAll();
    }

    @Override
    public List<Income> getIncomesForCurrentWeek() {
        DateTime todayDT = new DateTime(new Date());
        DateTime mondayDT = todayDT.dayOfWeek().withMinimumValue();

        return itemDao.getIncomesBetweenDates(mondayDT.toDate(),
                todayDT.toDate());
    }

    @Override
    public List<Outcome> getOutcomesForCurrentWeek() {
        DateTime todayDT = new DateTime(new Date());
        DateTime mondayDT = todayDT.dayOfWeek().withMinimumValue();

        return itemDao.getOutcomesBetweenDates(mondayDT.toDate(),
                todayDT.toDate());
    }
}
