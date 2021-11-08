/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;

/**
 *
 * @author GuoJun
 */
public class FrontOfficeModule {

    private Employee currentEmployee;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    public FrontOfficeModule() {
    }

    public void frontOfficeMenu() {
        Scanner scanner = new Scanner(System.in);
        int response;

        System.out.println("*** Hotel Front Office Module ***\n");
        
        while (true) {
            System.out.println("1. Walk-in Search Room");
            System.out.println("2. Walk-in Reserve Room");
            System.out.println("3. Check-in Guest");
            System.out.println("4. Check-out Guest");
            System.out.println("5. Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {

                } else if (response == 2) {

                } else if (response == 3) {
                    checkInGuest();
                } else if (response == 4) {
                    checkOutGuest();
                } else if (response == 5) {
                    break;
                }
            }
            if (response == 5) {
                break;
            }
        }
    }
    
    public void checkInGuest() {
        Scanner scanner = new Scanner(System.in);
        int response;

        System.out.println("*** Hotel Front Office Module :: Check In Guest ***\n");
        
        System.out.print("Enter guest username > ");
        String guestUsername = scanner.nextLine();
        
        
        
    }
    
    public void checkOutGuest() {
        
    }
}
