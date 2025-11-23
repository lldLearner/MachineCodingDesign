class LeakyBucket {
    private final int capacity;         // max tokens that bucket can hold
    private final double leakRate;      // tokens leaked per second
    private double tokens = 0;          // current tokens
    private long lastLeakTime;

    public LeakyBucket(int capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.lastLeakTime = System.nanoTime();
    }

    private void leak() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastLeakTime) / 1_000_000_000.0;
        double leaked = elapsedSeconds * leakRate;
        tokens = Math.max(0, tokens - leaked);
        lastLeakTime = now;
    }

    public synchronized boolean allowRequest() {
        leak();
        if (tokens < capacity) {
            tokens += 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
      LeakyBucket bucket = new LeakyBucket(3, 1); // capacity=3, leak=1 token/sec
  
      System.out.println(bucket.allowRequest()); // true
      System.out.println(bucket.allowRequest()); // true
      System.out.println(bucket.allowRequest()); // true
      System.out.println(bucket.allowRequest()); // false (limit hit)
  
      Thread.sleep(1100); // wait 1.1 sec → 1 token leaked out
      System.out.println(bucket.allowRequest()); // true
  }
}

/////Per User Locking

import java.util.concurrent.ConcurrentHashMap;

class UserRateLimiter {
    private final ConcurrentHashMap<String, LeakyBucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final double leakRate;

    public UserRateLimiter(int capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
    }

    public boolean allowRequest(String userId) {
        // ensures only one bucket per user (thread-safe)
        buckets.putIfAbsent(userId, new LeakyBucket(capacity, leakRate));

        LeakyBucket bucket = buckets.get(userId);
        return bucket.allowRequest();
    }

    public static void main(String[] args) throws InterruptedException {
      UserRateLimiter limiter = new UserRateLimiter(3, 1); // 3 requests, leak 1/sec
  
      System.out.println(limiter.allowRequest("u1")); // true
      System.out.println(limiter.allowRequest("u1")); // true
      System.out.println(limiter.allowRequest("u1")); // true
      System.out.println(limiter.allowRequest("u1")); // false
  
      System.out.println(limiter.allowRequest("u2")); // true (new bucket)
  }

}

/*
  🟦 Now the interviewer switches into CONCURRENCY discussion.

They ALWAYS do this next.

And to get Strong Hire, your answers need to be crisp.

Below is the exact script you give:

🔥 CONCURRENCY DEEP DIVE (Strong Hire Answer)
✔ Why is this thread-safe?
1. ConcurrentHashMap

Safe for concurrent put/get

Lock striping — reduces contention

Per-key locking → different users don’t block each other

2. Inside each bucket: synchronized allowRequest()

Each user’s requests synchronize on their own bucket, so:

Only requests for SAME USER block each other

Requests for DIFFERENT users DO NOT block

This is fine-grained locking → low contention

Strong line to say:

“Lock granularity is per-user, not global. This is optimal because each user’s state is isolated.”

3. Memory Visibility

synchronized ensures:

“Happens-before” guarantees

Visibility of updated tokens across threads

No stale reads

Say this:

“The synchronized block ensures visibility and prevents race conditions on token count.”

✔ What if interviewer asks:
“Why not use AtomicDouble or ReentrantLock?”

Answer:

“AtomicDouble does not exist and custom CAS loops introduce floating point inconsistency.
ReentrantLock is fine, but synchronized is cheaper for uncontended paths and clearer.”

✔ Contention & Scalability

You must say:

“Contention only happens on requests for the same user, which is acceptable.
Different users never block each other because each bucket has its own lock.”

THIS is what gets you Strong Hire.

✔ Thread Safety Problem to Point Out

Even though buckets are created with putIfAbsent, the internal allowRequest() must be synchronized, otherwise:

Two threads may leak incorrectly

Token increments collide

Token count may exceed capacity

Being able to articulate this is critical.
*/

///// Credit System

class CreditBucket {
    private final int baseCapacity;      // base allowed limit per second
    private final int maxCredits;        // additional capacity from unused requests
    private final double refillRate;     // tokens added per second
    private double tokens;               // current tokens
    private long lastRefillTime;

    public CreditBucket(int baseCapacity, int maxCredits, double refillRate) {
        this.baseCapacity = baseCapacity;
        this.maxCredits = maxCredits;
        this.refillRate = refillRate;
        this.tokens = baseCapacity;  // start full
        this.lastRefillTime = System.nanoTime();
    }

    private void refill() {
        long now = System.nanoTime();
        double seconds = (now - lastRefillTime) / 1_000_000_000.0;

        tokens = Math.min(baseCapacity + maxCredits, tokens + seconds * refillRate);
        lastRefillTime = now;
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }
}


import java.util.concurrent.ConcurrentHashMap;

class UserCreditLimiter {
    private final ConcurrentHashMap<String, CreditBucket> buckets = new ConcurrentHashMap<>();
    private final int baseCapacity;
    private final int maxCredits;
    private final double refillRate;

    public UserCreditLimiter(int baseCapacity, int maxCredits, double refillRate) {
        this.baseCapacity = baseCapacity;
        this.maxCredits = maxCredits;
        this.refillRate = refillRate;
    }

    public boolean allowRequest(String userId) {
        buckets.putIfAbsent(userId,
                new CreditBucket(baseCapacity, maxCredits, refillRate));
        return buckets.get(userId).allowRequest();
    }

    public static void main(String[] args) throws InterruptedException {
      UserCreditLimiter limiter = new UserCreditLimiter(
              3,   // base capacity
              5,   // max credits
              2    // refill 2 tokens/sec
      );
  
      // Burst at the beginning (full base capacity)
      System.out.println(limiter.allowRequest("u1")); // = true
      System.out.println(limiter.allowRequest("u1"));
      System.out.println(limiter.allowRequest("u1"));
      System.out.println(limiter.allowRequest("u1")); // likely false
  
      // Wait to accumulate credits
      Thread.sleep(3000); // earn 6 new tokens (2 tokens/sec)
  
      // Now burst again up to (3 + 5 = 8 requests)
      for (int i = 0; i < 8; i++) {
          System.out.println("Request " + i + ": " + limiter.allowRequest("u1"));
      }
  }

}




