package View;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for the score display bug fixed in commit 1639299
 * ("khac phuc loi hien thi diem").
 *
 * BUG DESCRIPTION:
 * The server sends rank data as List<String> where each entry is formatted as
 * "[username, score]" (e.g., "[player1, 100]"). The old RankView constructor
 * expected List<Object[]> and cast each entry to Object[], causing a
 * ClassCastException at runtime. This prevented the ranking table from being
 * displayed at all.
 *
 * THE FIX:
 * RankView was changed to accept List<String> and parse each string entry by:
 * 1. Removing bracket characters '[' and ']'
 * 2. Splitting by ',' to separate username and score
 * 3. Trimming whitespace and parsing the score as an integer
 *
 * These tests reproduce the original data format and verify the fix works.
 */
public class RankViewRegressionTest {

    /**
     * Helper to extract the JTable's DefaultTableModel from a RankView instance
     * via reflection, so we can inspect the parsed data without needing a live GUI.
     */
    private DefaultTableModel getTableModel(RankView rankView) throws Exception {
        Field tableField = RankView.class.getDeclaredField("table");
        tableField.setAccessible(true);
        JTable table = (JTable) tableField.get(rankView);
        return (DefaultTableModel) table.getModel();
    }

    /**
     * Simulates the exact data format the server sends: List<String> with
     * entries like "[username, score]". Before the fix, this would cause a
     * ClassCastException because RankView tried to cast String to Object[].
     */
    @Test
    @DisplayName("Regression: RankView should accept List<String> without ClassCastException")
    void testRankViewAcceptsStringListWithoutException() {
        // Arrange: simulate server response format (List<String> with bracket-wrapped entries)
        List<String> serverRankData = Arrays.asList(
            "[player1, 100]",
            "[player2, 85]",
            "[player3, 70]"
        );

        // Act & Assert: constructing RankView should NOT throw ClassCastException
        // Before the fix, this would throw because the old constructor tried:
        //   Object[] entry = rankList.get(i);  // ClassCastException: String cannot be cast to Object[]
        assertDoesNotThrow(() -> {
            RankView rankView = new RankView(serverRankData);
        }, "RankView should accept List<String> without throwing ClassCastException");
    }

    /**
     * Verifies that the string parsing logic correctly extracts username and score
     * from the server's "[username, score]" format.
     */
    @Test
    @DisplayName("Regression: Score values should be correctly parsed from string format")
    void testScoreParsingFromStringFormat() throws Exception {
        // Arrange
        List<String> serverRankData = Arrays.asList(
            "[player1, 100]",
            "[player2, 85]",
            "[player3, 70]"
        );

        // Act
        RankView rankView = new RankView(serverRankData);
        DefaultTableModel model = getTableModel(rankView);

        // Assert: verify correct number of rows
        assertEquals(3, model.getRowCount(), "Should have 3 rank entries");

        // Assert: verify each row's data is correctly parsed
        // Column 0 = rank number, Column 1 = username, Column 2 = score
        assertEquals(1, model.getValueAt(0, 0));
        assertEquals("player1", model.getValueAt(0, 1));
        assertEquals(100, model.getValueAt(0, 2));

        assertEquals(2, model.getValueAt(1, 0));
        assertEquals("player2", model.getValueAt(1, 1));
        assertEquals(85, model.getValueAt(1, 2));

        assertEquals(3, model.getValueAt(2, 0));
        assertEquals("player3", model.getValueAt(2, 1));
        assertEquals(70, model.getValueAt(2, 2));
    }

    /**
     * Verifies that rank data with extra whitespace is handled correctly
     * (the fix uses trim() to handle this).
     */
    @Test
    @DisplayName("Regression: Score parsing should handle extra whitespace")
    void testScoreParsingHandlesWhitespace() throws Exception {
        // Arrange: entries with extra whitespace
        List<String> serverRankData = Arrays.asList(
            "[ player1 ,  100 ]",
            "[player2,85]"
        );

        // Act
        RankView rankView = new RankView(serverRankData);
        DefaultTableModel model = getTableModel(rankView);

        // Assert
        assertEquals(2, model.getRowCount());
        assertEquals("player1", model.getValueAt(0, 1));
        assertEquals(100, model.getValueAt(0, 2));
        assertEquals("player2", model.getValueAt(1, 1));
        assertEquals(85, model.getValueAt(1, 2));
    }

