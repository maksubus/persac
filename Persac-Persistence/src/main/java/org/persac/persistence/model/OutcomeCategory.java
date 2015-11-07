package org.persac.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author mzhokha
 * @since 05.07.2014
 */

@Entity
@DiscriminatorValue("outcome")
public class OutcomeCategory extends Category {

    public OutcomeCategory() {
    }

    public OutcomeCategory(String name) {
        super(name);
    }

    public OutcomeCategory(Category category) {
        super(category);
    }
}
