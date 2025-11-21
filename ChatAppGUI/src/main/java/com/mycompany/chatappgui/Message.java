package com.mycompany.chatappgui;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Message POJO with auto-generated ID and hash. Has no-arg constructor for JSON (Jackson).
 */
public class Message {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private String recipient;
    private String payload;
    private String messageID;
    private String messageHash;

    public Message() {} // needed by Jackson

    public Message(String recipient, String payload) {
        this.recipient = recipient;
        this.payload = payload;
        this.messageID = generateMessageID();
        this.messageHash = generateMessageHash();
    }

    private String generateMessageID() {
        return String.format("MSG%05d", COUNTER.incrementAndGet());
    }

    private String generateMessageHash() {
        int h = (messageID + "|" + recipient + "|" + payload + "|" + System.currentTimeMillis()).hashCode();
        return "HASH" + Math.abs(h);
    }

    public void regenerateHash() { this.messageHash = generateMessageHash(); }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) { this.messageID = messageID; } // careful to use only in tests if needed

    public String getMessageHash() { return messageHash; }
    public void setMessageHash(String messageHash) { this.messageHash = messageHash; }

    public String displayMessage() {
        return "Message ID: " + messageID +
               "\nMessage Hash: " + messageHash +
               "\nRecipient: " + recipient +
               "\nMessage: " + payload;
    }
}
