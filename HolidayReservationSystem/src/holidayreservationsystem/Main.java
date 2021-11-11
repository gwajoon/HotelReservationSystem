/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import ws.client.ReservationWebService;
import ws.client.ReservationWebService_Service;

/**
 *
 * @author GuoJun
 */
public class Main {

    public static void main(String[] args) {

        // TODO code application logic here
        ReservationWebService_Service service = new ReservationWebService_Service();
        ReservationWebService port = service.getReservationWebServicePort();
        MainApp mainApp = new MainApp(port);
        mainApp.runApp();
    }

    // TODO code application logic here
}
