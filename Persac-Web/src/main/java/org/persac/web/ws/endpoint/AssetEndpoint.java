/*
package org.persac.web.ws.endpoint;

import org.persac.service.AssetService;
import org.persac.web.ws.Asset;
import org.persac.web.ws.GetAllCurrentAssetsRequest;
import org.persac.web.ws.GetAllCurrentAssetsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

*/
/**
 * @author mzhokha
 * @since 29.05.2015
 *//*

@Endpoint
public class AssetEndpoint {

    private static final String NAMESPACE_URI = "http://persac.com/assets";

    @Autowired
    AssetService assetService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllCurrentAssetsRequest")
    @ResponsePayload
    public GetAllCurrentAssetsResponse getAllCurrentAssets(@RequestPayload GetAllCurrentAssetsRequest request) {
        List<Asset> assets = new AssetAssembler().transformAllForWs(assetService.getAllCurrentAssets());

        GetAllCurrentAssetsResponse response = new GetAllCurrentAssetsResponse();
        response.getAssets().addAll(assets);
        return response;
    }
}
*/
