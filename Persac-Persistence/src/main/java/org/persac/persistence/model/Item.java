package org.persac.persistence.model;

import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mzhokha
 * @since 05.07.2014
 */

@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "item_type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Integer id;

    @Column(name = "amount", scale = 2)
    protected BigDecimal amount;

    @Column(name = "description")
    protected String description;

    @Column(name = "action_date")
    protected Date actionDate;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "category_id")
    protected Category subCategory;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    protected User user;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "asset_type_id")
    protected AssetType assetType;

    public Item() {
    }

    protected Item(BigDecimal amount, String description, Date actionDate, Category category) {
        this.amount = amount;
        this.description = description;
        this.actionDate = actionDate;
        this.subCategory = category;
    }

    protected Item(BigDecimal amount, String description, Date actionDate, Category category, AssetType assetType) {
        this.amount = amount;
        this.description = description;
        this.actionDate = actionDate;
        this.subCategory = category;
        this.assetType = assetType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public Category getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(Category subCategory) {
        this.subCategory = subCategory;
    }

    public Integer getYear() {
        return new DateTime(actionDate).getYear();
    }

    public Integer getMonthOfYear() {
        return new DateTime(actionDate).getMonthOfYear();
    }

    public Integer getDayOfMonth() {
        return new DateTime(actionDate).getDayOfMonth();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }
}
