package HospitalManagementSystem;
//This will be my driver class

import java.sql.*;
import java.util.Scanner;

public class HospitaManagementSystem {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/Hospital_Management_System";
    private static final String username = "root";
    private static final String password = "Gaukit@5446";
    static void menu(Connection connection, Scanner scanner,Patient patient, Doctor doctor){
        while (true) {
            System.out.println("Hospital Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patient");
            System.out.println("3. View Doctors");
            System.out.println("4. Book Appointment");
            System.out.println("5. Exit");

            System.out.println("Enter your choice : ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> patient.addPatient();
                case 2 -> patient.viewPatients();
                case 3 -> doctor.viewDoctors();
                case 4 -> BookAppointment(connection, scanner, patient, doctor);
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid Choice!!!");
            }
        }
    }

    public static void BookAppointment(Connection connection, Scanner scanner, Patient patient, Doctor doctor) {
        System.out.println("Enter Patient Id : ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id : ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD) : ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {

            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {

                String appointmentQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_dates) VALUES (?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointed Booked!");
                    } else {
                        System.out.println("Failed to book an appointment");
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date");
            }

        } else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_dates = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            menu(connection, scanner, patient, doctor);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

    }

}