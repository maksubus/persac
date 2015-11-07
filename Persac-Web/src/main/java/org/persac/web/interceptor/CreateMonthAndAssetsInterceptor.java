package org.persac.web.interceptor;

import org.persac.service.AssetService;
import org.persac.service.MonthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mzhokha
 * @since 06.02.2015
 */
public class CreateMonthAndAssetsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MonthService monthService;

    @Autowired
    private AssetService assetService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ("anonymousUser".equals(auth.getName())) {
            return true;
        }

        monthService.createCurrentMonthIfNotExists();
        assetService.createCurrentAssetsIfNotExist();

        return true;
    }
}
