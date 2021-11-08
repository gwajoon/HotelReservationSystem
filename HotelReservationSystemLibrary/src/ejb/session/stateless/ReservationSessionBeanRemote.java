/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Remote
public interface ReservationSessionBeanRemote {

    public Long createNewWalkInReservation(Reservation reservation, Long roomTypeId) throws UnknownPersistenceException;

    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId) throws UnknownPersistenceException;

    public List<Reservation> viewAllReservations(Long registeredGuestId) throws RegisteredGuestNotFoundException;

    public BigDecimal calculatePrice(Long reservationId);
    
}
