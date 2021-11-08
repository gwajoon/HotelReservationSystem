/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemreservationclient;

import ejb.session.stateless.RegisteredGuestSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import entity.RegisteredGuest;
import entity.RoomType;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.RegisteredGuestEmailExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class MainApp {

    private RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote;
    private RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    private RegisteredGuest currentGuest;

    public MainApp() {
    }

    public MainApp(RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote, RoomInventorySessionBeanRemote roomInventorySessionBeanRemote) {
        this.registeredGuestSessionBeanRemote = registeredGuestSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
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
                    doCreateNewRegisteredGuest();
                } else if (response == 2) {
                    doGuestLogin();
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
                } else if (response == 3) {
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

        for (int i = 0; i < numOfRooms; i++) {
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
            
            List<RoomType> availableRoomTypes = roomInventorySessionBeanRemote.getAvailableRoomTypes(checkInDate, checkOutDate);
            
            if (availableRoomTypes.size() > 0) {
                System.out.printf("Rooms available from %t to %t for room types: %s", checkInDate, checkOutDate);
                
                for (RoomType r : availableRoomTypes) {
                    System.out.print(r.getName() + ", \n");
                }
            }
        }
        
        if (currentGuest.getId() != null) {
            doReserveHotelRoom();
        }
    }
    
    public void doReserveHotelRoom() {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        System.out.print("Choose room based on number > ");
        int roomId = scanner.nextInt();
    }
    
    
}
