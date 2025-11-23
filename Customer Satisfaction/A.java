import java.util.*;

class RatingStats {
    int count = 0;
    double sum = 0.0;

    void add(double rating) {
        count++;
        sum += rating;
    }

    double average() {
        return sum / count;
    }
}

class AgentRating {
    String agent;
    double rating;

    AgentRating(String agent, double rating) {
        this.agent = agent;
        this.rating = rating;
    }
}

class CustomerRatingService {

    private Map<String, RatingStats> agentRatings = new HashMap<>();

    public boolean acceptRating(String agent, double rating) {
        if (rating <= 0 || rating > 5) return false;

        agentRatings
                .computeIfAbsent(agent, a -> new RatingStats())
                .add(rating);

        return true;
    }

    public List<AgentRating> getAverageRatings() {
        List<AgentRating> result = new ArrayList<>();

        for (var e : agentRatings.entrySet()) {
            result.add(new AgentRating(e.getKey(), e.getValue().average()));
        }

        result.sort((a, b) -> Double.compare(b.rating, a.rating)); // desc
        return result;
    }
}
