package org.persac.service;

import org.persac.persistence.model.Asset;
import org.persac.persistence.model.Item;
import org.persac.persistence.model.Month;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

/**
 * @author mzhokha
 * @since 11/30/13
 */
public interface MonthService {

    @Transactional
    public List<Month> getAllMonths();

    @Transactional
    public List<Month> getLast5Months();

    @Transactional
    public Month getCurrentMonth();

    @Transactional
    public void createCurrentMonthIfNotExists();

    public void updateMonthAfterCreatingItem(Item item);
    public void updateMonthAfterUpdatingItem(Item item, Item oldItem);
    public void updateMonthBeforeDeletingItem(Item oldItem);

    @Transactional
    public void recalculateAndSaveMonths();

    @Transactional
    public Double getCurrentCapital();

    @Transactional
    public void recalculateCapital();

    @Transactional
    public void recalculateCurrencyAssets(List<Asset> assets);
}
