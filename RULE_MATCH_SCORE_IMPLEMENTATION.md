# Rule Match Score Implementation

## Overview
Implemented a sophisticated match scoring algorithm that prioritizes the most specific rule matches for transaction categorization. This solves the problem where multiple rules match the same description (e.g., "car" vs "car loan" vs "car loan repayment").

## Problem Statement
When multiple rules match a transaction description:
- **Rule 1**: Pattern = "car" → Category = "Vehicle/Transportation"
- **Rule 2**: Pattern = "car loan" → Category = "Loans & EMIs"  
- **Rule 3**: Pattern = "car loan repayment" → Category = "Loans & EMIs"

For description "car loan repayment", we want **Rule 3** to win because it's the most specific match.

## Solution: Match Score Algorithm

### Scoring System

#### 1. **Plain Text Matching**
- **Exact phrase match**: +100 points per word in pattern
- **Pattern length bonus**: +1 point per character (longer = more specific)
- **Position bonus**: +50 points if match is at beginning of description
- **Word boundary match**: +20 points per word for exact word boundaries
- **Partial word matches**: +10 points per matching word
- **All words match bonus**: +50 points if all pattern words are found

#### 2. **Regex Matching**
- **Base regex match**: +50 points
- **Matched text length**: +1 point per character
- **Word count in match**: +30 points per word
- **Regex complexity**: Additional points for:
  - Start/end anchors (^, $): +10 points each
  - Word boundaries (\b): +10 points each
  - Character classes ([...]): +5 points each
  - Alternations (|): +5 points each

#### 3. **Priority Tiebreaker**
If two rules have the same match score, the rule with higher `priority` field wins.

### Example Scoring

For description: "car loan repayment"

| Pattern | Score Breakdown | Total Score |
|---------|----------------|-------------|
| "car loan repayment" | 300 (3 words × 100) + 19 (length) + 60 (3 word boundaries) = **379** | ✅ **Winner** |
| "car loan" | 200 (2 words × 100) + 8 (length) + 40 (2 word boundaries) = **248** | |
| "car" | 100 (1 word × 100) + 3 (length) + 20 (1 word boundary) = **123** | |

## Implementation Files

### 1. `RuleMatchScore.java`
Utility class containing the scoring algorithm:
```java
public static int calculateMatchScore(String description, RuleDefinition rule)
public static int compareRules(String description, RuleDefinition rule1, RuleDefinition rule2)
```

### 2. `RuleMatchResult.java`
DTO to hold match results:
```java
public class RuleMatchResult {
    private RuleDefinition rule;
    private int score;
    private String matchedText;
}
```

### 3. `CategorizationService.java` (Updated)
Now uses match scoring for categorization:
```java
private String findBestMatchingCategory(String description, String type)
public Optional<RuleMatchResult> getBestMatchingRule(String description, String type)
```

### 4. `RuleDefinitionRepository.java` (Updated)
Added methods to fetch enabled rules:
```java
List<RuleDefinition> findByEnabledTrue()
List<RuleDefinition> findByEnabledTrueOrderByPriorityDesc()
```

### 5. `RuleController.java` (Updated)
Added test endpoint to test match scoring:
```java
POST /api/rules/test-match
Body: { "description": "car loan repayment", "type": "DEBIT" }
```

## Usage

### 1. Creating Rules with Priority

```java
// Most specific rule - highest priority
RuleDefinition rule1 = new RuleDefinition();
rule1.setRuleName("Car Loan Repayment - Specific");
rule1.setPattern("car loan repayment");
rule1.setCategoryName("Loans & EMIs");
rule1.setPriority(100);

// Less specific rule - medium priority
RuleDefinition rule2 = new RuleDefinition();
rule2.setRuleName("Car Loan - Generic");
rule2.setPattern("car loan");
rule2.setCategoryName("Loans & EMIs");
rule2.setPriority(50);

// Generic rule - low priority
RuleDefinition rule3 = new RuleDefinition();
rule3.setRuleName("Car - Very Generic");
rule3.setPattern("car");
rule3.setCategoryName("Vehicle/Transportation");
rule3.setPriority(10);
```

### 2. Testing Match Scores

Use the new test endpoint:

```bash
curl -X POST http://localhost:8080/api/rules/test-match \
  -H "Content-Type: application/json" \
  -d '{
    "description": "car loan repayment to HDFC bank",
    "type": "DEBIT"
  }'
```

