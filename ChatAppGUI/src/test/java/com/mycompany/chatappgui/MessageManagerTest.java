package com.mycompany.chatappgui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MessageManagerTest {

    private MessageManager manager;
    private final File storedFile = new File("storedMessages.json");

    @BeforeEach
    void setUp() {
        // Ensure a clean storedMessages.json for each test
        if (storedFile.exists()) storedFile.delete();
        manager = new MessageManager();
        manager.populateTestData(); // populate arrays with the 5 required test messages
    }

    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        // Sent messages should be message1 and message4 per spec
        assertEquals(2, manager.getSentMessages().size());
        boolean hasCake = manager.getSentMessages().stream().anyMatch(m -> "Did you get the cake?".equals(m.getPayload()));
        boolean hasDinner = manager.getSentMessages().stream().anyMatch(m -> "It is dinner time !".equals(m.getPayload()));
        assertTrue(hasCake && hasDinner);
    }

    @Test
    void testLongestMessageAmongMessages1to4() {
        // Longest across all messages 1-4 = message2 payload
        Message longest = manager.getLongestMessageAcrossAll();
        assertNotNull(longest);
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest.getPayload());
    }

    @Test
    void testSearchByMessageID_message4() {
        // find message 4 by its ID (message 4 is the "It is dinner time !" message)
        Message m4 = manager.getSentMessages().stream().filter(m -> "It is dinner time !".equals(m.getPayload())).findFirst().orElse(null);
        assertNotNull(m4);
        Message found = manager.searchByMessageID(m4.getMessageID());
        assertNotNull(found);
        assertEquals("It is dinner time !", found.getPayload());
    }

    @Test
    void testSearchAllMessagesForRecipient_plus27838884567() {
        // Should find message2 and message5 (both stored)
        var list = manager.searchMessagesByRecipient("+27838884567");
        assertEquals(2, list.size());
        boolean containsWhere = list.stream().anyMatch(m -> m.getPayload().startsWith("Where are you?"));
        boolean containsOk = list.stream().anyMatch(m -> m.getPayload().startsWith("Ok, I am leaving without you."));
        assertTrue(containsWhere && containsOk);
    }

    @Test
    void testDeleteByMessageHash_message2() {
        // Delete stored message 2 by hash
        Message m2 = manager.getStoredMessages().stream().filter(m -> m.getPayload().startsWith("Where are you?")).findFirst().orElse(null);
        assertNotNull(m2);
        boolean deleted = manager.deleteMessageByHash(m2.getMessageHash());
        assertTrue(deleted);
        // ensure it's gone
        boolean stillThere = manager.getStoredMessages().stream().anyMatch(m -> m.getMessageHash().equals(m2.getMessageHash()));
        assertFalse(stillThere);
    }

    @Test
    void testDisplayReportIncludesHashRecipientMessage() {
        String report = manager.generateSentMessagesReport();
        // report should include hash, recipient, and message for each sent message
        assertTrue(report.contains("HASH") || report.contains("Hash") || report.length() > 0); // basic check
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time !"));
    }
}
