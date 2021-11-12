/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerEmailExistException_Exception;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.ReservationWebService;
import ws.client.ReservationWebService_Service;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author GuoJun
 */
public class MainApp {

    private ReservationWebService port;
    private Partner currentPartner;

    public MainApp() {
    }

    public MainApp(ReservationWebService port) {
        this.port = port;
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
                    doPartnerLogin();
                } else if (response == 2) {
                    doCreateNewPartner();
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

    public void doPartnerLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System :: Reservation :: Partner Login ***\n");

        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        try {
            currentPartner = port.partnerLogin(email, password);
            System.out.println("Successfully logged in!");

            partnerMenu();
        } catch (InvalidLoginCredentialException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void partnerMenu() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation System :: Reservation :: Registered Partner ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2. View My Reservation Details");
            System.out.println("3. View All My Reservations");

            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doReserveRoom();
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

    public void doReserveRoom() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;
        doSearchHotelRoom();

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

        ws.client.Partner newPartner = new ws.client.Partner();
        newPartner.setFirstName(firstName);
        newPartner.setLastName(lastName);
        newPartner.setEmail(email);
        newPartner.setPassword(password);

        try {
            Long newPartnerId = port.createNewPartner(newPartner);
            System.out.println("New registered guest created successfully!: " + newPartnerId + "\n");
        } catch (PartnerEmailExistException_Exception | UnknownPersistenceException_Exception ex) {
            System.out.println("An error has occurred while creating the new registered guest!: The email already exist\n");
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

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(checkInDate);
            XMLGregorianCalendar checkInDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            c.setTime(checkOutDate);
            XMLGregorianCalendar checkOutDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            List<ws.client.RoomType> availableRoomTypes = port.getAvailableRoomTypes(checkInDateXML, checkOutDateXML, numOfRooms);

            if (availableRoomTypes.size() > 0) {
                System.out.printf("Rooms available from %s to %s for room types: \n", checkInDate, checkOutDate);

                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    ws.client.RoomType roomType = availableRoomTypes.get(i - 1);
                    ReservationWebService_Service service = new ReservationWebService_Service();
                    System.out.println("" + i + " " + roomType.getName() + " " + service.getReservationWebServicePort().calculatePrice(checkInDateXML, checkOutDateXML, roomType.getId(), "Online", numOfRooms) + ", \n");
                }
            } else {
                System.out.println("No Rooms Available");
            }
            if (currentPartner != null) {
                System.out.println("Make a reservation? >");
                System.out.println("1: Yes");
                System.out.println("2: No");

                if (scanner.nextInt() == 1) {

                    System.out.println("Select Room Type >");
                    for (int i = 1; i <= availableRoomTypes.size(); i++) {
                        ws.client.RoomType roomType = availableRoomTypes.get(i - 1);
                        ReservationWebService_Service service = new ReservationWebService_Service();
                        System.out.println("" + i + " " + roomType.getName() + " " + service.getReservationWebServicePort().calculatePrice(checkInDateXML, checkOutDateXML, roomType.getId(), "Online", numOfRooms) + ", \n");
                    }
                    int selection = scanner.nextInt();

                    if (currentPartner.getId() != null) {
                        doReserveHotelRoom(availableRoomTypes.get(selection - 1).getId(), checkInDate, checkOutDate, numOfRooms, currentPartner.getId());
                    }
                }
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void doReserveHotelRoom(Long roomTypeId, Date checkInDate, Date checkOutDate, Integer numOfRooms, Long guestId) {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");

        ws.client.Reservation reservation = new ws.client.Reservation();

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(checkInDate);
            XMLGregorianCalendar checkInDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            c.setTime(checkOutDate);
            XMLGregorianCalendar checkOutDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            reservation.setCheckInDate(checkInDateXML);
            reservation.setCheckOutDate(checkOutDateXML);
            reservation.setNumberOfRooms(numOfRooms);
            reservation.setReservationType("Online");

            try {
                ReservationWebService_Service service = new ReservationWebService_Service();
                Long reservationId = service.getReservationWebServicePort().createNewPartnerReservation(reservation, roomTypeId, guestId);
                System.out.println("Reservation " + reservationId + " successfully made");
            } catch (UnknownPersistenceException_Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void doViewMyReservation() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Reservation Id>");
        Long reservationId = scanner.nextLong();
        ws.client.Reservation reservation;

        try {
            reservation = port.viewReservation(reservationId);
            System.out.println("" + reservation.getReservationType() + " Reservation " + reservation.getId() + " from "
                + reservation.getCheckInDate().toString() + " to " + reservation.getCheckOutDate().toString() + " for "
                    + reservation.getNumberOfRooms() + " " + reservation.getRoomType().getName());

        } catch (ReservationNotFoundException_Exception ex) {
            System.out.println("Reservation " + reservationId + " not found");
        }

    }

    public void doViewAllMyReservations() {
        try {
            List<ws.client.Reservation> reservations = port.viewAllPartnerReservations(currentPartner.getId());

            for (ws.client.Reservation reservation : reservations) {
                System.out.println("" + reservation.getReservationType() + " Reservation " + reservation.getId() + " from "
                + reservation.getCheckInDate().toString() + " to " + reservation.getCheckOutDate().toString() + " for "
                    + reservation.getNumberOfRooms() + " " + reservation.getRoomType().getName() + "\n");
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
