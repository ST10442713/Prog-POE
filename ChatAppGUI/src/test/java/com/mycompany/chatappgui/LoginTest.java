package com.mycompany.chatappgui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    @Test
    void usernameValidation() {
        assertTrue(login.checkUserName("a_b"));
        assertFalse(login.checkUserName("abcdef"));
    }

    @Test
    void passwordValidation() {
        assertTrue(login.checkPasswordComplexity("Abcdef1!"));
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    void cellphoneValidation() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        assertFalse(login.checkCellPhoneNumber("0838968976"));
    }

    @Test
    void registerAndLoginFlow() {
        String msg = login.registerUser("Kyle", "G", "kyl_1", "Ch&&sec@ke99!", "+27838968976", 5);
        assertEquals("User successfully registered.", msg);

        boolean ok = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertTrue(ok);
        assertNotNull(login.getLoggedInUser());
        assertEquals("Welcome Kyle ,G it is great to see you.", login.returnLoginStatus());
    }

    @Test
    void duplicateUsername() {
        login.registerUser("A","B","u_1","Abcdef1!","+27830000001",3);
        String res = login.registerUser("C","D","u_1","Abcdef1!","+27830000002",3);
        assertEquals("Username already exists.", res);
    }
}
