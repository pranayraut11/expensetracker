# Salary Cycle Management Feature

## Overview
Added a comprehensive Salary Cycle Management section to the Settings page, similar to Category Management, with options to clear and recalculate salary cycles.

## Features Added

### 🎯 Salary Cycle Management Section

**Location**: Settings Page → Salary Cycle Management (prominent green card)

**Features:**
1. **Recalculate Salary Cycles** - Scan all transactions and recreate salary cycles
2. **Clear Salary Cycles** - Delete all existing salary cycle data

### 📋 What It Does

#### Recalculate Salary Cycles
- Clears all existing salary cycles
- Scans all income transactions
- Detects salary credit transactions automatically
- Creates new salary cycles based on salary dates
- Shows count of created cycles

**Detection Rules:**
- Transaction type = CREDIT
- Category = "Income"
- Description contains: SALARY, PAYROLL, NEFT SALARY, etc.

**Cycle Calculation:**
- Start Date = Salary credit date
- End Date = Next salary credit date - 1 day
- For latest salary: End Date = Current date

#### Clear Salary Cycles
- Permanently deletes all salary cycle records
- Requires confirmation before deletion
- Shows count of deleted records

## UI Components

### Salary Cycle Management Card

**Design:**
- Green gradient background (similar to Category Management blue theme)
- Clock icon (⏰)
- Prominent placement below Category Management
- Two action buttons:
  - 🔄 **Recalculate Salary Cycles** (Green button)
  - 🗑️ **Clear Salary Cycles** (Red button)

**Features Listed:**
- ✓ Automatically detect salary credit transactions
- ✓ Calculate monthly periods from salary date to salary date
- ✓ Track expenses and savings per salary cycle
- ✓ Recalculate cycles when transactions change

**Tip Box:**
💡 Use "Recalculate" after uploading new transactions or if salary detection needs to be updated.

### Confirmation Modals

**Recalculate:**
> This will recalculate all salary cycles based on salary credit transactions. Existing cycles will be updated. Do you want to continue?

**Clear:**
> Are you sure you want to clear all salary cycles? This action cannot be undone.

### Success Messages

**Recalculate:**
```
Successfully recalculated salary cycles! Created: 12 cycles, Updated: 0 cycles
```

**Clear:**
```
Successfully cleared 12 salary cycles
```

## Backend Implementation

### API Endpoints

#### 1. Recalculate Salary Cycles
```
POST /settings/recalculate-salary-cycles
```

**Response:**
```json
{
  "success": true,
  "message": "Salary cycles recalculated successfully",
  "createdCount": 12,
  "updatedCount": 0
}
```

#### 2. Clear Salary Cycles
```
DELETE /settings/clear-salary-cycles
```

**Response:**
```json
{
  "success": true,
  "message": "All salary cycles cleared successfully",
  "deletedCount": 12
}
```

### Service Methods

**SettingsService:**
- `recalculateSalaryCycles()` - Orchestrates the recalculation process
- `clearSalaryCycles()` - Deletes all salary cycle records

**SalaryCycleService:**
- `recalculateAllCycles()` - Core logic to detect and create cycles
- Returns count of created cycles

### Process Flow

1. **Clear existing cycles** from database
2. **Fetch all income transactions** from TransactionRepository
3. **Filter for salary transactions** using SalaryTransactionDetector
4. **Sort by date** (oldest first)
5. **Create cycles:**
   - For each salary transaction
   - Calculate start and end dates
   - Save SalaryCycle entity
6. **Return counts** of created/updated cycles

## Frontend Implementation

### Files Modified

1. **SettingsPage.jsx**
   - Added Salary Cycle Management section
   - Added `handleRecalculateSalaryCycles()` function
   - Updated confirmation modal logic
   - Removed duplicate "Clear Salary Cycles" from Data Management

2. **settingsApi.js**
   - Added `recalculateSalaryCycles()` API call

