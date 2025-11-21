package com.mycompany.chatappgui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages sent/stored/disregarded messages, plus Part 3 features and JSON persistence.
 */
public class MessageManager {

    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final File storedFile = new File("storedMessages.json");

    public MessageManager() {
        loadStoredMessages();
    }

    // --- Add messages ---
    public void sendMessage(Message m) {
        sentMessages.add(m);
    }

    public void storeMessage(Message m) {
        storedMessages.add(m);
        saveStoredMessages();
    }

    public void disregardMessage(Message m) {
        disregardedMessages.add(m);
    }

    // --- Getters (arrays) ---
    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getStoredMessages() { return storedMessages; }
    public List<Message> getDisregardedMessages() { return disregardedMessages; }

    // parallel arrays derived
    public List<String> getAllMessageIDs() {
        List<String> ids = new ArrayList<>();
        ids.addAll(sentMessages.stream().map(Message::getMessageID).collect(Collectors.toList()));
        ids.addAll(storedMessages.stream().map(Message::getMessageID).collect(Collectors.toList()));
        ids.addAll(disregardedMessages.stream().map(Message::getMessageID).collect(Collectors.toList()));
        return ids;
    }
    public List<String> getAllMessageHashes() {
        List<String> hs = new ArrayList<>();
        hs.addAll(sentMessages.stream().map(Message::getMessageHash).collect(Collectors.toList()));
        hs.addAll(storedMessages.stream().map(Message::getMessageHash).collect(Collectors.toList()));
        hs.addAll(disregardedMessages.stream().map(Message::getMessageHash).collect(Collectors.toList()));
        return hs;
    }

    // --- Part 3 features ---

    /**
     * a) Display sender and recipient of all sent messages.
     * (The app only tracks the current user as sender, so sender shown generically.)
     */
    public String displaySenderRecipientAllSent() {
        if (sentMessages.isEmpty()) return "No sent messages.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Sender: (current user) | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString();
    }

    /**
     * b) Display the longest message (across all arrays — matches PoE requirement).
     */
    public Message getLongestMessageAcrossAll() {
        Message longest = null;
        int max = -1;
        for (Message m : allMessagesIterable()) {
            if (m.getPayload() != null && m.getPayload().length() > max) {
                max = m.getPayload().length();
                longest = m;
            }
        }
        return longest;
    }

    /**
     * c) Search for a message ID and return the Message (recipient + message).
     */
    public Message searchByMessageID(String id) {
        if (id == null) return null;
        for (Message m : allMessagesIterable()) if (id.equals(m.getMessageID())) return m;
        return null;
    }

    /**
     * d) Search for all messages sent to a particular recipient (across sent+stored).
     */
    public List<Message> searchMessagesByRecipient(String recipient) {
        List<Message> results = new ArrayList<>();
        for (Message m : allMessagesIterable()) {
            if (recipient != null && recipient.equals(m.getRecipient())) results.add(m);
        }
        return results;
    }

    /**
     * e) Delete a message using the message hash; persist stored changes.
     */
    public boolean deleteMessageByHash(String hash) {
        boolean removed = sentMessages.removeIf(m -> hash.equals(m.getMessageHash()));
        removed |= storedMessages.removeIf(m -> hash.equals(m.getMessageHash()));
        removed |= disregardedMessages.removeIf(m -> hash.equals(m.getMessageHash()));
        if (removed) saveStoredMessages();
        return removed;
    }

    /**
     * f) Display a report listing full details of all sent messages.
     */
    public String generateSentMessagesReport() {
        if (sentMessages.isEmpty()) return "No sent messages.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Hash: ").append(m.getMessageHash())
              .append(" | Recipient: ").append(m.getRecipient())
              .append(" | Message: ").append(m.getPayload())
              .append("\n");
        }
        return sb.toString();
    }

    // Helper to iterate through all messages (sent, stored, disregarded)
    private List<Message> allMessagesIterable() {
        List<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    // ---------- JSON persistence ----------
    private void saveStoredMessages() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(storedFile, storedMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStoredMessages() {
        try {
            if (storedFile.exists()) {
                List<Message> loaded = mapper.readValue(storedFile, new TypeReference<List<Message>>() {});
                storedMessages.clear();
                storedMessages.addAll(loaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Convenience for tests/demo: populate with the required test data ----------
    public void populateTestData() {
        // Clear current arrays (useful in tests)
        sentMessages.clear();
        storedMessages.clear();
        disregardedMessages.clear();

        // Message 1 - Sent
        Message m1 = new Message("+27834557896", "Did you get the cake?");
        sendMessage(m1);

        // Message 2 - Stored
        Message m2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        storeMessage(m2);

        // Message 3 - Disregard
        Message m3 = new Message("+27834484567", "Yohoooo, I am at your gate.");
        disregardMessage(m3);

        // Message 4 - Sent (Developer 0838884567)
        Message m4 = new Message("0838884567", "It is dinner time !");
        sendMessage(m4);

        // Message 5 - Stored
        Message m5 = new Message("+27838884567", "Ok, I am leaving without you.");
        storeMessage(m5);
    }
}
