package org.persac.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * @author mzhokha
 * @since 29.01.2015
 */

@Entity
@DiscriminatorValue("outcome")
public class MonthOutcome extends MonthRecord {

    public MonthOutcome() {
    }

    public MonthOutcome(Month month, String currencyCode, BigDecimal amount) {
        super(currencyCode, amount);
        this.month = month;
    }

    @ManyToOne
    @JoinColumn(name = "month_id", nullable = false)
    protected Month month;

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }
}
