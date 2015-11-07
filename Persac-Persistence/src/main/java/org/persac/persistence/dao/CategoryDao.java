package org.persac.persistence.dao;

import org.persac.persistence.model.Category;
import org.persac.persistence.model.IncomeCategory;
import org.persac.persistence.model.OutcomeCategory;

import java.util.List;

/**
 * @author mzhokha
 * @since 28.03.14
 */
public interface CategoryDao {

    public Category getById(Integer id);

    public List<IncomeCategory> getAllIncomeCategories();
    public List<OutcomeCategory> getAllOutcomeCategories();

    public List<IncomeCategory> getActiveIncomeCategories();
    public List<OutcomeCategory> getActiveOutcomeCategories();

    public void save(Category category);
    public void update(Category category);
    public List<Integer> getAllIds();
    public void updateCategoryStatusById(Integer id, Boolean active);
}
