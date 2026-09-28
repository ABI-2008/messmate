package com.sece.expert.service;

import com.sece.expert.dto.MealScheduleRequest;
import com.sece.expert.entity.Meal;
import com.sece.expert.entity.MealType;
import com.sece.expert.entity.MenuItem;
import com.sece.expert.exception.ResourceNotFoundException;
import com.sece.expert.repository.MealRepository;
import com.sece.expert.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MealService(MealRepository mealRepository, MenuItemRepository menuItemRepository) {
        this.mealRepository = mealRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Meal scheduleMeal(MealScheduleRequest request) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + request.getMenuItemId()));

        // Check if meal already scheduled for this date and type
        Optional<Meal> existing = mealRepository.findByDateAndMealType(request.getDate(), request.getMealType());
        Meal meal;
        if (existing.isPresent()) {
            meal = existing.get();
            meal.setMenuItem(menuItem);
            meal.setSpecialNote(request.getSpecialNote());
        } else {
            meal = new Meal(request.getDate(), request.getMealType(), menuItem, request.getSpecialNote());
        }
        return mealRepository.save(meal);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public List<Meal> getMealsByDate(LocalDate date) {
        return mealRepository.findByDate(date);
    }

    public List<Meal> getMealsForWeek(LocalDate startDate, LocalDate endDate) {
        return mealRepository.findByDateBetween(startDate, endDate);
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + id));
    }

    public void deleteMeal(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new ResourceNotFoundException("Meal not found with id: " + id);
        }
        mealRepository.deleteById(id);
    }
}
