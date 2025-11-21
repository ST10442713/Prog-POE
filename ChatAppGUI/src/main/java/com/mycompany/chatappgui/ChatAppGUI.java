package com.mycompany.chatappgui;

import javax.swing.*;
import java.util.List;

/**
 * Option A - Menu-driven popup (JOptionPane).
 * Follows original flow: register -> login -> operations (send/store/disregard/view/search/delete/report)
 */
public class ChatAppGUI {

    private final Login login = new Login();
    private final MessageManager manager = new MessageManager();
    private User currentUser = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatAppGUI().start());
    }

    public void start() {
        // 1) Registration / Login loop
        while (currentUser == null) {
            String[] options = {"Register", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Choose:", "ChatApp",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (choice == 0) doRegister();
            else if (choice == 1) doLogin();
            else return;
        }

        // 2) Main menu loop (Option A)
        boolean running = true;
        while (running) {
            String menu =
                    "1. Send message\n" +
                    "2. Store message\n" +
                    "3. Disregard message\n" +
                    "4. View sent messages\n" +
                    "5. View stored messages\n" +
                    "6. View disregarded messages\n" +
                    "7. Display sender & recipient of all sent messages\n" +
                    "8. Display longest message (all messages)\n" +
                    "9. Search by message ID\n" +
                    "10. Search messages by recipient\n" +
                    "11. Delete a message by hash\n" +
                    "12. Display full sent messages report\n" +
                    "13. Logout / Exit";

            String input = JOptionPane.showInputDialog(null, menu, "Select option (enter number)", JOptionPane.PLAIN_MESSAGE);
            if (input == null) break;
            int option;
            try {
                option = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
                continue;
            }

            switch (option) {
                case 1 -> doSendMessage();
                case 2 -> doStoreMessage();
                case 3 -> doDisregardMessage();
                case 4 -> JOptionPane.showMessageDialog(null, manager.generateSentMessagesReport(), "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
                case 5 -> showMessageList(manager.getStoredMessages(), "Stored Messages");
                case 6 -> showMessageList(manager.getDisregardedMessages(), "Disregarded Messages");
                case 7 -> JOptionPane.showMessageDialog(null, manager.displaySenderRecipientAllSent(), "Senders & Recipients", JOptionPane.INFORMATION_MESSAGE);
                case 8 -> {
                    Message longest = manager.getLongestMessageAcrossAll();
                    JOptionPane.showMessageDialog(null, longest == null ? "No messages." : longest.displayMessage(), "Longest Message", JOptionPane.INFORMATION_MESSAGE);
                }
                case 9 -> doSearchByMessageID();
                case 10 -> doSearchByRecipient();
                case 11 -> doDeleteByHash();
                case 12 -> JOptionPane.showMessageDialog(null, manager.generateSentMessagesReport(), "Full Report", JOptionPane.INFORMATION_MESSAGE);
                case 13 -> { running = false; JOptionPane.showMessageDialog(null, "Goodbye!"); }
                default -> JOptionPane.showMessageDialog(null, "Choose a valid option.");
            }
        }
    }

    private void doRegister() {
        String first = JOptionPane.showInputDialog("Enter first name:");
        if (first == null) return;
        String last = JOptionPane.showInputDialog("Enter last name:");
        if (last == null) return;
        String username = JOptionPane.showInputDialog("Enter username (must contain '_' and max 5 chars):");
        if (username == null) return;
        String password = JOptionPane.showInputDialog("Enter password (min 8 chars, uppercase, digit, special):");
        if (password == null) return;
        String cell = JOptionPane.showInputDialog("Enter cellphone (with international code, e.g. +27838968976):");
        if (cell == null) return;
        String quotaStr = JOptionPane.showInputDialog("How many messages should this user be allowed to send? (enter integer)");
        if (quotaStr == null) return;
        int quota;
        try { quota = Integer.parseInt(quotaStr.trim()); }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number for quota; using 5 as default.");
            quota = 5;
        }

        String res = login.registerUser(first, last, username, password, cell, quota);
        JOptionPane.showMessageDialog(null, res);
    }

    private void doLogin() {
        String username = JOptionPane.showInputDialog("Login - Enter username:");
        if (username == null) return;
        String password = JOptionPane.showInputDialog("Login - Enter password:");
        if (password == null) return;

        boolean ok = login.loginUser(username, password);
        JOptionPane.showMessageDialog(null, login.returnLoginStatus());
        if (ok) currentUser = login.getLoggedInUser();
    }

    private void doSendMessage() {
        if (currentUser == null) { JOptionPane.showMessageDialog(null, "Not logged in."); return; }
        if (currentUser.getMessageQuota() <= 0) { JOptionPane.showMessageDialog(null, "You have no messages left to send."); return; }

        String recipient = JOptionPane.showInputDialog("Enter recipient (with +countrycode):");
        if (recipient == null) return;
        String payload = JOptionPane.showInputDialog("Enter message (max 250 chars):");
        if (payload == null) return;
        if (payload.length() > 250) {
            JOptionPane.showMessageDialog(null, "Message exceeds 250 characters. Use store instead.");
            return;
        }
        Message msg = new Message(recipient, payload);
        manager.sendMessage(msg);
        currentUser.decrementQuota();
        JOptionPane.showMessageDialog(null, "Message sent. Messages remaining: " + currentUser.getMessageQuota());
    }

    private void doStoreMessage() {
        String recipient = JOptionPane.showInputDialog("Enter recipient (with +countrycode):");
        if (recipient == null) return;
        String payload = JOptionPane.showInputDialog("Enter message (this will be stored):");
        if (payload == null) return;
        Message msg = new Message(recipient, payload);
        manager.storeMessage(msg);
        JOptionPane.showMessageDialog(null, "Message stored to JSON.");
    }

    private void doDisregardMessage() {
        String recipient = JOptionPane.showInputDialog("Enter recipient (with +countrycode):");
        if (recipient == null) return;
        String payload = JOptionPane.showInputDialog("Enter message (this will be disregarded):");
        if (payload == null) return;
        Message msg = new Message(recipient, payload);
        manager.disregardMessage(msg);
        JOptionPane.showMessageDialog(null, "Message disregarded.");
    }

    private void showMessageList(List<Message> list, String title) {
        if (list.isEmpty()) { JOptionPane.showMessageDialog(null, "No " + title.toLowerCase() + "."); return; }
        StringBuilder sb = new StringBuilder();
        for (Message m : list) sb.append(m.displayMessage()).append("\n-----------------\n");
        JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void doSearchByMessageID() {
        String id = JOptionPane.showInputDialog("Enter message ID to search:");
        if (id == null) return;
        Message m = manager.searchByMessageID(id);
        JOptionPane.showMessageDialog(null, m == null ? "Message not found." : m.displayMessage());
    }

    private void doSearchByRecipient() {
        String recipient = JOptionPane.showInputDialog("Enter recipient to search:");
        if (recipient == null) return;
        List<Message> found = manager.searchMessagesByRecipient(recipient);
        if (found.isEmpty()) JOptionPane.showMessageDialog(null, "No messages found for " + recipient);
        else showMessageList(found, "Messages for " + recipient);
    }

    private void doDeleteByHash() {
        String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
        if (hash == null) return;
        boolean ok = manager.deleteMessageByHash(hash);
        JOptionPane.showMessageDialog(null, ok ? "Message deleted." : "Message hash not found.");
    }
}
