package com.sece.expert.repository;

import com.sece.expert.entity.MealOptOut;
import com.sece.expert.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealOptOutRepository extends JpaRepository<MealOptOut, Long> {

    List<MealOptOut> findByResidentId(Long residentId);

    List<MealOptOut> findByDate(LocalDate date);

    Optional<MealOptOut> findByResidentIdAndDateAndMealType(Long residentId, LocalDate date, MealType mealType);

    boolean existsByResidentIdAndDateAndMealType(Long residentId, LocalDate date, MealType mealType);

    long countByDate(LocalDate date);

    long countByDateAndMealType(LocalDate date, MealType mealType);
}
