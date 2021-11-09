/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.RegisteredGuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import entity.RegisteredGuest;
import entity.Reservation;
import entity.RoomType;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.RegisteredGuestEmailExistException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class MainApp {

    private RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote;
    private RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    private RegisteredGuest currentGuest;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote, RoomInventorySessionBeanRemote roomInventorySessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote) {
        this.registeredGuestSessionBeanRemote = registeredGuestSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation System :: Reservation ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2. Register as Guest");
            System.out.println("3. Search Hotel Room");

            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doGuestLogin();
                } else if (response == 2) {
                    doCreateNewRegisteredGuest();
                } else if (response == 3) {
                    doSearchHotelRoom();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void doGuestLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Reservation :: Guest Login ***\n");

        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        try {
            currentGuest = registeredGuestSessionBeanRemote.registeredGuestLogin(email, password);
            System.out.println("Successfully logged in!");

            registeredGuestMenu();
        } catch (InvalidLoginCredentialException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void doCreateNewRegisteredGuest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Reservation :: Create New RegisteredGuest ***\n");

        System.out.print("Enter first name > ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name > ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        RegisteredGuest newRegisteredGuest = new RegisteredGuest(firstName, lastName, email, password);

        try {
            Long newRegisteredGuestId = registeredGuestSessionBeanRemote.createNewRegisteredGuest(newRegisteredGuest);
            System.out.println("New registered guest created successfully!: " + newRegisteredGuestId + "\n");
        } catch (RegisteredGuestEmailExistException | UnknownPersistenceException ex) {
            System.out.println("An error has occurred while creating the new registered guest!: The email already exist\n");
        }
    }

    public void registeredGuestMenu() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2. View My Reservation Details");
            System.out.println("3. View All My Reservations");
            System.out.println("4. Exit");

            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doSearchHotelRoom();
                } else if (response == 2) {
                    doViewMyReservation();
                } else if (response == 3) {
                    doViewAllMyReservations();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void doSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Reservation :: Search Available Hotel Rooms ***\n");

        System.out.print("Enter number of rooms > ");
        int numOfRooms = scanner.nextInt();
        scanner.nextLine();
        Date checkInDate = new Date();
        Date checkOutDate = new Date();
        try {
            System.out.print("Enter check-in date (dd/mm/yyyy) > ");
            checkInDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());

            System.out.print("Enter check-out date > ");
            checkOutDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());

        } catch (ParseException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        LocalDateTime newCheckInDate = new java.sql.Timestamp(
                checkInDate.getTime()).toLocalDateTime();
        newCheckInDate.plusHours(14);

        LocalDateTime newCheckOutDate = new java.sql.Timestamp(
                checkInDate.getTime()).toLocalDateTime();
        newCheckOutDate.plusHours(12);

        List<RoomType> availableRoomTypes = roomInventorySessionBeanRemote.getAvailableRoomTypes(checkInDate, checkOutDate, numOfRooms);

        if (availableRoomTypes.size() > 0) {
            System.out.printf("Rooms available from %t to %t for room types: %s", checkInDate, checkOutDate);

            for (int i = 0; i < availableRoomTypes.size(); i++) {
                RoomType roomType = availableRoomTypes.get(i);
                System.out.println(""+ i + roomType.getName() + reservationSessionBeanRemote.calculatePrice(newCheckInDate, newCheckOutDate, roomType, "Online") + ", \n");
            }
        }

        System.out.println("Select room type to reserve >");
        int selection = scanner.nextInt();

        if (currentGuest.getId() != null) {
            doReserveHotelRoom(availableRoomTypes.get(selection), checkInDate, checkOutDate, numOfRooms, currentGuest.getId());
        }
    }

    public void doReserveHotelRoom(RoomType roomType, Date checkInDate, Date checkOutDate, Integer numOfRooms, Long guestId) {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkInDate);
        reservation.setNumberOfRooms(numOfRooms);
        reservation.setReservationType("Online");

        try {
            Long reservationId = reservationSessionBeanRemote.createNewOnlineReservation(reservation, roomType.getId(), guestId);
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void doViewMyReservation() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Reservation Id>");
        Long reservationId = scanner.nextLong();
        Reservation reservation;

        try {
            reservation = reservationSessionBeanRemote.viewReservation(reservationId);
            System.out.println(reservation.toString());
                
                
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation " + reservationId + " not found");
        }
        
        
                

    }
    
    public void doViewAllMyReservations(){
        
        
        try{
            List<Reservation> reservations = reservationSessionBeanRemote.viewAllReservations(this.currentGuest.getId());
            
            for(Reservation reservation: reservations){
                System.out.println(reservation.toString());
            }
        } catch (RegisteredGuestNotFoundException ex){
            System.out.println("Registered Guest " + this.currentGuest.getId() + " not found");
        }
    }

}
