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
import entity.Reservation;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public FrontOfficeModule(ReservationSessionBeanRemote reservationSessionBeanRemote,
            RoomInventorySessionBeanRemote roomInventorySessionBeanRemote, AllocationSessionBeanRemote allocationSessionBeanRemote) {
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
        this.allocationSessionBeanRemote = allocationSessionBeanRemote;
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
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
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

            doReserveHotelRoom(availableRoomTypes.get(selection - 1).getId(), checkInDate, checkOutDate, numOfRooms, firstName, lastName, email);

        }

    }

    public void doReserveHotelRoom(Long roomTypeId, Date checkInDate, Date checkOutDate, Integer numOfRooms, String firstName, String lastName, String email) {
        System.out.println("*** Hotel Reservation System :: Reservation :: Registered Guest :: Reserve Room ***\n");

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfRooms(numOfRooms);
        reservation.setReservationType("Walk-In");

        try {
            Long reservationId = reservationSessionBeanRemote.createNewWalkInReservation(reservation, roomTypeId, firstName, lastName, email);
            System.out.println("Reservation " + reservationId + " successfully made");

            if (doCheckIfSameDay(checkInDate, new Date())) {
                System.out.println("allocating now");
                allocationSessionBeanRemote.allocateCurrentDay(reservationId, checkInDate);
            }

        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void checkInGuest() {
        Scanner scanner = new Scanner(System.in);
        int response;

        System.out.println("*** Hotel Front Office Module :: Check In Guest ***\n");

        System.out.print("Enter guest email > ");
        String email = scanner.nextLine();
        System.out.print("Enter reservation id > ");
        Long reservationId = scanner.nextLong();

    }

    public void checkOutGuest() {

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
}
