package com.sece.expert.dto;

import java.time.LocalDate;

public class HeadcountReportDto {
    private LocalDate date;
    private long totalResidents;
    private long totalOptedOut;
    private long expectedHeadcount;
    private long breakfastOptOuts;
    private long lunchOptOuts;
    private long dinnerOptOuts;

    public HeadcountReportDto() {
    }

    public HeadcountReportDto(LocalDate date, long totalResidents, long totalOptedOut, long expectedHeadcount,
                              long breakfastOptOuts, long lunchOptOuts, long dinnerOptOuts) {
        this.date = date;
        this.totalResidents = totalResidents;
        this.totalOptedOut = totalOptedOut;
        this.expectedHeadcount = expectedHeadcount;
        this.breakfastOptOuts = breakfastOptOuts;
        this.lunchOptOuts = lunchOptOuts;
        this.dinnerOptOuts = dinnerOptOuts;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(long totalResidents) {
        this.totalResidents = totalResidents;
    }

    public long getTotalOptedOut() {
        return totalOptedOut;
    }

    public void setTotalOptedOut(long totalOptedOut) {
        this.totalOptedOut = totalOptedOut;
    }

    public long getExpectedHeadcount() {
        return expectedHeadcount;
    }

    public void setExpectedHeadcount(long expectedHeadcount) {
        this.expectedHeadcount = expectedHeadcount;
    }

    public long getBreakfastOptOuts() {
        return breakfastOptOuts;
    }

    public void setBreakfastOptOuts(long breakfastOptOuts) {
        this.breakfastOptOuts = breakfastOptOuts;
    }

    public long getLunchOptOuts() {
        return lunchOptOuts;
    }

    public void setLunchOptOuts(long lunchOptOuts) {
        this.lunchOptOuts = lunchOptOuts;
    }

    public long getDinnerOptOuts() {
        return dinnerOptOuts;
    }

    public void setDinnerOptOuts(long dinnerOptOuts) {
        this.dinnerOptOuts = dinnerOptOuts;
    }
}
