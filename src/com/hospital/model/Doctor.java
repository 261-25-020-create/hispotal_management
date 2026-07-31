package com.hospital.model;

/**
 * Represents a Doctor, inheriting common attributes from HospitalRecord.
 */
public class Doctor extends HospitalRecord {
    private String specialization;
    private String contactNumber;
    private double salary; // Storing salary as a number type for better calculation later

    // Constructor
    public Doctor(String id, String firstName, String lastName, String specialization, String contactNumber, String salary) {
        super(id); // Calls the constructor of HospitalRecord (sets 'id')
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        // Attempt to convert salary string to a numeric type
        try {
            this.salary = Double.parseDouble(salary);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse salary for Doctor " + id + ". Setting to 0.");
            this.salary = 0.0;
        }
    }

    // --- Getters (Essential for data retrieval) ---
    public String getSpecialization() {
        return specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public double getSalary() {
        return salary;
    }

    // Implementing the Polymorphic method from HospitalRecord
    @Override
    public void displayDetails() {
        System.out.println("==========================================");
        System.out.println("RECORD TYPE: Doctor");
        System.out.println("Doctor ID: " + getId());
        System.out.println("Name: " + this.lastName + ", " + this.firstName);
        System.out.println("Specialization: " + this.specialization);
        System.out.println("Contact: " + this.contactNumber);
        System.out.println("Salary: $" + String.format("%.2f", this.salary)); // Formatted for readability
        System.out.println("==========================================");
    }
}
