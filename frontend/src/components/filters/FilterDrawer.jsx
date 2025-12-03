import React from 'react'
import { motion } from 'framer-motion'
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetFooter,
} from '../ui/sheet'
import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { X, Calendar, Tag, CreditCard } from 'lucide-react'
import { CATEGORIES } from '../../constants/categories'

const FilterDrawer = ({ open, onOpenChange, filters, onFilterChange, onApply, onClear }) => {
  const handleChange = (name, value) => {
    onFilterChange({ target: { name, value } })
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-md">
        <SheetHeader>
          <SheetTitle className="text-2xl font-bold">Filters</SheetTitle>
          <SheetDescription>
            Filter transactions by date, category, and type
          </SheetDescription>
        </SheetHeader>

        <div className="mt-8 space-y-6">
          {/* Date Range */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="space-y-3"
          >
            <label className="flex items-center gap-2 text-sm font-medium text-slate-700 dark:text-slate-300">
              <Calendar className="w-4 h-4" />
              Date Range
            </label>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-500 dark:text-slate-400 mb-1 block">
                  From
                </label>
                <Input
                  type="date"
                  name="fromDate"
                  value={filters.fromDate}
                  onChange={onFilterChange}
                  className="w-full"
                />
              </div>
              <div>
                <label className="text-xs text-slate-500 dark:text-slate-400 mb-1 block">
                  To
                </label>
                <Input
                  type="date"
                  name="toDate"
                  value={filters.toDate}
                  onChange={onFilterChange}
                  className="w-full"
                />
              </div>
            </div>
          </motion.div>

          {/* Category */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="space-y-3"
          >
            <label className="flex items-center gap-2 text-sm font-medium text-slate-700 dark:text-slate-300">
              <Tag className="w-4 h-4" />
              Category
            </label>
            <select
              name="category"
              value={filters.category}
              onChange={onFilterChange}
              className="w-full h-10 rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-950 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">All Categories</option>
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </motion.div>

          {/* Type */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="space-y-3"
          >
            <label className="flex items-center gap-2 text-sm font-medium text-slate-700 dark:text-slate-300">
              <CreditCard className="w-4 h-4" />
              Transaction Type
            </label>
            <div className="grid grid-cols-3 gap-2">
              {['All', 'CREDIT', 'DEBIT'].map((type) => (
                <button
                  key={type}
                  onClick={() => handleChange('type', type === 'All' ? '' : type)}
                  className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${
                    (type === 'All' && !filters.type) || filters.type === type
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700'
                  }`}
                >
                  {type === 'All' ? 'All' : type === 'CREDIT' ? 'Income' : 'Expense'}
                </button>
              ))}
            </div>
          </motion.div>

          {/* Credit Card Toggle */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="flex items-center justify-between p-4 rounded-xl bg-slate-50 dark:bg-slate-900"
          >
            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-100 dark:bg-purple-950/50 rounded-lg">
                <CreditCard className="w-5 h-5 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <p className="text-sm font-medium text-slate-900 dark:text-slate-100">
                  Credit Card Only
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400">
                  Show only credit card transactions
                </p>
              </div>
            </div>
            <button
              onClick={() => handleChange('isCreditCard', !filters.isCreditCard)}
              className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                filters.isCreditCard ? 'bg-indigo-600' : 'bg-slate-300 dark:bg-slate-600'
              }`}
            >
              <span
                className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                  filters.isCreditCard ? 'translate-x-6' : 'translate-x-1'
                }`}
              />
            </button>
          </motion.div>
        </div>

        <SheetFooter className="mt-8 gap-3">
          <Button
            variant="outline"
            onClick={onClear}
            className="flex-1"
          >
            Clear All
          </Button>
          <Button
            onClick={() => {
              onApply()
              onOpenChange(false)
            }}
            className="flex-1"
          >
            Apply Filters
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}

export default FilterDrawer

