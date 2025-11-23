import java.util.*;

public class RouterBImpl implements RouterB {

    private final Map<String, String> exactRoutes = new HashMap<>();
    private final List<RouteEntry> wildcardRoutes = new ArrayList<>();

    static class RouteEntry {
        String prefix;
        String result;

        RouteEntry(String prefix, String result) {
            this.prefix = prefix;
            this.result = result;
        }
    }

    @Override
    public void addRoute(String path, String result) {
        if (path.endsWith("/*")) {
            String prefix = path.substring(0, path.length() - 1); // remove '*'
            wildcardRoutes.add(new RouteEntry(prefix, result));
        } else {
            exactRoutes.put(path, result);
        }
    }

    @Override
    public String callRoute(String path) {
        // 1. Check exact match
        if (exactRoutes.containsKey(path))
            return exactRoutes.get(path);

        // 2. Check wildcard routes — longest prefix wins
        RouteEntry best = null;

        for (RouteEntry r : wildcardRoutes) {
            if (path.startsWith(r.prefix)) {
                if (best == null || r.prefix.length() > best.prefix.length()) {
                    best = r;
                }
            }
        }

        return best == null ? null : best.result;
    }
}
////////
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RouterBTest {

    @Test
    void testExactMatch() {
        RouterB router = new RouterBImpl();
        router.addRoute("/foo", "exact");

        assertEquals("exact", router.callRoute("/foo"));
    }

    @Test
    void testWildcardMatch() {
        RouterB router = new RouterBImpl();
        router.addRoute("/foo/*", "wild");

        assertEquals("wild", router.callRoute("/foo/123"));
        assertEquals("wild", router.callRoute("/foo/bar"));
    }

    @Test
    void testLongestWildcardWins() {
        RouterB router = new RouterBImpl();

        router.addRoute("/foo/*", "foo-wild");
        router.addRoute("/foo/bar/*", "bar-wild");

        assertEquals("bar-wild", router.callRoute("/foo/bar/123"));
    }

    @Test
    void testNoMatch() {
        RouterB router = new RouterBImpl();

        assertNull(router.callRoute("/missing"));
    }
}


