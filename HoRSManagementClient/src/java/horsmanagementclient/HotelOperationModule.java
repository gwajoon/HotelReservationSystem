/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import static util.enumeration.EmployeeType.OPERATION_MANAGER;
import static util.enumeration.EmployeeType.SALES_MANAGER;
import util.exception.DeleteRoomRateException;
import util.exception.RoomRateNameExistsException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNameExistsException;
import util.exception.UnknownPersistenceException;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Room;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.RateType;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author GuoJun
 */
public class HotelOperationModule {

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private Employee currentEmployee;
    private RoomSessionBeanRemote roomSessionBeanRemote;

    public HotelOperationModule() {
    }

    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, Employee employee, RoomSessionBeanRemote roomSessionBeanRemote) {
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.currentEmployee = employee;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
    }

    public void hotelOperationMenu() {
        Scanner scanner = new Scanner(System.in);
        int response = 0;

        while (true) {

            System.out.println("*** Hotel Operation Menu ***\n");

            if (currentEmployee.getEmployeeType() == OPERATION_MANAGER) {
                System.out.println("1. Create New Room Type");
                System.out.println("2. View Room Type Details");
                System.out.println("3. Create New Room");
                System.out.println("4. View All Rooms");
                System.out.println("5. View Room Allocation Exception Report");
                System.out.println("6. Back\n");
                response = 0;

                while (response < 1 || response > 6) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewRoomType();
                    } else if (response == 2) {
                        doViewRoomTypeDetails();
                    } else if (response == 3) {
                        doCreateNewRoom();
                    } else if (response == 4) {
                        doViewRooms();
                    } else if (response == 5) {
                    } else if (response == 6) {
                        break;
                    }
                }
                if (response == 6) {
                    break;
                }
            }

            if (currentEmployee.getEmployeeType() == SALES_MANAGER) {
                System.out.println("1. Create New Room Rate");
                System.out.println("2. View Room Rate Details");
                System.out.println("3. View All Room Rates");
                System.out.println("4. Back\n");
                response = 0;

                while (response < 1 || response > 6) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewRoomRate();
                    } else if (response == 2) {
                        doViewRoomRateDetails();
                    } else if (response == 3) {
                        doViewAllRoomRates();
                    } else if (response == 4) {
                        break;
                    }
                }
                if (response == 4) {
                    break;
                }
            }

        }

    }

    private void doCreateNewRoomType() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Create New Room Type ***\n");
        System.out.print("Enter name of room type > ");
        String name = scanner.nextLine();

        System.out.print("Enter description of room type > ");
        String description = scanner.nextLine();

        System.out.print("Enter size of room type > ");
        String size = scanner.nextLine();

        System.out.print("Enter bed of room type > ");
        String bed = scanner.nextLine();

        System.out.print("Enter capacity > ");
        String capacity = scanner.nextLine().trim();

        System.out.print("Enter amenities > ");
        String amenities = scanner.nextLine().trim();

        System.out.print("Enter priority > ");
        int priority = scanner.nextInt();

        RoomType newRoomType = new RoomType(name, description, size, bed, capacity, amenities, priority);
        System.out.println(newRoomType.getName() + newRoomType.getDescription() + newRoomType.getSize() + newRoomType.getBed() + newRoomType.getCapacity() + newRoomType.getAmenities());

        try {
            Long newRoomRateId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType);
            System.out.println("New room rate created successfully!: " + newRoomRateId + "\n");
        } catch (RoomTypeNameExistsException ex) {
            System.out.println("An error has occurred while creating the new room rate!: The room rate name already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new room rate!: " + ex.getMessage() + "\n");
        }
    }

    private void doViewRoomTypeDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: View Room Type Details ***\n");
        System.out.println("Enter ID of Room Type to view");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();

        for (RoomType rt : roomTypes) {
            System.out.println("Room Id: " + rt.getId() + ". " + rt.getName());
        }

        System.out.print(">");

        try {
            Long roomTypeId = scanner.nextLong();
            RoomType roomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeId(roomTypeId);

            System.out.println("** " + "Room type ID: " + roomType.getId() + ", Room type name: " + roomType.getName() + " **\n");
            System.out.printf("Size: %s, Bed: %s, Capacity: %s, Amenities: %s, Priority: %s", roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities(), roomType.getPriority());
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            int response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoomType(roomType);
            } else if (response == 2) {
                doDeleteRoomType(roomType);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving room type: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Update Room Type ***\n");

        System.out.print("Enter description of room type > ");
        String description = scanner.nextLine();
        if (description.length() > 0) {
            roomType.setDescription(description);
        }

        System.out.print("Enter size of room type > ");
        String size = scanner.nextLine();
        if (size.length() > 0) {
            roomType.setSize(size);
        }

        System.out.print("Enter bed of room type > ");
        String bed = scanner.nextLine();
        if (bed.length() > 0) {
            roomType.setBed(bed);
        }

        System.out.print("Enter capacity > ");
        String capacity = scanner.nextLine().trim();
        if (capacity.length() > 0) {
            roomType.setCapacity(capacity);
        }

        System.out.print("Enter amenities > ");
        String amenities = scanner.nextLine().trim();
        if (amenities.length() > 0) {
            roomType.setAmenities(amenities);
        }

        try {
            roomTypeSessionBeanRemote.updateRoomType(roomType);
            System.out.println("Room type updated successfully!\n");
        } catch (RoomTypeNotFoundException | UpdateRoomTypeException ex) {
            System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");
        }

    }

    private void doDeleteRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Delete Room Type ***\n");
        System.out.printf("Confirm Delete Room Type %s Description: %s Size:%s Bed:%s Capacity: %s Amenities: %s (Room Type ID: %d) (Enter 'Y' to Delete)> ", roomType.getName(), roomType.getDescription(), roomType.getSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities(), roomType.getId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getId());
                System.out.println("Room Type deleted successfully!\n");
            } catch (DeleteRoomTypeException ex) {
                System.out.println("An error has occurred while deleting the room type: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room rate NOT deleted!\n");
        }
    }

    private void doCreateNewRoom() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Create New Room ***\n");
        System.out.print("Enter room number > ");
        String roomNum = scanner.nextLine().trim();

        System.out.println("Select Room Type");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();
        for (RoomType rt : roomTypes) {

            System.out.println("Room Type Id: " + rt.getId() + ". " + rt.getName());

        }

        System.out.print(">");

        Long response = scanner.nextLong();

        if (response > 0 && response <= roomTypes.size()) {

            Room newRoom = new Room(roomNum);
//           
            try {
                Long newRoomId = roomSessionBeanRemote.createNewRoom(newRoom, response);
                System.out.print("New Room " + newRoomId + " successfully created");
            } catch (RoomNumberExistException | UnknownPersistenceException ex) {
                System.out.print("error");
            }

        } else {
            System.out.println("Room Type not in choices");
        }

    }

    private void doViewRooms() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: View Room Details ***\n");
        List<Room> rooms = roomSessionBeanRemote.viewAllRooms();

        for (Room r : rooms) {
            System.out.println("Room Id: " + r.getId() + ". " + r.getRoomNumber());
        }
        System.out.println("Enter ID of Room to view");
        System.out.print(">");

        try {
            Long roomId = scanner.nextLong();
            Room room = roomSessionBeanRemote.retrieveRoomByRoomId(roomId);

            System.out.println("** " + "Room ID: " + room.getId() + ", Room number: " + room.getRoomNumber() + " **\n");
            System.out.printf("Room Type: %s, Room Status: %s\n", room.getRoomType().getName(), room.getRoomStatus().toString());
            System.out.println("------------------------");
            System.out.println("1: Update Room");
            System.out.println("2: Delete Room");
            System.out.println("3: Back\n");
            System.out.print("> ");
            int response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoom(room);
            } else if (response == 2) {
                doDeleteRoom(room);
            }
        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while retrieving room: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoom(Room room) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Update  Room ***\n");
        System.out.print("Enter new room number (blank if no change) > ");
        String number = scanner.nextLine();
        if (number.length() > 0) {
            room.setRoomNumber(number);
        }

        List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();

        for (RoomType rt : roomTypes) {
            System.out.println("Room Id: " + rt.getId() + ". " + rt.getName());
        }
        System.out.print("Choose ID of new room type > ");

        Long roomTypeId = scanner.nextLong();

        int availability = 0;

        while (availability < 1 || availability > 2) {
            System.out.println("Set the availability of the room > ");
            System.out.println("1. True");
            System.out.println("2. False");
            System.out.print("> ");

            availability = scanner.nextInt();
            if (availability == 1) {
                room.setRoomStatus(true);
            } else if (availability == 2) {
                room.setRoomStatus(false);
            } else {
                System.out.println("Invalid option. Please choose again.");
            }
        }

        try {
            if (roomTypeId.toString().length() > 0) {
                RoomType roomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeId(roomTypeId);
                room.setRoomType(roomType);
            }
            roomSessionBeanRemote.updateRoom(room);
            System.out.println("Room updated successfully!\n");
        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while updating room: " + ex.getMessage() + "\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error occurred while updating room: " + ex.getMessage() + "\n");
        }
    }

    private void doDeleteRoom(Room room) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Delete Room ***\n");
        System.out.printf("Confirm Delete Room %s (Room ID: %d) (Enter 'Y' to Delete)> ", room.getRoomNumber(), room.getId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomSessionBeanRemote.deleteRoom(room.getId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (RoomNotFoundException | DeleteRoomException ex) {
                System.out.println("An error has occurred while deleting the room: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room rate NOT deleted!\n");
        }
    }

    public void doViewAllRooms() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: View All Room ***\n");

        List<Room> rooms = roomSessionBeanRemote.viewAllRooms();

        for (Room r : rooms) {
            System.out.println("** " + "Room ID: " + r.getId() + ", Room number: " + r.getRoomNumber() + ", Room Status: " + r.getRoomStatus() + ", Room Type: " + r.getRoomType().getName() + "\n");
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doCreateNewRoomRate() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Create New Room Rate ***\n");
        System.out.print("Enter name of room rate > ");
        String name = scanner.nextLine();

        System.out.print("Enter rate type > ");
        System.out.print("1: " + RateType.values()[0]);
        System.out.print("2: " + RateType.values()[1]);
        System.out.print("3: " + RateType.values()[2]);
        System.out.print("4: " + RateType.values()[3]);
        Integer selection = scanner.nextInt();
        RateType rateType = RateType.values()[selection - 1];

        System.out.print("Enter rate per night > ");
        Double ratePerNight = scanner.nextDouble();
        scanner.nextLine();

        RoomRate newRoomRate = new RoomRate(name, rateType, ratePerNight);

        if (rateType.equals(RateType.PEAK) || rateType.equals(RateType.PROMOTION)) {

            try {
                System.out.print("Enter start date (DDMMYYYY) of room rate (press enter if no validity period) > ");
                Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());

                System.out.print("Enter end date (DDMMYYYY) of room rate (press enter if no validity period) > ");
                Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());
                newRoomRate.setStartDate(startDate);
                newRoomRate.setEndDate(endDate);
            } catch (ParseException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        System.out.println(newRoomRate.getName() + newRoomRate.getRatePerNight() + newRoomRate.getStartDate() + newRoomRate.getEndDate());

        System.out.println("Select Room Type");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();
        for (RoomType rt : roomTypes) {
            System.out.println("Room type id: " + rt.getId() + ". " + rt.getName());
        }

        System.out.print(">");
        Long roomTypeId = scanner.nextLong();

        try {
            Long newRoomRateId = roomRateSessionBeanRemote.createNewRoomRate(newRoomRate, roomTypeId);
            System.out.println("New room rate created successfully!: " + newRoomRateId + "\n");
        } catch (RoomRateNameExistsException ex) {
            System.out.println("An error has occurred while creating the new room rate!: The room rate name already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new room rate!: " + ex.getMessage() + "\n");
        }
    }

    private void doViewRoomRateDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: View Room Rate Details ***\n");
        System.out.println("Enter ID of Room Rate to view");
        List<RoomRate> roomRates = roomRateSessionBeanRemote.viewAllRoomRates();

        for (RoomRate rr : roomRates) {
            int listing = 1;
            System.out.println(listing + ". " + rr.getName());
            listing++;
        }

        System.out.print(">");

        try {
            Long roomRateId = scanner.nextLong();
            RoomRate roomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateId(roomRateId);

            System.out.println("** " + "Room rate ID: " + roomRate.getId() + ", Room rate name: " + roomRate.getName() + " **\n");
            System.out.printf("Rate per night: %d, start date: %s, end date: %s", roomRate.getRatePerNight(), roomRate.getStartDate(), roomRate.getEndDate());
            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            int response = scanner.nextInt();

            if (response == 1) {
                doUpdateRoomRate(roomRate);
            } else if (response == 2) {
                doDeleteRoomRate(roomRate);
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving room rate: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Update  Room Rate ***\n");
        System.out.print("Enter name of room rate (blank if no change) > ");
        String name = scanner.nextLine();
        if (name.length() > 0) {
            roomRate.setName(name);
        }

        System.out.print("Enter rate per night (blank if no change) > ");
        Double ratePerNight = scanner.nextDouble();
        scanner.nextLine();
        if (ratePerNight.toString().length() > 0) {
            roomRate.setRatePerNight(ratePerNight);
        }

        System.out.print("Enter start date (DDMMYYYY) of room rate (blank if no change) > ");
        String startDate = scanner.nextLine().trim();
        if (startDate.length() > 0) {
            try {
                Date newStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());
                roomRate.setStartDate(newStartDate);
            } catch (ParseException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.print("Enter end date (DDMMYYYY) of room rate (blank if no change) > ");
        String endDate = scanner.nextLine().trim();
        if (endDate.length() > 0) {
            try {
                Date newEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine());
                roomRate.setEndDate(newEndDate);
            } catch (ParseException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            roomRateSessionBeanRemote.updateRoomRate(roomRate);
            System.out.println("Room rate updated successfully!\n");
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while updating room rate: " + ex.getMessage() + "\n");
        }
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                roomRateSessionBeanRemote.deleteRoomRate(roomRate.getId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (RoomRateNotFoundException | DeleteRoomRateException ex) {
                System.out.println("An error has occurred while deleting the room rate: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room rate NOT deleted!\n");
        }
    }

    public void doViewAllRoomRates() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: Hotel Operation :: Sales Manager :: View All Room Rates  ***\n");

        List<RoomRate> roomRates = roomRateSessionBeanRemote.viewAllRoomRates();

        for (RoomRate rr : roomRates) {
            System.out.println("** " + "Room rate ID: " + rr.getId() + ", Room rate name: " + rr.getName() + " **\n" + "Rate Per Night: $" + rr.getRatePerNight() + ", Start Date: " + rr.getStartDate() + ", End Date: " + rr.getEndDate() + "\n");
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
