/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import entity.Employee;
import entity.FirstTypeException;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.SecondTypeException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class FrontOfficeModule {

    private Employee currentEmployee;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    private AllocationSessionBeanRemote allocationSessionBeanRemote;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FrontOfficeModule(ReservationSessionBeanRemote reservationSessionBeanRemote,
            RoomInventorySessionBeanRemote roomInventorySessionBeanRemote, AllocationSessionBeanRemote allocationSessionBeanRemote) {
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
        this.allocationSessionBeanRemote = allocationSessionBeanRemote;
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public void frontOfficeMenu() {
        Scanner scanner = new Scanner(System.in);
        int response;

        System.out.println("*** Hotel Front Office Module ***\n");

        while (true) {
            System.out.println("1. Walk-in Search Room");
            System.out.println("2. Check-in Guest");
            System.out.println("3. Check-out Guest");
            System.out.println("4. Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doSearchHotelRoom();
                } else if (response == 2) {
                    checkInGuest();
                } else if (response == 3) {
                    checkOutGuest();
                } else if (response == 4) {
                    break;
                }

            }
            if (response == 4) {
                break;
            }
        }

    }

    public void doSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Front Office :: Reservation :: Search Available Hotel Rooms ***\n");

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

        System.out.println("Make a reservation? >");
        System.out.println("1: Yes");
        System.out.println("2: No");

        if (scanner.nextInt() == 1) {

            System.out.println("Select Room Type >");
            for (int i = 1; i <= availableRoomTypes.size(); i++) {
                RoomType roomType = availableRoomTypes.get(i - 1);
                System.out.println("" + i + " " + roomType.getName() + " " + reservationSessionBeanRemote.calculatePrice(checkInDate, checkOutDate, roomType.getId(), "Walk-In", numOfRooms) + ", \n");
            }
            int selection = scanner.nextInt();

            scanner.nextLine();

            System.out.println("Input first name >");
            String firstName = scanner.nextLine().trim();
            System.out.println("Input last name >");
            String lastName = scanner.nextLine().trim();
            System.out.println("Input email >");
            String email = scanner.nextLine().trim();

            if (selection > 0 && selection <= availableRoomTypes.size()) {

                doReserveHotelRoom(availableRoomTypes.get(selection - 1).getId(), checkInDate, checkOutDate, numOfRooms, firstName, lastName, email);
            } else {
                System.out.println("Invalid Selection");
            }
        }

    }

    public void doReserveHotelRoom(Long roomTypeId, Date checkInDate, Date checkOutDate, Integer numOfRooms, String firstName, String lastName, String email) {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfRooms(numOfRooms);
        reservation.setReservationType("Walk-In");

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

        if (constraintViolations.isEmpty()) {

            try {
                Long reservationId = reservationSessionBeanRemote.createNewWalkInReservation(reservation, roomTypeId, firstName, lastName, email);
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
            showInputDataValidationErrors(constraintViolations);
        }
    }

    public void checkInGuest() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Front Office Module :: Check In Guest ***\n");

        System.out.print("Enter reservation id > ");
        Long reservationId = scanner.nextLong();
        try {
            Reservation reservation = reservationSessionBeanRemote.checkInGuest(reservationId);
            for (Room room : reservation.getRooms()) {
                System.out.println("Reservation " + reservation.getId() + " : Your allocated " + room.getRoomType()
                        + " number is " + room.getRoomNumber());
            }
            if (!reservation.getAllocationExceptions().isEmpty()) {
                for (SecondTypeException secondTypeException : reservation.getAllocationExceptions()) {
                    if (secondTypeException.getClass().equals(FirstTypeException.class)) {
                        FirstTypeException firstTypeException = (FirstTypeException) secondTypeException;
                        System.out.println("Reservation " + reservation.getId() + " : Your upgraded allocated "
                                + firstTypeException.getNewRoomType() + " number is " + firstTypeException.getNewRoom().getRoomNumber());
                    } else {
                        System.out.println("Reservation " + reservation.getId() + " Insufficient room availability for " + secondTypeException.getOldRoomType());
                    }
                }
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation " + reservationId + " not found");

            System.out.println("Successfully checked in guest");
        }

    }

    public void checkOutGuest() {
        System.out.println("Thank you for your visit");

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

    private void showInputDataValidationErrors(Set<ConstraintViolation<Reservation>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
