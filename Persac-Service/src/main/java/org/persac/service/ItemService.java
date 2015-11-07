package org.persac.service;

import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 11/19/13
 */
public interface ItemService {

    public void save(Item item);
    public Item save(BigDecimal amount, String description, Date actionDate, Integer categoryID, Integer subCategoryID);
    public Item save(BigDecimal amount, String description, Date actionDate, String categoryName, Integer subCategoryID, Integer assetTypeId);

    @Transactional
    public Income getIncomeById(Integer id);

    @Transactional
    public Outcome getOutcomeById(Integer id);

    @Transactional
    public List<Income> getIncomeByDate(Date date);

    @Transactional
    public List<Outcome> getOutcomeByDate(Date date);

    @Transactional
    public List<Income> getAllIncomes();

    @Transactional
    public List<Outcome> getAllOutcomes();

    public void update(Item item);
    public Item update(Integer id, BigDecimal amount, String description, Date actionDate, Integer categoryID, Integer subCategoryID);
    public Item update(Integer id, BigDecimal amount, String description, Date actionDate, String categoryName, Integer subCategoryID, Integer assetTypeId);
    public void deleteById(int id);

    @Transactional
    public void deleteAll();

    @Transactional
    public List<Income> getIncomesForCurrentWeek();

    @Transactional
    public List<Outcome> getOutcomesForCurrentWeek();
}
