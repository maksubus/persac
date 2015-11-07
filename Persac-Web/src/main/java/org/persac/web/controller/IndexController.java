package org.persac.web.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.persac.persistence.model.*;
import org.persac.service.*;
import org.persac.web.interceptor.LastModelAndViewInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;


/**
 * @author mzhokha
 * @since 13.11.13
 */

@Controller
@RequestMapping("/")
@ControllerAdvice
public class IndexController {

    Logger logger = Logger.getLogger(IndexController.class.getName());

    @Autowired
    private MonthService monthService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private DayService dayService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AssetTypeService assetTypeService;

    @RequestMapping("/")
    public String defaultIndex(Model model, Principal principal) throws ParseException {
        return index(model, principal);
    }

    @RequestMapping("index")
    public String index(Model model, Principal principal) throws ParseException {

        logger.log(Level.INFO, "entering index method of indexcontroller. hahaha");

        List<Day> days = dayService.getDaysForCurrentWeek();
        model.addAttribute("days", days);

        List<Week> weeks = weekService.getLast5Weeks();
        model.addAttribute("weeks", weeks);

        List<Month> months = monthService.getLast5Months();
        model.addAttribute("months", months);

        generateChartDataSetsForMonthsIncomesAndOutcomes(model, months);

        List<AssetType> assetTypes = assetTypeService.getAllActiveAssetTypes();
        model.addAttribute("assetTypes", assetTypes);

        model.addAttribute("incomeCategories", categoryService.getAllIncomeCategories());
        model.addAttribute("outcomeCategories", categoryService.getAllOutcomeCategories());

        return "index";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(HttpServletRequest request, Exception ex) {
        ModelAndView lastModelAndView = (ModelAndView)request
                .getSession().getAttribute(LastModelAndViewInterceptor.LAST_MODEL_VIEW_ATTRIBUTE);

        lastModelAndView = lastModelAndView == null? new ModelAndView("index"): lastModelAndView;
        lastModelAndView.addObject("errMsg", ex.toString());

        logger.error("Exception occurred: ", ex);

        return lastModelAndView;
    }

    private void generateChartDataSetsForMonthsIncomesAndOutcomes(Model model, List<Month> months) {
        Set<String> incomeCurrencyCodes = new HashSet<String>();
        Set<String> outcomeCurrencyCodes = new HashSet<String>();
        List<Date> monthBeginDates = new ArrayList<Date>();

        for (Month month: months) {
            gatherCurrencyCodes(incomeCurrencyCodes, month, true);
            gatherCurrencyCodes(outcomeCurrencyCodes, month, false);

            monthBeginDates.add(month.getBeginDate());
        }

        model.addAttribute("incomesChartData", buildMultiMapForMonthsAndCurrencyCodes(months, incomeCurrencyCodes, true).asMap());
        model.addAttribute("outcomesChartData", buildMultiMapForMonthsAndCurrencyCodes(months, outcomeCurrencyCodes, false).asMap());
        model.addAttribute("monthBeginDates", monthBeginDates);
    }

    private void gatherCurrencyCodes(Set<String> currencyCodes, Month month, boolean incomes) {
        Collection<? extends MonthRecord> monthRecords = incomes ? month.getIncomes() : month.getOutcomes();
        for (MonthRecord monthRecord : monthRecords) {
            currencyCodes.add(monthRecord.getCurrencyCode());
        }
    }

    /**
     * Building multimap so that each currency has its value in each month.
     * If month does not have currency record the value will be 0.
     * For example
     * {EUR=[0.0, 567.0, 0.0, 2000.0], PLN=[0.0, 7647.2, 6457.0, 6500.0], USD=[450.0, 0.0, 0.0, 270.0]}
     *
     * @param months all months which data is being gathering for
     * @param currencyCodes set of currency codes which are present at least in one month
     * @param incomes is calculating for incomes?
     * @return
     */
    private ListMultimap<String, BigDecimal> buildMultiMapForMonthsAndCurrencyCodes(List<Month> months, Set<String> currencyCodes, boolean incomes) {
        ListMultimap<String, BigDecimal> recordsForAllMonths = ArrayListMultimap.create();
        for (String currencyCode: currencyCodes) {
            for (Month month: months) {
                Collection<? extends MonthRecord> monthRecords = incomes ? month.getIncomes(): month.getOutcomes();

                boolean hasRecordForThisCurrency = false;
                for (MonthRecord monthRecord : monthRecords) {
                    if (currencyCode.equals(monthRecord.getCurrencyCode())) {
                        hasRecordForThisCurrency = true;
                        //if income then positive value, else negative
                        BigDecimal chartValue = incomes ? monthRecord.getAmount() : new BigDecimal(0).subtract(monthRecord.getAmount());
                        recordsForAllMonths.put(monthRecord.getCurrencyCode(), chartValue);
                        break;
                    }
                }

                if (!hasRecordForThisCurrency) {
                    recordsForAllMonths.put(currencyCode, new BigDecimal(0));
                }
            }
        }

        return recordsForAllMonths;
    }


}
