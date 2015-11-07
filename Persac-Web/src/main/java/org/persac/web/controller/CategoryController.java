package org.persac.web.controller;

import org.persac.persistence.model.Category;
import org.persac.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author mzhokha
 * @since 29.03.14
 */
@Controller
@RequestMapping("category/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "subCategories/selectedCategory/{selectedCategory}", method = RequestMethod.GET)
    public @ResponseBody List<? extends Category> getSubCategoriesBySelectedCategory(@PathVariable String selectedCategory) {
        List<? extends Category> categories = null;

        if (selectedCategory.equals("Income")) {
            categories = categoryService.getAllIncomeCategories();
        } else if (selectedCategory.equals("Outcome")) {
            categories = categoryService.getAllOutcomeCategories();
        }

        return categories;
    }
}
