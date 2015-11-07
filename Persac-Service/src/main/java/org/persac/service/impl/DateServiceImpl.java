package org.persac.service.impl;

import org.joda.time.DateTime;
import org.persac.persistence.model.Item;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mzhokha
 * @since 23.03.14
 */
public class DateServiceImpl {

    public static final String MIN = "min";
    public static final String MAX = "max";   
    

    public Map<String, DateTime> getMinAndMaxDate(List<? extends Item> incomes,
                                                List<? extends Item> outcomes) {

        DateTime minInDate = getMinDate(incomes);
        DateTime maxInDate = getMaxDate(incomes);

        DateTime minOutDate = getMinDate(outcomes);
        DateTime maxOutDate = getMaxDate(outcomes);

        DateTime minDate;
        if (minInDate.isAfter(minOutDate)) {
            minDate = minOutDate;
        } else {
            minDate = minInDate;
        }

        DateTime maxDate;
        if (maxInDate.isAfter(maxOutDate)) {
            maxDate = maxInDate;
        } else {
            maxDate = maxOutDate;
        }

        Map<String, DateTime> minMaxDates = new HashMap<String, DateTime>();
        minMaxDates.put(MIN, minDate);
        minMaxDates.put(MAX, maxDate);

        return  minMaxDates;
    }

    private DateTime getMinDate(List<? extends Item> items) {
        if (items.isEmpty()) {
            return  new DateTime(new Date());
        }

        DateTime minDT = new DateTime(items.get(0).getActionDate());

        for (Item item : items) {
            DateTime itemDT = new DateTime(item.getActionDate());

            if (itemDT.isBefore(minDT)) {
                minDT = itemDT;
            }
        }

        return minDT;
    }

    private DateTime getMaxDate(List<? extends Item> items) {
        if (items.isEmpty()) {
            return  new DateTime(new Date());
        }

        DateTime maxDT = new DateTime(items.get(0).getActionDate());

        for (Item item : items) {
            DateTime itemDT = new DateTime(item.getActionDate());

            if (itemDT.isAfter(maxDT)) {
                maxDT = itemDT;
            }
        }

        return maxDT;
    }
}
