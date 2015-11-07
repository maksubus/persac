package org.persac.persistence.model;

import org.hibernate.annotations.Parent;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * @author mzhokha
 * @since 21.01.2015
 */
@Embeddable
public class CurrencyAsset {

    @Parent
    Month month;

    @Column(name = "amount", nullable = false)
    BigDecimal amount = new BigDecimal(0);

    @Column(name = "currency_code")
    private String currencyCode;

    public CurrencyAsset() {
    }

    public CurrencyAsset(BigDecimal amount) {
        this.amount = amount;
    }

    public CurrencyAsset(BigDecimal amount, Month month) {
        this.amount = amount;
        this.month = month;
    }

    public CurrencyAsset(BigDecimal amount, Month month, String currencyCode) {
        this.currencyCode = currencyCode;
        this.month = month;
        this.amount = amount;
    }

    public CurrencyAsset(Month month, String currencyCode) {
        this.month = month;
        this.currencyCode = currencyCode;
    }

    public CurrencyAsset(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void addAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyAsset)) return false;

        CurrencyAsset that = (CurrencyAsset) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (!currencyCode.equals(that.currencyCode)) return false;
        if (!month.equals(that.month)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month.hashCode();
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + currencyCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CurrencyAsset{");
        sb.append("month=").append(month);
        sb.append(", amount=").append(amount);
        sb.append(", currencyCode=").append(currencyCode);
        sb.append('}');
        return sb.toString();
    }
}
