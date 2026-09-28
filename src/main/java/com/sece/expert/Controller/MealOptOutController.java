package com.sece.expert.controller;

import com.sece.expert.dto.HeadcountReportDto;
import com.sece.expert.dto.OptOutRequest;
import com.sece.expert.entity.MealOptOut;
import com.sece.expert.service.MealOptOutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/opt-outs")
@CrossOrigin(origins = "*")
public class MealOptOutController {

    private final MealOptOutService mealOptOutService;

    @Autowired
    public MealOptOutController(MealOptOutService mealOptOutService) {
        this.mealOptOutService = mealOptOutService;
    }

    @PostMapping
    public ResponseEntity<MealOptOut> registerOptOut(@Valid @RequestBody OptOutRequest request) {
        MealOptOut optOut = mealOptOutService.registerOptOut(request);
        return new ResponseEntity<>(optOut, HttpStatus.CREATED);
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<MealOptOut>> getOptOutsByResident(@PathVariable Long residentId) {
        return ResponseEntity.ok(mealOptOutService.getOptOutsByResident(residentId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<MealOptOut>> getOptOutsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealOptOutService.getOptOutsByDate(date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOptOut(@PathVariable Long id) {
        mealOptOutService.cancelOptOut(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/headcount")
    public ResponseEntity<HeadcountReportDto> getHeadcountReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealOptOutService.getHeadcountReport(date));
    }
}
