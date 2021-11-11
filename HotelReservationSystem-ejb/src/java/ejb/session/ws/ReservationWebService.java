/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomInventorySessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerEmailExistException;
import util.exception.PartnerNotFoundException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")
    @EJB 
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB 
    private RoomInventorySessionBeanLocal roomInventorySessionBeanLocal;
    
    
    
    @WebMethod(operationName = "createNewWalkInReservation")
    public Long createNewWalkInReservation(@WebParam(name = "reservation") Reservation reservation, @WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "firstName") String firstName, @WebParam(name = "lastName") String lastName, @WebParam(name = "email") String email) throws UnknownPersistenceException {
        return reservationSessionBeanLocal.createNewWalkInReservation(reservation, roomTypeId, firstName, lastName, email);
    }

    @WebMethod(operationName = "calculatePrice")
    public Double calculatePrice(@WebParam(name = "checkInDate") Date checkInDate, @WebParam(name = "checkOutDate") Date checkOutDate, @WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "reservationType") String reservationType, @WebParam(name = "numOfRooms") Integer numOfRooms) {
        return reservationSessionBeanLocal.calculatePrice(checkInDate, checkOutDate, roomTypeId, reservationType, numOfRooms);
    }

    @WebMethod(operationName = "viewReservation")
    public Reservation viewReservation(@WebParam(name = "reservationId") Long reservationId) throws ReservationNotFoundException {
        return reservationSessionBeanLocal.viewReservation(reservationId);
    }

    @WebMethod(operationName = "viewAllReservations")
    public List<Reservation> viewAllReservations(@WebParam(name = "registeredGuestId") Long registeredGuestId) throws RegisteredGuestNotFoundException {
        return reservationSessionBeanLocal.viewAllReservations(registeredGuestId);
    }

    @WebMethod(operationName = "getRoomRates")
    public List<RoomRate> getRoomRates(@WebParam(name = "checkInDate") Date checkInDate, @WebParam(name = "checkOutDate") Date checkOutDate, @WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "reservationType") String reservationType) {
        return reservationSessionBeanLocal.getRoomRates(checkInDate, checkOutDate, roomTypeId, reservationType);
    }

    @WebMethod(operationName = "createNewOnlineReservation")
    public Long createNewOnlineReservation(@WebParam(name = "reservation") Reservation reservation, @WebParam(name = "roomTypeId") Long roomTypeId, @WebParam(name = "guestId") Long guestId) throws UnknownPersistenceException {
        return reservationSessionBeanLocal.createNewOnlineReservation(reservation, roomTypeId, guestId);
    }

    @WebMethod(operationName = "getAvailableRoomTypes")
    public List<RoomType> getAvailableRoomTypes(@WebParam(name = "checkInDate") Date checkInDate, @WebParam(name = "checkOutDate") Date checkOutDate, @WebParam(name = "numOfRooms") Integer numOfRooms) {
        List<RoomType> roomTypes = roomInventorySessionBeanLocal.getAvailableRoomTypes(checkInDate, checkOutDate, numOfRooms);
        for (RoomType rt : roomTypes) {
            for (RoomRate rr : rt.getRoomRates()) {
            em.detach(rr);
            rr.setRoomType(null);
        }
           for (Room r : rt.getRooms()) {
               em.detach(r);
               r.setRoomType(null);
           }
        }
        return roomTypes;
    }
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "email") String email, @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        Partner partner = partnerSessionBeanLocal.partnerLogin(email, password);
        
        for (Reservation r : partner.getReservations()) {
            em.detach(r);
            r.setPartner(null);
        }
        
        return partner;
    }

    @WebMethod(operationName = "retrievePartnerByEmail")
    public Partner retrievePartnerByEmail(@WebParam(name = "email") String email) throws PartnerNotFoundException {
        return partnerSessionBeanLocal.retrievePartnerByEmail(email);
    }

    @WebMethod(operationName = "createNewPartner")
    public Long createNewPartner(@WebParam(name = "newPartner") Partner newPartner) throws PartnerEmailExistException, UnknownPersistenceException {
        return partnerSessionBeanLocal.createNewPartner(newPartner);
    }

    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        return partnerSessionBeanLocal.retrieveAllPartners();
    
    }

}
