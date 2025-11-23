// File: CustomerRatingServiceATest.java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRatingServiceATest {

    @Test
    void testAcceptValidRating() {
        CustomerRatingServiceA svc = new CustomerRatingServiceA();
        assertTrue(svc.acceptRating("agent1", 5.0));
    }

    @Test
    void testRejectInvalidRating() {
        CustomerRatingServiceA svc = new CustomerRatingServiceA();
        assertFalse(svc.acceptRating("agent1", 6.0));
        assertFalse(svc.acceptRating("agent1", 0.0));
    }

    @Test
    void testAverageRatingCalculation() {
        CustomerRatingServiceA svc = new CustomerRatingServiceA();
        svc.acceptRating("agent1", 4.0);
        svc.acceptRating("agent1", 2.0);

        List<AgentRating> result = svc.getAverageRatings();
        assertEquals(1, result.size());
        assertEquals(3.0, result.get(0).rating);
    }

    @Test
    void testSortingDescendingOrder() {
        CustomerRatingServiceA svc = new CustomerRatingServiceA();

        svc.acceptRating("A", 5.0);
        svc.acceptRating("B", 3.0);

        List<AgentRating> result = svc.getAverageRatings();

        assertEquals("A", result.get(0).agent);
        assertEquals("B", result.get(1).agent);
    }
}
