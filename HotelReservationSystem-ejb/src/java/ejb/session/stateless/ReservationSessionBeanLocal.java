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
import javax.ejb.Local;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Local
public interface ReservationSessionBeanLocal {

    public Double calculatePrice(LocalDateTime checkInDate, LocalDateTime checkOutDate, RoomType roomType, String reservationType);

    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId, Long guestId) throws UnknownPersistenceException;
}
