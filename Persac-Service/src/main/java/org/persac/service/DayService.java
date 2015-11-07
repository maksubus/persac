package org.persac.service;

import org.persac.persistence.model.Day;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 13.04.14
 */
public interface DayService {

    public List<Day> getDaysForCurrentMonth();
    public List<Day> getDaysForCurrentWeek();
    public List<Day> getDaysBetweenSpecifiedDateAndToday(Date date);
    public List<Day> getDaysForMonthAndYear(Integer month, Integer year);

    public List<Day> getDaysForLastYear();
}
