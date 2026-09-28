package com.sece.expert.dto;

import com.sece.expert.entity.MealType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MealScheduleRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    private String specialNote;

    public MealScheduleRequest() {
    }

    public MealScheduleRequest(LocalDate date, MealType mealType, Long menuItemId, String specialNote) {
        this.date = date;
        this.mealType = mealType;
        this.menuItemId = menuItemId;
        this.specialNote = specialNote;
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

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }
}
