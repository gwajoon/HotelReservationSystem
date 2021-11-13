/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.RegisteredGuest;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import entity.SecondTypeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.PartnerNotFoundException;
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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewWalkInReservation(Reservation reservation, Long roomTypeId, String firstName, String lastName, String email) throws UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

        if (constraintViolations.isEmpty()) {

            try {
                Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = ?1");
                query.setParameter(1, email);

                Guest guest;

                try {
                    guest = (Guest) query.getSingleResult();
                } catch (NoResultException ex) {
                    guest = new Guest(firstName, lastName, email);
                    em.persist(guest);
                }

                RoomType roomType = em.find(RoomType.class, roomTypeId);
                reservation.setRoomType(roomType);
                reservation.setGuest(guest);
                reservation.setReservationType("Walk-In");
                em.persist(reservation);
                em.flush();
                guest.getReservations().add(reservation);
                return reservation.getId();

            } catch (PersistenceException ex) {

                throw new UnknownPersistenceException(ex.getMessage());

            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public Long createNewOnlineReservation(Reservation reservation, Long roomTypeId, Long guestId) throws UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

        if (constraintViolations.isEmpty()) {

            try {
                RoomType roomType = em.find(RoomType.class, roomTypeId);
                RegisteredGuest guest = em.find(RegisteredGuest.class, guestId);

                reservation.setRoomType(roomType);
                reservation.setGuest(guest);
                reservation.setReservationType("Online");
                em.persist(reservation);
                em.flush();
                guest.getReservations().add(reservation);
                return reservation.getId();

            } catch (PersistenceException ex) {

                throw new UnknownPersistenceException(ex.getMessage());

            }

        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));

        }
    }

    public Long createNewPartnerReservation(Reservation reservation, Long roomTypeId, Long partnerId) throws UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation);

        if (constraintViolations.isEmpty()) {

            try {
                RoomType roomType = em.find(RoomType.class, roomTypeId);
                Partner partner = em.find(Partner.class, partnerId);

                reservation.setRoomType(roomType);
                reservation.setGuest(partner);
                reservation.setReservationType("Online");
                em.persist(reservation);
                partner.getReservations().add(reservation);
                em.flush();
                return reservation.getId();

            } catch (PersistenceException ex) {

                throw new UnknownPersistenceException(ex.getMessage());

            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));

        }
    }

    public Reservation viewReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);

        if (reservation != null) {
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation ID " + reservation + " does not exist!");
        }
    }

    @Override
    public List<Reservation> viewAllReservations(Long registeredGuestId) throws RegisteredGuestNotFoundException {
        Guest guest = em.find(Guest.class, registeredGuestId);

        if (guest != null) {
            guest.getReservations().size();
            return guest.getReservations();
        } else {
            throw new RegisteredGuestNotFoundException("Registered Guest ID " + registeredGuestId + " does not exist!");
        }
    }

    @Override
    public List<Reservation> viewAllPartnerReservations(Long partnerId) throws PartnerNotFoundException {
        Guest guest = em.find(Guest.class, partnerId);

        if (guest != null) {
            guest.getReservations().size();
            return guest.getReservations();
        } else {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist!");
        }
    }

    public List<SecondTypeException> viewAllocationExceptionReport(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException();
        }
        reservation.getAllocationExceptions().size();

        return reservation.getAllocationExceptions();
    }

    public Reservation checkInGuest(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);

        if (reservation == null) {
            throw new ReservationNotFoundException();
        }

        reservation.getRooms().size();
        reservation.getAllocationExceptions().size();

        return reservation;
    }

    @Override
    public Double calculatePrice(Date checkInDate, Date checkOutDate, Long roomTypeId, String reservationType, Integer numOfRooms) {

        List<RoomRate> roomRates;
        Double price = 0.0;

        RoomType roomType = em.find(RoomType.class, roomTypeId);

        if (reservationType.equals("Walk-In")) {
            roomRates = getRoomRates(checkInDate, checkOutDate, roomTypeId, "Walk-In");
        } else {
            roomRates = getRoomRates(checkInDate, checkOutDate, roomTypeId, "Online");
        }

        for (RoomRate roomRate : roomRates) {
            price += roomRate.getRatePerNight();
        }

        return price * numOfRooms;

    }

    public List<RoomRate> getRoomRates(Date checkInDate, Date checkOutDate, Long roomTypeId, String reservationType) {
        List<RoomRate> roomRates = new ArrayList<RoomRate>();

        long diffInMillies = Math.abs(checkOutDate.getTime() - checkInDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        Calendar start = Calendar.getInstance();
        start.setTime(checkInDate);
        Calendar end = Calendar.getInstance();
        end.setTime(checkOutDate);

        if (reservationType.equals("Walk-In")) {
            Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.id = ?1 AND r.rateType = ?2");
            query.setParameter(1, roomTypeId);
            query.setParameter(2, RateType.PUBLISHED);
            RoomRate roomRate = (RoomRate) query.getSingleResult();
            for (int i = 0; i < diff; i++) {
                roomRates.add(roomRate);
            }
        } else {

            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                RoomRate roomRateOnline;
                RoomRate roomRatePromo;
                RoomRate roomRatePeak;
                Date newDate = date;

                Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.id = ?1 AND r.rateType = ?2");
                query.setParameter(1, roomTypeId);
                query.setParameter(2, RateType.NORMAL);
                roomRateOnline = (RoomRate) query.getSingleResult();
                System.out.println(roomRateOnline);

                Query query1 = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.id = ?1 "
                        + "AND r.rateType = ?2 AND ?3 BETWEEN r.startDate AND r.endDate");
                query1.setParameter(1, roomTypeId);
                query1.setParameter(2, RateType.PROMOTION);
                query1.setParameter(3, newDate);
                try {
                    roomRatePromo = (RoomRate) query1.getSingleResult();
                } catch (NoResultException | NonUniqueResultException ex) {
                    roomRatePromo = null;
                }
                Query query2 = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType.id = ?1 "
                        + "AND r.rateType = ?2 AND ?3 BETWEEN r.startDate AND r.endDate");
                query2.setParameter(1, roomTypeId);
                query2.setParameter(2, RateType.PEAK);
                query2.setParameter(3, newDate);
                try {
                    roomRatePeak = (RoomRate) query2.getSingleResult();
                } catch (NoResultException | NonUniqueResultException ex) {
                    roomRatePeak = null;
                }

                if (roomRatePromo == null && roomRatePeak == null) {
                    roomRates.add(roomRateOnline);

                } else if (roomRatePromo != null) {
                    roomRates.add(roomRatePromo);
                } else {
                    roomRates.add(roomRatePeak);
                }

            }
        }
        return roomRates;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
