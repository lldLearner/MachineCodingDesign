// File: CustomerRatingServiceCTest.java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRatingServiceCTest {

    @Test
    void testMonthlyRatingsStoredSeparately() {
        CustomerRatingServiceC svc = new CustomerRatingServiceC();

        svc.acceptRating("May", "agent1", 5.0);
        svc.acceptRating("June", "agent1", 2.0);

        assertEquals(5.0, svc.getAverageRatings("May").get(0).rating);
        assertEquals(2.0, svc.getAverageRatings("June").get(0).rating);
    }

    @Test
    void testHighestRatedAgentForMonth() {
        CustomerRatingServiceC svc = new CustomerRatingServiceC();

        svc.acceptRating("May", "A", 4.0);
        svc.acceptRating("May", "B", 5.0);

        Optional<AgentRating> top = svc.getHighestRatedAgentForMonth("May");

        assertTrue(top.isPresent());
        assertEquals("B", top.get().agent);
        assertEquals(5.0, top.get().rating);
    }

    @Test
    void testHighestRatedAgentEmptyMonth() {
        CustomerRatingServiceC svc = new CustomerRatingServiceC();
        assertTrue(svc.getHighestRatedAgentForMonth("Jan").isEmpty());
    }
}
