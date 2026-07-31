package com.hospital.dao;

import com.hospital.model.Patient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    /**
     * Reads patient data from the 1_Patient.csv file and populates a list of Patient objects.
     */
    public List<Patient> loadPatients(String filePath) throws IOException {
        List<Patient> patients = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Read and skip the header row
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Move past the header line
                    continue;
                }

                // Split the line by comma delimiter
                String[] data = line.split(",");

                // We expect 7 fields: PatientID, FirstName, LastName, DateOfBirth, Gender, ContactNumber, RoomNumber
                if (data.length >= 7) { 
                    try {
                        String patientId = data[0].trim();
                        String firstName = data[1].trim();
                        String lastName = data[2].trim();
                        String dob = data[3].trim();
                        String gender = data[4].trim();
                        String contactNumber = data[5].trim();
                        String roomNumber = data[6].trim();

                        // *** Object Creation ***
                        Patient patient = new Patient(patientId, firstName, lastName, dob, gender, contactNumber, roomNumber);
                        patients.add(patient);

                    } catch (Exception e) {
                        // Exception Handling: Crucial for data integrity
                        System.err.println("Skipping invalid row in " + filePath + ": " + line + ". Error: " + e.getMessage());
                    }
                }
            }
        } 
        System.out.println("--- Finished reading file: " + filePath + ". Total patients loaded: " + patients.size() + " ---");
        return patients;
    }
}
