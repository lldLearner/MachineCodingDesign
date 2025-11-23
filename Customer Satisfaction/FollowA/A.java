// File: CustomerRatingServiceA.java
import java.util.*;

/**
 * Part (a) - Basic rating system with interface.
 * - accept rating (agent, rating)
 * - getAverageRatings() -> sorted descending by average
 */

interface ICustomerRatingServiceA {
    boolean acceptRating(String agent, double rating);
    List<AgentRating> getAverageRatings(); // sorted high -> low
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

class CustomerRatingServiceA implements ICustomerRatingServiceA {
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
        res.sort((a, b) -> Double.compare(b.rating, a.rating)); // desc
        return res;
    }

    // demo
    public static void main(String[] args) {
        CustomerRatingServiceA svc = new CustomerRatingServiceA();
        svc.acceptRating("agent1", 5.0);
        svc.acceptRating("agent1", 3.0);
        svc.acceptRating("agent2", 4.0);
        List<AgentRating> avgs = svc.getAverageRatings();
        avgs.forEach(System.out::println);
    }
}
