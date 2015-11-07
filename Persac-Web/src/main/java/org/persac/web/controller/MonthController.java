package org.persac.web.controller;

import org.persac.persistence.model.Month;
import org.persac.service.MonthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.List;

/**
 * @author mzhokha
 * @since 30.03.14
 */
@Controller
public class MonthController {

    @Autowired
    private MonthService monthService;

    @RequestMapping("monthly")
    public String monthly(Model model) throws ParseException {
        List<Month> months = monthService.getAllMonths();

        model.addAttribute("months", months);

        return "monthly";
    }

    @RequestMapping("recalculate-months")
    public String recalculateMonths() throws ParseException {
        monthService.recalculateAndSaveMonths();

        return "redirect:/index";
    }
}
