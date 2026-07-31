package com.hospital.server;

import com.hospital.dao.*; // To access the loaded data lists
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JsonServer {

    // --- Data Storage (Where we store the results from the DAOs) ---
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Room> rooms;
    private List<Appointment> appointments;

    // Constructor: Receives the loaded data objects from your service layer
    public JsonServer(List<Patient> patients, List<Doctor> doctors, List<Room> rooms, List<Appointment> appointments) {
        this.patients = patients;
        this.doctors = doctors;
        this.rooms = rooms;
        this.appointments = appointments;
    }

    /**
     * Starts the server to listen for incoming requests on a specific port.
     */
    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("JSON API Server started successfully on port " + port);

            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress());

                // Handle the request from the client
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }


    /**
     * Handles incoming requests from the client and sends back JSON data.
     */
    private void handleRequest(Socket clientSocket) throws IOException {
        // Read the request line sent by the client (e.g., GET /api/patients)
        String requestLine = clientSocket.getInputStream().readLine();
        System.out.println("Received Request: " + requestLine);

        String response = "";
        int statusCode = 200;

        // --- Simple Routing Logic based on the request ---
        if (requestLine.startsWith("GET /api/patients")) {
            // Fetch Patient data and convert it to JSON string
            response = generateJsonFromList(patients);
            sendResponse(clientSocket, response, statusCode);
        } 
        else if (requestLine.startsWith("GET /api/doctors")) {
            // Fetch Doctor data
            response = generateJsonFromList(doctors);
            sendResponse(clientSocket, response, statusCode);
        }
        else if (requestLine.startsWith("GET /api/rooms")) {
            // Fetch Room data
            response = generateJsonFromList(rooms);
            sendResponse(clientSocket, response, statusCode);
        }
        else if (requestLine.startsWith("GET /api/appointments")) {
            // Fetch Appointment data
            response = generateJsonFromList(appointments);
            sendResponse(clientSocket, response, statusCode);
        }
        else {
            // Handle unknown requests
            String errorMsg = "404 Not Found";
            response = errorMsg;
            statusCode = 404;
            sendResponse(clientSocket, response, statusCode);
        }
    }

    /**
     * Helper function to convert a Java List of objects into a JSON string.
     * (In a real project, you would use a library like Jackson for this!)
     */
    private String generateJsonFromList(List<?> dataList) {
        // *** WARNING: This is a simple placeholder. A real application needs a JSON Library! ***
        StringBuilder json = new StringBuilder();
        json.append("{\"status\": \"success\", \"data\": [");

        for (int i = 0; i < dataList.size(); i++) {
            // Simple serialization: Prints the object's string representation.
            // In a real app, you would use ObjectMapper from Jackson to convert objects to proper JSON strings.
            json.append(dataList.get(i).toString()); 
            if (i < dataList.size() - 1) {
                json.append(", ");
            }
        }

        json.append("]}");
        return json.toString();
    }


    /**
     * Helper function to send the response back to the client.
     */
    private void sendResponse(Socket socket, String response, int statusCode) throws IOException {
        // Send HTTP Status Line and Headers
        String responseHeader = "HTTP/1.1 " + statusCode + " OK\r\nContent-Type: application/json\r\nContent-Length: " + response.length() + "\r\n\r\n";
        
        // Send the response content
        clientSocket.getOutputStream().write(responseHeader.getBytes());
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) {
        int port = 8080;
        
        // --- STEP 1: Load all data into memory using your DAOs ---
        System.out.println("--- Initializing Data Loading... ---");
        try {
            PatientDAO patientDAO = new PatientDAO();
            DoctorDAO doctorDAO = new DoctorDAO();
            RoomDAO roomDAO = new RoomDAO();
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            // Load the data into memory (this is where the heavy lifting happens)
            List<Patient> patients = patientDAO.loadPatients("data/1_Patient.csv");
            List<Doctor> doctors = doctorDAO.loadDoctors("data/2_Doctor.csv");
            List<Room> rooms = roomDAO.loadRooms("data/4_Room.csv");
            List<Appointment> appointments = appointmentDAO.loadAppointments("data/3_Appointment.csv");

            // --- STEP 2: Initialize the Server with Loaded Data ---
            JsonServer server = new JsonServer(patients, doctors, rooms, appointments);
            
            // Start listening for requests
            server.startServer(port);

        } catch (IOException e) {
            System.err.println("\nCRITICAL ERROR: Failed to initialize data or start server.");
            System.err.println("Check that all CSV files exist in the 'data/' folder and are correctly formatted.");
            e.printStackTrace();
        }
    }
}
