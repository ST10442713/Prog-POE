package com.mycompany.chatappgui;

/**
 * Simple user record for login/registration and quota tracking.
 */
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhone;
    private int messageQuota; // how many messages this user may send

    public User() {}

    public User(String firstName, String lastName, String username, String password, String cellPhone, int messageQuota) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhone = cellPhone;
        this.messageQuota = Math.max(0, messageQuota);
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getCellPhone() { return cellPhone; }
    public int getMessageQuota() { return messageQuota; }

    public void decrementQuota() { if (messageQuota > 0) messageQuota--; }
}
