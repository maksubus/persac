package org.persac.service;

import org.persac.persistence.model.Category;
import org.persac.persistence.model.IncomeCategory;
import org.persac.persistence.model.OutcomeCategory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mzhokha
 * @since 28.03.14
 */
@Transactional
public interface CategoryService {

    public List<IncomeCategory> getAllIncomeCategories();
    public List<OutcomeCategory> getAllOutcomeCategories();

    public List<IncomeCategory> getActiveIncomeCategories();
    public List<OutcomeCategory> getActiveOutcomeCategories();

    public void save(Category category);
    public void update(Category category);

    public Map<String, BigDecimal> getOutcomeDataByCategoriesBetweenDates(Date startDate, Date endDate);
    public Map<String, BigDecimal> getOutcomeDataByCategoriesAfterDate(Date date);
    public Map<String, BigDecimal> getOutcomeDataByCategoriesForMonthAndYear(Integer month, Integer year);
    public Map<String, BigDecimal> getOutcomeDataByCategoriesForCurrentMonth();

    public void updateCategoriesActiveStatus(Integer[] inCategoriesIds, Integer[] outCategoriesIds);
}