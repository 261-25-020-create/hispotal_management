package com.hospital.service;

import com.hospital.dao.*; // Import all your Data Access Objects
import com.hospital.model.*; // Import all your Model Classes
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HospitalService {

    // --- Dependencies (Injecting the DAOs) ---
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private final RoomDAO roomDAO;
    private final AppointmentDAO appointmentDAO;

    // Constructor: Injecting the dependencies makes testing easier and follows good OOP practice.
    public HospitalService(PatientDAO patientDAO, DoctorDAO doctorDAO, RoomDAO roomDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
        this.roomDAO = roomDAO;
        this.appointmentDAO = appointmentDAO;
    }

    // --- Data Loading Methods (Orchestration) ---

    public void loadAllData() throws IOException {
        System.out.println("\n==========================================");
        System.out.println("STARTING DATA LOADING PROCESS...");
        System.out.println("==========================================");

        // 1. Load Patients (using Collection)
        List<Patient> patients = patientDAO.loadPatients("data/1_Patient.csv");
        System.out.println("Loaded " + patients.size() + " Patient records.");
        
        // 2. Load Doctors (using Collection)
        List<Doctor> doctors = doctorDAO.loadDoctors("data/2_Doctor.csv");
        System.out.println("Loaded " + doctors.size() + " Doctor records.");

        // 3. Load Rooms (using Collection)
        List<Room> rooms = roomDAO.loadRooms("data/4_Room.csv");
        System.out.println("Loaded " + rooms.size() + " Room records.");
        
        // 4. Load Appointments (using Collection)
        List<Appointment> appointments = appointmentDAO.loadAppointments("data/3_Appointment.csv");
        System.out.println("Loaded " + appointments.size() + " Appointment records.");

        // In a real application, you would store these lists in instance variables here:
        // this.allPatients = patients;
        // ... etc. 
        
        System.out.println("\nData loading complete.");
    }


    // --- Example Polymorphic/Business Logic Method ---

    /**
     * Demonstrates linking data using the loaded collections and Polymorphism.
     * This method shows how to use the loaded lists together.
     */
    public void generateDoctorAppointmentReport(String doctorId) {
        System.out.println("\n==========================================");
        System.out.println("GENERATING REPORT FOR DOCTOR ID: " + doctorId);
        System.out.println("==========================================");

        // 1. Find the Doctor object (using Collection search)
        Doctor targetDoctor = doctorDAO.loadDoctors("data/2_Doctor.csv")
                                     .stream()
                                     .filter(d -> d.getId().equals(doctorId))
                                     .findFirst()
                                     .orElse(null);

        if (targetDoctor == null) {
            System.out.println("Error: Doctor ID " + doctorId + " not found.");
            return;
        }

        // 2. Find all appointments for this Doctor (using another Collection search)
        List<Appointment> relatedAppointments = appointmentDAO.loadAppointments("data/3_Appointment.csv").stream()
                                                  .filter(app -> app.getDoctorId().equals(doctorId))
                                                  .toList();

        if (relatedAppointments.isEmpty()) {
            System.out.println("No appointments found for this doctor.");
            return;
        }

        // 3. Report using Polymorphism
        System.out.println("\n--- Appointments Linked to " + targetDoctor.getLastName() + " ---");
        for (Appointment appt : relatedAppointments) {
            appt.displayDetails(); // Calls the specific display method defined in Appointment.java!
        }
    }
}
