package org.persac.web.fullcycle;

import org.junit.Test;
import org.persac.persistence.dao.AssetDao;
import org.persac.persistence.model.Asset;
import org.persac.service.AssetService;
import org.persac.service.AssetTypeService;
import org.persac.service.ItemService;
import org.persac.service.MonthService;
import org.persac.service.UserService;
import org.persac.web.ApplicationContextAwareTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 25.08.2014
 */
public class AssetIntegrationTest extends ApplicationContextAwareTest {

    @Autowired
    AssetDao assetDao;

    @Autowired
    UserService userService;

    @Autowired
    AssetService assetService;

    @Autowired
    AssetTypeService assetTypeService;

    @Autowired
    MonthService monthService;

    @Autowired
    ItemService itemService;

    @Test
    public void bla() {
        userService.saveUserDetailsInSession("maksym");

        List<Asset> assets = assetDao.getAllCurrentAssets();

        itemService.save(777D, "integration test", new Date(), 1, 3);

        System.out.println("bla");
    }


}