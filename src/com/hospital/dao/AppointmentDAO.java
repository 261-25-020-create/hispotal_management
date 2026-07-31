package com.hospital.dao;

import com.hospital.model.Appointment; // Assuming you create an Appointment model class
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    /**
     * Reads appointment data from the 3_Appointment.csv file and populates a list of Appointment objects.
     */
    public List<Appointment> loadAppointments(String filePath) throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Read and skip the header row
            br.readLine(); 

            System.out.println("--- Starting to read file: " + filePath + " ---");

            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by comma delimiter
                String[] data = line.split(",");

                if (data.length >= 6) { // Expecting 6 fields: ID, P_ID, D_ID, Date, Type, Status
                    try {
                        String appointmentId = data[0].trim();
                        // NOTE: These IDs must match the actual IDs in your Patient/Doctor tables!
                        String patientId = data[1].trim(); 
                        String doctorId = data[2].trim();
                        String appointmentDate = data[3].trim();
                        String type = data[4].trim();
                        String status = data[5].trim();

                        // *** Object Creation ***
                        Appointment appointment = new Appointment(
                                appointmentId, 
                                patientId, 
                                doctorId, 
                                appointmentDate, 
                                type, 
                                status
                        );
                        appointments.add(appointment);

                    } catch (Exception e) {
                        // Exception Handling: Crucial for data integrity
                        System.err.println("Skipping invalid row in " + filePath + ": " + line + ". Error: " + e.getMessage());
                    }
                }
            }
        } // File stream is automatically closed

        System.out.println("--- Finished reading file: " + filePath + ". Total appointments loaded: " + appointments.size() + " ---");
        return appointments;
    }
}
