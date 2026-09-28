package com.sece.expert.controller;

import com.sece.expert.dto.FeedbackRequest;
import com.sece.expert.dto.MealRatingSummaryDto;
import com.sece.expert.entity.Feedback;
import com.sece.expert.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        Feedback feedback = feedbackService.submitFeedback(request);
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByMeal(@PathVariable Long mealId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByMealId(mealId));
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByResident(@PathVariable Long residentId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByResidentId(residentId));
    }

    @GetMapping("/average/meal/{mealId}")
    public ResponseEntity<MealRatingSummaryDto> getMealAverageRating(@PathVariable Long mealId) {
        return ResponseEntity.ok(feedbackService.getMealRatingSummary(mealId));
    }

    @GetMapping("/average/day/{date}")
    public ResponseEntity<Map<String, Object>> getDayAverageRating(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double avgRating = feedbackService.getAverageRatingByDate(date);
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("averageRating", avgRating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-rated")
    public ResponseEntity<List<MealRatingSummaryDto>> getLowRatedMeals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "3.0") double threshold) {
        return ResponseEntity.ok(feedbackService.getLowRatedMeals(startDate, endDate, threshold));
    }
}
