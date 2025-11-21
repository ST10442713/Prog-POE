package com.mycompany.chatappgui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Registration and login logic with validation helpers.
 */
public class Login {
    private final List<User> users = new ArrayList<>();
    private User loggedInUser = null;

    public Login() {
        // no default users by design; tests/register flow create users
    }

    public boolean checkUserName(String username) {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null) return false;
        // At least 8 chars, at least one uppercase, one lowercase, one digit, one special char
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

    public boolean checkCellPhoneNumber(String cell) {
        if (cell == null) return false;
        // +<countrycode><number>, country 1-3 digits, subscriber 4-12 digits
        return Pattern.matches("^\\+\\d{1,3}\\d{4,12}$", cell);
    }

    /**
     * Register a user. Returns a human-readable message for UI/tests.
     */
    public String registerUser(String firstName, String lastName, String username, String password, String cellPhone, int messageQuota) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, an uppercase letter, a lowercase letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cellPhone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        // Unique username
        for (User u : users) if (u.getUsername().equals(username)) return "Username already exists.";

        users.add(new User(firstName, lastName, username, password, cellPhone, messageQuota));
        return "User successfully registered.";
    }

    public boolean loginUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedInUser = u;
                return true;
            }
        }
        loggedInUser = null;
        return false;
    }

    public String returnLoginStatus() {
        if (loggedInUser != null) {
            return String.format("Welcome %s ,%s it is great to see you.", loggedInUser.getFirstName(), loggedInUser.getLastName());
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public User getLoggedInUser() { return loggedInUser; }

    // helpers for tests and admin
    public void clearUsers() { users.clear(); loggedInUser = null; }
    public List<User> getUsers() { return users; }
}
