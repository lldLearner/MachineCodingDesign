/*CASE 1 — Tree hierarchy (single parent per group; each employee in exactly one group)
ASCII diagram (tree)
                          Company
                         /       \
                      Engg        HR
                     /   \
                   BE     FE
                  /  \   /  \
               Alice Bob Lisa Marley


(Here BE → Engg, FE → Engg; single parent per group)

Interview explanation (what to say)

“This is the simplest case: the org is a tree (each group has one parent and employees belong to exactly one group). The closest common parent for a set of employees is the Lowest Common Ancestor (LCA) of their group nodes. I compute pairwise LCA by reducing across employees: LCA(g1, g2, g3, ...) = LCA(...(LCA(g1,g2),g3)...). Implementation uses parent pointers and depths (lift the deeper node). Complexity is O(k * H) for k employees and tree height H. This is simple, robust, and fast when the hierarchy is a tree.”

Java (Tree LCA) — runnable*/
import java.util.*;

public class TreeLCA {
    static class Group { String id; Group parent; Group(String id, Group p){this.id=id;this.parent=p;} }
    private final Map<String, Group> empToGroup;
    public TreeLCA(Map<String, Group> empToGroup){ this.empToGroup = empToGroup; }

    private int depth(Group g){
        int d=0; while(g!=null){ d++; g=g.parent; } return d;
    }
    private Group lcaTwo(Group a, Group b){
        if(a==null||b==null) return null;
        int da = depth(a), db = depth(b);
        while(da>db){ a=a.parent; da--; }
        while(db>da){ b=b.parent; db--; }
        while(a!=b){ a=a.parent; b=b.parent; if(a==null||b==null) return null; }
        return a;
    }
    public Group findCommon(List<String> employees){
        if(employees==null||employees.isEmpty()) return null;
        Group cur = empToGroup.get(employees.get(0));
        for(int i=1;i<employees.size() && cur!=null;i++){
            Group g = empToGroup.get(employees.get(i));
            cur = lcaTwo(cur,g);
        }
        return cur;
    }

    // Demo
    public static void main(String[] args){
        Group company = new Group("Company", null);
        Group engg = new Group("Engg", company);
        Group be = new Group("BE", engg);
        Group fe = new Group("FE", engg);
        Map<String, Group> m = new HashMap<>();
        m.put("Alice", be); m.put("Bob", be); m.put("Lisa", fe); m.put("Marley", fe);
        TreeLCA svc = new TreeLCA(m);
        System.out.println(svc.findCommon(List.of("Alice","Bob")).id);   // BE
        System.out.println(svc.findCommon(List.of("Alice","Lisa")).id);  // Engg
        System.out.println(svc.findCommon(List.of("Alice","Marley")).id);// Engg
    }
}
/*
Complexity

Time: O(k * H) where k = #employees queried, H = tree height

Space: O(1) additional (plus input mapping)

Follow-up notes to speak

If queries are frequent and tree is deep, preprocess with binary lifting for O(log H) LCA per pair (extra O(V log H) preprocessing).

Edge cases: missing employee (return null), disconnected graphs (return null).

CASE 2 — DAG hierarchy (shared groups & multi-parent groups / employees in multiple groups)

You specifically asked BE -> Engg, BE -> Platform, FE -> Engg — this is a DAG example.

ASCII diagram (DAG with BE having two parents)
                         Company
                       /    |     \
                    Engg    HR   Platform
                   /   \          ^
                 BE     FE        |
                /  \   /  \       |
           Alice Bob Lisa Marley  |
                \________________/
                (Alice also in FE? or BE links to Platform)


Concretely for your mapping:

BE → Engg

BE → Platform

FE → Engg
(so BE has two parents: Engg and Platform)

Interview explanation (what to say)

“When the hierarchy is a DAG (groups can have multiple parents) or employees can be in multiple groups, the LCA concept for trees doesn’t directly apply. Approach: for each target employee, treat all their groups as BFS sources and traverse upward (towards parents), recording distances to every reachable ancestor. After doing this for all employees, compute the intersection of ancestors reachable from every employee. Choose the ancestor that minimizes a closeness metric; I choose the ancestor with minimum maximum distance from any employee (minimize worst-case distance). This yields a single ‘closest’ common group. Complexity worst-case is O(k*(V+E)) but often much smaller because traversal stops early or ancestors are shallow.”

Java (DAG upward BFS + best candidate)*/
import java.util.*;

public class DAGClosest {
    private final Map<String,List<String>> parents;   // child -> parent list
    private final Map<String,List<String>> empGroups; // emp -> groups

    public DAGClosest(Map<String,List<String>> parents, Map<String,List<String>> empGroups){
        this.parents = parents; this.empGroups = empGroups;
    }

