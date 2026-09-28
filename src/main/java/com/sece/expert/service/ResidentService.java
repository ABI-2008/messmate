package com.sece.expert.service;

import com.sece.expert.entity.Resident;
import com.sece.expert.exception.BusinessRuleViolationException;
import com.sece.expert.exception.ResourceNotFoundException;
import com.sece.expert.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;

    @Autowired
    public ResidentService(ResidentRepository residentRepository) {
        this.residentRepository = residentRepository;
    }

    public Resident createResident(Resident resident) {
        if (residentRepository.existsByRollNumber(resident.getRollNumber())) {
            throw new BusinessRuleViolationException("Resident with roll number " + resident.getRollNumber() + " already exists.");
        }
        return residentRepository.save(resident);
    }

    public List<Resident> getAllResidents() {
        return residentRepository.findAll();
    }

    public Resident getResidentById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));
    }

    public Resident getResidentByRollNumber(String rollNumber) {
        return residentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with roll number: " + rollNumber));
    }

    public long getTotalResidentsCount() {
        return residentRepository.count();
    }
}
