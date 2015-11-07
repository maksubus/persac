package org.persac.service.operation;

import org.joda.time.DateTime;
import org.persac.persistence.model.Item;
import org.persac.service.AssetService;
import org.persac.service.ItemService;
import org.persac.service.MonthService;
import org.persac.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mzhokha
 * @since 06.02.2015
 */
public class ItemOperationService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private MonthService monthService;

    @Autowired
    private AssetService assetService;

    public ItemOperationService() {
    }

    public ItemOperationService(ItemService itemService,
                                WeekService weekService,
                                MonthService monthService,
                                AssetService assetService) {
        this.itemService = itemService;
        this.weekService = weekService;
        this.monthService = monthService;
        this.assetService = assetService;
    }

    @Transactional
    public void saveNewItem(String categoryName,
                             Integer subCategoryId,
                             Integer assetTypeId,
                             BigDecimal amount,
                             String description,
                             Date actionDate) {
        Item item = itemService.save(amount, description, actionDate, categoryName, subCategoryId, assetTypeId);

        weekService.updateWeekAfterCreatingItem(item);
        monthService.updateMonthAfterCreatingItem(item);
        assetService.updateAssetAfterCreatingItem(item);
    }

    @Transactional
    public void updateItem(Integer id,
                            String categoryName,
                            Integer subCategoryId,
                            Integer assetTypeId,
                            BigDecimal amount,
                            String description,
                            Date actionDate,
                            Item oldItem) {
        Item item = itemService.update(id, amount, description, actionDate, categoryName, subCategoryId, assetTypeId);

        weekService.updateWeekAfterUpdatingItem(item, oldItem);
        monthService.updateMonthAfterUpdatingItem(item, oldItem);
        assetService.updateAssetAfterUpdatingItem(oldItem, item);
    }

    @Transactional
    public void deleteItem(Integer id) {
        Item item = itemService.getIncomeById(id);
        if (item == null) {
            item = itemService.getOutcomeById(id);
        }

        weekService.updateWeekBeforeDeletingItem(item);
        monthService.updateMonthBeforeDeletingItem(item);
        assetService.updateAssetBeforeDeletingItemIfCurrentMonthItem(item);

        itemService.deleteById(id);
    }
}
