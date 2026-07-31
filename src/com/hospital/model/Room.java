package com.hospital.model;

/**
 * Represents a Room, inheriting common attributes from HospitalRecord.
 */
public class Room extends HospitalRecord {
    private String roomNumber;
    private String type; // e.g., Single, Double
    private String status; // e.g., Occupied, Vacant
    private double rent;

    // Constructor
    public Room(String id, String roomNumber, String type, String status, String rent) {
        super(id); // Calls the constructor of HospitalRecord (sets 'id')
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = status;
        try {
            this.rent = Double.parseDouble(rent);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse rent for Room " + id + ". Setting to 0.0.");
            this.rent = 0.0;
        }
    }

    // --- Getters ---
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public double getRent() {
        return rent;
    }

    // Implementing the Polymorphic method from HospitalRecord
    @Override
    public void displayDetails() {
        System.out.println("==========================================");
        System.out.println("RECORD TYPE: Room");
        System.out.println("Room ID: " + getId());
        System.out.println("Room Number: " + this.roomNumber);
        System.out.println("Type: " + this.type);
        System.out.println("Status: " + this.status);
        System.out.println("Rent: $" + String.format("%.2f", this.rent));
        System.out.println("==========================================");
    }
}
