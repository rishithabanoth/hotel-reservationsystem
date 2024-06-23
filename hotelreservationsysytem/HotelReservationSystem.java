import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HotelReservationSystem{
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "";
    public static void main (String[] args) throws ClassNotFoundException, SQLException {
        try{
           Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        
        try{
            Connection con = DriverManager.getConnection(url, username ,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");

                
                Scanner sc = new Scanner(System.in);
                System.out.println("Choose an option:");
                int choice = sc.nextInt();
                sc.nextLine();
                switch(choice){
                    case 1: 
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoomNumber(con, sc);
                        break;
                    case 4:
                        updateReservation(con, sc);
                        break;
                    case 5:
                        deleteReservations(con, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice! Try again....");      
                }

            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }


    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i != 0) {
            System.out.print(",");
            Thread.sleep(500);
            i--;           
        }
        System.out.println();
        System.out.println("Thank you for using Hotel Reservation System:)");
    }

    private static void deleteReservations(Connection con, Scanner sc) {
        try{
            System.out.println("Enter Reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = sc.nextLine();

            if(!reservationExists(con, reservationId)){
                System.out.println();
                System.out.println("Reservation not found for the given Reservation ID :(");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement s = con.createStatement()){
                int affectedRows = s.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println();
                    System.out.println("Reesrvation deleted successfully :)");
                }
                else {
                    System.out.println();
                    System.out.println("Reservation delection failed :(");
                }
            }        
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner sc) {
        try {
            System.out.println("Enter Reservation ID to update: ");
            int reservationID = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(con, reservationID)) {
                System.out.println();
                System.out.println("Reservation not found for the given Reservation ID :(");
                return;
            }
            System.out.println("Enter New Guest Name: ");
            String newGuestName = sc.nextLine();
            System.out.println("Enter New Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter New Contact Number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " + "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationID;
            try(Statement s = con.createStatement()){
                int affectedRows = s.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println();
                    System.out.println("Reservation updated successfully :)");
                }else {
                    System.out.println();
                    System.out.println("Reservation update failed :(");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationID) {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationID;

        try(Statement s = con.createStatement()) {
            ResultSet r = s.executeQuery(sql);
            return r.next();   // if there's a result, the reservation exists
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;  // Handle database errors as needed
         
        }
    }

    private static void getRoomNumber(Connection con, Scanner sc) {
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations" +
                    "WHERE reservation_id = " + reservationId +
                    "AND guest_name = '" + guestName + "'";
            try(Statement s = con.createStatement()){
                ResultSet r = s.executeQuery(sql);
                if(r.next()){
                    int roomNumber = r.getInt("room_number");
                    System.out.println();
                    System.out.println("Room number for Reservation ID "+ reservationId +
                            "and Guest '" + guestName + "' is: " + roomNumber);
                }
                else{
                    System.out.println();
                    System.out.println("Reservation not found for the Reservation ID and Guest Name :(");
                }
            }       
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void reserveRoom(Connection con, Scanner sc){
        try {
            System.out.println("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = sc.next();
            
            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)" +
                    "VALUES('" + guestName + "', " + roomNumber + ", '" + contactNumber + "' )";
                    
            try(Statement s = con.createStatement()){
                int effectedRows = s.executeUpdate(sql);
    
                if (effectedRows > 0) {
                    System.out.println();
                    System.out.println("Reservation successful :(");              
                }else {
                    System.out.println();
                    System.out.println("Reservation failed :(");
                }
            }        
        }
        catch (SQLException e){
            e.printStackTrace();
        }  
    }
    

    private static void viewReservation(Connection con) throws SQLException{
    String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
    try(Statement s = con.createStatement()){
        ResultSet r = s.executeQuery(sql);
        System.out.println("Current Reservations:");
        System.err.println("+------------------------------+------------------------+----------------------+-------------------------+--------------------------+");
        System.out.println("| Reservation ID               | Guest Name             | Room Numner          | Contact Number          | ReservationDate          |");
        System.out.println("+------------------------------+------------------------+----------------------+-------------------------+--------------------------+");

        while (r.next()) {
            int reservationId = r.getInt("reservation_id");
            String guestName = r.getString("guest_name");
            int roomNumber = r.getInt("room_number");
            String contactNumber = r.getString("contact_number");
            String reservationDate = r.getTimestamp("reservation_date").toString();

            System.out.printf("| %-14d             | %-15s               | %-13d             | %-20s           | %-19s         |\n",
                    reservationId, guestName, roomNumber, contactNumber, reservationDate);
        }
        System.out.println("+------------------------------+------------------------+----------------------+-------------------------+--------------------------+");
    }
   }
} 

