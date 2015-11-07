package org.persac.service.impl;

import com.google.common.collect.ObjectArrays;
import org.joda.time.DateTime;
import org.persac.persistence.dao.CategoryDao;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.model.Category;
import org.persac.persistence.model.IncomeCategory;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.persac.persistence.model.OutcomeCategory;
import org.persac.service.CategoryService;
import org.persac.service.util.DateUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mzhokha
 * @since 28.03.14
 */
public class CategoryServiceImpl implements CategoryService {

    private CategoryDao categoryDao;
    private ItemDao itemDao;

    public CategoryServiceImpl(CategoryDao categoryDao, ItemDao itemDao) {
        this.categoryDao = categoryDao;
        this.itemDao = itemDao;
    }

    @Override
    public List<IncomeCategory> getAllIncomeCategories() {
        return categoryDao.getAllIncomeCategories();
    }

    @Override
    public List<OutcomeCategory> getAllOutcomeCategories() {
        return categoryDao.getAllOutcomeCategories();
    }

    @Override
    public List<IncomeCategory> getActiveIncomeCategories() {
        return categoryDao.getActiveIncomeCategories();
    }

    @Override
    public List<OutcomeCategory> getActiveOutcomeCategories() {
        return categoryDao.getActiveOutcomeCategories();
    }

    @Override
    public void save(Category category) {
        categoryDao.save(category);
    }

    @Override
    public void update(Category category) {
        categoryDao.update(category);
    }

    @Override
    public Map<String, BigDecimal> getOutcomeDataByCategoriesBetweenDates(Date startDate, Date endDate) {
        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(startDate, endDate);
        List<OutcomeCategory> outCategories = categoryDao.getAllOutcomeCategories();

        Map<String, BigDecimal> categorizedData = new HashMap<String, BigDecimal>();
        for (OutcomeCategory outcomeCategory : outCategories) {
            BigDecimal amount = getAmountForCategory(outcomeCategory.getName(), outcomes);
            categorizedData.put(outcomeCategory.getName(), amount);
        }

        return categorizedData;
    }

    @Override
    public Map<String, BigDecimal> getOutcomeDataByCategoriesAfterDate(Date date) {
        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(date, new Date());
        List<OutcomeCategory> outCategories = categoryDao.getAllOutcomeCategories();

        Map<String, BigDecimal> categorizedData = new HashMap<String, BigDecimal>();
        for (OutcomeCategory outCategory: outCategories) {
            BigDecimal amount = getAmountForCategory(outCategory.getName(), outcomes);
            categorizedData.put(outCategory.getName(), amount);
        }

        return categorizedData;
    }

    @Override
    public Map<String, BigDecimal> getOutcomeDataByCategoriesForMonthAndYear(Integer month, Integer year) {
        DateUtil dateUtil = new DateUtil();
        dateUtil.calculateStartAndEndDatesForMonthAndYear(month, year);

        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(dateUtil.getStartDate(), dateUtil.getEndDate());
        List<OutcomeCategory> outCategories = categoryDao.getAllOutcomeCategories();

        Map<String, BigDecimal> categorizedData = new HashMap<String, BigDecimal>();
        for (OutcomeCategory outCategory: outCategories) {
            BigDecimal amount = getAmountForCategory(outCategory.getName(), outcomes);
            categorizedData.put(outCategory.getName(), amount);
        }

        return categorizedData;
    }

    @Override
    public Map<String, BigDecimal> getOutcomeDataByCategoriesForCurrentMonth() {
        DateUtil dateUtil = new DateUtil();
        dateUtil.calculateStartAndEndDatesForMonth(new DateTime(new Date()));

        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(dateUtil.getStartDate(), dateUtil.getEndDate());
        List<OutcomeCategory> outCategories = categoryDao.getAllOutcomeCategories();

        Map<String, BigDecimal> categorizedData = new HashMap<String, BigDecimal>();
        for (OutcomeCategory outCategory: outCategories) {
            BigDecimal amount = getAmountForCategory(outCategory.getName(), outcomes);
            categorizedData.put(outCategory.getName(), amount);
        }

        return categorizedData;
    }

    @Override
    public void updateCategoriesActiveStatus(Integer[] inCategories, Integer[] outCategories) {
        //TODO: maybe get from UI one array instead of two
        Integer[] fromUIIdsArray = ObjectArrays.concat(inCategories, outCategories, Integer.class);
        List<Integer> fromUIIds = Arrays.asList(fromUIIdsArray);

        List<Integer> categoriesIds = categoryDao.getAllIds();

        for (Integer id: categoriesIds) {
            categoryDao.updateCategoryStatusById(id, false);

            if (fromUIIds.contains(id)) {
                categoryDao.updateCategoryStatusById(id, true);
            }
        }
    }

    private BigDecimal getAmountForCategory(String categoryName, List<? extends Item> items) {
        BigDecimal amount = new BigDecimal(0);
        for (Item item: items) {
            if (item.getSubCategory().getName().equals(categoryName)) {
                amount = amount.add(item.getAmount());
            }
        }
        return amount;
    }
}