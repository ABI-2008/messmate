package com.sece.expert.service;

import com.sece.expert.dto.HeadcountReportDto;
import com.sece.expert.dto.OptOutRequest;
import com.sece.expert.entity.MealOptOut;
import com.sece.expert.entity.MealType;
import com.sece.expert.entity.Resident;
import com.sece.expert.exception.BusinessRuleViolationException;
import com.sece.expert.exception.ResourceNotFoundException;
import com.sece.expert.repository.MealOptOutRepository;
import com.sece.expert.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealOptOutService {

    private final MealOptOutRepository mealOptOutRepository;
    private final ResidentRepository residentRepository;

    @Autowired
    public MealOptOutService(MealOptOutRepository mealOptOutRepository, ResidentRepository residentRepository) {
        this.mealOptOutRepository = mealOptOutRepository;
        this.residentRepository = residentRepository;
    }

    public MealOptOut registerOptOut(OptOutRequest request) {
        Resident resident = residentRepository.findById(request.getResidentId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + request.getResidentId()));

        boolean exists = mealOptOutRepository.existsByResidentIdAndDateAndMealType(
                request.getResidentId(), request.getDate(), request.getMealType());

        if (exists) {
            throw new BusinessRuleViolationException("Opt-out already recorded for resident on " + request.getDate() + 
                    (request.getMealType() != null ? " for " + request.getMealType() : " (Full Day)"));
        }

        MealOptOut optOut = new MealOptOut(resident, request.getDate(), request.getMealType(), request.getReason());
        return mealOptOutRepository.save(optOut);
    }

    public List<MealOptOut> getOptOutsByResident(Long residentId) {
        return mealOptOutRepository.findByResidentId(residentId);
    }

    public List<MealOptOut> getOptOutsByDate(LocalDate date) {
        return mealOptOutRepository.findByDate(date);
    }

    public void cancelOptOut(Long id) {
        if (!mealOptOutRepository.existsById(id)) {
            throw new ResourceNotFoundException("Opt-out record not found with id: " + id);
        }
        mealOptOutRepository.deleteById(id);
    }

    /**
     * Business Rule 2:
     * Compute expected headcount for a given date excluding opted-out residents.
     */
    public HeadcountReportDto getHeadcountReport(LocalDate date) {
        long totalResidents = residentRepository.count();

        List<MealOptOut> optOuts = mealOptOutRepository.findByDate(date);

        long fullDayOptOuts = optOuts.stream().filter(o -> o.getMealType() == null).count();
        long breakfastOptOuts = fullDayOptOuts + optOuts.stream().filter(o -> MealType.BREAKFAST.equals(o.getMealType())).count();
        long lunchOptOuts = fullDayOptOuts + optOuts.stream().filter(o -> MealType.LUNCH.equals(o.getMealType())).count();
        long dinnerOptOuts = fullDayOptOuts + optOuts.stream().filter(o -> MealType.DINNER.equals(o.getMealType())).count();

        long totalOptedOut = optOuts.size();
        long expectedHeadcount = Math.max(0, totalResidents - fullDayOptOuts);

        return new HeadcountReportDto(date, totalResidents, totalOptedOut, expectedHeadcount,
                breakfastOptOuts, lunchOptOuts, dinnerOptOuts);
    }
}
