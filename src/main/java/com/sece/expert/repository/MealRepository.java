package com.sece.expert.repository;

import com.sece.expert.entity.Meal;
import com.sece.expert.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByDate(LocalDate date);
    List<Meal> findByDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<Meal> findByDateAndMealType(LocalDate date, MealType mealType);
    List<Meal> findByMealType(MealType mealType);
}
