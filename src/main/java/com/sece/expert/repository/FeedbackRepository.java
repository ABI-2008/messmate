package com.sece.expert.repository;

import com.sece.expert.entity.Feedback;
import com.sece.expert.entity.Meal;
import com.sece.expert.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByMealId(Long mealId);

    List<Feedback> findByResidentId(Long residentId);

    Optional<Feedback> findByResidentIdAndMealId(Long residentId, Long mealId);

    boolean existsByResidentIdAndMealId(Long residentId, Long mealId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.meal.id = :mealId")
    Double findAverageRatingByMealId(@Param("mealId") Long mealId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.meal.id = :mealId")
    Long countByMealId(@Param("mealId") Long mealId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.meal.date = :date")
    Double findAverageRatingByDate(@Param("date") LocalDate date);

    @Query("SELECT f FROM Feedback f WHERE f.meal.date BETWEEN :startDate AND :endDate")
    List<Feedback> findByMealDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
