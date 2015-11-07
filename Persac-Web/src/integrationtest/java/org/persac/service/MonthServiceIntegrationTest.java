package org.persac.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.persac.persistence.dao.MonthDao;
import org.persac.persistence.model.Month;
import org.persac.web.ApplicationContextAwareTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author mzhokha
 * @since 07.02.2015
 */

//TODO: move to Persac-Service module
//TODO: integration tests need only for main flows. Other flows must be covered by unit tests.
public class MonthServiceIntegrationTest extends ApplicationContextAwareTest {

    @Autowired
    MonthDao monthDao;

    @Autowired
    MonthService monthService;

    @Autowired
    UserService userService;

    @Before
    public void setUp() {
        userService.saveUserDetailsInSession("maksym");
    }

    @Test
    public void testCreateCurrentMonthIfNotExists_createCurrentMonthIfNotExists() throws Exception {
        monthDao.deleteAll();

        monthService.createCurrentMonthIfNotExists();

        Month month = monthDao.getCurrentMonth();

        assertNotNull(month);
    }

    @Test
    public void testCreateCurrentMonthIfNotExists_doNotCreateMonthIfCurrentMonthAlreadyExists() throws Exception {
        monthDao.deleteAll();
        Date currentMonthFirstDayDate = new DateTime(new Date()).dayOfMonth().withMinimumValue().toDateMidnight().toDate();
        Integer insertedMonthId = monthDao.save(new Month(currentMonthFirstDayDate));

        monthService.createCurrentMonthIfNotExists();

        Month month = monthDao.getCurrentMonth();

        assertEquals(insertedMonthId, month.getId());
        assertEquals(1, monthDao.getAll().size());
    }
}
