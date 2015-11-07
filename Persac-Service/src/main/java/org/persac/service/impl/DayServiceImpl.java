package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.model.Day;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Outcome;
import org.persac.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.persac.service.util.Constants.DD_MM_YYYY;

/**
 * @author mzhokha
 * @since 13.04.14
 */
public class DayServiceImpl extends DateServiceImpl implements DayService{

    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Day> getDaysForCurrentMonth() {
        DateTime firstMonthDayDT = new DateTime().dayOfMonth().withMinimumValue();

        return getDaysBetweenSpecifiedDateAndToday(firstMonthDayDT.toDate());
    }

    @Override
    public List<Day> getDaysForCurrentWeek() {
        DateTime mondayDT = new DateTime().dayOfWeek().withMinimumValue();

        return getDaysBetweenSpecifiedDateAndToday(mondayDT.toDate());
    }

    @Override
    public List<Day> getDaysBetweenSpecifiedDateAndToday(Date date) {
        DateTime startDT = new DateTime(date);
        DateTime todayDT = new DateTime(new Date());
        Map<String, Day> dayMap = getDayMapBetweenTwoDates(startDT, todayDT);

        List<Income> incomes = itemDao.getIncomesBetweenDates(date, todayDT.toDate());
        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(date, todayDT.toDate());

        return fillDaysData(dayMap, incomes, outcomes);
    }

    @Override
    public List<Day> getDaysForMonthAndYear(Integer month, Integer year) {
        DateTime startDT = new DateTime(year, month, 1, 0, 0);
        DateTime endDT = startDT.dayOfMonth().withMaximumValue();

        Map<String, Day> dayMap = getDayMapBetweenTwoDates(startDT, endDT);

        List<Income> incomes = itemDao.getIncomesBetweenDates(startDT.toDate(), endDT.toDate());
        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(startDT.toDate(), endDT.toDate());

        return fillDaysData(dayMap, incomes, outcomes);
    }

    @Override
    public List<Day> getDaysForLastYear() {
        DateTime todayDT = new DateTime(new Date());
        DateTime todayYearBeforeDT = todayDT.minusYears(1);

        Map<String, Day> dayMap = getDayMapBetweenTwoDates(todayYearBeforeDT, todayDT);

        List<Income> incomes = itemDao.getIncomesBetweenDates(todayYearBeforeDT.toDate(), todayDT.toDate());
        List<Outcome> outcomes = itemDao.getOutcomesBetweenDates(todayYearBeforeDT.toDate(), todayDT.toDate());

        return fillDaysData(dayMap, incomes, outcomes);
    }

    /**
     * This method static only for testing purposes. It is used in main method bellow.
     */
    private static Map<String, Day> getDayMapBetweenTwoDates(DateTime startDT, DateTime endDT) {
        Map<String, Day> dayMap = new LinkedHashMap<String, Day>();

        DateTime dayDT = startDT;
        while (!dayDT.isAfter(endDT)) {
            String dayStringDate = dayDT.toString(DD_MM_YYYY);
            dayMap.put(dayStringDate, new Day(dayDT.toDate()));

            dayDT = dayDT.plusDays(1);
        }
        return dayMap;
    }

    /**
     * @throws java.lang.NullPointerException if outcomes contains Item with date which is not present in dayMap.
     */
    private List<Day> fillDaysData(Map<String, Day> dayMap, List<Income> incomes, List<Outcome> outcomes) {
        for (Income income : incomes) {
            dayMap.get(new DateTime(income.getActionDate()).toString(DD_MM_YYYY)).addIncome(income);
        }

        for (Outcome outcome : outcomes) {
            dayMap.get(new DateTime(outcome.getActionDate()).toString(DD_MM_YYYY)).addOutcome(outcome);
        }

        List<Day> days = new ArrayList<Day>();
        for (String key: dayMap.keySet()) {
            days.add(dayMap.get(key));
        }

        return days;
    }

    public static void main(String[] args) {
        Date todaysDate = new Date();
        DateTime todayDT = new DateTime(todaysDate);

        DateTime todayYearBeforeDT = todayDT.minusYears(1);

        System.out.println(todayYearBeforeDT.toString(DD_MM_YYYY));

        Map<String, Day> dayMap = getDayMapBetweenTwoDates(todayYearBeforeDT, todayDT);

        System.out.println("bla");
    }


}
