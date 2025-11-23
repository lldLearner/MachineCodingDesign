import java.util.*;

public class RouterCImpl implements RouterC {

    private final Map<String, String> exactRoutes = new HashMap<>();
    private final List<WildcardEntry> wildcardRoutes = new ArrayList<>();
    private final List<ParamEntry> paramRoutes = new ArrayList<>();

    static class WildcardEntry {
        String prefix;
        String result;
        WildcardEntry(String prefix, String result) {
            this.prefix = prefix;
            this.result = result;
        }
    }

    static class ParamEntry {
        String[] tokens;
        String result;
        ParamEntry(String[] tokens, String result) {
            this.tokens = tokens;
            this.result = result;
        }
    }

    @Override
    public void addRoute(String path, String result) {
        if (path.contains(":")) {                     // param route
            paramRoutes.add(new ParamEntry(path.split("/"), result));
        } 
        else if (path.endsWith("/*")) {               // wildcard route
            wildcardRoutes.add(new WildcardEntry(path.substring(0, path.length() - 1), result));
        } 
        else {                                        // exact route
            exactRoutes.put(path, result);
        }
    }

    @Override
    public String callRoute(String path) {
        // 1. Exact
        if (exactRoutes.containsKey(path))
            return exactRoutes.get(path);

        // 2. Wildcard (longest prefix wins)
        WildcardEntry best = null;
        for (WildcardEntry we : wildcardRoutes) {
            if (path.startsWith(we.prefix)) {
                if (best == null || we.prefix.length() > best.prefix.length())
                    best = we;
            }
        }
        if (best != null) return best.result;

        // 3. Param matching
        String[] incoming = path.split("/");

        for (ParamEntry pe : paramRoutes) {
            if (incoming.length != pe.tokens.length) continue;

            boolean match = true;
            for (int i = 0; i < incoming.length; i++) {
                if (pe.tokens[i].startsWith(":")) continue; // param
                if (!pe.tokens[i].equals(incoming[i])) {
                    match = false; break;
                }
            }
            if (match) return pe.result;
        }

        return null;
    }
}


////////

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RouterCTest {

    @Test
    void testSimplePathParam() {
        RouterC router = new RouterCImpl();
        router.addRoute("/user/:id", "user-page");

        assertEquals("user-page", router.callRoute("/user/123"));
        assertEquals("user-page", router.callRoute("/user/xyz"));
    }

    @Test
    void testMultiParam() {
        RouterC router = new RouterCImpl();
        router.addRoute("/order/:oid/item/:iid", "order-item");

        assertEquals("order-item", router.callRoute("/order/10/item/55"));
    }

    @Test
    void testParamsLoseToExactMatch() {
        RouterC router = new RouterCImpl();

        router.addRoute("/user/:id", "param");
        router.addRoute("/user/123", "exact");

        assertEquals("exact", router.callRoute("/user/123"));
    }

    @Test
    void testWildcardBeatsParamIfMoreSpecific() {
        RouterC router = new RouterCImpl();

        router.addRoute("/foo/*", "wild");
        router.addRoute("/foo/:id", "param");

        assertEquals("wild", router.callRoute("/foo/anything/here"));
    }

    @Test
    void testNoMatch() {
        RouterC router = new RouterCImpl();
        assertNull(router.callRoute("/nothing"));
    }
}
