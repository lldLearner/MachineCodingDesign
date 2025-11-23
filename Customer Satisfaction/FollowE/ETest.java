// File: CustomerRatingServiceETest.java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRatingServiceETest {

    @Test
    void testUnsortedAverageRatings() {
        CustomerRatingServiceE svc = new CustomerRatingServiceE();

        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "bob", 4.0);

        List<AgentRating> unsorted = svc.getAverageRatingsUnsorted("May");

        // Should not be sorted, order depends on insertion
        assertEquals("alice", unsorted.get(0).agent);
        assertEquals("bob", unsorted.get(1).agent);
    }

    @Test
    void testGetTotalRatings() {
        CustomerRatingServiceE svc = new CustomerRatingServiceE();

        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "alice", 3.0);

        List<AgentRating> totals = svc.getTotalRatings("May");

        assertEquals(8.0, totals.get(0).rating); // sum = 5 + 3
    }

    @Test
    void testSortedAveragesStillWorkInPartE() {
        CustomerRatingServiceE svc = new CustomerRatingServiceE();

        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "bob", 4.0);

        List<AgentRating> sorted = svc.getAverageRatings("May");

        assertEquals("alice", sorted.get(0).agent);
        assertEquals("bob", sorted.get(1).agent);
    }

    @Test
    void testCSVStillWorksInPartE() {
        CustomerRatingServiceE svc = new CustomerRatingServiceE();

        svc.acceptRating("May", "alice", 5.0);

        String csv = svc.exportMonthlyRatingsAsCSV("May");

        assertTrue(csv.contains("alice"));
    }
}
