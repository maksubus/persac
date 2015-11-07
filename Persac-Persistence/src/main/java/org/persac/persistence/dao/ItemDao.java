package org.persac.persistence.dao;

import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 11/19/13
 */
@Transactional
public interface ItemDao {
    
    public void save(Item item);

    public Income getIncomeByID(Integer id);
    public Outcome getOutcomeByID(Integer id);

    public List<Income> getIncomeByActionDate(Date date);
    public List<Outcome> getOutcomeByActionDate(Date date);

    public List<Income> getAllIncomes();
    public List<Outcome> getAllOutcomes();

    public void update(Item item);

    public void deleteById(Integer id);
    public void deleteAll();

    public List<Income> getIncomesBetweenDates(Date startDate, Date endDate);
    public List<Outcome> getOutcomesBetweenDates(Date startDate, Date endDate);

//    public List<Income> getIncomesByMonthDate(Date monthDate);
//    public List<Outcome> getOutcomesByMonthDate(Date monthDate);
//
//    public List<Income> getAllIncomesByMonthAndYear(Integer month, Integer year);
//    public List<Outcome> getAllOutcomesByMonthAndYear(Integer month, Integer year);

//    @Deprecated
//    /**
//     * Use method with start date and end date.
//     * This method use if really need just start date.
//     */
//    public List<Income> getIncomesAfterDate(String date);
//
//    @Deprecated
//    /**
//     * Use method with start date and end date.
//     * This method use if really need just start date.
//     */
//    public List<Outcome> getOutcomesAfterDate(String date);
}
