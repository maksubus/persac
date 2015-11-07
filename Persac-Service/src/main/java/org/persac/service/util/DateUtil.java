package org.persac.service.util;

import org.joda.time.DateTime;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.persac.service.util.Constants.DD_MM_YYYY;


/**
 * @author mzhokha
 * @since 01.05.14
 */
public class DateUtil {

    private DateTime startDT;
    private DateTime endDT;

    public void calculateStartAndEndDatesForMonthAndYear(Integer month, Integer year) {
        startDT = new DateTime(year, month, 1, 0, 0);
        endDT = startDT.dayOfMonth().withMaximumValue();
    }

    public void calculateStartAndEndDatesForMonth(DateTime dateTime) {
        startDT = dateTime.dayOfMonth().withMinimumValue();
        endDT = dateTime.dayOfMonth().withMaximumValue();
    }

    public DateTime getStartDT() {
        return startDT;
    }

    public DateTime getEndDT() {
        return endDT;
    }

    public String getStartDateString() {
        checkNotNull(startDT, "Before using this method call calculateStartAndEndDatesForMonthAndYear() method.");
        return startDT.toString(DD_MM_YYYY);
    }

    public String getEndDateString() {
        checkNotNull(endDT, "Before using this method call calculateStartAndEndDatesForMonthAndYear() method.");
        return endDT.toString(DD_MM_YYYY);
    }

    public Date getStartDate() {
        checkNotNull(startDT, "Before using this method call calculateStartAndEndDatesForMonthAndYear() method.");
        return startDT.toDate();
    }

    public Date getEndDate() {
        checkNotNull(endDT, "Before using this method call calculateStartAndEndDatesForMonthAndYear() method.");
        return endDT.toDate();
    }
}
