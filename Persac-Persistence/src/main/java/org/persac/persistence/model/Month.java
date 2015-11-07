package org.persac.persistence.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author mzhokha
 * @since 05.07.2014
 */

@Entity
@Table(name = "month")
public class Month {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name= "capital", columnDefinition="Decimal(15,2)")
    private BigDecimal capital = new BigDecimal(0);

    @Column(name = "begin_date")
    private Date beginDate;

    @OneToMany(mappedBy = "month",
            targetEntity = MonthIncome.class,
            fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Where(clause = "type = 'income'")
    private Set<MonthIncome> incomes = new LinkedHashSet<MonthIncome>();

    @OneToMany(mappedBy = "month",
            targetEntity = MonthOutcome.class,
            fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Where(clause = "type = 'outcome'")
    private Set<MonthOutcome> outcomes = new LinkedHashSet<MonthOutcome>();


    @CollectionOfElements(fetch = FetchType.EAGER)
    @JoinTable(
            name = "currency_asset",
            joinColumns = @JoinColumn(name = "month_id")
    )
    private Set<CurrencyAsset> currencyAssets = new LinkedHashSet<CurrencyAsset>();

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    protected User user;

    public Month() {
    }

    public Month(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Month(Integer year, Integer month) {
        this.beginDate = new DateTime(year, month, 1, 0, 0).toDate();
    }

    //TODO: maybe use only 1 parameter. Item(income)
    public void addIncomeAmount(BigDecimal amount, final String currencyCode) {
        //Do we have incomes in this currency
        Collection filteredIncomes = Collections2.filter(incomes, new Predicate<MonthIncome>() {
            @Override
            public boolean apply(MonthIncome income) {
                return income.getCurrencyCode().equals(currencyCode);
            }
        });

        //If incomes list is empty or we don't have income in this currency
        //create income in this currency
        if (incomes.isEmpty() || filteredIncomes.isEmpty()) {
            incomes.add(new MonthIncome(this, currencyCode, amount));
        } else {
            for (MonthIncome monthIncome: incomes) {
                if (monthIncome.getCurrencyCode().equals(currencyCode)) {
                    monthIncome.addAmount(amount);
                }
            }
        }
    }

    public void addOutcomeAmount(BigDecimal amount, final String currencyCode) {
        Collection filteredOutcomes = Collections2.filter(outcomes, new Predicate<MonthOutcome>() {
            @Override
            public boolean apply(MonthOutcome outcome) {
                return outcome.getCurrencyCode().equals(currencyCode);
            }
        });

        if (outcomes.isEmpty() || filteredOutcomes.isEmpty()) {
            outcomes.add(new MonthOutcome(this, currencyCode, amount));
        } else {
            for (MonthOutcome monthOutcome: outcomes) {
                if (monthOutcome.getCurrencyCode().equals(currencyCode)) {
                    monthOutcome.addAmount(amount);
                }
            }
        }
    }

    public void subtractIncomeAmount(BigDecimal amount, final String currencyCode) {
        Collection filteredIncomes = Collections2.filter(incomes, new Predicate<MonthIncome>() {
            @Override
            public boolean apply(MonthIncome income) {
                return income.getCurrencyCode().equals(currencyCode);
            }
        });

        if (incomes.isEmpty() || filteredIncomes.isEmpty()) {
            incomes.add(new MonthIncome(this, currencyCode, amount));
        } else {
            for (MonthIncome monthIncome: incomes) {
                if (monthIncome.getCurrencyCode().equals(currencyCode)) {
                    monthIncome.subtractAmount(amount);
                }
            }
        }
    }

    public void subtractOutcomeAmount(BigDecimal amount, final String currencyCode) {
        //todo: recheck logic. initial filtering is enough to get needed outcome

        Collection filteredOutcomes = Collections2.filter(outcomes, new Predicate<MonthOutcome>() {
            @Override
            public boolean apply(MonthOutcome outcome) {
                return outcome.getCurrencyCode().equals(currencyCode);
            }
        });

        if (outcomes.isEmpty() || filteredOutcomes.isEmpty()) {
            outcomes.add(new MonthOutcome(this, currencyCode, amount));
        } else {
            for (MonthOutcome monthOutcome: outcomes) {
                if (monthOutcome.getCurrencyCode().equals(currencyCode)) {
                    monthOutcome.subtractAmount(amount);
                }
            }
        }
    }

    public void addCurrencyAsset(CurrencyAsset currencyAsset) {
        currencyAsset.setMonth(this);
        currencyAssets.add(currencyAsset);
    }

    public Integer getYear() {
        return new DateTime(beginDate).getYear();
    }

    public Integer getMonthOfYear() {
        return new DateTime(beginDate).getMonthOfYear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public Set<CurrencyAsset> getCurrencyAssets() {
        return currencyAssets;
    }

    public void setCurrencyAssets(Set<CurrencyAsset> currencyAssets) {
        this.currencyAssets = currencyAssets;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Collection<MonthIncome> getIncomes() {
        return incomes;
    }

    public void setIncomes(Set<MonthIncome> incomes) {
        this.incomes = incomes;
    }

    public Collection<MonthOutcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(Set<MonthOutcome> outcomes) {
        this.outcomes = outcomes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Month)) return false;

        Month month = (Month) o;

        if (beginDate != null ? !beginDate.equals(month.beginDate) : month.beginDate != null) return false;
        if (user != null ? !user.equals(month.user) : month.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beginDate != null ? beginDate.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
