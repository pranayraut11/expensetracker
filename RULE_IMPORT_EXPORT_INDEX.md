# 📥📤 Rule Import/Export Feature - Documentation Index

## Quick Navigation

### 🚀 Getting Started
**New to this feature?** Start here:
- [Quick Reference Card](RULE_IMPORT_EXPORT_QUICK_REF.md) - One-page guide

### 📚 Complete Documentation
**Want all the details?** Read this:
- [Full Feature Documentation](RULE_IMPORT_EXPORT.md) - Complete guide with examples

### 📊 Implementation Details
**Developer/stakeholder view:**
- [Implementation Summary](RULE_IMPORT_EXPORT_SUMMARY.md) - What was built

### 🎨 Visual Guides
**Visual learner?** Check this out:
- [Flow Diagrams](RULE_IMPORT_EXPORT_DIAGRAMS.md) - Visual flowcharts

### 📦 Sample Files
**Want to try it now?**
- [Sample Rules Template](sample-rules-template.json) - 23 ready-to-use rules

---

## Documentation Files Overview

| File | Type | Audience | Purpose |
|------|------|----------|---------|
| **RULE_IMPORT_EXPORT_QUICK_REF.md** | Quick Ref | All users | One-page cheat sheet |
| **RULE_IMPORT_EXPORT.md** | Full Guide | Developers | Complete documentation |
| **RULE_IMPORT_EXPORT_SUMMARY.md** | Summary | PM/Stakeholders | What was delivered |
| **RULE_IMPORT_EXPORT_DIAGRAMS.md** | Visual | Visual learners | Flow diagrams |
| **sample-rules-template.json** | Template | End users | Ready-to-use rules |
| **INDEX.md** | Navigation | Everyone | This file |

---

## By Use Case

### 🎯 I want to USE the feature
1. Read: [Quick Reference Card](RULE_IMPORT_EXPORT_QUICK_REF.md)
2. Try: Import [sample-rules-template.json](sample-rules-template.json)
3. Go to: http://localhost:5173/rules

### 🔧 I want to UNDERSTAND how it works
1. Read: [Full Documentation](RULE_IMPORT_EXPORT.md)
2. See: [Flow Diagrams](RULE_IMPORT_EXPORT_DIAGRAMS.md)
3. Review: Code files listed below

### 📊 I want to KNOW what was built
1. Read: [Implementation Summary](RULE_IMPORT_EXPORT_SUMMARY.md)
2. Check: Files created/modified list
3. Verify: Test results

### 🎓 I want to LEARN the details
1. Start: [Quick Reference](RULE_IMPORT_EXPORT_QUICK_REF.md)
2. Deep dive: [Full Documentation](RULE_IMPORT_EXPORT.md)
3. Visualize: [Flow Diagrams](RULE_IMPORT_EXPORT_DIAGRAMS.md)

---

## Code Files

### Backend (Java/Spring Boot)

```
backend/src/main/java/com/example/expensetracker/

├── controller/
│   └── RuleController.java
│       - GET  /api/rules/export
│       - POST /api/rules/import
│
├── dto/
│   ├── RuleExportDto.java          ✨ NEW
│   └── ImportResultDto.java        ✨ NEW
│
├── service/
│   └── RuleManagementService.java
│       - exportRules()
│       - importRules(rules, skipDuplicates)
│
└── repository/
    └── RuleDefinitionRepository.java
        - findByRuleName(name)
```

### Frontend (React)

```
frontend/src/

├── pages/
│   └── RuleListPage.jsx
│       - Export button
│       - Import button + modal
│       - File handling
│
└── services/
    └── ruleService.js
        - exportRules()
        - importRules(rules, skipDuplicates)
```

---

## API Quick Reference

### Export
```bash
GET /api/rules/export

Response:
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

### Import
```bash
POST /api/rules/import?skipDuplicates=false

Request Body:
[
  {
    "ruleName": "Food_Swiggy",
    "categoryName": "Food",
    "pattern": "(swiggy|zomato)",
    "priority": 10,
    "enabled": true
  }
]

Response:
{
  "successCount": 1,
  "errorCount": 0,
  "skippedCount": 0,
  "message": "Import completed: 1 imported, 0 skipped, 0 errors"
}
```

---

## Quick Commands

### Test Export API
```bash
curl http://localhost:8080/api/rules/export
```

### Test Import API
```bash
curl -X POST http://localhost:8080/api/rules/import \
  -H "Content-Type: application/json" \
  -d @sample-rules-template.json
```

### Start Application
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm run dev

# Open browser
open http://localhost:5173/rules
```

---

## Features at a Glance

✅ **Export Rules**
- One-click download
- JSON format
- Auto-dated filename
- No sensitive data (IDs/timestamps removed)

