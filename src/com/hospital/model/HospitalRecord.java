package com.hospital.model;

/**
 * Abstract base class for all entities in the Hospital Management System.
 * Demonstrates the concept of Inheritance.
 */
public abstract class HospitalRecord {
    protected String id;

    // Constructor to initialize the mandatory ID field
    public HospitalRecord(String id) {
        this.id = id;
    }

    // Abstract method: Forces every subclass to define its own way to display details (Polymorphism setup)
    public abstract void displayDetails();

    // Getter for the ID
    public String getId() {
        return id;
    }
}

