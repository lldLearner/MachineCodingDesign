# 🚀 Distributed Rate Limiter (with Burst Credits)

To enforce global per-user rate limits across multiple servers, we use **Redis** as the shared state store and perform **atomic token-bucket updates** using **Lua scripts**.

---

## 🏗️ Architecture (High-Level)

App Server A ──┐
App Server B ───┼──→ Redis (Shared Token State)
App Server C ──┘

yaml
Copy code

Each server checks Redis before allowing a request, ensuring **global consistency**.

---

## 🔑 Redis Data Model (Per User)

**Key:**  
rl:{userId}

makefile
Copy code

**Fields:**  
tokens = current usable tokens (includes credits)
ts = last update timestamp (ms)
capacity = base bucket size
maxCredits = max extra burst capacity

yaml
Copy code

---

## ⚙️ Atomic Operation (Refill → Credits → Consume)

Every request triggers ONE atomic operation in Redis:

1. Read `tokens` + `ts`.
2. Calculate elapsed time, compute refill.
3. Add refill to tokens.
4. Clamp to `capacity + maxCredits` (this enables *burst credits*).
5. If `tokens >= 1` → consume → allow.
6. Else reject.
7. Update hash + TTL.

Atomicity is guaranteed by Redis Lua script execution.

---

## 💥 Burst Credits (Simple Model)

Credits are represented implicitly:

0 ≤ tokens ≤ capacity + maxCredits

yaml
Copy code

Meaning:
- If user doesn’t consume tokens, bucket fills above `capacity`.
- Extra tokens = **credits** which allow future bursts.

This is the simplest, most interview-friendly design.

---

## 🌍 Scaling (Redis Cluster)

- One key per user → naturally sharded across Redis Cluster nodes.
- No multi-key operations → fully cluster-safe.
- O(1) per request.

---

## ⚠️ Operational Notes

- **Hot keys:** heavy users concentrate traffic on one shard → mitigate with local token prefetch.
- **TTL:** auto-clean inactive user buckets.
- **Failure:** on Redis outage choose:
  - fail-open,
  - fail-closed,
  - or fallback to local limiter.
- **Monitoring:** rejected-count, Redis latency, tokens-left histogram.

---

## 📝 Interview Summary

> “We store a per-user token bucket in Redis. Each request calls an atomic Lua script that performs refill and token consumption. Tokens can exceed base capacity up to maxCredits, enabling burst credits. Using Redis Cluster gives horizontal scale, and TTL cleanup plus optional local prefetch reduce load. This ensures a globally consistent distributed rate limiter.”
