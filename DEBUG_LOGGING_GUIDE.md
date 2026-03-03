# Debug Logging Guide for Rule Match Scoring

## Logging Added

I've added comprehensive logging to help debug why your transaction is being categorized incorrectly.

### What's Logged

1. **Rule Evaluation Start** - Shows which rule is being evaluated
2. **Pattern Type Detection** - Identifies if it's alternation, regex, or plain text
3. **Alternation Processing** - Shows each alternative and its score
4. **Plain Text Matching** - Shows exact phrase matches and bonuses
5. **Partial Word Matching** - Shows which words match and their scores
6. **Final Scores** - Shows all matching rules and their scores
7. **Winner Selection** - Shows which rule won and why

## How to See the Logs

### Option 1: Use the Test Endpoint

```bash
curl -X POST http://localhost:8080/api/rules/test-match \
  -H "Content-Type: application/json" \
  -d '{
    "description": "IMPS-605727581167-PRANAY RAUT HOME LOAN-SBIN-XXXXXXX5139-CAR LOAN REPAYMENT",
    "type": "DEBIT"
  }'
```

Then check the backend logs:

```bash
docker-compose logs -f backend | grep -A 50 "CALCULATING MATCH SCORE"
```

### Option 2: Check Docker Logs Directly

```bash
# Follow all backend logs
docker-compose logs -f backend

# Or check recent logs
docker-compose logs --tail=200 backend
```

### Option 3: View Logs Inside Container

```bash
docker exec -it expensetracker-backend tail -f /var/log/app.log
```

## Example Log Output

```
════════════════════════════════════════════════════════
📊 CALCULATING MATCH SCORE
Rule Name: Loans and EMI Rule
Category: Loans and EMI
Pattern: (EMI|PRANAY RAUT HOME LOAN|car emi|LOAN-SBIN|car loan repayment)
Description: IMPS-605727581167-PRANAY RAUT HOME LOAN-SBIN-XXXXXXX5139-CAR LOAN REPAYMENT
════════════════════════════════════════════════════════
✅ Detected ALTERNATION pattern (contains |)
🔍 Processing ALTERNATION pattern
   Pattern content (without parens): EMI|PRANAY RAUT HOME LOAN|car emi|LOAN-SBIN|car loan repayment
   Number of alternatives: 5
   ┌─ Alternative #1: 'EMI'
   │  Score: 0
   └─
   ┌─ Alternative #2: 'PRANAY RAUT HOME LOAN'
      🔎 Checking plain text match for pattern: 'pranay raut home loan'
      ✅ Exact phrase FOUND in description!
         + Length bonus: 20 (pattern length)
         + Word count bonus: 400 (4 words × 100)
         + Word boundary bonus: 80 (4 words × 20)
      📊 Total score: 500
   │  Score: 500
   └─ ✅ NEW BEST MATCH! Score: 500
   ┌─ Alternative #3: 'car emi'
   │  Score: 0
   └─
   ┌─ Alternative #4: 'LOAN-SBIN'
      🔎 Checking plain text match for pattern: 'loan-sbin'
      ✅ Exact phrase FOUND in description!
         + Length bonus: 9
         + Word count bonus: 200 (2 words × 100)
         + Word boundary bonus: 40 (2 words × 20)
      📊 Total score: 249
   │  Score: 249
   └─
   ┌─ Alternative #5: 'car loan repayment'
      🔎 Checking plain text match for pattern: 'car loan repayment'
      ✅ Exact phrase FOUND in description!
         + Length bonus: 18
         + Word count bonus: 300 (3 words × 100)
         + Word boundary bonus: 60 (3 words × 20)
      📊 Total score: 378
   │  Score: 378
   └─
🏆 Best matching alternative: 'PRANAY RAUT HOME LOAN' with score: 500
🎯 FINAL SCORE for rule 'Loans and EMI Rule': 500
════════════════════════════════════════════════════════

════════════════════════════════════════════════════════
📊 CALCULATING MATCH SCORE
Rule Name: Vehicle Rule
Category: Vehicle and Transportation
Pattern: (BIKE|CAR|wheel alignment)
Description: IMPS-605727581167-PRANAY RAUT HOME LOAN-SBIN-XXXXXXX5139-CAR LOAN REPAYMENT
════════════════════════════════════════════════════════
✅ Detected ALTERNATION pattern (contains |)
🔍 Processing ALTERNATION pattern
   Pattern content (without parens): BIKE|CAR|wheel alignment
   Number of alternatives: 3
   ┌─ Alternative #1: 'BIKE'
   │  Score: 0
   └─
   ┌─ Alternative #2: 'CAR'
      🔎 Checking plain text match for pattern: 'car'
      ✅ Exact phrase FOUND in description!
         + Length bonus: 3
         + Word count bonus: 100 (1 words × 100)
         + Word boundary bonus: 20 (1 words × 20)
      📊 Total score: 123
   │  Score: 123
   └─ ✅ NEW BEST MATCH! Score: 123
   ┌─ Alternative #3: 'wheel alignment'
   │  Score: 0
   └─
🏆 Best matching alternative: 'CAR' with score: 123
🎯 FINAL SCORE for rule 'Vehicle Rule': 123
════════════════════════════════════════════════════════

📊 ════════ MATCH RESULTS SUMMARY ════════
   • Rule: 'Loans and EMI Rule' | Category: 'Loans and EMI' | Score: 500
   • Rule: 'Vehicle Rule' | Category: 'Vehicle and Transportation' | Score: 123

🏆 ════════ WINNER ════════
   Rule: 'Loans and EMI Rule'
   Category: 'Loans and EMI'
   Pattern: (EMI|PRANAY RAUT HOME LOAN|car emi|LOAN-SBIN|car loan repayment)
   Score: 500
   Priority: 0
════════════════════════════════════
```

