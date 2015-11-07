package org.persac.persistence.dao;

import org.persac.persistence.model.CurrencyAsset;

import java.util.List;

/**
 * @author mzhokha
 * @since 27.01.2015
 */
public interface CurrencyAssetDao {

    public CurrencyAsset getById(Integer id);
    public List<CurrencyAsset> getAllCurrencyAssets();
    public void save(CurrencyAsset currencyAsset);
    public void update(CurrencyAsset currencyAsset);
    public void deleteById(Integer id);
}
