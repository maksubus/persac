package org.persac.persistence.dao;

import org.persac.persistence.model.Month;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 06.07.2014
 */
public interface MonthDao {

    public Integer save(Month month);
    public Month getById(int id);
    public List<Month> getLast5Months();
    public Month getCurrentMonth();
    public Month getLastMonth();
    public List<Month> getAll();
    public Month getByFirstDayDate(Date date);
    public Month getByYearAndMonthNumber(Date date);
    public Double getCurrentCapital();
    public void update(Month month);
    public void deleteById(int id);
    public void deleteAll();
}
