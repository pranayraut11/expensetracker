# Visual Explanation: Alternation Pattern Matching

## Your Transaction Description
```
IMPS-605727581167-PRANAY RAUT HOME LOAN-SBIN-XXXXXXX5139-CAR LOAN REPAYMENT
```

---

## Rule 1: Loans and EMI
**Pattern**: `(EMI|PRANAY RAUT HOME LOAN|car emi|LOAN-SBIN|car loan repayment)`

### How It's Processed

1. **Detect Alternation Pattern** ✅
   - Pattern starts with `(`
   - Contains `|`
   - Ends with `)`
   - → Treat as alternation

2. **Extract Alternatives**
   ```
   ┌─ EMI
   ├─ PRANAY RAUT HOME LOAN
   ├─ car emi
   ├─ LOAN-SBIN
   └─ car loan repayment
   ```

3. **Score Each Alternative**
   
   | Alternative | Found in Description? | Score Calculation | Total Score |
   |------------|----------------------|-------------------|-------------|
   | EMI | ❌ No | - | 0 |
   | PRANAY RAUT HOME LOAN | ✅ Yes | 4 words × 100 + 20 chars + word boundaries | **500+** |
   | car emi | ❌ No | - | 0 |
   | LOAN-SBIN | ✅ Yes | 2 words × 100 + 9 chars + word boundaries | **249** |
   | car loan repayment | ✅ Yes | 3 words × 100 + 18 chars + word boundaries | **378** |

4. **Select Best Match** 🏆
   - Maximum Score: **500+** from "PRANAY RAUT HOME LOAN"
   - **Final Score for Rule 1: ~500**

---

## Rule 2: Vehicle and Transportation
**Pattern**: `(BIKE|CAR|wheel alignment)`

### How It's Processed

1. **Detect Alternation Pattern** ✅

2. **Extract Alternatives**
   ```
   ┌─ BIKE
   ├─ CAR
   └─ wheel alignment
   ```

3. **Score Each Alternative**
   
   | Alternative | Found in Description? | Score Calculation | Total Score |
   |------------|----------------------|-------------------|-------------|
   | BIKE | ❌ No | - | 0 |
   | CAR | ✅ Yes | 1 word × 100 + 3 chars + word boundaries | **123** |
   | wheel alignment | ❌ No | - | 0 |

4. **Select Best Match**
   - Maximum Score: **123** from "CAR"
   - **Final Score for Rule 2: 123**

---

## Final Comparison

```
Rule 1 (Loans):  Score = 500  ✅ WINNER
Rule 2 (Vehicle): Score = 123
```

**Category Assigned**: Loans and EMI ✅

---

## Why This Works

### Before the Fix ❌
The algorithm tried to match the entire pattern `(EMI|PRANAY...|car emi...)` as a single regex, which didn't properly handle the alternation logic.

### After the Fix ✅
1. **Detects** alternation pattern
2. **Splits** into individual alternatives
3. **Scores** each alternative independently
4. **Picks** the highest scoring alternative
5. **Most specific match wins!**

---

## Visual Flow

```
Description: "...PRANAY RAUT HOME LOAN...CAR LOAN REPAYMENT..."
                      ↓                         ↓
                      |                         |
       Rule 1: (EMI|PRANAY RAUT HOME LOAN|...|car loan repayment)
                      ↓                         ↓
                   Match!                    Match!
                   Score: 500+               Score: 378
                      ↓                         
                   Best: 500+  ← Use highest score
                      ↓
              Rule 1 Total Score: 500
                      
       vs
       
       Rule 2: (BIKE|CAR|wheel alignment)
                      ↓
                   CAR Match!
                   Score: 123
                      ↓
              Rule 2 Total Score: 123
                      
       
       Result: Rule 1 (500) > Rule 2 (123)
       
       ✅ Category: Loans and EMI
```

---

## Key Takeaways

1. **Alternation patterns** `(A|B|C)` now work correctly
2. **Each alternative** is scored independently
3. **Best alternative** determines the rule's final score
4. **Longer, more specific** alternatives score higher
5. **Your case**: "PRANAY RAUT HOME LOAN" (4 words) beats "CAR" (1 word)

---

## Pattern Design Tips

### ✅ DO: Order alternatives by specificity
```
(car loan repayment|car loan|car)  ← Most specific first
```

### ✅ DO: Use meaningful alternatives
```
(EMI|home loan|personal loan|car loan repayment)
```

### ✅ DO: Keep patterns readable
```
(amazon|flipkart|myntra|shopping)
```

### ❌ DON'T: Mix very different concepts
```
(car|grocery|salary|rent)  ← Too broad, split into separate rules
```

### ❌ DON'T: Use redundant alternatives
```
(car|car loan|car loan repayment|car)  ← "car" is redundant
```

---

## Testing Your Patterns

Use the test endpoint with your actual transaction descriptions:

```bash
curl -X POST http://localhost:8080/api/rules/test-match \
  -H "Content-Type: application/json" \
  -d '{
    "description": "YOUR_ACTUAL_TRANSACTION_DESCRIPTION",
    "type": "DEBIT"
  }'
```

The response will show:
- ✅ Matched category
- ✅ Matched rule name
- ✅ Pattern used
- ✅ **Score** (helps you debug!)

---

**Now your rules will work exactly as expected! 🎯**
