// File: CustomerRatingServiceBTest.java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRatingServiceBTest {

    @Test
    void testTieBreakingLexicographically() {
        CustomerRatingServiceB svc = new CustomerRatingServiceB();

        svc.acceptRating("bob", 4.0);
        svc.acceptRating("alice", 4.0);

        List<AgentRating> result = svc.getAverageRatings();

        assertEquals("alice", result.get(0).agent);
        assertEquals("bob", result.get(1).agent);
    }

    @Test
    void testSortingWithTieBreak() {
        CustomerRatingServiceB svc = new CustomerRatingServiceB();

        svc.acceptRating("charlie", 5.0);
        svc.acceptRating("bob", 4.0);
        svc.acceptRating("alice", 4.0);

        List<AgentRating> res = svc.getAverageRatings();

        assertEquals("charlie", res.get(0).agent);
        assertEquals("alice", res.get(1).agent);  // tie break
        assertEquals("bob", res.get(2).agent);
    }
}
