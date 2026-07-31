package com.hospital.dao;

import com.hospital.model.Room;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    /**
     * Reads room data from the 4_Room.csv file and populates a list of Room objects.
     */
    public List<Room> loadRooms(String filePath) throws IOException {
        List<Room> rooms = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Read and skip the header row
            br.readLine(); 

            System.out.println("--- Starting to read file: " + filePath + " ---");

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 4) { // Expecting at least 4 fields based on your input
                    try {
                        String roomId = data[0].trim();
                        String roomNumber = data[1].trim();
                        String type = data[2].trim();
                        String status = data[3].trim();
                        String rent = data[4].trim();

                        // *** Object Creation ***
                        Room room = new Room(roomId, roomNumber, type, status, rent);
                        rooms.add(room);

                    } catch (Exception e) {
                        System.err.println("Skipping invalid row in " + filePath + ": " + line + ". Error: " + e.getMessage());
                    }
                }
            }
        } 

        System.out.println("--- Finished reading file: " + filePath + ". Total rooms loaded: " + rooms.size() + " ---");
        return rooms;
    }
}
