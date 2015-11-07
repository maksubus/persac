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
@DiscriminatorValue("income")
public class Income extends Item {

    public Income() {
    }

    public Income(BigDecimal amount, String description, Date actionDate, Category category) {
        super(amount, description, actionDate, category);
    }

    public Income(BigDecimal amount, String description, Date actionDate, Category category, AssetType assetType) {
        super(amount, description, actionDate, category, assetType);
    }
}
