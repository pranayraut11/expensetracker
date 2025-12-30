// ⚠️ DEPRECATED - DO NOT USE THIS FILE
// ===================================
// Categories are now stored in DATABASE and fetched via API
//
// To use categories in your component:
//
// 1. Import the hook:
//    import { useCategories } from '../context/CategoryContext'
//
// 2. Use in component:
//    const { categories, getCategoryColor, getCategoryIcon } = useCategories()
//
// 3. Map over categories:
//    {categories.map(cat => (
//      <option key={cat.id} value={cat.name}>{cat.name}</option>
//    ))}
//
// 4. Get category color:
//    const color = getCategoryColor(categoryName)
//
// This ensures categories are always up-to-date from the database.
// DO NOT use the arrays below - they are empty and deprecated.
  Groceries: '#22c55e',
export const CATEGORIES = []
export const CATEGORY_COLORS = {}
export const CHART_COLORS = {}
  Fuel: '#ef4444',
  Medical: '#ec4899',
  Rent: '#6366f1',
  Entertainment: '#8b5cf6',
  Income: '#10b981',
  'ATM Withdrawals': '#64748b',
  Transfers: '#06b6d4',
  Miscellaneous: '#6b7280'
}

