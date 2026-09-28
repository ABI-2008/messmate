package com.sece.expert.repository;

import com.sece.expert.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    Optional<Resident> findByRollNumber(String rollNumber);
    boolean existsByRollNumber(String rollNumber);
}
