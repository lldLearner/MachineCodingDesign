/*
You are given:

fileName, collectionName, fileSize


Example input:

f1, A, 10
f2, A, 5
f3, B, 20
f4, C, 7
f5, C, 3


You must compute:

1️⃣ Total size of all files in the system
2️⃣ Top N collections by size

Collection size = sum of sizes of its files.

🧠 Approach (Simple Version)

Use Map<String, Long> → collectionName → totalSize

Compute total system size while iterating.

Sort collections by size (descending).

Return top N.

Time: O(N log N)

🟢 Java Solution (Simple Flat Collections)*/
import java.util.*;

class FileEntry {
    String name;
    String collection;
    long size;

    FileEntry(String name, String collection, long size) {
        this.name = name;
        this.collection = collection;
        this.size = size;
    }
}

public class CollectionSizeCalculator {

    public static class Result {
        long totalSize;
        List<Map.Entry<String, Long>> topNCollections;

        Result(long totalSize, List<Map.Entry<String, Long>> topNCollections) {
            this.totalSize = totalSize;
            this.topNCollections = topNCollections;
        }
    }

    public Result compute(List<FileEntry> files, int N) {
        Map<String, Long> collectionSize = new HashMap<>();
        long totalSystemSize = 0;

        for (FileEntry file : files) {
            totalSystemSize += file.size;
            collectionSize.put(file.collection,
                    collectionSize.getOrDefault(file.collection, 0L) + file.size);
        }

        // Sort: highest size first
        List<Map.Entry<String, Long>> sorted =
                new ArrayList<>(collectionSize.entrySet());

        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        // Top N
        List<Map.Entry<String, Long>> topN =
                sorted.subList(0, Math.min(N, sorted.size()));

        return new Result(totalSystemSize, topN);
    }

    public static void main(String[] args) {
        List<FileEntry> files = Arrays.asList(
                new FileEntry("f1", "A", 10),
                new FileEntry("f2", "A", 5),
                new FileEntry("f3", "B", 20),
                new FileEntry("f4", "C", 7),
                new FileEntry("f5", "C", 3)
        );

        CollectionSizeCalculator calc = new CollectionSizeCalculator();
        Result result = calc.compute(files, 2);

        System.out.println("Total System Size = " + result.totalSize);
        System.out.println("Top Collections:");
        for (var entry : result.topNCollections) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
/*
📌 Dry Run (Simple Case)

Input:

A → 10 + 5 = 15
B → 20
C → 7 + 3 = 10


Sorted:

B = 20
A = 15
C = 10


Top 2 → B, A

Total Size → 20 + 15 + 10 = 45

🕒 Complexity
Processing files: O(N)
Sorting collections: O(K log K) where K = #collections
Total: O(N + K log K)
*/

/*
🚀 FOLLOW-UP (Nested Collections)

This is the REAL interviewer filter.

Input now looks like:
file f1, collection A/B/C, size 10
file f2, collection A/B, size 5
file f3, collection A, size 20


Collections are hierarchical (like directories).
Meaning:

A has size = its files + all files under A/B + A/B/C
A/B has size = its files + all files under C
A/B/C has size = its files


This is a tree.

🎯 Approach for Nested Collections

Parse collection path "A/B/C" → nodes: A → B → C

For each file:

Add size to every parent in the path
Example: A/B/C file contributes size to C, B, and A

Store in a Map<String, Long>

Sort and pick top N

🟣 Java Solution — Nested Collections*/
public Result computeNested(List<FileEntry> files, int N) {
    Map<String, Long> collectionSize = new HashMap<>();
    long totalSystemSize = 0;

    for (FileEntry file : files) {
        totalSystemSize += file.size;

        String[] parts = file.collection.split("/");

        // Build all ancestor paths
        String path = "";
        for (String p : parts) {
            path = path.isEmpty() ? p : path + "/" + p;
            collectionSize.put(path,
                    collectionSize.getOrDefault(path, 0L) + file.size);
        }
    }

    List<Map.Entry<String, Long>> sorted =
            new ArrayList<>(collectionSize.entrySet());

    sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

    List<Map.Entry<String, Long>> topN =
            sorted.subList(0, Math.min(N, sorted.size()));

    return new Result(totalSystemSize, topN);
}

/*
🔍 Dry Run (Nested Example)

Files:

f1: A/B/C → 10
f2: A/B   → 5
f3: A     → 20


Calculated:

C = 10
B = 10 + 5 = 15
A = 10 + 5 + 20 = 35


Sorted:

A = 35
B = 15
C = 10*/

/*

Time & Space Complexity — Nested Collections

Let:

N = number of files

D = maximum depth of a collection path (e.g., A/B/C → D = 3)

K = number of unique collections (all prefix paths)

Time Complexity:

Processing each file:

Splitting a path takes O(D)

Adding file size to each parent prefix takes O(D)
Therefore, processing all files takes O(N * D).

Sorting collections by size:

We sort K collection entries

Time = O(K log K)

Total Time Complexity:
O(N * D + K log K)

Space Complexity:

Collection size map:

Each file may generate up to D new prefix collections

Total unique collections = K

Space for the map = O(K)

Temporary path arrays:

Splitting a path into D parts requires O(D) space (negligible compared to map)

Total Space Complexity:
O(K)

Summary:

Time: O(N * D + K log K)

Space: O(K)

Where:
N = number of files
D = depth of nested collections
K = number of unique collection paths*/
