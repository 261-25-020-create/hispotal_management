package com.hospital;

import com.hospital.dao.*; // Import DAOs
import com.hospital.service.HospitalService; // Import the main logic class
import java.io.IOException;

public class MainApp {

    public static void main(String[] args) {
        // --- 1. Setup Dependencies (Dependency Injection) ---
        // Instantiate all Data Access Objects (DAOs)
        DoctorDAO doctorDAO = new DoctorDAO();
        RoomDAO roomDAO = new RoomDAO();
        PatientDAO patientDAO = new PatientDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();

        // Initialize the Service Layer, passing the DAOs to it
        HospitalService hospitalService = new HospitalService(
            patientDAO, 
            doctorDAO, 
            roomDAO, 
            appointmentDAO
        );

        try {
            // --- 2. Load All Data ---
            // This triggers the loading of all CSV files into memory using Collections and Exception Handling.
            hospitalService.loadAllData();

            // --- 3. Demonstrate Polymorphism/Business Logic ---
            System.out.println("\n**************************************************");
            System.out.println("DEMONSTRATING BUSINESS LOGIC (Polymorphism)");
            System.out.println("**************************************************");
            
            // Example: Generate a report by linking Appointments to Doctors
            hospitalService.generateDoctorAppointmentReport("D101"); // Example using one of the IDs from the data

        } catch (IOException e) {
            System.err.println("\nFATAL ERROR: Failed to read one or more CSV files.");
            System.err.println("Please check if all CSV files exist in the 'data/' folder and are correctly formatted.");
            e.printStackTrace();
        }

        System.out.println("\nApplication execution finished.");
    }
}
