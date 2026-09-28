package com.sece.expert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "residents")
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Roll/Resident number is required")
    @Column(unique = true)
    private String rollNumber;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private String hostelBlock;

    @Email(message = "Email should be valid")
    private String email;

    public Resident() {
    }

    public Resident(Long id, String name, String rollNumber, String roomNumber, String hostelBlock, String email) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.roomNumber = roomNumber;
        this.hostelBlock = hostelBlock;
        this.email = email;
    }

    public Resident(String name, String rollNumber, String roomNumber, String hostelBlock, String email) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.roomNumber = roomNumber;
        this.hostelBlock = hostelBlock;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
