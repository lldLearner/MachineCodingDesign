# MachineCodingDesign

## ⏱️ Time & Space Complexity — Customer Satisfaction

Let:
- A = number of agents in a month
- R = total ratings
- M = number of months

### 1. acceptRating(month, agent, rating)
- Time: **O(1)**
- Space: **O(A)** (store sum + count per agent)

### 2. getAllAgentsAverageRatings(month)
- Time: **O(A log A)** (sorting)
- Space: **O(A)** (result list)

### 3. highestRatedAgentForMonth(month)
- Time: **O(A log A)** (calls getAllAgentsAverageRatings)
- Space: **O(A)**

### 4. exportMonthlyRatings (CSV/JSON/XML)
- Time: **O(A)**
- Space: **O(A)**

### 5. return unsorted averages / return total ratings
- Time: **O(A)**  
- Space: **O(A)**  


## ⏱️ Time & Space Complexity — Middleware Router

Let:
- E = number of exact routes
- W = number of wildcard / param routes
- P = path length (segments)

### 1. Exact Route Match (HashMap)
- addRoute(): **O(1)**
- callRoute(): **O(1)**
- Space: **O(E)**

### 2. Wildcard / Path-Param Routes (Ordered Checking)
- addRoute(): **O(1)**
- callRoute(): **O(W * P)** (linear scan, as required by problem)
- Space: **O(W)**

### 3. Combined Router (Exact + Wildcard + Params)
- callRoute():
  - exact match: **O(1)**
  - wildcard/params: **O(W * P)**
- Space: **O(E + W)** (store all routes)
