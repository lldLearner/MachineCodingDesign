# 🐍 Snake Game — Time & Space Complexity

## 🟩 Part A — Snake Grows Every 5 Moves

### **Time Complexity**
| Operation | Complexity | Notes |
|----------|------------|-------|
| `moveSnake()` | **O(1)** | Add head, remove tail, hash-set lookup |
| Collision check | **O(1)** | HashSet contains check |
| Growth (every 5 moves) | **O(1)** | Skip tail removal |
| `isGameOver()` | **O(1)** | Boolean flag |

### **Space Complexity**
| Storage | Complexity | Notes |
|---------|------------|-------|
| Snake body | **O(L)** | L = snake length (Deque + HashSet) |

### **Summary**
- **TC:** `O(1)` per move  
- **SC:** `O(L)`

---

## 🟦 Part B — Snake Grows When Eating Food

### **Time Complexity**
| Operation | Complexity | Notes |
|----------|------------|-------|
| `moveSnake()` | **O(1)** | Same deque & hash set ops |
| Eating food | **O(1)** | Skip tail removal |
| Food placement | **O(1) average** | Randomly searching free cell |
| Collision check | **O(1)** | HashSet lookup |
| `isGameOver()` | **O(1)** | Boolean flag |

### **Worst-case Food Placement**
If the grid is almost full:

```
O(rows × cols)
```

(but **expected O(1)**)

### **Space Complexity**
| Storage | Complexity | Notes |
|---------|------------|-------|
| Snake body | **O(L)** | Only grows when eating food |

### **Summary**
- **TC:** `O(1)` average per move  
- **SC:** `O(L)`

---

## ⭐ Combined Summary Table

| Version | Time Per Move | Space | Notes |
|--------|----------------|--------|-------|
| **Part A** (grow every 5 moves) | `O(1)` | `O(L)` | Deterministic growth |
| **Part B** (grow on eating food) | `O(1)` | `O(L)` | Food-driven growth |

