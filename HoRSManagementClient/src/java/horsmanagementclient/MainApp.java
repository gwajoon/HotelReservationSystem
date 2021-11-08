/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RegisteredGuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import static util.enumeration.EmployeeType.GUEST_RELATION;
import static util.enumeration.EmployeeType.OPERATION_MANAGER;
import static util.enumeration.EmployeeType.SALES_MANAGER;
import static util.enumeration.EmployeeType.SYSTEM_ADMIN;

/**
 *
 * @author GuoJun
 */
public class MainApp {

    private Employee currentEmployee;

    private HotelOperationModule hotelOperationModule;
    private SystemAdministrationModule systemAdministrationModule;
    private FrontOfficeModule frontOfficeModule;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote,
            RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote,
            RoomInventorySessionBeanRemote roomInventorySessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote,
            RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {

        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.registeredGuestSessionBeanRemote = registeredGuestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomInventorySessionBeanRemote = roomInventorySessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;

    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);

        int response = 0;

        while (true) {
            System.out.println("*** Welcome to Hotel Reservation System ***");
            System.out.println("1. Login");
            System.out.println("2. Exit\n");

            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!");

                        hotelOperationModule = new HotelOperationModule(roomTypeSessionBeanRemote, roomRateSessionBeanRemote, currentEmployee, roomSessionBeanRemote);
                        systemAdministrationModule = new SystemAdministrationModule(employeeSessionBeanRemote);
                        frontOfficeModule = new FrontOfficeModule();
                        mainMenu();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {

                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }

    }

    public void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.print("Enter email> ");
        email = scanner.nextLine();
        System.out.print("Enter password> ");
        password = scanner.nextLine();

        if (email.length() > 0 && password.length() > 0) {
            currentEmployee = employeeSessionBeanRemote.employeeLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }

    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System) ***\n");
        System.out.println("You are login as " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + " with " + currentEmployee.getEmployeeType().toString() + " rights\n");
           
        
        if (currentEmployee.getEmployeeType() == SYSTEM_ADMIN) {
            systemAdministrationModule.systemAdministrationMenu();
        }
        else if (currentEmployee.getEmployeeType() == OPERATION_MANAGER || currentEmployee.getEmployeeType() == SALES_MANAGER) {
            hotelOperationModule.hotelOperationMenu();
        } 
        else if (currentEmployee.getEmployeeType() == GUEST_RELATION) {
            frontOfficeModule.frontOfficeMenu();
        } 
    }

}
