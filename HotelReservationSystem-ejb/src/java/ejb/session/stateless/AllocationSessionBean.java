/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FirstTypeException;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import entity.SecondTypeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author seanang
 */
@Stateless
public class AllocationSessionBean implements AllocationSessionBeanRemote, AllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Schedule(dayOfWeek = "*", hour = "2", info = "allocationTimer")
    public void allocationTimer() {

        Date date = new Date(System.currentTimeMillis() - 3600 * 2000);
        allocateRoomToCurrentDayReservations(date);

    }

    public void allocateRoomToCurrentDayReservations(Date date) {

//        LocalDateTime date = new java.sql.Timestamp(
//                currentDate.getTime()).toLocalDateTime();
//        
//        Integer hour = date.getHour();
//        date.plusHours(new Long(2 - hour));
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkInDate = ?1");
        query.setParameter(1, date);
        List<Reservation> list = query.getResultList();

        Query query1 = em.createQuery("SELECT r From RoomType r");

        

        for (Reservation reservation : list) {
            RoomType roomType = reservation.getRoomType();
            Integer rank = roomType.getPriority();
            Integer higherRank = rank + 1;
            
            List<Room> availableRooms = getAvailableRooms(roomType, date);
            Integer numOfRooms = reservation.getNumberOfRooms();

            while (numOfRooms != 0 || rank == higherRank + 1) {
                if (!availableRooms.isEmpty()) {
                    Room room = availableRooms.get(0);
                    RoomType allocatedRoomType = room.getRoomType();

                    allocateRoomToReservation(room, reservation);
                    availableRooms.remove(0);
                    numOfRooms--;

                    if (!allocatedRoomType.equals(roomType)) {
                        FirstTypeException firstTypeException = new FirstTypeException(roomType, allocatedRoomType, reservation, room);
                        em.persist(firstTypeException);
                        em.flush();
                        reservation.getAllocationExceptions().add(firstTypeException);
                    }

                } else {
                    Query nextHigherRoomTypeQuery = em.createQuery("SELECT r FROM RoomType r WHERE r.priority = ?1");
                    nextHigherRoomTypeQuery.setParameter(1, higherRank);
                    try {
                        RoomType nextHigherRoomType = (RoomType) nextHigherRoomTypeQuery.getSingleResult();
                        availableRooms = getAvailableRooms(nextHigherRoomType, date);
                        rank++;
                    } catch (NoResultException ex) {
                        break;
                    }

                }
            }

            if (numOfRooms > 0) {
                for (int i = 0; i < numOfRooms; i++) {
                    SecondTypeException secondTypeException = new SecondTypeException(roomType, reservation);
                    em.persist(secondTypeException);
                    em.flush();
                    reservation.getAllocationExceptions().add(secondTypeException);
                }
            }

        }

    }

    public void allocateRoomToReservation(Room room, Reservation reservation) {

        reservation.getRooms().add(room);
        room.getReservations().add(reservation);

    }

    public List<Room> getAvailableRooms(RoomType roomType, Date date) {
        Query roomQuery = em.createQuery("SELECT r FROM Room r WHERE r.roomType = ?1");
        roomQuery.setParameter(1, roomType);
        List<Room> rooms = roomQuery.getResultList();

        List<Room> availableRooms = new ArrayList<Room>();

        for (Room room : rooms) {
            if (room.getReservations().isEmpty()) {
                availableRooms.add(room);
            } else {
                Date checkOutDate = room.getReservations().get(room.getReservations().size() - 1).getCheckOutDate();
                if ((checkOutDate.before(date) || checkOutDate.equals(date)) && room.getRoomStatus()) {
                    availableRooms.add(room);
                }
            }
        }
        return availableRooms;
    }

    public void allocateCurrentDay(Long reservationId, Date date) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        RoomType roomType = reservation.getRoomType();
        Integer rank = roomType.getPriority();
        Integer higherRank = rank + 1;

        List<Room> availableRooms = getAvailableRooms(roomType, date);
        Integer numOfRooms = reservation.getNumberOfRooms();

        while (numOfRooms != 0 || rank == higherRank + 1) {
            if (!availableRooms.isEmpty()) {
                Room room = availableRooms.get(0);
                RoomType allocatedRoomType = room.getRoomType();

                allocateRoomToReservation(room, reservation);
                availableRooms.remove(0);
                numOfRooms--;

                if (!allocatedRoomType.equals(roomType)) {
                    FirstTypeException firstTypeException = new FirstTypeException(roomType, allocatedRoomType, reservation, room);
                    em.persist(firstTypeException);
                    em.flush();
                    reservation.getAllocationExceptions().add(firstTypeException);
                }

            } else {
                Query nextHigherRoomTypeQuery = em.createQuery("SELECT r FROM RoomType r WHERE r.priority = ?1");
                nextHigherRoomTypeQuery.setParameter(1, higherRank);
                try {
                    RoomType nextHigherRoomType = (RoomType) nextHigherRoomTypeQuery.getSingleResult();
                    availableRooms = getAvailableRooms(nextHigherRoomType, date);
                    rank++;
                } catch (NoResultException ex) {
                    break;
                }

            }
        }
    }

}
