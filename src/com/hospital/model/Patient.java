package com.hospital.model;

/**
 * Represents a Patient, inheriting from HospitalRecord.
 */
public class Patient extends HospitalRecord {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String contactNumber;
    private String roomNumber; // Linking to Room data

    // Constructor
    public Patient(String id, String firstName, String lastName, String dateOfBirth, String gender, String contactNumber, String roomNumber) {
        super(id); // Calls the constructor of HospitalRecord
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
    }

    // Implementing the required displayDetails method (Polymorphism)
    @Override
    public void displayDetails() {
        System.out.println("==========================================");
        System.out.println("RECORD TYPE: Patient");
        System.out.println("Patient ID: " + getId());
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("DOB: " + dateOfBirth);
        System.out.println("Gender: " + gender);
        System.out.println("Contact: " + contactNumber);
        System.out.println("Room Assigned: " + roomNumber);
        System.out.println("------------------------------------------");
    }

    // Getters (omitted for brevity, but you should add them!)
}