    private Map<String,Integer> bfsUp(String start){
        Map<String,Integer> dist = new HashMap<>();
        ArrayDeque<String> q = new ArrayDeque<>();
        q.add(start); dist.put(start,0);
        while(!q.isEmpty()){
            String g = q.poll();
            int d = dist.get(g);
            for(String p : parents.getOrDefault(g, Collections.emptyList())){
                if(!dist.containsKey(p)){ dist.put(p, d+1); q.add(p); }
            }
        }
        return dist;
    }

    public String closestCommon(List<String> employees){
        if(employees==null || employees.isEmpty()) return null;
        List<Map<String,Integer>> list = new ArrayList<>();
        for(String emp : employees){
            Map<String,Integer> combined = new HashMap<>();
            for(String g : empGroups.getOrDefault(emp, List.of())){
                Map<String,Integer> d = bfsUp(g);
                for(var e : d.entrySet()) combined.merge(e.getKey(), e.getValue(), Math::min);
            }
            list.add(combined);
        }
        // intersection
        Set<String> candidates = new HashSet<>(list.get(0).keySet());
        for(int i=1;i<list.size();i++) candidates.retainAll(list.get(i).keySet());
        if(candidates.isEmpty()) return null;
        String best=null; int bestMax=Integer.MAX_VALUE;
        for(String c : candidates){
            int m=0;
            for(var map : list) m = Math.max(m, map.get(c));
            if(m<bestMax || (m==bestMax && (best==null || c.compareTo(best)<0))){
                best=c; bestMax=m;
            }
        }
        return best;
    }

    // Demo main with BE -> Engg, BE -> Platform
    public static void main(String[] args){
        Map<String,List<String>> parents = new HashMap<>();
        parents.put("BE", List.of("Engg","Platform"));
        parents.put("FE", List.of("Engg"));
        parents.put("Engg", List.of("Company"));
        parents.put("Platform", List.of("Company"));
        Map<String,List<String>> empGroups = new HashMap<>();
        empGroups.put("Alice", List.of("BE"));
        empGroups.put("Bob", List.of("BE"));
        empGroups.put("Lisa", List.of("FE"));
        DAGClosest svc = new DAGClosest(parents, empGroups);
        System.out.println(svc.closestCommon(List.of("Alice","Lisa"))); // Engg or Platform (tie-breaker lexicographic)
    }
}
/*
Complexity

Time: Worst-case O(k * (V + E)) where k = #employees in query.

Space: O(V) for BFS maps per employee (can reuse memory).

Interview talking points & trade-offs

Metric choice: I used minimize-maximum-distance for robustness. Could use minimize-sum (gives different result) — ask product which they prefer.

Optimizations: memoize upward reachability per group, stop BFS early when current distances exceed best found, prune by ancestor degrees, use bitsets for intersection if V large.

If DAG is static and queries frequent, preprocess transitive closure or compute ancestor bitsets (costly memory but O(1) query).

CASE 3 — Concurrency: snapshot approach (read-heavy)
ASCII visual (concept)
Writer thread(s)            AtomicReference<Snapshot>            Reader threads
make new copy                 setReference(newSnapshot)            read snapshot ref.get()
   |                                   |                                |
   +----> build new immutable snapshot -+--------------------------------> use consistent view

Interview explanation (what to say)

“For read-heavy systems where queries must reflect a consistent latest state, I prefer copy-on-write snapshots: maintain an immutable GraphSnapshot object and an AtomicReference<GraphSnapshot> to the current one. Writers create a modified copy and atomically swap it in. Readers simply read the AtomicReference.get() and operate on the immutable snapshot with no locking — extremely fast and consistent. This trades heavier writes and memory for lock-free reads. If writes are very frequent, consider ReadWriteLock or finer-grained copy/sharing patterns (persistent data structures).”

Java (snapshot store + read usage)*/
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SnapshotStore {
    static final class Snapshot {
        final Map<String,List<String>> parents;
        final Map<String,List<String>> empGroups;
        Snapshot(Map<String,List<String>> p, Map<String,List<String>> e){
            this.parents = deepCopyUnmodifiable(p); this.empGroups = deepCopyUnmodifiable(e);
        }
        private static Map<String,List<String>> deepCopyUnmodifiable(Map<String,List<String>> m){
            Map<String,List<String>> out = new HashMap<>();
            for(var e : m.entrySet()) out.put(e.getKey(), Collections.unmodifiableList(new ArrayList<>(e.getValue())));
            return Collections.unmodifiableMap(out);
        }
    }

    private final AtomicReference<Snapshot> ref;
    public SnapshotStore(Map<String,List<String>> parents, Map<String,List<String>> empGroups){
        ref = new AtomicReference<>(new Snapshot(parents, empGroups));
    }
    public Snapshot getSnapshot(){ return ref.get(); }

    // example writer: add employee to group
    public void addEmployeeToGroup(String emp, String group){
        while(true){
            Snapshot old = ref.get();
            Map<String,List<String>> newEmp = new HashMap<>(old.empGroups);
            List<String> g = new ArrayList<>(newEmp.getOrDefault(emp, List.of()));
            if(!g.contains(group)) g.add(group);
            newEmp.put(emp, g);
            Snapshot nw = new Snapshot(old.parents, newEmp);
            if(ref.compareAndSet(old,nw)) return;
        }
    }

    // reader usage: build dag service on snapshot and call closestCommon(...)
    public String queryClosest(List<String> employees){
        Snapshot s = ref.get();
        DAGClosest dag = new DAGClosest(s.parents, s.empGroups);
        return dag.closestCommon(employees);
    }

    // main
    public static void main(String[] args) throws Exception {
        Map<String,List<String>> parents = new HashMap<>();
        parents.put("BE", List.of("Engg","Platform"));
        parents.put("FE", List.of("Engg"));
        parents.put("Engg", List.of("Company"));

        Map<String,List<String>> empGroups = new HashMap<>();
        empGroups.put("Alice", List.of("BE"));
        empGroups.put("Lisa", List.of("FE"));

        SnapshotStore store = new SnapshotStore(parents, empGroups);
        System.out.println(store.queryClosest(List.of("Alice","Lisa"))); // Engg or Platform
        // writer thread demo omitted for brevity
    }
}

