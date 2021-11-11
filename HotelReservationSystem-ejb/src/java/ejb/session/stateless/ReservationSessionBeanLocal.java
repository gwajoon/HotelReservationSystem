/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.PartnerNotFoundException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Local
public interface ReservationSessionBeanLocal {

    public Long createNewWalkInReservation(Reservation reservation, Long roomTypeId, String firstName, String lastName, String email) throws UnknownPersistenceException;

    public Double calculatePrice(Date checkInDate, Date checkOutDate, Long roomTypeId, String reservationType, Integer numOfRooms);

    public Reservation viewReservation(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> viewAllReservations(Long registeredGuestId) throws RegisteredGuestNotFoundException;

    public List<RoomRate> getRoomRates(Date checkInDate, Date checkOutDate, Long roomTypeId, String reservationType);

    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId, Long guestId) throws UnknownPersistenceException;
    
    public List<Reservation> viewAllPartnerReservations(Long partnerId) throws PartnerNotFoundException;
}
