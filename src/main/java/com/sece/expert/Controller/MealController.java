package com.sece.expert.controller;

import com.sece.expert.dto.MealScheduleRequest;
import com.sece.expert.entity.Meal;
import com.sece.expert.service.MealService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<Meal> scheduleMeal(@Valid @RequestBody MealScheduleRequest request) {
        Meal scheduled = mealService.scheduleMeal(request);
        return new ResponseEntity<>(scheduled, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Meal>> getMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (date != null) {
            return ResponseEntity.ok(mealService.getMealsByDate(date));
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(mealService.getMealsForWeek(startDate, endDate));
        }
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
