// File: CustomerRatingServiceB.java
import java.util.*;

/**
 * Part (b) - Tie-breaking added (lexicographical).
 * Interface unchanged from part (a).
 */

interface ICustomerRatingServiceB {
    boolean acceptRating(String agent, double rating);
    List<AgentRating> getAverageRatings(); // sorted high->low with tie-breaker
}

final class AgentRating {
    public final String agent;
    public final double rating;

    public AgentRating(String agent, double rating) {
        this.agent = agent;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return agent + " -> " + rating;
    }
}

final class RatingStats {
    int count = 0;
    double sum = 0.0;

    void add(double r) {
        count++;
        sum += r;
    }

    double average() {
        if (count == 0) return 0.0;
        return sum / count;
    }
}

class CustomerRatingServiceB implements ICustomerRatingServiceB {
    private final Map<String, RatingStats> agentRatings = new HashMap<>();

    private boolean valid(double rating) {
        return rating > 0.0 && rating <= 5.0;
    }

    @Override
    public boolean acceptRating(String agent, double rating) {
        if (!valid(rating)) return false;
        agentRatings
            .computeIfAbsent(agent, k -> new RatingStats())
            .add(rating);
        return true;
    }

    @Override
    public List<AgentRating> getAverageRatings() {
        List<AgentRating> res = new ArrayList<>();
        for (var e : agentRatings.entrySet()) {
            res.add(new AgentRating(e.getKey(), e.getValue().average()));
        }
        // Descending average; tie-break by agent name (lexicographic)
        res.sort((a, b) -> {
            int cmp = Double.compare(b.rating, a.rating);
            if (cmp != 0) return cmp;
            return a.agent.compareTo(b.agent);
        });
        return res;
    }

    // demo
    public static void main(String[] args) {
        CustomerRatingServiceB svc = new CustomerRatingServiceB();
        svc.acceptRating("alice", 4.0);
        svc.acceptRating("bob", 4.0);
        svc.acceptRating("alice", 4.0);
        svc.acceptRating("charlie", 5.0);
        svc.acceptRating("bob", 4.0);
        List<AgentRating> avgs = svc.getAverageRatings();
        avgs.forEach(System.out::println);
    }
}
