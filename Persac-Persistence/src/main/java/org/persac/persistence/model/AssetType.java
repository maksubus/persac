package org.persac.persistence.model;

import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author mzhokha
 * @since 09.08.2014
 */

@Entity
@Table(name = "asset_type")
public class AssetType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "active")
    @Type(type = "yes_no")
    private Boolean active;

    @Column(name = "currency_code")
    private String currencyCode;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    protected User user;

    public AssetType() {
    }

    public AssetType(String name) {
        this.name = name;
    }

    public AssetType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public AssetType(Integer id, String name, String currencyCode) {
        this.id = id;
        this.name = name;
        this.currencyCode = currencyCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (!(o instanceof AssetType)) return false;

        AssetType assetType = (AssetType) o;

        if (active != null ? !active.equals(assetType.active) : assetType.active != null) return false;
        if (currencyCode != null ? !currencyCode.equals(assetType.currencyCode) : assetType.currencyCode != null) return false;
        if (name != null ? !name.equals(assetType.name) : assetType.name != null) return false;
        if (user != null ? !user.equals(assetType.user) : assetType.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssetType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + user +
                '}';
    }
}
