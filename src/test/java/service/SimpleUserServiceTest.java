package service;

import interfaces.repository.custom.UserRepository;
import interfaces.utils.PasswordManager;
import interfaces.utils.UserValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SimpleUserServiceTest {
    private PasswordManager passwordManager;
    private UserValidator userValidator;
    private UserRepository userRepository;
    private SimpleUserService simpleUserService;

    @Before
    public void setUp(){
        passwordManager = mock(PasswordManager.class);
        userValidator = mock(UserValidator.class);
        userRepository = mock(UserRepository.class);
        simpleUserService = new SimpleUserService(passwordManager, userValidator, userRepository);
    }

    @After
    public void tearDown() {
        passwordManager = null;
        userValidator = null;
        userRepository = null;
    }

    @Test
    public void registrationUser() throws Exception {
        assertTrue("SimpleUserServiceTest:registrationUser failed",simpleUserService.registrationUser("test123","password123"));
    }

    @Test
    public void updateLogin() throws Exception {
    }

    @Test
    public void updatePassword() throws Exception {

    }

    @Test
    public void openUser() throws Exception {

    }

    @Test
    public void closeUser() throws Exception {

    }

    @Test
    public void searchUser() throws Exception {

    }

    @Test
    public void hasPremission() throws Exception {

    }

}