package org.persac.persistence.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mzhokha
 * @since 25.03.14
 */

@NamedQueries({
        @NamedQuery(
                name = "getWeekByMondayDate",
                query = "from Week where user.id = :userId and mondayDate = :date"
        )
})
@Entity
@Table(name = "week")
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "monday_date")
    private Date mondayDate;

    @Column(name = "income_amount", scale = 2)
    private BigDecimal incomeAmount = new BigDecimal(0);

    @Column(name = "outcome_amount", scale = 2)
    private BigDecimal outcomeAmount = new BigDecimal(0);

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    protected User user;

    public Week() {
    }

    public Week(Date mondayDate) {
        this.mondayDate = mondayDate;
    }

    public Week(Long milliseconds) {
        this.mondayDate = new Date(milliseconds);
    }

    public Week(Date mondayDate, BigDecimal inAmount, BigDecimal outcomeAmount) {
        this.mondayDate = mondayDate;
        this.incomeAmount = inAmount;
        this.outcomeAmount = outcomeAmount;
    }

    public void addIncomeAmount(BigDecimal amount) {
        incomeAmount = incomeAmount.add(amount);
    }

    public void addOutcomeAmount(BigDecimal amount) {
        outcomeAmount = outcomeAmount.add(amount);
    }

    public void subtractIncomeAmount(BigDecimal amount) {
        incomeAmount = incomeAmount.subtract(amount);
    }

    public void subtractOutcomeAmount(BigDecimal amount) {
        outcomeAmount = outcomeAmount.subtract(amount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getMondayDate() {
        return mondayDate;
    }

    public void setMondayDate(Date mondayDate) {
        this.mondayDate = mondayDate;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getOutcomeAmount() {
        return outcomeAmount;
    }

    public void setOutcomeAmount(BigDecimal outcomeAmount) {
        this.outcomeAmount = outcomeAmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
