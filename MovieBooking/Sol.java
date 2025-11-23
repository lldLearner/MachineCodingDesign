import java.util.*;

class Movie {
    String title;
    int durationInMinutes;

    Movie(String title, int durationInMinutes) {
        this.title = title;
        this.durationInMinutes = durationInMinutes;
    }
}

class Screening {
    String title;
    int startTime; // minutes from midnight

    Screening(String title, int startTime) {
        this.title = title;
        this.startTime = startTime;
    }
}

class MovieSchedule {
    List<Movie> movies;
    List<Screening> screenings;

    MovieSchedule(List<Movie> movies, List<Screening> screenings) {
        this.movies = movies;
        this.screenings = screenings;
    }

    int getDuration(String movieTitle) {
        for (Movie m : movies) {
            if (m.title.equals(movieTitle)) return m.durationInMinutes;
        }
        throw new RuntimeException("Movie duration not found: " + movieTitle);
    }
}

public class ScheduleManager {

    private static final int OPEN_TIME = 600;   // 10 AM
    private static final int CLOSE_TIME = 1380; // 11 PM

    public boolean canSchedule(Movie movieToAdd, MovieSchedule schedule) {

        int duration = movieToAdd.durationInMinutes;

        List<Screening> screenings = new ArrayList<>(schedule.screenings);
        screenings.sort(Comparator.comparingInt(s -> s.startTime));

        // 1) Check before first screening
        if (!screenings.isEmpty()) {
            Screening first = screenings.get(0);
            if (first.startTime - OPEN_TIME >= duration) return true;
        } else {
            // no screenings at all
            return (CLOSE_TIME - OPEN_TIME) >= duration;
        }

        // 2) Check between screenings
        for (int i = 0; i < screenings.size() - 1; i++) {
            Screening cur = screenings.get(i);
            Screening next = screenings.get(i + 1);

            int curEnd = cur.startTime + schedule.getDuration(cur.title);
            int gap = next.startTime - curEnd;

            if (gap >= duration) return true;
        }

        // 3) Check after the last screening
        Screening last = screenings.get(screenings.size() - 1);
        int lastEnd = last.startTime + schedule.getDuration(last.title);

        return (CLOSE_TIME - lastEnd) >= duration;
    }

    public static void main(String[] args) {
        // Movies
        Movie lotr = new Movie("Lord Of The Rings", 120);
        Movie bttf = new Movie("Back To The Future", 90);

        // Existing screenings
        List<Screening> screenings = Arrays.asList(
                new Screening("Lord Of The Rings", 660),  // 11:00 AM
                new Screening("Lord Of The Rings", 840),  // 2:00 PM
                new Screening("Back To The Future", 1020),// 5:00 PM
                new Screening("Lord Of The Rings", 1200)  // 8:00 PM
        );

        List<Movie> movies = Arrays.asList(lotr, bttf);

        MovieSchedule schedule = new MovieSchedule(movies, screenings);
        ScheduleManager manager = new ScheduleManager();

        System.out.println("Can fit LOTR (120 min)? " + manager.canSchedule(lotr, schedule));
        System.out.println("Can fit BTTF (90 min)? " + manager.canSchedule(bttf, schedule));
    }
}


//UT

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleManagerTest {

    private MovieSchedule getSampleSchedule() {
        Movie lotr = new Movie("Lord Of The Rings", 120);
        Movie bttf = new Movie("Back To The Future", 90);

        List<Movie> movies = Arrays.asList(lotr, bttf);

        List<Screening> screenings = Arrays.asList(
                new Screening("Lord Of The Rings", 660),
                new Screening("Lord Of The Rings", 840),
                new Screening("Back To The Future", 1020),
                new Screening("Lord Of The Rings", 1200)
        );

        return new MovieSchedule(movies, screenings);
    }

    @Test
    void testCannotFitLongMovie() {
        ScheduleManager manager = new ScheduleManager();
        MovieSchedule schedule = getSampleSchedule();

        Movie longMovie = new Movie("TestLong", 120); // 120 min
        assertFalse(manager.canSchedule(longMovie, schedule));
    }

    @Test
    void testCanFitShortMovie() {
        ScheduleManager manager = new ScheduleManager();
        MovieSchedule schedule = getSampleSchedule();

        Movie shortMovie = new Movie("TestShort", 90); // 90 min
        assertTrue(manager.canSchedule(shortMovie, schedule));
    }

    @Test
    void testEmptySchedule() {
        ScheduleManager manager = new ScheduleManager();

        List<Movie> movies = List.of(new Movie("Short", 60));
        MovieSchedule schedule = new MovieSchedule(movies, new ArrayList<>());

        assertTrue(manager.canSchedule(new Movie("Short", 60), schedule));
    }

    @Test
    void testCanFitBeforeFirstScreening() {
        ScheduleManager manager = new ScheduleManager();

        MovieSchedule schedule = new MovieSchedule(
                List.of(new Movie("M1", 100)),
                List.of(new Screening("M1", 700))  // First screening at 700
        );

        // From 600 → 700 we have 100 min
        Movie newMovie = new Movie("Test", 100);
        assertTrue(manager.canSchedule(newMovie, schedule));
    }
}


/*
O(N log N) — sort screenings
O(N)       — check gaps
Total: O(N log N)
O(N) — sorted copy of screenings

*/