/*Complexity & trade-offs

Reader: O(query cost on snapshot) — no locking overhead.

Writer: cost of copying maps for changed parts (O(size of changed structures)); compare-and-set ensures atomic swap.

Space: old snapshots remain until GC; memory spikes possible if many writes; mitigate with structural sharing or persistent collections.

What to say if asked about alternatives

ReadWriteLock: simpler but readers block during writes; ok if writes are frequent.

Fine-grained sharding: partition graph and update only shard copies.

Distributed systems: versioned snapshots via event sourcing (Kafka) + materialized views.

CASE 4 — Single-level groups (flat group sets)
ASCII diagram (flat)
Groups:
  BE:      { Alice, Bob }
  FE:      { Lisa, Marley }
  Platform:{ Alice, Lisa }
  HR:      { Mona, Springs }

Interview explanation (what to say)

“If hierarchy is single-level (no subgroups), each group is just a set of employees. The closest common group for a set of employees is any group whose member set includes all target employees — just compute intersection of group-sets for the target employees. Complexity O(k * G) where k = #employees and G = avg groups per employee.”

Java (single-level intersection)*/
import java.util.*;

public class SingleLevel {
    private final Map<String, Set<String>> empGroups;
    public SingleLevel(Map<String, Set<String>> empGroups){ this.empGroups = empGroups; }

    public String findGroupContainingAll(List<String> employees){
        if(employees==null||employees.isEmpty()) return null;
        Iterator<String> it = employees.iterator();
        Set<String> inter = new HashSet<>(empGroups.getOrDefault(it.next(), Set.of()));
        while(it.hasNext() && !inter.isEmpty()){
            inter.retainAll(empGroups.getOrDefault(it.next(), Set.of()));
        }
        if(inter.isEmpty()) return null;
        return inter.stream().sorted().findFirst().orElse(null); // deterministic tie-break
    }

    public static void main(String[] args){
        Map<String,Set<String>> m = new HashMap<>();
        m.put("Alice", Set.of("BE","Platform"));
        m.put("Bob", Set.of("BE"));
        m.put("Lisa", Set.of("FE","Platform"));
        SingleLevel s = new SingleLevel(m);
        System.out.println(s.findGroupContainingAll(List.of("Alice","Bob"))); // BE
        System.out.println(s.findGroupContainingAll(List.of("Alice","Lisa"))); // Platform
    }
}

/*Complexity

Time: O(k * G) where G is avg #groups per employee.

Space: O(G) for intersection set.

Final tips: what to say in interview (verbatim / bullet points)

Clarify assumptions: “Are groups a tree, DAG, or flat? Can employees be in multiple groups?”

For Tree: “Use LCA. Efficient, trivial to explain; can be optimized with binary lifting for many queries.”

For DAG: “Use upward multi-source BFS per employee to collect ancestors and distances, intersect ancestor sets, choose candidate minimizing max distance. Precompute ancestor bitsets for very frequent queries or memoize BFS results.”

For concurrency: “Prefer snapshot (immutable) + AtomicReference for read-heavy workloads. Readers are lock-free and see consistent state. Writers cost more; if writes scale, consider sharding or persistent data structures.”

Tie-break: “Pick deterministic rule (lexicographic id or business rule).”

Testing: “Unit test tree, DAG ties, disconnected cases, multi-group employees; concurrency test with snapshot consistency.”*/
