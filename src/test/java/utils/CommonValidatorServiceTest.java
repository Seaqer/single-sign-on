package utils;

import domain.exceptions.SSOException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class CommonValidatorServiceTest {
    private CommonValidatorService commonValidatorService;

    @Before
    public void setUp() throws Exception {
        commonValidatorService = new CommonValidatorService();
    }

    @Test(expected = SSOException.class)
    public void validateNull() throws Exception {
        commonValidatorService.validate(null);
    }

    @Test
    public void validateTrue() throws Exception {
        assertTrue("failed CommonValidatorServiceTest:validateTrue",commonValidatorService.validate("testlogin1"));
    }

    @Test(expected = SSOException.class)
    public void validateFalse() throws Exception {
        commonValidatorService.validate("123");
    }

}