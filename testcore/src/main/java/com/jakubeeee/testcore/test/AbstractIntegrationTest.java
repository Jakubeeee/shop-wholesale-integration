package com.jakubeeee.testcore.test;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.jakubeeee.Application;
import com.jakubeeee.testcore.container.CustomPostgreSQL12Container;
import com.jakubeeee.testutils.marker.IntegrationTestCategory;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Abstract superclass that provides basic functionality for integration tests.
 */
@Category(IntegrationTestCategory.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
@PropertySource(value = "classpath:generated/testdatabase-generated.properties", ignoreResourceNotFound = true)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

    private static final boolean AUTOCONFIGURE_TEST_DATABASE = true;

    static {
        if (AUTOCONFIGURE_TEST_DATABASE)
            CustomPostgreSQL12Container.getInstance().start();
    }

}