    /**
     * Verifies that an empty rank list does not cause errors.
     * In MainWindow, an empty list shows a "no data" dialog instead of RankView,
     * but RankView should still handle it gracefully.
     */
    @Test
    @DisplayName("Regression: Empty rank list should be handled gracefully")
    void testEmptyRankList() {
        List<String> emptyRankData = new ArrayList<>();

        assertDoesNotThrow(() -> {
            RankView rankView = new RankView(emptyRankData);
            DefaultTableModel model = getTableModel(rankView);
            assertEquals(0, model.getRowCount(), "Empty list should produce 0 rows");
        });
    }

    /**
     * Verifies that rank numbers are assigned sequentially starting from 1.
     * This was part of the display logic that the bug prevented from working.
     */
    @Test
    @DisplayName("Regression: Rank numbers should be sequential starting from 1")
    void testRankNumbersAreSequential() throws Exception {
        List<String> serverRankData = Arrays.asList(
            "[alice, 200]",
            "[bob, 150]",
            "[charlie, 100]",
            "[diana, 50]"
        );

        RankView rankView = new RankView(serverRankData);
        DefaultTableModel model = getTableModel(rankView);

        for (int i = 0; i < model.getRowCount(); i++) {
            assertEquals(i + 1, model.getValueAt(i, 0),
                "Rank number at row " + i + " should be " + (i + 1));
        }
    }

    /**
     * Demonstrates that the old approach (casting String to Object[]) would fail.
     * This is the exact bug that was present before commit 1639299.
     */
    @Test
    @DisplayName("Regression: Old Object[] cast approach should fail with server data format")
    void testOldApproachWouldFailWithClassCastException() {
        // Simulate server data: List containing String elements
        List<String> serverRankData = Arrays.asList(
            "[player1, 100]",
            "[player2, 85]"
        );

        // The old buggy code tried to do this:
        //   Object[] entry = (Object[]) rankList.get(i);  // ClassCastException!
        //   String username = (String) entry[0];
        //   int score = (Integer) entry[1];
        //
        // This demonstrates the bug: a String cannot be cast to Object[]
        assertThrows(ClassCastException.class, () -> {
            for (int i = 0; i < serverRankData.size(); i++) {
                // This simulates what the old code did - it will throw ClassCastException
                Object rawEntry = serverRankData.get(i);
                Object[] entry = (Object[]) rawEntry; // BUG: ClassCastException here
                String username = (String) entry[0];
                int score = (Integer) entry[1];
            }
        }, "Old approach of casting String to Object[] should throw ClassCastException");
    }

    /**
     * Verifies that the new parsing approach (the fix) correctly handles
     * the same data that would crash the old code.
     */
    @Test
    @DisplayName("Regression: New string parsing approach should work with server data format")
    void testNewParsingApproachWorksCorrectly() {
        List<String> serverRankData = Arrays.asList(
            "[player1, 100]",
            "[player2, 85]"
        );

        // The fixed code does this:
        //   String entry = rankList.get(i);
        //   entry = entry.replace("[", "").replace("]", "").trim();
        //   String[] parts = entry.split(",");
        //   String username = parts[0].trim();
        //   int score = Integer.parseInt(parts[1].trim());
        assertDoesNotThrow(() -> {
            for (int i = 0; i < serverRankData.size(); i++) {
                String entry = serverRankData.get(i);
                entry = entry.replace("[", "").replace("]", "").trim();
                String[] parts = entry.split(",");
                String username = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());

                assertNotNull(username);
                assertTrue(score >= 0);
            }
        }, "New string parsing approach should work without exceptions");
    }
}
