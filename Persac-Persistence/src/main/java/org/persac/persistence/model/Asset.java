package org.persac.persistence.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mzhokha
 * @since 09.08.2014
 */

@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "amount", scale = 2, nullable = false)
    private BigDecimal amount = new BigDecimal(0);

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "asset_type_id")
    private AssetType assetType;

    @Column(name = "record_date", nullable = false)
    private Date recordDate;

    public Asset() {
    }

    public Asset(BigDecimal amount, AssetType assetType) {
        this.amount = amount;
        this.assetType = assetType;
    }

    public void addAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);

    }

    public void subtractAmount(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", amount=" + amount +
                ", assetType=" + assetType +
                ", recordDate=" + recordDate +
                '}';
    }
}