✅ **Import Rules**
- File upload
- Two modes: Update or Skip
- Visual modal dialog
- Detailed feedback
- Auto rule reload
- Auto transaction recategorization

✅ **Smart Handling**
- Duplicate detection
- Error reporting
- Success/skip/error counts
- Validation checks

---

## Import Modes

| Mode | Existing Rules | New Rules | Use When |
|------|----------------|-----------|----------|
| **Update** | Replaced | Added | Merging configurations |
| **Skip** | Kept | Added | Adding without overwriting |

---

## Sample Template

**File:** [sample-rules-template.json](sample-rules-template.json)

**Contains:** 23 pre-configured rules

**Categories covered:**
- Food (Swiggy, Zomato, restaurants)
- Groceries (DMart, BigBasket, online delivery)
- Shopping (Amazon, Flipkart, fashion)
- Travel (Uber, Ola, public transport)
- Fuel (petrol pumps)
- Bills (mobile, utilities)
- Medical (pharmacy, hospital)
- Entertainment (OTT, gaming)
- Rent, Income, ATM, Transfers

**Usage:** Import to get started quickly!

---

## Common Workflows

### Workflow 1: Backup Rules
```
1. Go to /rules
2. Click Export
3. Save file to backup location
```

### Workflow 2: Share Rules
```
1. Export rules
2. Send file to teammate
3. Teammate imports with "Skip" mode
4. They keep their custom rules + get yours
```

### Workflow 3: Restore Backup
```
1. Go to /rules
2. Click Import
3. Select backup file
4. Choose "Update" mode
5. Confirm
```

### Workflow 4: Use Template
```
1. Go to /rules
2. Click Import
3. Select sample-rules-template.json
4. Choose mode
5. Get 23 rules instantly!
```

---

## Troubleshooting

| Problem | Document | Section |
|---------|----------|---------|
| How to use? | Quick Reference | All |
| Export not working | Full Documentation | Troubleshooting |
| Import fails | Full Documentation | Error Handling |
| Understand flow | Flow Diagrams | Export/Import Flow |
| API details | Full Documentation | API Documentation |

---

## Learning Path

### Beginner
1. Read: [Quick Reference Card](RULE_IMPORT_EXPORT_QUICK_REF.md)
2. Import: [sample-rules-template.json](sample-rules-template.json)
3. Try: Export your rules

### Intermediate
1. Read: [Full Documentation](RULE_IMPORT_EXPORT.md)
2. Understand: Import modes
3. Create: Your own templates

### Advanced
1. Study: [Flow Diagrams](RULE_IMPORT_EXPORT_DIAGRAMS.md)
2. Review: Code implementation
3. Extend: Add new features

---

## Status

| Component | Status |
|-----------|--------|
| Backend Implementation | ✅ Complete |
| Frontend Implementation | ✅ Complete |
| Documentation | ✅ Complete |
| Testing | ✅ Passed |
| Production Ready | ✅ Yes |

---

## Quick Stats

**Files Created:** 5
- RuleExportDto.java
- ImportResultDto.java
- RULE_IMPORT_EXPORT.md
- sample-rules-template.json
- RULE_IMPORT_EXPORT_SUMMARY.md

**Files Modified:** 4
- RuleController.java
- RuleManagementService.java
- RuleListPage.jsx
- ruleService.js

**Documentation:** 5 files
**Sample Rules:** 23 rules
**API Endpoints:** 2 endpoints

---

## Support

### Self-Help Resources
1. Quick Reference → Fast answers
2. Full Documentation → Detailed info
3. Flow Diagrams → Visual understanding
4. Sample Template → Working example

### Common Questions

**Q: How do I export rules?**  
A: See [Quick Reference Card](RULE_IMPORT_EXPORT_QUICK_REF.md)

**Q: What's the difference between import modes?**  
A: See [Flow Diagrams](RULE_IMPORT_EXPORT_DIAGRAMS.md) - Import Mode Comparison

**Q: Can I edit the JSON file?**  
A: Yes! See [Full Documentation](RULE_IMPORT_EXPORT.md) - JSON File Format

**Q: How do I test the API?**  
A: See [Quick Reference Card](RULE_IMPORT_EXPORT_QUICK_REF.md) - Quick Test

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Nov 30, 2025 | Initial release |

---

## Next Steps

1. **Read the Quick Reference** to get started
2. **Import the sample template** to see it in action
3. **Export your rules** to create a backup
4. **Share with your team** if needed

---

**🎉 Everything you need is here!**

Pick a document and dive in! 📚✨

---

*Last updated: November 30, 2025*  
*Status: Production Ready ✅*

