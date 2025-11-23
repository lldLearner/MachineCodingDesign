### ⏱ Time Complexity
| API | Complexity | Explanation |
|-----|------------|-------------|
| `monthlyCostList()` | **O(12)** → **O(1)** | Always fills 12 months |
| `annualCost()` | **O(12)** → **O(1)** | Sums the precomputed monthly list |

### 💾 Space Complexity
| API | Complexity | Explanation |
|-----|------------|-------------|
| `monthlyCostList()` | **O(12)** | Returns fixed-size list |
| `annualCost()` | **O(1)** | Uses streaming sum only |

Overall: **O(1)** time and **O(1)** space because year size is fixed.
