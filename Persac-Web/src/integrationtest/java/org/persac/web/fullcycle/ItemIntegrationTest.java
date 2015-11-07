package org.persac.web.fullcycle;

import org.joda.time.DateTime;
import org.junit.Test;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.dao.WeekDao;
import org.persac.persistence.model.Month;
import org.persac.persistence.model.MonthIncome;
import org.persac.persistence.model.MonthOutcome;
import org.persac.persistence.model.Week;
import org.persac.service.MonthService;
import org.persac.service.WeekService;
import org.persac.web.ApplicationContextAwareTest;
import org.persac.web.controller.ItemController;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import static org.persac.service.util.Constants.*;

/**
 * @author mzhokha
 * @since 07.02.2015
 */
public class ItemIntegrationTest extends ApplicationContextAwareTest {

    @Autowired
    WeekDao weekDao;

    @Autowired
    WeekService weekService;

    @Autowired
    MonthDao monthDao;

    @Autowired
    MonthService monthService;

    @Autowired
    ItemController itemController;

    @Test
    public void testSave_ifItemIsInCurrentMonth_thenUpdateWeek_CurrentMonth_Assets() throws Exception {
        //get current week
        Date currentDate = new Date();
        Week week = weekDao.getByMondayDate(currentDate);
        // and its incomes and outcomes

        //get current month
        Month currentMonth = monthDao.getCurrentMonth();

        Collection<MonthIncome> monthIncomes = currentMonth.getIncomes();
        Collection<MonthOutcome> monthOutcomes = currentMonth.getOutcomes();

        // and its incomes and outcomes
        //and assets
        //and currency assets


        String todayDateString = SDF.format(new Date());
        itemController.save(INCOME, 3, 1, 200D, "Salary creating test", todayDateString);

        //check that new item is saved and it has same data which was passed to controller method
        //check that week incomes and outcomes respectively changed
        //check that month incomes and outcomes respectively changed
        //check that current assets respectively changed

    }

    @Test
    public void testSave_ifItemIsInPastMonth_thenUpdateWeeksIncomesAndOutcomes_andMonthIncomesAndOutcomes() throws Exception {
        //get current week
        // and its incomes and outcomes

        //get current month
        // and its incomes and outcomes
        //and assets
        //and currency assets


        String todayDateString = SDF.format(new Date());
        itemController.save(INCOME, 3, 1, 200D, "Salary creating test", todayDateString);

        //check that new item is saved and it has same data which was passed to controller method
        //check that week incomes and outcomes respectively changed
        //check that month incomes and outcomes respectively changed
        //check that current assets respectively changed

    }
}
