package org.persac.web.controller;

import org.persac.persistence.model.Week;
import org.persac.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mzhokha
 * @since 30.03.14
 */
@Controller
public class WeekController {

    @Autowired
    private WeekService weekService;

    @RequestMapping("weekly")
    public String weekly(Model model) throws ParseException {
        List<Week> weeks = weekService.getAllWeeks();

        model.addAttribute("weeks", weeks);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        List<String> mondayDates = new ArrayList<String>(weeks.size());
        for(Week week: weeks) {
            mondayDates.add(sdf.format(week.getMondayDate()));
        }

        model.addAttribute("mondayDates", mondayDates);

        return "weekly";
    }


    @RequestMapping("recalculate-weeks")
    public String recalculateWeeks() throws ParseException {
        weekService.recalculateAndSaveWeeks();

        return "redirect:/index";
    }
}
