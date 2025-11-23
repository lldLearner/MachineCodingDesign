/*📌 PART 1 — Max Commodity Price (Frequent Reads & Writes)
Problem

You receive a continuous stream of:

<timestamp, commodityPrice>


Requirements:

timestamps may arrive out of order

same timestamp may appear again → overwrite

maintain current max commodity price

optimised for frequent reads + writes

✔ Optimal Data Structures

We use:

1. HashMap<Integer, Integer> timestamp → price

To update overwritten timestamps in O(1)

2. TreeMap<Integer, Integer> price → frequency

Sorted keys

Last key = max price in O(1)

Handles duplicates

Why TreeMap?

Because if a timestamp changes from price 27 → 28:

oldPriceFrequency--

newPriceFrequency++

Max = priceFreq.lastKey() → O(1)

✔ Time / Space
upsert()   → O(log N)
getMax()   → O(1)
space      → O(N)


Perfect for frequent reads & writes.

🟢 JAVA IMPLEMENTATION (Part 1)*/
import java.util.*;

class RunningCommodityPrice {

    private final Map<Integer, Integer> tsToPrice = new HashMap<>();
    private final TreeMap<Integer, Integer> priceFreq = new TreeMap<>();
    private int latestTimestamp = -1;

    public void upsertCommodityPrice(int timestamp, int price) {

        latestTimestamp = Math.max(latestTimestamp, timestamp);

        // remove old price freq if timestamp existed
        if (tsToPrice.containsKey(timestamp)) {
            int oldPrice = tsToPrice.get(timestamp);
            int freq = priceFreq.get(oldPrice);

            if (freq == 1) priceFreq.remove(oldPrice);
            else priceFreq.put(oldPrice, freq - 1);
        }

        // record new price
        tsToPrice.put(timestamp, price);
        priceFreq.put(price, priceFreq.getOrDefault(price, 0) + 1);
    }

    public int getMaxCommodityPrice() {
        if (priceFreq.isEmpty()) return -1;
        return priceFreq.lastKey();
    }

    public int getLatestTimestamp() {
        return latestTimestamp;
    }
}
/*
🧪 Dry run (Part 1)
upsert(4, 27)
upsert(6, 26)
upsert(9, 27)
getMax() → 27

upsert(4, 28)
getMax() → 28


Works perfectly.

🚀 PART 2 — Checkpoint-Based Versioning

(This is the advanced follow-up you saw in your friend’s file.)

Problem

Every update returns a checkpointId (monotonically increasing):

int checkpoint = upsertCommodityPrice(ts, price);


Then queries ask:

getCommodityPrice(timestamp, checkpoint)


Meaning:

“What was the price of this timestamp at this checkpoint,
i.e., at or before that update?”

This is historical lookup, similar to Git commits or time-travel storage.

⭐ Optimal Data Structure for Versioning

We use:

✔ Map<timestamp, TreeMap<checkpoint, price>>

Because for each timestamp, we maintain a sorted map of:

checkpoint → priceAtThatCheckpoint


Why TreeMap?
Because for query:

getCommodityPrice(timestamp, checkpoint)


We need:

floorEntry(checkpoint)


Which returns:

the largest checkpoint <= requested checkpoint

This solves historical read in O(log N).

🟣 JAVA IMPLEMENTATION (Part 2 — Checkpoint System)*/
import java.util.*;

class VersionedCommodityPrice {

    // timestamp → (checkpoint → price)
    private final Map<Integer, TreeMap<Integer, Integer>> history = new HashMap<>();
    private int checkpointCounter = 0;

    public int upsertCommodityPrice(int timestamp, int price) {
        checkpointCounter++;

        history.putIfAbsent(timestamp, new TreeMap<>());
        history.get(timestamp).put(checkpointCounter, price);

        return checkpointCounter;
    }

    public int getCommodityPrice(int timestamp, int checkpoint) {

        if (!history.containsKey(timestamp))
            return -1;

        TreeMap<Integer, Integer> versions = history.get(timestamp);

        Map.Entry<Integer, Integer> entry = versions.floorEntry(checkpoint);
        if (entry == null) return -1;

        return entry.getValue();
    }

    public static void main(String[] args) {
        VersionedCommodityPrice r = new VersionedCommodityPrice();

        int c1 = r.upsertCommodityPrice(14, 27);
        int c2 = r.upsertCommodityPrice(16, 26);
        int c3 = r.upsertCommodityPrice(19, 25);
        int c4 = r.upsertCommodityPrice(14, 24);
        int c5 = r.upsertCommodityPrice(16, 23);
        int c6 = r.upsertCommodityPrice(14, 20);

        System.out.println(r.getCommodityPrice(14, c5)); // 24
        System.out.println(r.getCommodityPrice(14, c2)); // 27
        System.out.println(r.getCommodityPrice(16, c6)); // 23
    }
}
/*
🧠 Dry Run (Part 2)

Operations:

c1: ts=14 → 27
c2: ts=16 → 26
c3: ts=19 → 25
c4: ts=14 → 24
c5: ts=16 → 23
c6: ts=14 → 20


Checking history for timestamp 14:

c1 → 27
c4 → 24
c6 → 20


Query:

getCommodityPrice(14, c5)


floorEntry(c5) → c4 → 24

Works.

📈 COMPLEXITY (Part 2)

Let:

N = number of upserts

M = average # of updates per timestamp

Upsert

Put into a TreeMap: O(log M)

Query

floorEntry in a TreeMap: O(log M)

Space

All checkpoints stored → O(N)*/
