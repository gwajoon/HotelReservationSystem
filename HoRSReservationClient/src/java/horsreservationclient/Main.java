/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.RegisteredGuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import horsreservationclient.MainApp;
import javax.ejb.EJB;

/**
 *
 * @author GuoJun
 */
public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote;
    @EJB
    private static RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    

    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(registeredGuestSessionBeanRemote, roomInventorySessionBeanRemote, reservationSessionBean);
        mainApp.runApp();
    }
}
