package utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HashPasswordManagerTest {
    private HashPasswordManager hashPasswordManager;

    @Before
    public void setUp() throws Exception {
        hashPasswordManager = new HashPasswordManager();
    }

    @Test
    public void getPassword() throws Exception {
        assertEquals("failed HashPasswordManager","\t�k�F!�s��N�&'��",hashPasswordManager.getPassword("test"));
    }

}