/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author seanang
 */
@Stateless
public class RoomInventorySessionBean implements RoomInventorySessionBeanRemote, RoomInventorySessionBeanLocal {

     @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // do a check on roomtype
    public List<RoomType> getAvailableRoomTypes(Date checkInDate, Date checkOutDate, Integer numOfRooms) {
        Query query = em.createQuery("SELECT r FROM RoomType r");
        List<RoomType> allRoomTypes = query.getResultList();
        List<RoomType> availableRoomTypes = new ArrayList<RoomType>();
        
        for(RoomType roomType: allRoomTypes){
            roomType.getRoomRates().size();
            roomType.getRooms().size();
        }
        
        for(int i = 0; i < allRoomTypes.size(); i++){
            RoomType roomType = allRoomTypes.get(i);
            Query roomQuery = em.createQuery("SELECT r FROM Room r WHERE r.roomType = ?1 AND r.roomStatus = TRUE ");
            roomQuery.setParameter(1, roomType);
            
            Query reservationQuery = em.createQuery("SELECT r FROM Reservation r WHERE r.roomType = ?1"
                    + " AND ( (?2 >= r.checkInDate AND ?3 < r.checkOutDate)"
                    + "OR (?4 > r.checkInDate AND ?5 <= r.checkOutDate))");
            
            reservationQuery.setParameter(1, roomType);
            reservationQuery.setParameter(2, checkInDate);
            reservationQuery.setParameter(3, checkInDate);
            reservationQuery.setParameter(4, checkOutDate);
            reservationQuery.setParameter(5, checkOutDate);
            
            Integer takenRooms = 0;
            for(Reservation reservation : (List<Reservation>) reservationQuery.getResultList()){
                takenRooms += reservation.getNumberOfRooms();
            }
            
            Integer availability = roomQuery.getResultList().size() - takenRooms;
            if(availability >= numOfRooms){
                availableRoomTypes.add(roomType);
            }
            
        }
        return availableRoomTypes;
    }
}