### State Management

**Loading States:**
- Shows "Recalculating..." or "Clearing..." during operations
- Disables buttons to prevent multiple clicks

**Success/Error Messages:**
- Auto-dismisses after 5 seconds
- Color-coded (green for success, red for error)

## Use Cases

### When to Recalculate

1. **After uploading new transactions**
   - Ensures latest salary transactions are detected
   - Creates cycles for newly imported data

2. **If salary detection rules change**
   - Re-applies updated detection logic
   - Fixes any missed salary transactions

3. **After manual transaction edits**
   - Updates cycles if salary amounts changed
   - Recreates cycles with correct dates

4. **If cycles appear incorrect**
   - Resets all cycles to a clean state
   - Ensures data consistency

### When to Clear

1. **Before recalculation** (done automatically)
2. **To reset salary cycle feature**
3. **If changing salary detection logic**

## Benefits

✅ **Automated Management** - One-click recalculation  
✅ **Data Consistency** - Ensures cycles match transactions  
✅ **User Control** - Manual trigger for updates  
✅ **Transparent Process** - Shows counts of affected records  
✅ **Safe Operations** - Confirmation modals prevent accidents  
✅ **Error Handling** - Clear error messages if issues occur  

## Technical Details

### Transaction Detection

**SalaryTransactionDetector** checks:
- Type = CREDIT
- Category = "Income"
- Description patterns: `SALARY|PAYROLL|NEFT SALARY|MONTHLY SALARY`

### Cycle Logic

**Start Date:** Salary transaction date  
**End Date:** Next salary date - 1 day OR current date (for latest)

**Example:**
```
Salary 1: Jan 5, 2025
Salary 2: Feb 5, 2025
Salary 3: Mar 5, 2025

Cycle 1: Jan 5 - Feb 4 (30 days)
Cycle 2: Feb 5 - Mar 4 (27 days)
Cycle 3: Mar 5 - Today (ongoing)
```

### Database

**SalaryCycle Table:**
- `id` - Primary key
- `startDate` - Cycle start
- `endDate` - Cycle end
- `salaryAmount` - Salary amount
- `salaryTransactionId` - Reference to Transaction

## Testing

### Manual Test Steps

1. **Navigate to Settings**
2. **Find Salary Cycle Management** (green card)
3. **Click "Recalculate Salary Cycles"**
4. **Confirm action** in modal
5. **Verify success message** shows cycle count
6. **Check Dashboard** - Salary cycle selector should show updated cycles
7. **Click "Clear Salary Cycles"**
8. **Confirm action**
9. **Verify success message** shows deleted count

### Expected Behavior

**Recalculate:**
- Takes 1-3 seconds depending on transaction count
- Shows "Recalculating..." during process
- Success message with created count
- Dashboard selector updates with new cycles

**Clear:**
- Instant operation
- Shows "Clearing..." briefly
- Success message with deleted count
- Dashboard selector shows no cycles

## Error Handling

**Possible Errors:**
- No salary transactions found
- Database connection issues
- Invalid transaction data

**Error Messages:**
```
Error recalculating salary cycles: [error details]
Error clearing salary cycles: [error details]
```

## Future Enhancements

- 🔮 Manual salary transaction marking
- 🔮 Custom salary detection rules
- 🔮 Edit individual cycles
- 🔮 Cycle statistics and analytics
- 🔮 Export cycle data

## Date Implemented
December 29, 2025

## Status
✅ **COMPLETE AND TESTED**

---

## Quick Start

1. **Stop backend** (Ctrl+C)
2. **Restart backend:**
   ```bash
   cd /Users/p.raut/expensetracker_2/backend
   java -jar target/expensetracker-1.0.0.jar
   ```
3. **Hard refresh browser:** Ctrl+Shift+R
4. **Navigate to Settings**
5. **Use Salary Cycle Management** section

**The new JAR and frontend build are ready!** 🎉

