package com.example.expensetracker.dto;

public class AverageCategoryDto {
    private String category;
    private Double totalIncome;
    private Double totalExpense;
    private Double averageMonthlyIncome;
    private Double averageMonthlyExpense;
    private Integer monthsAnalyzed;

    public AverageCategoryDto() {
    }

    public AverageCategoryDto(String category, Double totalIncome, Double totalExpense,
                             Double averageMonthlyIncome, Double averageMonthlyExpense,
                             Integer monthsAnalyzed) {
        this.category = category;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.averageMonthlyIncome = averageMonthlyIncome;
        this.averageMonthlyExpense = averageMonthlyExpense;
        this.monthsAnalyzed = monthsAnalyzed;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getAverageMonthlyIncome() {
        return averageMonthlyIncome;
    }

    public void setAverageMonthlyIncome(Double averageMonthlyIncome) {
        this.averageMonthlyIncome = averageMonthlyIncome;
    }

    public Double getAverageMonthlyExpense() {
        return averageMonthlyExpense;
    }

    public void setAverageMonthlyExpense(Double averageMonthlyExpense) {
        this.averageMonthlyExpense = averageMonthlyExpense;
    }

    public Integer getMonthsAnalyzed() {
        return monthsAnalyzed;
    }

    public void setMonthsAnalyzed(Integer monthsAnalyzed) {
        this.monthsAnalyzed = monthsAnalyzed;
    }
}