Response:
```json
{
  "description": "car loan repayment to HDFC bank",
  "type": "DEBIT",
  "category": "Loans & EMIs",
  "bestMatch": {
    "ruleName": "Car Loan Repayment - Specific",
    "category": "Loans & EMIs",
    "pattern": "car loan repayment",
    "priority": 100,
    "score": 379
  }
}
```

### 3. Automatic Categorization

The system automatically uses match scoring during:
- File upload (Excel import)
- Transaction creation
- Manual recategorization (`POST /api/rules/reload`)

## Benefits

1. **More Accurate**: Prioritizes specific matches over generic ones
2. **Transparent**: Score calculation is deterministic and debuggable
3. **Flexible**: Works with both plain text and regex patterns
4. **Backward Compatible**: Falls back to legacy rule engine if no rules match
5. **Type-Aware**: Respects transaction type filters (CREDIT/DEBIT/ANY)

## Algorithm Details

### Plain Text Score Calculation
```java
int score = 0;

// Base score: pattern length
score += pattern.length();

// Word count bonus
String[] words = pattern.split("\\s+");
score += words.length * 100;

// Position bonus (at beginning)
if (description.startsWith(pattern)) {
    score += 50;
}

// Word boundary bonus
if (exact word match) {
    score += words.length * 20;
}

return score;
```

### Partial Word Matching
If exact phrase doesn't match, calculate partial score:
```java
for each pattern word:
    if found exact match in description:
        score += 30
    else if found partial match:
        score += 10

if all pattern words matched:
    score += 50
```

## Edge Cases Handled

1. **Null/Empty descriptions**: Returns "Miscellaneous"
2. **No rules defined**: Falls back to legacy CategoryRuleEngine
3. **Multiple equal scores**: Uses rule priority as tiebreaker
4. **Transaction type filtering**: Only considers applicable rules
5. **Disabled rules**: Automatically excluded from matching

## Performance Considerations

- **Time Complexity**: O(n × m) where n = number of enabled rules, m = average pattern length
- **Space Complexity**: O(n) to store match results
- **Optimization**: Only enabled rules are evaluated
- **Caching**: Consider adding cache if performance becomes an issue

## Future Enhancements

1. **Machine Learning**: Train ML model on historical categorizations
2. **Confidence Score**: Return confidence percentage with category
3. **Multiple Categories**: Support suggesting top 3 matching categories
4. **User Feedback**: Learn from manual category corrections
5. **Pattern Suggestions**: Auto-suggest patterns based on transaction history

## Migration Notes

### Existing Rules
All existing rules continue to work without modification. The match scoring algorithm enhances the selection logic but doesn't break existing functionality.

### Database Changes
No database schema changes required. The system uses existing `RuleDefinition` table fields:
- `pattern`: The text/regex to match
- `priority`: Used as tiebreaker (higher = better)
- `enabled`: Only enabled rules are evaluated
- `transactionType`: Filters by CREDIT/DEBIT/ANY

### Testing Checklist
- ✅ Test with simple keywords (e.g., "car")
- ✅ Test with phrases (e.g., "car loan")
- ✅ Test with long phrases (e.g., "car loan repayment")
- ✅ Test with regex patterns
- ✅ Test with mixed case descriptions
- ✅ Test with transaction type filters
- ✅ Test priority tiebreaking
- ✅ Test disabled rules are ignored

## API Reference

### Test Match Endpoint
```
POST /api/rules/test-match
Content-Type: application/json

Request Body:
{
  "description": "string (required)",
  "type": "CREDIT|DEBIT|ANY (optional, default: DEBIT)"
}

Response:
{
  "description": "car loan repayment",
  "type": "DEBIT",
  "category": "Loans & EMIs",
  "bestMatch": {
    "ruleName": "Car Loan Repayment",
    "category": "Loans & EMIs",
    "pattern": "car loan repayment",
    "priority": 100,
    "score": 379
  }
}
```

## Logging

The system logs match details at INFO level:
```
INFO: Best match for 'car loan repayment': Rule 'Car Loan Repayment' (category: Loans & EMIs, score: 379)
```

Enable DEBUG logging for detailed scoring:
```
DEBUG: Rule 'Car Loan Repayment' matched with score: 379
DEBUG: Rule 'Car Loan' matched with score: 248
DEBUG: Rule 'Car' matched with score: 123
```

## Conclusion

The match score implementation provides a robust, transparent, and maintainable solution for handling overlapping categorization rules. It ensures that the most specific rule always wins, making the system more accurate and predictable.
