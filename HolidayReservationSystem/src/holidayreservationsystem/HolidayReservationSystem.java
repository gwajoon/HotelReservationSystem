
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomInventorySessionBeanRemote;
import holidayreservationsystem.MainApp;
import javax.ejb.EJB;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author GuoJun
 */
public class HolidayReservationSystem {

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;
    @EJB
    private static RoomInventorySessionBeanRemote roomInventorySessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // TODO code application logic here
        MainApp mainApp = new MainApp(partnerSessionBeanRemote, roomInventorySessionBeanRemote);
        mainApp.runApp();
    }
}

