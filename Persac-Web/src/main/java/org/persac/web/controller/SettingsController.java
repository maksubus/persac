package org.persac.web.controller;


import org.persac.persistence.model.IncomeCategory;
import org.persac.persistence.model.OutcomeCategory;
import org.persac.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author mzhokha
 * @since 04.05.14
 */

@Controller
@RequestMapping("/")
public class SettingsController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/settings")
    public String getSettingsPage(Model model) {
        List<IncomeCategory> inCategories = categoryService.getAllIncomeCategories();
        List<OutcomeCategory> outCategories = categoryService.getAllOutcomeCategories();

        model.addAttribute("inCategories", inCategories);
        model.addAttribute("outCategories", outCategories);

        return "settings";
    }

    @RequestMapping(value ="/save-settings", method = RequestMethod.POST)
    public String saveSettings(Model model,
                               @RequestParam(value = "inCategories[]", required = false) Integer[] inCategoriesIds,
                               @RequestParam(value = "outCategories[]", required = false) Integer[] outCategoriesIds) {

        categoryService.updateCategoriesActiveStatus(inCategoriesIds, outCategoriesIds);

        List<IncomeCategory> inCategories = categoryService.getAllIncomeCategories();
        List<OutcomeCategory> outCategories = categoryService.getAllOutcomeCategories();

        model.addAttribute("inCategories", inCategories);
        model.addAttribute("outCategories", outCategories);

        return "redirect:/settings";
    }
}
