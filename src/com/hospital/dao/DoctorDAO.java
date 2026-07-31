package com.hospital.dao;

import com.hospital.model.Doctor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Reads doctor data from the 2_Doctor.csv file and populates a list of Doctor objects.
     */
    public List<Doctor> loadDoctors(String filePath) throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Read and skip the header row
            br.readLine(); 

            System.out.println("--- Starting to read file: " + filePath + " ---");

            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by comma delimiter
                String[] data = line.split(",");

                if (data.length >= 6) { // Expecting 6 fields based on your previous input format
                    try {
                        String doctorId = data[0].trim();
                        String firstName = data[1].trim();
                        String lastName = data[2].trim();
                        String specialization = data[3].trim();
                        String contactNumber = data[4].trim();
                        String salary = data[5].trim();

                        // *** Object Creation ***
                        Doctor doctor = new Doctor(doctorId, firstName, lastName, specialization, contactNumber, salary);
                        doctors.add(doctor);

                    } catch (Exception e) {
                        // Exception Handling: Log and skip bad rows
                        System.err.println("Skipping invalid row in " + filePath + ": " + line + ". Error: " + e.getMessage());
                    }
                }
            }
        } // File stream is automatically closed

        System.out.println("--- Finished reading file: " + filePath + ". Total doctors loaded: " + doctors.size() + " ---");
        return doctors;
    }
}
