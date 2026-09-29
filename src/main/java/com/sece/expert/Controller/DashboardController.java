package com.sece.expert.controller;

import com.sece.expert.dto.HeadcountReportDto;
import com.sece.expert.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ResidentService residentService;
    private final MenuItemService menuItemService;
    private final MealService mealService;
    private final FeedbackService feedbackService;
    private final MealOptOutService mealOptOutService;

    @Autowired
    public DashboardController(ResidentService residentService,
                               MenuItemService menuItemService,
                               MealService mealService,
                               FeedbackService feedbackService,
                               MealOptOutService mealOptOutService) {
        this.residentService = residentService;
        this.menuItemService = menuItemService;
        this.mealService = mealService;
        this.feedbackService = feedbackService;
        this.mealOptOutService = mealOptOutService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate targetDate = date != null ? date : LocalDate.now();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalResidents", residentService.getTotalResidentsCount());
        stats.put("totalMenuItems", menuItemService.getAllMenuItems().size());
        stats.put("totalMealsScheduled", mealService.getAllMeals().size());
        stats.put("totalFeedbacksCount", feedbackService.getAllFeedbacks().size());
        stats.put("todayAvgRating", feedbackService.getAverageRatingByDate(targetDate));
        
        HeadcountReportDto headcount = mealOptOutService.getHeadcountReport(targetDate);
        stats.put("headcountReport", headcount);

        return ResponseEntity.ok(stats);
    }
}
