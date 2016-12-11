package utils;

import domain.exceptions.SSOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class UserValidatorServiceTest {
    private UserValidatorService userValidatorService;

    @Before
    public void setUp() throws Exception {
        userValidatorService = new UserValidatorService();
    }

    @Test
    public void validate() throws Exception {
        Assert.assertTrue("failed UserValidatorServiceTest:validate",userValidatorService.validate("testlogin1","testpassswd1"));
    }


    @Test(expected = SSOException.class)
    public void validateLoginNull() throws Exception {
        userValidatorService.validateLogin(null);
    }

    @Test
    public void validateLoginTrue() throws Exception {
        assertTrue("failed CommonValidatorServiceTest:validateTrue",userValidatorService.validateLogin("testlogin1"));
    }

    @Test(expected = SSOException.class)
    public void validateLoginMinLenght() throws Exception {
        userValidatorService.validateLogin("d134");
    }

    @Test(expected = SSOException.class)
    public void validateLoginMaxLenght() throws Exception {
        userValidatorService.validateLogin("d134fdjshjsdkjhfjkshde");
    }

    @Test(expected = SSOException.class)
    public void validateLogindSpecSymbols() throws Exception {
        userValidatorService.validateLogin("sadas+");
    }

    @Test(expected = SSOException.class)
    public void validatePasswordNull() throws Exception {
        userValidatorService.validatePassword(null);
    }

    @Test
    public void validatePasswordTrue() throws Exception {
        assertTrue("failed CommonValidatorServiceTest:validateTrue",userValidatorService.validatePassword("testpassword1"));
    }

    @Test(expected = SSOException.class)
    public void validatePasswordMinLenght() throws Exception {
        userValidatorService.validatePassword("d134");
    }

    @Test(expected = SSOException.class)
    public void validatePasswordMaxLenght() throws Exception {
        userValidatorService.validatePassword("d134fdjshjsdkjhf");
    }

    @Test(expected = SSOException.class)
    public void validatePasswordSpecSymbols() throws Exception {
        userValidatorService.validatePassword("sadas+");
    }


}