package test

import main.ActiveDirectoryConnector
import org.jmock.integration.junit4.*;
import org.junit.Rule
import org.junit.Test

/**
 * Created by jsh3571 on 06/01/2017.
 */
class ActiveDirectoryConnectorTest extends GroovyTestCase {

    @Rule
    private JUnit4Mockery context = new JUnit4Mockery();

    private ActiveDirectoryConnector adc = context;

    @Test
    void testSetDomain() {
        adc.setDomain("addr");
        // assertThat();
    }

    @Test
    void testSetAttrs() {

    }

    @Test
    void testGetUsers() {

    }

    @Test
    void testClose() {

    }
}
