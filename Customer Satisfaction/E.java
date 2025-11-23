// File: CustomerRatingServiceE.java
import java.util.*;

/**
 * Part (e) - Add:
 *  - getAverageRatingsUnsorted(month)
 *  - getTotalRatings(month) // returns total sum per agent (not average)
 * Interface updated.
 */

interface ICustomerRatingServiceE {
    boolean acceptRating(String month, String agent, double rating);

    // sorted descending with tie-break
    List<AgentRating> getAverageRatings(String month);

    // unsorted averages (in insertion or map order; not sorted)
    List<AgentRating> getAverageRatingsUnsorted(String month);

    Optional<AgentRating> getHighestRatedAgentForMonth(String month);

    // export CSV of sorted averages (re-using sorted method)
    String exportMonthlyRatingsAsCSV(String month);

    // total rating sum (not average) for each agent in the month
    List<AgentRating> getTotalRatings(String month);
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

class CustomerRatingServiceE implements ICustomerRatingServiceE {
    private final Map<String, LinkedHashMap<String, RatingStats>> store = new HashMap<>();
    // Using LinkedHashMap inside month to preserve insertion order if caller cares about "unsorted" stable iteration.

    private boolean valid(double rating) {
        return rating > 0.0 && rating <= 5.0;
    }

    @Override
    public boolean acceptRating(String month, String agent, double rating) {
        if (!valid(rating)) return false;
        store
            .computeIfAbsent(month, m -> new LinkedHashMap<>())
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
    public List<AgentRating> getAverageRatingsUnsorted(String month) {
        Map<String, RatingStats> agentMap = store.getOrDefault(month, Collections.emptyMap());
        List<AgentRating> res = new ArrayList<>();
        for (var e : agentMap.entrySet()) {
            res.add(new AgentRating(e.getKey(), e.getValue().average()));
        }
        return res;
    }

    @Override
    public Optional<AgentRating> getHighestRatedAgentForMonth(String month) {
        List<AgentRating> sorted = getAverageRatings(month);
        if (sorted.isEmpty()) return Optional.empty();
        return Optional.of(sorted.get(0));
    }

    @Override
    public String exportMonthlyRatingsAsCSV(String month) {
        List<AgentRating> list = getAverageRatings(month);
        StringBuilder sb = new StringBuilder();
        sb.append("agent,averageRating\n");
        for (AgentRating ar : list) {
            sb.append(ar.agent).append(",").append(ar.rating).append("\n");
        }
        return sb.toString();
    }

    @Override
    public List<AgentRating> getTotalRatings(String month) {
        Map<String, RatingStats> agentMap = store.getOrDefault(month, Collections.emptyMap());
        List<AgentRating> res = new ArrayList<>();
        for (var e : agentMap.entrySet()) {
            res.add(new AgentRating(e.getKey(), e.getValue().sum)); // total sum (not average)
        }
        return res;
    }

    // demo
    public static void main(String[] args) {
        CustomerRatingServiceE svc = new CustomerRatingServiceE();
        svc.acceptRating("May", "alice", 5.0);
        svc.acceptRating("May", "bob", 4.0);
        svc.acceptRating("May", "alice", 3.0);

        System.out.println("Sorted averages (May):");
        svc.getAverageRatings("May").forEach(System.out::println);

        System.out.println("\nUnsorted averages (May):");
        svc.getAverageRatingsUnsorted("May").forEach(System.out::println);

        System.out.println("\nTotal ratings (May):");
        svc.getTotalRatings("May").forEach(System.out::println);

        System.out.println("\nCSV (May):\n" + svc.exportMonthlyRatingsAsCSV("May"));

        System.out.println("\nHighest (May): " + svc.getHighestRatedAgentForMonth("May").orElse(null));
    }
}
