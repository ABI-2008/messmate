package com.sece.expert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "meal_opt_outs",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"resident_id", "opt_out_date", "meal_type"})
    }
)
public class MealOptOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @NotNull(message = "Opt out date is required")
    @Column(name = "opt_out_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType; // null means opt out for full day

    private String reason;

    private LocalDateTime createdAt;

    public MealOptOut() {
        this.createdAt = LocalDateTime.now();
    }

    public MealOptOut(Resident resident, LocalDate date, MealType mealType, String reason) {
        this.resident = resident;
        this.date = date;
        this.mealType = mealType;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
