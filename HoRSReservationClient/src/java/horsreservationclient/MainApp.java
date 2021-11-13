/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.AllocationSessionBeanRemote;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
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
    private AllocationSessionBeanRemote allocationSessionBeanRemote;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MainApp() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public MainApp(RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote, RoomInventorySessionBeanRemote roomInventorySessionBeanRemote,
            ReservationSessionBeanRemote reservationSessionBeanRemote, AllocationSessionBeanRemote allocationSessionBeanRemote) {
        this();
        this.registeredGuestSessionBeanRemote = registeredGuestSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.allocationSessionBeanRemote = allocationSessionBeanRemote;

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

        Set<ConstraintViolation<RegisteredGuest>> constraintViolations = validator.validate(newRegisteredGuest);

        if (constraintViolations.isEmpty()) {

            try {
                Long newRegisteredGuestId = registeredGuestSessionBeanRemote.createNewRegisteredGuest(newRegisteredGuest);
                System.out.println("New registered guest created successfully!: " + newRegisteredGuestId + "\n");
            } catch (RegisteredGuestEmailExistException | UnknownPersistenceException ex) {
                System.out.println("An error has occurred while creating the new registered guest!: The email already exist\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrors(constraintViolations);
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
            System.out.println("Invalid Check In or Check Out Date!");
            return;

        }

        List<RoomType> availableRoomTypes = roomInventorySessionBeanRemote.getAvailableRoomTypes(checkInDate, checkOutDate, numOfRooms);

        if (availableRoomTypes.size() > 0) {
            System.out.printf("Rooms available from %s to %s for room types: ", checkInDate, checkOutDate);

            for (int i = 1; i <= availableRoomTypes.size(); i++) {
                RoomType roomType = availableRoomTypes.get(i - 1);
                System.out.println("" + i + " " + roomType.getName() + " " + reservationSessionBeanRemote.calculatePrice(checkInDate, checkOutDate, roomType.getId(), "Online", numOfRooms) + ", \n");
            }
        } else {
            System.out.println("No Rooms Available");
        }
        if (currentGuest != null) {
            System.out.println("Make a reservation? >");
            System.out.println("1: Yes");
            System.out.println("2: No");

            if (scanner.nextInt() == 1) {

                System.out.println("Select Room Type >");
                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    RoomType roomType = availableRoomTypes.get(i - 1);
                    System.out.println("" + i + " " + roomType.getName() + " " + reservationSessionBeanRemote.calculatePrice(checkInDate, checkOutDate, roomType.getId(), "Online", numOfRooms) + ", \n");
                }
                int selection = scanner.nextInt();
                
                if ( selection > 0 && selection <= availableRoomTypes.size() && currentGuest.getId() != null) {
                    doReserveHotelRoom(availableRoomTypes.get(selection - 1).getId(), checkInDate, checkOutDate, numOfRooms, currentGuest.getId());
                } else {
                    System.out.println("Invalid Selection");
                }
            }
        }
    }

    
    public void doReserveHotelRoom(Long roomTypeId, Date checkInDate, Date checkOutDate, Integer numOfRooms, Long guestId) {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfRooms(numOfRooms);
        reservation.setReservationType("Online");
        
        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

        if (constraintViolations.isEmpty()) {

        try {
            Long reservationId = reservationSessionBeanRemote.createNewOnlineReservation(reservation, roomTypeId, guestId);
            System.out.println("Reservation " + reservationId + " successfully made");
            if (doCheckIfSameDay(checkInDate, new Date())) {
                System.out.println("allocating now");
                allocationSessionBeanRemote.allocateCurrentDay(reservationId, checkInDate);
            }
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForReservation(constraintViolations);
        }
    }

    public void doViewMyReservation() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Reservation Id>");
        Long reservationId = scanner.nextLong();
        Reservation reservation;

        try {
            reservation = reservationSessionBeanRemote.viewReservation(reservationId);
            if (reservation.getGuest().getId().equals(currentGuest.getId())) {
                System.out.println(reservation.toString());
            } else {
                System.out.println("Reservation " + reservationId + " does not belong to you");
            }

        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation " + reservationId + " not found");
        }

    }

    public void doViewAllMyReservations() {

        try {
            List<Reservation> reservations = reservationSessionBeanRemote.viewAllReservations(this.currentGuest.getId());

            for (Reservation reservation : reservations) {
                System.out.println(reservation.toString());
            }
        } catch (RegisteredGuestNotFoundException ex) {
            System.out.println("Registered Guest " + this.currentGuest.getId() + " not found");
        }
    }

    public boolean doCheckIfSameDay(Date checkInDate, Date currentDate) {
        LocalDateTime newCheckInDate = checkInDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime newCurrentDate = currentDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return newCheckInDate.getDayOfYear() == newCurrentDate.getDayOfYear() && newCheckInDate.getYear() == newCurrentDate.getYear()
                && newCurrentDate.getHour() >= 2;

    }

    private void showInputDataValidationErrors(Set<ConstraintViolation<RegisteredGuest>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForReservation(Set<ConstraintViolation<Reservation>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
