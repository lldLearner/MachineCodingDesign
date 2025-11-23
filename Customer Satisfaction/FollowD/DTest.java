// File: CustomerRatingServiceDTest.java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRatingServiceDTest {

    @Test
    void testCSVExportFormat() {
        CustomerRatingServiceD svc = new CustomerRatingServiceD();

        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "bob", 3.0);

        String csv = svc.exportMonthlyRatingsAsCSV("May");

        String expectedHeader = "agent,averageRating";
        assertTrue(csv.startsWith(expectedHeader));
        assertTrue(csv.contains("alice"));
        assertTrue(csv.contains("bob"));
    }

    @Test
    void testCSVExportSortedCorrectly() {
        CustomerRatingServiceD svc = new CustomerRatingServiceD();

        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "bob", 1.0);

        String csv = svc.exportMonthlyRatingsAsCSV("May");

        String[] lines = csv.split("\n");
        assertTrue(lines[1].startsWith("alice"));
        assertTrue(lines[2].startsWith("bob"));
    }
}