## What to Share for Debugging

When you see incorrect categorization, please share:

1. **The transaction description** (exact text)
2. **Your rule patterns** (both rules that might match)
3. **The log output** from above (use `docker-compose logs backend | grep -A 100 "CALCULATING MATCH SCORE"`)
4. **Expected category vs Actual category**

## Quick Commands

### Test a Specific Transaction

```bash
# Replace with your actual description
curl -X POST http://localhost:8080/api/rules/test-match \
  -H "Content-Type: application/json" \
  -d '{
    "description": "YOUR_TRANSACTION_DESCRIPTION_HERE",
    "type": "DEBIT"
  }'
```

### Watch Logs in Real-Time

```bash
# Terminal 1: Watch logs
docker-compose logs -f backend

# Terminal 2: Upload file or test match
curl -X POST http://localhost:8080/api/rules/test-match ...
```

### Save Logs to File

```bash
docker-compose logs backend > backend-logs.txt
```

Then search for "CALCULATING MATCH SCORE" in the file.

## Rebuild and Deploy

```bash
# Stop containers
cd /Users/p.raut/expensetracker_2
docker-compose down

# Rebuild backend with new logging
cd backend
mvn clean package
docker build -t pranayraut11/expensetracker-backend:latest .

# Start containers
cd ..
docker-compose up -d

# Watch logs
docker-compose logs -f backend
```

## Enable More Verbose Logging (Optional)

Add to `application.properties`:

```properties
# Enable DEBUG level for rule matching
logging.level.com.example.expensetracker.util.RuleMatchScore=DEBUG
logging.level.com.example.expensetracker.service.CategorizationService=DEBUG

# Log to file
logging.file.name=/app/logs/application.log
logging.file.max-size=10MB
```

## What to Look For in Logs

1. **Check if alternation is detected**: Look for "✅ Detected ALTERNATION pattern"
2. **Check alternative scores**: Each alternative should show its score
3. **Check best match**: The highest scoring alternative should be selected
4. **Check final comparison**: All rules should be compared with their scores
5. **Check winner**: The rule with highest score should win

## Common Issues to Debug

### Issue 1: Pattern Not Detected as Alternation
**Look for**: "✅ Treating as PLAIN TEXT pattern" instead of "✅ Detected ALTERNATION pattern"
**Fix**: Ensure pattern is exactly `(option1|option2|option3)` with parentheses

### Issue 2: Alternative Not Matching
**Look for**: Score: 0 for alternatives that should match
**Fix**: Check if the alternative text matches the description (case-insensitive)

### Issue 3: Wrong Rule Winning
**Look for**: The scores in "MATCH RESULTS SUMMARY"
**Fix**: The rule with lower score might have higher priority, or scoring might need adjustment

## Next Steps

1. **Rebuild the backend** with the new logging
2. **Test your problematic transaction** using the test endpoint
3. **Share the log output** so we can see exactly what's happening
4. **We'll fix the issue** based on the actual scores shown in logs

The logs will show us exactly:
- ✅ Which alternatives are matching
- ✅ What scores they're getting
- ✅ Why one rule wins over another
- ✅ If there's a bug in the scoring logic

**Ready to debug! Share the logs and we'll solve this together! 🐛🔍**
