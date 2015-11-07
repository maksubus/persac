package org.persac.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author mzhokha
 * @since 05.07.2014
 */

@Entity
@DiscriminatorValue("income")
public class IncomeCategory extends Category {

    public IncomeCategory() {
    }

    public IncomeCategory(String name) {
        super(name);
    }

    public IncomeCategory(Category category) {
        super(category);
    }
}
