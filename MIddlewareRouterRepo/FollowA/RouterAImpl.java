public interface RouterA {
    void addRoute(String path, String result);
    String callRoute(String path);
}

import java.util.HashMap;
import java.util.Map;

public class RouterAImpl implements RouterA {

    private final Map<String, String> routeMap = new HashMap<>();

    @Override
    public void addRoute(String path, String result) {
        routeMap.put(path, result);
    }

    @Override
    public String callRoute(String path) {
        return routeMap.getOrDefault(path, null);
    }
}

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RouterATest {

    @Test
    void testBasicRouting() {
        RouterA router = new RouterAImpl();
        router.addRoute("/bar", "result");

        assertEquals("result", router.callRoute("/bar"));
    }

    @Test
    void testRouteNotFound() {
        RouterA router = new RouterAImpl();

        assertNull(router.callRoute("/missing"));
    }
}
