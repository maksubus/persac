package org.persac.web.controller;

import org.persac.persistence.model.Asset;
import org.persac.persistence.model.AssetType;
import org.persac.persistence.model.CurrencyAsset;
import org.persac.persistence.model.Month;
import org.persac.service.AssetService;
import org.persac.service.AssetTypeService;
import org.persac.service.MonthService;
import org.persac.service.exception.AssetTransferException;
/*import org.persac.web.ws.exchange.CurrencyConvertor;
import org.persac.web.ws.exchange.CurrencyConvertorSoap;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import static org.persac.service.util.Constants.USD;

/**
 * @author mzhokha
 * @since 09.08.2014
 */
@Controller
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    AssetService assetService;

    @Autowired
    MonthService monthService;

    @Autowired
    AssetTypeService assetTypeService;

    @RequestMapping(method = RequestMethod.GET)
    public String viewAssets(Model model) {
        List<Asset> assets = assetService.getAllCurrentAssets();
        model.addAttribute("assets", assets);

        //BigDecimal capital = monthService.getCurrentCapital();
        //model.addAttribute("capital", capital);
        model.addAttribute("capital", 0);

        List<AssetType> assetTypes = assetTypeService.getAllActiveAssetTypes();
        model.addAttribute("assetTypes", assetTypes);

        Month month = monthService.getCurrentMonth();
        model.addAttribute("month", month);

        Set<Currency> currencies = Currency.getAvailableCurrencies();
        model.addAttribute("currencies", currencies);


//        CurrencyConvertor currencyConvertor = new CurrencyConvertor();
//        CurrencyConvertorSoap port = currencyConvertor.getCurrencyConvertorSoap();
//        BigDecimal resultedAmount = new BigDecimal(0);
//        double rate;
//        for (CurrencyAsset currencyAsset: month.getCurrencyAssets()) {
//            if (USD.equals(currencyAsset.getCurrencyCode())) {
//                resultedAmount = resultedAmount.add(currencyAsset.getAmount());
//            } else {
//                rate = port.conversionRate(USD, currencyAsset.getCurrencyCode());
//                resultedAmount = currencyAsset.getAmount().divide(new BigDecimal(rate));
//            }
//
//        }
//        model.addAttribute("capital", resultedAmount);

        return "assets";
    }

    //todo: maybe move to other controller
    @RequestMapping("recalculate-currency-amounts")
    public String recalculateCurrencyAssets() {
        monthService.recalculateCurrencyAssets(assetService.getAllCurrentAssets());

        return "redirect:/assets";
    }

//    @RequestMapping("recalculate-capital")
//    public String recalculateCapital() {
//        monthService.recalculateCapital();
//
//        return "redirect:/app/assets";
//    }


    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public String transferBetweenAssets(@RequestParam Integer sourceAssetTypeId,
                           @RequestParam Integer destAssetTypeId,
                           @RequestParam BigDecimal amount) throws AssetTransferException {

        if (sourceAssetTypeId.equals(destAssetTypeId)) {
            System.out.println("Transfer to the same asset");
            return "redirect:/assets";
        }

        assetService.transfer(sourceAssetTypeId, destAssetTypeId, amount);

        return "redirect:/assets";
    }


    @RequestMapping(value = "exchange", method = RequestMethod.POST)
    public String exchange(@RequestParam Integer sourceAssetTypeIdForExchange,
                           @RequestParam BigDecimal amount,
                           @RequestParam BigDecimal rate,
                           @RequestParam String rateOperation,
                           @RequestParam String currencyCode,
                           @RequestParam(required = false) Integer destAssetTypeIdForExchange) {
        boolean isBuyingOperation = rateOperation.equals("buy");

        assetService.exchange(sourceAssetTypeIdForExchange,
                amount,
                rate,
                isBuyingOperation,
                currencyCode,
                destAssetTypeIdForExchange);

        return "redirect:/assets";
    }

    @RequestMapping(value = "create-asset", method = RequestMethod.POST)
    public String createAsset(@RequestParam String assetTypeName,
                              @RequestParam(required = false) BigDecimal amount,
                              @RequestParam String currencyCode) {
        assetService.createAssetTypeAndAsset(assetTypeName, amount, currencyCode);

        //monthService.recalculateCapital();

        return "redirect:/assets";
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public @ResponseBody Asset getAssetById(@RequestParam Integer id) {
        return assetService.getById(id);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody String updateAsset(@RequestParam Integer id,
                                            @RequestParam String assetTypeName,
                                            @RequestParam BigDecimal amount) {
        Asset asset = assetService.getById(id);
        BigDecimal oldAmount = asset.getAmount();
        asset.setAmount(amount);
        asset.getAssetType().setName(assetTypeName);

        assetService.updateAssetAfterEditing(asset);

//        monthService.recalculateCapital();

        return "OK";
    }

    @RequestMapping(value = "deactivate", method = RequestMethod.GET)
    public @ResponseBody String deactivateAsset(@RequestParam Integer id) {
        assetService.deactivateAsset(id);
        return "OK";
    }
}
