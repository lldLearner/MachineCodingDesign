// File: CustomerRatingServiceC.java
import java.util.*;

/**
 * Part (c) - Monthly ratings + highest-per-month.
 * Interface includes month-aware operations.
 */

interface ICustomerRatingServiceC {
    boolean acceptRating(String month, String agent, double rating);
    List<AgentRating> getAverageRatings(String month); // sorted high->low
    Optional<AgentRating> getHighestRatedAgentForMonth(String month);
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

class CustomerRatingServiceC implements ICustomerRatingServiceC {
    // month -> (agent -> stats)
    private final Map<String, Map<String, RatingStats>> store = new HashMap<>();

    private boolean valid(double rating) {
        return rating > 0.0 && rating <= 5.0;
    }

    @Override
    public boolean acceptRating(String month, String agent, double rating) {
        if (!valid(rating)) return false;
        store
            .computeIfAbsent(month, m -> new HashMap<>())
            .computeIfAbsent(agent, a -> new RatingStats())
            .add(rating);
        return true;
    }

    @Override
    public List<AgentRating> getAverageRatings(String month) {
        Map<String, RatingStats> agentMap = store.getOrDefault(month, Collections.emptyMap());
        List<AgentRating> res = new ArrayList<>();
        for (var e : agentMap.entrySet()) {
            res.add(new AgentRating(e.getKey(), e.getValue().average()));
        }
        res.sort((a, b) -> {
            int cmp = Double.compare(b.rating, a.rating);
            if (cmp != 0) return cmp;
            return a.agent.compareTo(b.agent);
        });
        return res;
    }

    @Override
    public Optional<AgentRating> getHighestRatedAgentForMonth(String month) {
        List<AgentRating> sorted = getAverageRatings(month);
        if (sorted.isEmpty()) return Optional.empty();
        return Optional.of(sorted.get(0));
    }

    // demo
    public static void main(String[] args) {
        CustomerRatingServiceC svc = new CustomerRatingServiceC();
        svc.acceptRating("May", "agent1", 5.0);
        svc.acceptRating("May", "agent1", 4.0);
        svc.acceptRating("May", "agent2", 5.0);
        svc.acceptRating("June", "agent3", 5.0);

        System.out.println("May averages:");
        svc.getAverageRatings("May").forEach(System.out::println);

        System.out.println("Top in May: " + svc.getHighestRatedAgentForMonth("May").orElse(null));
    }
}
