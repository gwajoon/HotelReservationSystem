/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.PartnerNotFoundException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Remote
public interface ReservationSessionBeanRemote {

    public List<Reservation> viewAllReservations(Long registeredGuestId) throws RegisteredGuestNotFoundException;

    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId, Long guestId) throws UnknownPersistenceException;

    public Reservation viewReservation(Long reservationId) throws ReservationNotFoundException;

    public Double calculatePrice(Date checkInDate, Date checkOutDate, Long roomTypeId, String reservationType, Integer numOfRooms);

    public Long createNewWalkInReservation(Reservation reservation, Long roomTypeId, String firstName, String lastName, String email) throws UnknownPersistenceException;
    
    public List<Reservation> viewAllPartnerReservations(Long partnerId) throws PartnerNotFoundException;

}
