package com.sece.expert.dto;

import com.sece.expert.entity.Meal;

public class MealRatingSummaryDto {
    private Meal meal;
    private Double averageRating;
    private Long feedbackCount;

    public MealRatingSummaryDto() {
    }

    public MealRatingSummaryDto(Meal meal, Double averageRating, Long feedbackCount) {
        this.meal = meal;
        this.averageRating = averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0;
        this.feedbackCount = feedbackCount != null ? feedbackCount : 0L;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0;
    }

    public Long getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(Long feedbackCount) {
        this.feedbackCount = feedbackCount;
    }
}
