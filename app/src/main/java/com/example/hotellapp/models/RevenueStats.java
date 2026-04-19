package com.example.hotellapp.models;

public class RevenueStats {
    private double todayRevenue;
    private double monthRevenue;
    private double totalRevenue;

    public RevenueStats() {
    }

    public RevenueStats(double todayRevenue, double monthRevenue, double totalRevenue) {
        this.todayRevenue = todayRevenue;
        this.monthRevenue = monthRevenue;
        this.totalRevenue = totalRevenue;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public double getMonthRevenue() {
        return monthRevenue;
    }

    public void setMonthRevenue(double monthRevenue) {
        this.monthRevenue = monthRevenue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}