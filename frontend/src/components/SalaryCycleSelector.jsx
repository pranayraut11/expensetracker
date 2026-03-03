import React, { useState, useEffect } from 'react'
import { getSalaryCycles } from '../services/salaryCycleApi'

/**
 * Salary Cycle Selector Component
 *
 * Allows user to choose between Calendar Month and Salary Cycle
 * When Salary Cycle is selected, shows dropdown of available cycles
 */
const SalaryCycleSelector = ({
  onCycleChange,
  onModeChange,
  selectedMode = 'calendar', // 'calendar' or 'salary'
  selectedCycleId = null
}) => {
  const [salaryCycles, setSalaryCycles] = useState([])
  const [loading, setLoading] = useState(false)
  const [mode, setMode] = useState(selectedMode)
  const [cycleId, setCycleId] = useState(selectedCycleId)

  // Sync internal mode state with prop changes (e.g., when Clear button is clicked)
  useEffect(() => {
    setMode(selectedMode)
  }, [selectedMode])

  // Sync internal cycleId state with prop changes
  useEffect(() => {
    setCycleId(selectedCycleId)
  }, [selectedCycleId])

  useEffect(() => {
    if (mode === 'salary') {
      fetchSalaryCycles()
    }
  }, [mode])

  const fetchSalaryCycles = async () => {
    try {
      setLoading(true)
      const cycles = await getSalaryCycles()
      setSalaryCycles(cycles)

      // Auto-select first cycle if none selected
      if (!cycleId && cycles.length > 0) {
        const firstCycle = cycles[0]
        setCycleId(firstCycle.cycleId)
        if (onCycleChange) {
          onCycleChange(firstCycle)
        }
      }
    } catch (error) {
      console.error('Error fetching salary cycles:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleModeChange = (e) => {
    const newMode = e.target.value
    setMode(newMode)

    if (onModeChange) {
      onModeChange(newMode)
    }

    // If switching to calendar mode, clear cycle selection
    if (newMode === 'calendar') {
      setCycleId(null)
      if (onCycleChange) {
        onCycleChange(null)
      }
    }
  }

  const handleCycleChange = (e) => {
    const selectedId = parseInt(e.target.value)
    setCycleId(selectedId)

    // Find the full cycle object
    const selectedCycle = salaryCycles.find(c => c.cycleId === selectedId)

    if (onCycleChange && selectedCycle) {
      onCycleChange(selectedCycle)
    }
  }

  return (
    <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
      {/* Month Type Selector */}
      <div className="flex flex-col">
        <label className="text-sm font-medium text-gray-700 mb-1">
          Month Type
        </label>
        <select
          value={mode}
          onChange={handleModeChange}
          className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
        >
          <option value="calendar">Calendar Month</option>
          <option value="salary">Salary Cycle</option>
        </select>
      </div>

      {/* Salary Cycle Selector */}
      {mode === 'salary' && (
        <div className="flex flex-col flex-1">
          <label className="text-sm font-medium text-gray-700 mb-1">
            Select Salary Cycle
          </label>
          {loading ? (
            <div className="px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500">
              Loading cycles...
            </div>
          ) : salaryCycles.length === 0 ? (
            <div className="px-3 py-2 border border-gray-300 rounded-lg bg-yellow-50 text-yellow-700">
              No salary cycles found. Upload salary transactions first.
            </div>
          ) : (
            <select
              value={cycleId || ''}
              onChange={handleCycleChange}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
            >
              {salaryCycles.map(cycle => (
                <option key={cycle.cycleId} value={cycle.cycleId}>
                  {cycle.label}
                </option>
              ))}
            </select>
          )}
        </div>
      )}
    </div>
  )
}

export default SalaryCycleSelector

