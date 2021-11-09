/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
    
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //@Schedule(dayOfWeek = "*", hour = "2", info = "allocationTimer")
    public void allocationTimer() {
//        LocalDateTime date = LocalDateTime.now();
//        date.plusHours(10);
        Date date = new Date();
        allocateRoomToCurrentDayReservations(date);

    }

    public void allocateRoomToCurrentDayReservations(Date currentDate) {

        LocalDateTime date = new java.sql.Timestamp(
                currentDate.getTime()).toLocalDateTime();
        
        Integer hour = date.getHour();
        date.plusHours(new Long(2- hour));

        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkInDate = ?1");
        query.setParameter(1, date);
        List<Reservation> list = query.getResultList();

        for (Reservation reservation : list) {
            RoomType roomType = reservation.getRoomType();
            Integer rank = roomType.getPriority();
            List<Room> availableRooms = getAvailableRooms(roomType, date);
            Integer numOfRooms = reservation.getNumberOfRooms();

            while (numOfRooms != 0 || rank != 6) {
                if (!availableRooms.isEmpty()) {
                    allocateRoomToReservation(availableRooms.get(0), reservation);
                    availableRooms.remove(0);
                    numOfRooms--;

                } else {
                    rank++;
                    query = em.createQuery("SELECT r FROM RoomType r WHERE r.order = ?1");
                    query.setParameter(1, rank);
                    roomType = (RoomType) query.getSingleResult();
                    availableRooms = getAvailableRooms(roomType, date);
                }

            }

            if (numOfRooms > 0) {
//                generateSecondTypeException
            } else if (rank != roomType.getPriority()) {
//                generateFirstTypeException
            }

        }

    }

    public void allocateRoomToReservation(Room room, Reservation reservation) {

        reservation.getRooms().add(room);
        room.getReservations().add(reservation);

    }

    public List<Room> getAvailableRooms(RoomType roomType, LocalDateTime date) {
        Query roomQuery = em.createQuery("SELECT r FROM Room r WHERE r.roomType = ?1");
        roomQuery.setParameter(1, roomType);
        List<Room> rooms = roomQuery.getResultList();

        List<Room> availableRooms = new ArrayList<Room>();

        for (Room room : rooms) {
            
            LocalDateTime checkOutDate = new java.sql.Timestamp(
                room.getReservations().get(room.getReservations().size() - 1).getCheckOutDate().getTime()).toLocalDateTime();
            
            if (checkOutDate.isBefore(date)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

}
