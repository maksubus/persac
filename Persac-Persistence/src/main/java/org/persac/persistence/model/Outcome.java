package org.persac.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mzhokha
 * @since 05.07.2014
 */

@Entity
@DiscriminatorValue("outcome")
public class Outcome extends Item {

    public Outcome() {
    }

    public Outcome(BigDecimal amount, String description, Date actionDate, Category category) {
        super(amount, description, actionDate, category);
    }

    public Outcome(BigDecimal amount, String description, Date actionDate, Category subCategory, AssetType assetType) {
        super(amount, description, actionDate, subCategory, assetType);
    }
}
