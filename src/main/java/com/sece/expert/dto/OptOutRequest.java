package com.sece.expert.dto;

import com.sece.expert.entity.MealType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class OptOutRequest {

    @NotNull(message = "Resident ID is required")
    private Long residentId;

    @NotNull(message = "Opt out date is required")
    private LocalDate date;

    private MealType mealType; // null for full day skip

    private String reason;

    public OptOutRequest() {
    }

    public OptOutRequest(Long residentId, LocalDate date, MealType mealType, String reason) {
        this.residentId = residentId;
        this.date = date;
        this.mealType = mealType;
        this.reason = reason;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
