# 📥📤 Rule Import/Export - Quick Reference Card

## One-Page Guide

### Export Rules (Backup)

**UI Method:**
1. Go to `/rules` page
2. Click **📥 Export** (green button)
3. File downloads: `categorization-rules-YYYY-MM-DD.json`

**API Method:**
```bash
curl http://localhost:8080/api/rules/export > my-rules.json
```

---

### Import Rules (Restore)

**UI Method:**
1. Go to `/rules` page
2. Click **📤 Import** (blue button)
3. Select JSON file
4. Choose import mode:
   - **Update Existing** = Replace duplicates
   - **Skip Existing** = Keep current, add new only
5. Confirm

**API Method:**
```bash
# Update mode (replace duplicates)
curl -X POST http://localhost:8080/api/rules/import \
  -H "Content-Type: application/json" \
  -d @my-rules.json

# Skip mode (keep existing)
curl -X POST "http://localhost:8080/api/rules/import?skipDuplicates=true" \
  -H "Content-Type: application/json" \
  -d @my-rules.json
```

---

### JSON Format

```json
[
  {
    "ruleName": "Food_Swiggy",
    "categoryName": "Food",
    "pattern": "(swiggy|zomato)",
    "priority": 10,
    "enabled": true
  }
]
```

**Required Fields:**
- `ruleName` (unique)
- `categoryName`
- `pattern` (regex)

**Optional Fields:**
- `priority` (default: 0)
- `enabled` (default: true)

---

### Common Use Cases

**1. Backup Before Changes**
```
Export → Make Changes → Import if needed
```

**2. Share with Team**
```
Export → Send file → Team imports
```

**3. Use Template**
```
Import sample-rules-template.json → 23 rules added
```

**4. Migrate Environments**
```
Export from Dev → Import to Prod
```

---

### Import Modes

| Mode | Behavior |
|------|----------|
| **Update Existing** | Existing rules replaced, new rules added |
| **Skip Existing** | Existing rules kept, new rules added |

---

### After Import

Automatically happens:
1. ✅ Drools rules reload
2. ✅ All transactions recategorized
3. ✅ UI refreshes

---

### Files

**Documentation:**
- `RULE_IMPORT_EXPORT.md` - Full guide
- `RULE_IMPORT_EXPORT_SUMMARY.md` - Summary

**Template:**
- `sample-rules-template.json` - 23 ready-to-use rules

---

### Quick Test

```bash
# 1. Start app
cd backend && mvn spring-boot:run
cd frontend && npm run dev

# 2. Import sample template
curl -X POST http://localhost:8080/api/rules/import \
  -H "Content-Type: application/json" \
  -d @sample-rules-template.json

# 3. Export to verify
curl http://localhost:8080/api/rules/export

# 4. Check UI
open http://localhost:5173/rules
```

---

### Troubleshooting

| Problem | Solution |
|---------|----------|
| Export disabled | Create at least one rule |
| Invalid JSON | Check format is array |
| All skipped | Use Update mode instead |
| Import fails | Check file is valid JSON |

---

### API Endpoints

```
GET  /api/rules/export
POST /api/rules/import?skipDuplicates=false
```

---

### Success Messages

```
Export: "Exported 10 rule(s) successfully"
Import: "Import completed: 8 imported, 2 skipped, 0 errors"
```

---

**Status:** ✅ Production Ready  
**Version:** 1.0  
**Date:** Nov 30, 2025

---

**Print this for quick reference!** 📄

