#!/bin/bash

# Salary Cycle Setup Script
# This script helps you initialize and manage salary cycles

API_BASE_URL="http://localhost:8080"

echo "=========================================="
echo "  Salary Cycle Management Script"
echo "=========================================="
echo ""

# Function to detect salary cycles
detect_cycles() {
    echo "🔍 Detecting salary cycles from existing transactions..."
    response=$(curl -s -X POST "${API_BASE_URL}/api/salary-cycles/detect")
    echo "✅ $response"
    echo ""
}

# Function to refresh salary cycles
refresh_cycles() {
    echo "🔄 Refreshing salary cycles (delete and recreate)..."
    response=$(curl -s -X POST "${API_BASE_URL}/api/salary-cycles/refresh")
    echo "✅ $response"
    echo ""
}

# Function to update last salary cycle
update_last_cycle() {
    echo "📅 Updating last salary cycle end date to today..."
    response=$(curl -s -X POST "${API_BASE_URL}/api/salary-cycles/update-last")
    echo "✅ $response"
    echo ""
}

# Function to list all salary cycles
list_cycles() {
    echo "📋 Listing all salary cycles..."
    response=$(curl -s "${API_BASE_URL}/api/salary-cycles")
    echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
    echo ""
}

# Function to get cycle totals
get_cycle_totals() {
    echo "Enter Salary Cycle ID:"
    read cycle_id
    echo "💰 Fetching totals for cycle ${cycle_id}..."
    response=$(curl -s "${API_BASE_URL}/api/salary-cycles/${cycle_id}/totals")
    echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
    echo ""
}

# Main menu
while true; do
    echo "Select an option:"
    echo "1. Detect Salary Cycles (from existing transactions)"
    echo "2. Refresh Salary Cycles (delete and recreate)"
    echo "3. Update Last Cycle End Date"
    echo "4. List All Salary Cycles"
    echo "5. Get Cycle Totals"
    echo "6. Exit"
    echo ""
    echo -n "Enter choice [1-6]: "
    read choice

    case $choice in
        1)
            detect_cycles
            ;;
        2)
            refresh_cycles
            ;;
        3)
            update_last_cycle
            ;;
        4)
            list_cycles
            ;;
        5)
            get_cycle_totals
            ;;
        6)
            echo "Goodbye!"
            exit 0
            ;;
        *)
            echo "❌ Invalid choice. Please select 1-6."
            echo ""
            ;;
    esac
done

