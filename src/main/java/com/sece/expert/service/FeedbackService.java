package com.sece.expert.service;

import com.sece.expert.dto.FeedbackRequest;
import com.sece.expert.dto.MealRatingSummaryDto;
import com.sece.expert.entity.Feedback;
import com.sece.expert.entity.Meal;
import com.sece.expert.entity.Resident;
import com.sece.expert.exception.DuplicateFeedbackException;
import com.sece.expert.exception.ResourceNotFoundException;
import com.sece.expert.repository.FeedbackRepository;
import com.sece.expert.repository.MealRepository;
import com.sece.expert.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ResidentRepository residentRepository;
    private final MealRepository mealRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository,
                           ResidentRepository residentRepository,
                           MealRepository mealRepository) {
        this.feedbackRepository = feedbackRepository;
        this.residentRepository = residentRepository;
        this.mealRepository = mealRepository;
    }

    /**
     * Business Rule Enforcement:
     * A resident can submit ONLY ONE feedback entry per meal per day.
     * Enforced in the service layer prior to database persistence.
     */
    public Feedback submitFeedback(FeedbackRequest request) {
        Resident resident = residentRepository.findById(request.getResidentId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + request.getResidentId()));

        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + request.getMealId()));

        // Enforce Business Rule
        boolean alreadySubmitted = feedbackRepository.existsByResidentIdAndMealId(request.getResidentId(), request.getMealId());
        if (alreadySubmitted) {
            throw new DuplicateFeedbackException("Resident (ID: " + request.getResidentId() + 
                    ") has already submitted feedback for meal on " + meal.getDate() + " (" + meal.getMealType() + ").");
        }

        Feedback feedback = new Feedback(resident, meal, request.getRating(), request.getComment());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> getFeedbacksByMealId(Long mealId) {
        return feedbackRepository.findByMealId(mealId);
    }

    public List<Feedback> getFeedbacksByResidentId(Long residentId) {
        return feedbackRepository.findByResidentId(residentId);
    }

    public MealRatingSummaryDto getMealRatingSummary(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        Double avgRating = feedbackRepository.findAverageRatingByMealId(mealId);
        Long count = feedbackRepository.countByMealId(mealId);
        return new MealRatingSummaryDto(meal, avgRating, count);
    }

    public Double getAverageRatingByDate(LocalDate date) {
        Double avg = feedbackRepository.findAverageRatingByDate(date);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    /**
     * Retrieve low-rated meals (average rating below specified threshold, default 3.0)
     * for a given date range.
     */
    public List<MealRatingSummaryDto> getLowRatedMeals(LocalDate startDate, LocalDate endDate, double threshold) {
        List<Meal> meals = mealRepository.findByDateBetween(startDate, endDate);
        List<MealRatingSummaryDto> lowRatedList = new ArrayList<>();

        for (Meal meal : meals) {
            Double avgRating = feedbackRepository.findAverageRatingByMealId(meal.getId());
            Long count = feedbackRepository.countByMealId(meal.getId());
            if (avgRating != null && avgRating < threshold && count > 0) {
                lowRatedList.add(new MealRatingSummaryDto(meal, avgRating, count));
            }
        }
        return lowRatedList;
    }
}
