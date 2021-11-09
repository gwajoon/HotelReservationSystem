/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RegisteredGuest;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Long createNewWalkInReservation(Reservation reservation, Long roomTypeId) throws UnknownPersistenceException {
        try {
            RoomType roomType = em.find(RoomType.class, roomTypeId);
            reservation.setRoomType(roomType);
            reservation.setReservationType("Walk-In");
            em.persist(reservation);
            em.flush();
            return reservation.getId();

        } catch (PersistenceException ex) {

            throw new UnknownPersistenceException(ex.getMessage());

        }
    }
    
    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId, Long guestId) throws UnknownPersistenceException {
        try {
            RoomType roomType = em.find(RoomType.class, roomTypeId);
            RegisteredGuest guest = em.find(RegisteredGuest.class, guestId);
            
            reservation.setRoomType(roomType);
            reservation.setReservationType("Online");
            em.persist(reservation);
            em.flush();
            guest.getReservations().add(reservation);
            return reservation.getId();

        } catch (PersistenceException ex) {

            throw new UnknownPersistenceException(ex.getMessage());

        }
    }
    
    public Reservation viewReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if(reservation != null)
        {
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Reservation ID " + reservation + " does not exist!");
        }
    }

    public List<Reservation> viewAllReservations(Long registeredGuestId) throws RegisteredGuestNotFoundException {
        RegisteredGuest registeredGuest = em.find(RegisteredGuest.class, registeredGuestId);

        if (registeredGuest != null) {
            return registeredGuest.getReservations();
        } else {
            throw new RegisteredGuestNotFoundException("Registered Guest ID " + registeredGuestId + " does not exist!");
        }
    }

    public BigDecimal calculatePrice(LocalDateTime checkInDate, LocalDateTime checkOutDate, RoomType roomType, String reservationType) {

        List<RoomRate> roomRates;
        BigDecimal price = new BigDecimal(0.0);
;

        if (reservationType.equals("Walk-In")) {
            roomRates = getRoomRates(checkInDate, checkOutDate, roomType, "Walk-In");
        } else {
            roomRates = getRoomRates(checkInDate, checkOutDate, roomType, "Online");
        }

        for (RoomRate roomRate : roomRates) {
            price = price.add(roomRate.getRatePerNight());
        }
        return price;

    }

    public List<RoomRate> getRoomRates(LocalDateTime checkInDate, LocalDateTime checkOutDate, RoomType roomType, String reservationType) {
        List<RoomRate> roomRates = new ArrayList<RoomRate>();

        
        long nightsBetween = Duration.between(checkOutDate, checkInDate).toDays() + 1;
        
        
        if (reservationType.equals("Walk-In")) {
            Query query = em.createQuery("SELECT r FROM RoomRate WHERE r.roomType = ?1 AND r.rateType = ?2");
            query.setParameter(1, roomType);
            query.setParameter(2, "Walk-In");
            RoomRate roomRate = (RoomRate) query.getSingleResult();
            for (int i = 0; i < nightsBetween; i++) {
                roomRates.add(roomRate);
            }
        } else {
            for (LocalDateTime date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                RoomRate roomRateOnline;
                RoomRate roomRatePromo;
                RoomRate roomRatePeak;
                Date newDate = java.sql.Timestamp.valueOf(date);
                
                Query query = em.createQuery("SELECT r FROM RoomRate WHERE r.roomType = ?1 AND r.rateType = ?2");
                query.setParameter(1, roomType);
                query.setParameter(2, "Online");
                roomRateOnline = (RoomRate) query.getSingleResult();
                
                Query query1 = em.createQuery("SELECT r FROM RoomRate WHERE r.roomType = ?1 "
                        + "AND r.rateType = ?2 AND ?3 BETWEEN r.startDate AND r.endDate");
                query1.setParameter(1, roomType);
                query1.setParameter(2, "Promo");
                query1.setParameter(3, newDate);
                try{
                    roomRatePromo = (RoomRate) query.getSingleResult();
                } catch(NoResultException | NonUniqueResultException ex){
                    roomRatePromo = null;
                }
                Query query2 = em.createQuery("SELECT r FROM RoomRate WHERE r.roomType = ?1 "
                        + "AND r.rateType = ?2 AND ?3 BETWEEN r.startDate AND r.endDate");
                query2.setParameter(1, roomType);
                query2.setParameter(2, "Peak");
                query2.setParameter(3, newDate);
                try{
                    roomRatePeak = (RoomRate) query.getSingleResult();
                } catch(NoResultException | NonUniqueResultException ex){
                    roomRatePeak = null;
                }
                if(roomRatePromo == null && roomRatePeak == null){
                    roomRates.add(roomRateOnline);
                } else if(roomRatePromo != null){
                    roomRates.add(roomRatePromo);
                } else{
                    roomRates.add(roomRatePeak);
                }
                
            }
        }
        return roomRates;
    }
}
