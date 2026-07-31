package com.hospital.model;

public class Appointment extends HospitalRecord {

    private String appointmentId;
    private String patientId; // Foreign Key to Patient
    private String doctorId;  // Foreign Key to Doctor
    private String appointmentDate;
    private String type;
    private String status;

    // Constructor
    public Appointment(String appointmentId, String patientId, String doctorId, String appointmentDate, String type, String status) {
        super(appointmentId); // Inherits ID from HospitalRecord
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.type = type;
        this.status = status;
    }

    // --- Getters (Essential for Polymorphism and Service Layer) ---
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getType() { return type; }
    public String getStatus() { return status; }

    // Override the general display method for specific detail output
    @Override
    public void displayDetails() {
        System.out.println("==========================================");
        System.out.println("RECORD TYPE: Appointment");
        System.out.println("Appointment ID: " + this.appointmentId);
        System.out.println("Patient ID: " + this.patientId);
        System.out.println("Doctor ID: " + this.doctorId);
        System.out.println("Date: " + this.appointmentDate);
        System.out.println("Type: " + this.type);
        System.out.println("Status: " + this.status);
        System.out.println("==========================================");
    }
}
