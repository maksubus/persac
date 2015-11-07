package org.persac.web.controller;

import org.joda.time.DateTime;
import org.persac.persistence.model.Day;
import org.persac.service.AssetTypeService;
import org.persac.service.CategoryService;
import org.persac.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.persac.service.util.Constants.SDF;

/**
 * @author mzhokha
 * @since 13.04.14
 */
@Controller
public class DayController {

    @Autowired
    private DayService dayService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AssetTypeService assetTypeService;

    @RequestMapping("/daily")
    public String daily(Model model) {
        List<Day> days = dayService.getDaysForCurrentMonth();

        enrichModelWithNecessaryData(model, days);

        model.addAttribute("categoryData", categoryService.getOutcomeDataByCategoriesForCurrentMonth());

        return "daily";
    }

    @RequestMapping("daily-after-date")
    public String dailyAfterDate(Model model, @RequestParam String date) throws ParseException {
        Date specifiedDate = SDF.parse(date);
        List<Day> days = dayService.getDaysBetweenSpecifiedDateAndToday(specifiedDate);

        enrichModelWithNecessaryData(model, days);

        model.addAttribute("categoryData", categoryService.getOutcomeDataByCategoriesAfterDate(specifiedDate));

        return "daily";
    }

    @RequestMapping("daily-for-month-and-year")
    public String dailyForMonthAndYear(Model model, @RequestParam Integer month, @RequestParam Integer year) {
        List<Day> days = dayService.getDaysForMonthAndYear(month , year);

        enrichModelWithNecessaryData(model, days);

        model.addAttribute("categoryData", categoryService.getOutcomeDataByCategoriesForMonthAndYear(month, year));

        return "daily";
    }

    private void enrichModelWithNecessaryData(Model model, List<Day> days) {
        model.addAttribute("days", days);
        model.addAttribute("incomeCategories", categoryService.getAllIncomeCategories());
        model.addAttribute("outcomeCategories", categoryService.getAllOutcomeCategories());
        model.addAttribute("assetTypes", assetTypeService.getAllAssetTypes()); //seems to be is redundant

        generateMonthsListForSelectAndAddToModel(model);
        generateYearsListForSelectAndAddToModel(model);

        model.addAttribute("periodStartDate", days.get(0).getDate());
    }

    private void generateYearsListForSelectAndAddToModel(Model model) {
        int currentYear = new DateTime().getYear();
        int fiveYearsAgo = new DateTime().minusYears(5).getYear();

        List<Integer> years = new ArrayList<Integer>();
        int year = currentYear;
        while (year > fiveYearsAgo) {
            years.add(year);
            year = year - 1;
        }

        model.addAttribute("years", years);
    }

    private void generateMonthsListForSelectAndAddToModel(Model model) {
        DateTime monthDT = new DateTime().monthOfYear().withMinimumValue();

        List<Date> months = new ArrayList<Date>();
        for (int i = 0; i < 12; i++) {
            monthDT = monthDT.plusMonths(1);
            months.add(monthDT.toDate());
        }

        model.addAttribute("months", months);
    }
}