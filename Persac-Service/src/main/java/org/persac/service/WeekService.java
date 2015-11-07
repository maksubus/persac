package org.persac.service;

import org.persac.persistence.model.Item;
import org.persac.persistence.model.Week;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

/**
 * @author mzhokha
 * @since 27.03.14
 */
public interface WeekService {

    @Transactional
    public List<Week> getLast5Weeks();

    @Transactional
    public void recalculateAndSaveWeeks();

    @Transactional
    public List<Week> getAllWeeks();

    public void updateWeekAfterCreatingItem(Item item);
    public void updateWeekAfterUpdatingItem(Item item, Item oldItem);
    public void updateWeekBeforeDeletingItem(Item oldItem);
}
