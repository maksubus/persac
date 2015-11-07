package org.persac.web.controller;

import org.joda.time.DateTime;
import org.persac.persistence.model.Item;
import org.persac.service.AssetService;
import org.persac.service.ItemService;
import org.persac.service.MonthService;
import org.persac.service.WeekService;
import org.persac.service.operation.ItemOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.persac.service.util.Constants.SDF;

/**
 * @author mzhokha
 * @since 24.03.14
 */

@Controller
@RequestMapping("item/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private MonthService monthService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ItemOperationService itemOperationService;

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public @ResponseBody Item get(@RequestParam Integer id) {
        Item item = itemService.getOutcomeById(id);

        if (item == null) {
            item = itemService.getIncomeById(id);
        }

        return item;
    }

    @RequestMapping(value="save", method = RequestMethod.POST)
    public String save(@RequestParam String categoryName,
                       @RequestParam Integer subCategoryId,
                       @RequestParam Integer assetTypeId,
                       @RequestParam BigDecimal amount,
                       @RequestParam String description,
                       @RequestParam String date) throws ParseException {

        checkArgument(amount != null, "Amount must be not empty");
        checkArgument(amount.doubleValue() > 0, "Amount must be greater than zero");
        checkArgument(date.length() <= 10, "Date string is too long.");
        checkArgument(date.matches("[0-9]{2}-[0-9]{2}-[0-9]{4}"), "Date string doesn't match pattern.");
        checkArgument(new DateTime(SDF.parse(date)).isBeforeNow(), "Date must not be after current date");


        Date actionDate = SDF.parse(date);

        itemOperationService.saveNewItem(categoryName, subCategoryId, assetTypeId, amount, description, actionDate);

        Integer itemMonthOfYear = new DateTime(actionDate).getMonthOfYear();
        Integer currentMonthOfYear = new DateTime().getMonthOfYear();

        /*if (currentMonthOfYear.equals(itemMonthOfYear)) {
            monthService.recalculateCapital();
        }*/

        return "redirect:/index";
    }

    @RequestMapping(value="update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestParam Integer id,
                       @RequestParam String categoryName,
                       @RequestParam Integer subCategoryId,
                       @RequestParam(required = false) Integer assetTypeId,
                       @RequestParam BigDecimal amount,
                       @RequestParam String description,
                       @RequestParam String date) throws ParseException {

        //TODO: add check that item was not changed. If was not changed do nothing.

        checkArgument(date.length() <= 10, "Date string is too long.");
        checkArgument(date.matches("[0-9]{2}-[0-9]{2}-[0-9]{4}"), "Date string doesn't match pattern.");
        checkArgument(new DateTime(SDF.parse(date)).isBeforeNow(), "Date must not be after current date");
        checkNotNull(id, "Id must not be null");

        Date actionDate = SDF.parse(date);

        Item oldItem = itemService.getIncomeById(id);
        if (oldItem == null) {
            oldItem = itemService.getOutcomeById(id);
        }

        //TODO: Do not update month, week, assets if only subCategory or description changed.
        //TODO: Gap in design. If one of the next operations fails, item anyway will be updated. Which leads to inconsistency of data.
        //TODO: So all these operations must me performed in one transaction.
        itemOperationService.updateItem(id, categoryName,
                subCategoryId, assetTypeId,
                amount, description,
                actionDate, oldItem);

        return "OK";
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public @ResponseBody String delete(@RequestParam Integer id) {
        itemOperationService.deleteItem(id);

        return "OK";
    }
}
