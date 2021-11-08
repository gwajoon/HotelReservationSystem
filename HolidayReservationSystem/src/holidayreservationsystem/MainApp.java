/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import entity.Partner;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.PartnerEmailExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class MainApp {

    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;

    public MainApp() {
    }

    public MainApp(PartnerSessionBeanRemote partnerSessionBeanRemote, RoomInventorySessionBeanRemote roomInventorySessionBeanRemote) {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation System :: Reservation ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2. Register as Partner");
            System.out.println("3. Search Hotel Room");

            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewPartner();
                } else if (response == 2) {
                    doPartnerLogin();
                } else if (response == 3) {
                    doSearchHotelRooms();
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

    public void doPartnerLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System :: Reservation :: Partner Login ***\n");

        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        try {
            partnerSessionBeanRemote.partnerLogin(email, password);
            System.out.println("Successfully logged in!");

            partnerMenu();
        } catch (InvalidLoginCredentialException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void doCreateNewPartner() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System :: Reservation :: Create New Partner ***\n");

        System.out.print("Enter first name > ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name > ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        Partner newPartner = new Partner(firstName, lastName, email, password);

        try {
            Long newPartnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
            System.out.println("New registered guest created successfully!: " + newPartnerId + "\n");
        } catch (PartnerEmailExistException | UnknownPersistenceException ex) {
            System.out.println("An error has occurred while creating the new registered guest!: The email already exist\n");
        }
    }

    public void doSearchHotelRooms() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Holiday Reservation System :: Reservation :: Search Available Hotel Rooms ***\n");

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
           // int numberOfAvailableRooms = roomInventorySessionBeanRemote.getNumberOfAvailableRooms(checkInDate, checkOutDate);
//            if (numberOfAvailableRooms > 0) {
//                System.out.printf("Rooms available from %t to %t", checkInDate, checkOutDate);
//            };
        }
        
    }

    public void partnerMenu() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation System :: Reservation :: Registered Partner ***\n");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2. View Partner Reservation Details");
            System.out.println("3. View All Partner Reservations");

            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doReserveRoom();
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

    public void doReserveRoom() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        doSearchHotelRooms();
        
        System.out.print("Choose room based on number > ");
        int roomId = scanner.nextInt();
    }
}
