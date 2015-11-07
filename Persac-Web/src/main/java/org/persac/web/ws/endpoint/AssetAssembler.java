/*
package org.persac.web.ws.endpoint;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.persac.web.ws.Asset;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * @author mzhokha
 * @since 01.06.2015
 *//*

public class AssetAssembler {

    public List<Asset>  transformAllForWs(List<org.persac.persistence.model.Asset> modelAssets) {
        List<Asset> assets = new ArrayList<Asset>(modelAssets.size());

        for (org.persac.persistence.model.Asset modelAsset: modelAssets) {
            assets.add(transformForWs(modelAsset));
        }

        return assets;
    }

    public Asset transformForWs(org.persac.persistence.model.Asset modelAsset) {
        Asset asset = new Asset();
        asset.setId(modelAsset.getId());
        asset.setName(modelAsset.getAssetType().getName());
        asset.setAmount(modelAsset.getAmount());
        asset.setCurrency(modelAsset.getAssetType().getCurrencyCode());
//        asset.setRecordDate(modelAsset.getRecordDate());
        asset.setRecordDate(new XMLGregorianCalendarImpl());

        return asset;
    }
}
*/
