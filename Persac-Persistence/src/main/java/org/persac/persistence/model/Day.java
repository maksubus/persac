package org.persac.persistence.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 13.04.14
 */
public class Day {

    private Date date;
    private List<Income> incomes = new ArrayList<Income>();
    private List<Outcome> outcomes = new ArrayList<Outcome>();
    private BigDecimal totalIncomeAmount = new BigDecimal(0);
    private BigDecimal totalOutcomeAmount = new BigDecimal(0);

    public Day(Date date) {
        this.date = date;
    }

    public void addOutcome(Outcome outcome) {
        outcomes.add(outcome);
        totalOutcomeAmount = totalOutcomeAmount.add(outcome.getAmount());
    }

    public void addIncome(Income income) {
        incomes.add(income);
        totalIncomeAmount = totalIncomeAmount.add(income.getAmount());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }

    public List<Outcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = outcomes;
    }

    public BigDecimal getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    public void setTotalIncomeAmount(BigDecimal totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    public BigDecimal getTotalOutcomeAmount() {
        return totalOutcomeAmount;
    }

    public void setTotalOutcomeAmount(BigDecimal totalOutcomeAmount) {
        this.totalOutcomeAmount = totalOutcomeAmount;
    }
}
