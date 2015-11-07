package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.dao.ItemDao;
import org.persac.persistence.dao.WeekDao;
import org.persac.persistence.model.Income;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Outcome;
import org.persac.persistence.model.Week;
import org.persac.service.WeekService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mzhokha
 * @since 27.03.14
 */
public class WeekServiceImpl extends DateServiceImpl implements WeekService {

    private WeekDao weekDao;

    private ItemDao itemDao;

    public WeekServiceImpl(WeekDao weekDao, ItemDao itemDao) {
        this.weekDao = weekDao;
        this.itemDao = itemDao;
    }

    @Override
    public List<Week> getLast5Weeks() {
        return weekDao.getLast5Weeks();
    }


    @Override
    public void recalculateAndSaveWeeks() {
        List<Income> incomes = itemDao.getAllIncomes();
        List<Outcome> outcomes = itemDao.getAllOutcomes();

        Map<String, DateTime> minMaxDates = getMinAndMaxDate(incomes, outcomes);
        List<Week> weeks = getWeeksList(minMaxDates);
        weeks = fillWeeksData(incomes, outcomes, weeks);

        weekDao.deleteAll();

        //TODO: maybe save all weeks by one query
        for (Week week: weeks) {
            weekDao.save(week);
        }
    }

    @Override
    public void updateWeekAfterCreatingItem(Item item) {
        DateTime itemDT = new DateTime(item.getActionDate());
        DateTime mondayDT = itemDT.dayOfWeek().withMinimumValue();

        Week week = weekDao.getByMondayDate(mondayDT.toDate());

        //todo: week must not be null at this point. Never.
        if (week == null) {
            week = new Week(mondayDT.toDate());
            week = addAmountToWeek(week, item);
            weekDao.save(week);
        } else {
            week = addAmountToWeek(week, item);
            weekDao.update(week);
        }
    }

    @Override
    public void updateWeekAfterUpdatingItem(Item item, Item oldItem) {
        DateTime itemDT = new DateTime(item.getActionDate());
        DateTime itemMondayDT = itemDT.dayOfWeek().withMinimumValue();

        DateTime oldItemDT = new DateTime(oldItem.getActionDate());
        DateTime oldItemMondayDT = oldItemDT.dayOfWeek().withMinimumValue();

        if (itemMondayDT.equals(oldItemMondayDT)) {
            Week week = weekDao.getByMondayDate(itemMondayDT.toDate());

            week = subtractAmountFromWeek(week, oldItem);
            week = addAmountToWeek(week, item);
            weekDao.update(week);
        } else {
            Week itemWeek = weekDao.getByMondayDate(itemMondayDT.toDate());
            addAmountToWeek(itemWeek, item);
            weekDao.update(itemWeek);

            Week oldItemWeek = weekDao.getByMondayDate(oldItemMondayDT.toDate());
            subtractAmountFromWeek(oldItemWeek, oldItem);
            weekDao.update(oldItemWeek);
        }
    }

    public void updateWeekBeforeDeletingItem(Item oldItem) {
        DateTime itemDT = new DateTime(oldItem.getActionDate());
        DateTime mondayDT = itemDT.dayOfWeek().withMinimumValue();

        Week week = weekDao.getByMondayDate(new Date(mondayDT.getMillis()));

        week = subtractAmountFromWeek(week, oldItem);

        weekDao.update(week);
    }

    private Week addAmountToWeek(Week week, Item item) {
        if (item instanceof Income) {
            week.addIncomeAmount(item.getAmount());
        } else if (item instanceof Outcome) {
            week.addOutcomeAmount(item.getAmount());
        }

        return week;
    }

    private Week subtractAmountFromWeek(Week week, Item oldItem) {
        if (oldItem instanceof Income) {
            week.subtractIncomeAmount(oldItem.getAmount());
        } else if (oldItem instanceof Outcome) {
            week.subtractOutcomeAmount(oldItem.getAmount());
        }

        return week;
    }

    @Override
    public List<Week> getAllWeeks() {
        return weekDao.getAll();
    }

    private List<Week> getWeeksList(Map<String, DateTime> minAndMaxDate) {
        DateTime minDT = minAndMaxDate.get(MIN);
        DateTime maxDT = minAndMaxDate.get(MAX);

        int dayOfWeek = minDT.getDayOfWeek();
        if (dayOfWeek != 1) {
            minDT = minDT.minusDays(dayOfWeek - 1);
        }

        dayOfWeek = maxDT.getDayOfWeek();
        if (dayOfWeek != 7) {
            maxDT = maxDT.plusDays(7 - dayOfWeek);
        }

        List<Week> weeks = new ArrayList<Week>();
        for  (DateTime dateTime = minDT; !dateTime.isAfter(maxDT); dateTime = dateTime.plusDays(7)) {
            weeks.add(new Week(dateTime.getMillis()));
        }

        return weeks;
    }

    private List<Week> fillWeeksData(List<Income> incomes, List<Outcome> outcomes, List<Week> weeks) {
        for (Week week: weeks) {
            DateTime mondayDT = new DateTime(week.getMondayDate());
            DateTime sundayDT = mondayDT.plusDays(6);

            for (Income income : incomes) {
                DateTime inDT = new DateTime(income.getActionDate());
                if (inDT.isAfter(mondayDT) && inDT.isBefore(sundayDT) || inDT.isEqual(mondayDT) || inDT.isEqual(sundayDT)) {
                    week.addIncomeAmount(income.getAmount());
                }
            }

            for (Outcome outcome : outcomes) {
                DateTime outDT = new DateTime(outcome.getActionDate());
                if (outDT.isAfter(mondayDT) && outDT.isBefore(sundayDT) || outDT.isEqual(mondayDT) || outDT.isEqual(sundayDT)) {
                    week.addOutcomeAmount(outcome.getAmount());
                }
            }
        }

        return weeks;
    }
}
