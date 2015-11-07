package org.persac.web;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.persac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author mzhokha
 * @since 25.08.2014
 *
 * Base class fo unit tests with spring context.
 * <p>
 * Each method is run within a transaction which is rolled back when method is over.
 *
 * @see org.springframework.test.context.transaction.TransactionConfiguration
 * @see org.springframework.transaction.annotation.Transactional
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-Service.xml",
        "classpath:applicationContext-Persistence.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@Transactional
@WebAppConfiguration("classpath:app-servlet.xml")
@ActiveProfiles("dev")
public abstract class ApplicationContextAwareTest {

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        userService.saveUserDetailsInSession("maksym");
    }

    @After
    public void afterMethod() {
        // nothing
    }

}
