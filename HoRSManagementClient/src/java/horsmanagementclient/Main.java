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
import javax.ejb.EJB;
import ejb.session.stateless.RoomTypeSessionBeanRemote;

/**
 *
 * @author GuoJun
 */
public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    @EJB
    private static RegisteredGuestSessionBeanRemote registeredGuestSessionBeanRemote;
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;
    @EJB
    private static RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeEntitySessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(employeeSessionBeanRemote, registeredGuestSessionBeanRemote,
                reservationSessionBeanRemote, roomInventorySessionBeanRemote,
                roomRateSessionBeanRemote, roomSessionBeanRemote, roomTypeEntitySessionBeanRemote);
        mainApp.runApp();
    }

}
