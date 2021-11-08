/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
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
    public List<RoomType> getAvailableRoomTypes(Date checkInDate, Date checkOutDate) {
        Query totalRoomTypeQuery = em.createQuery("SELECT r FROM RoomType r WHERE r.checkInDate BETWEEN ?1 AND ?2");
        totalRoomTypeQuery.setParameter(1, checkInDate);
        totalRoomTypeQuery.setParameter(2, checkOutDate);
        List<RoomType> totalRoomTypes = totalRoomTypeQuery.getResultList();
        
        Query reservedRoomTypeQuery = em.createQuery("SELECT r FROM Reservation r WHERE r.checkInDate BETWEEN ?1 AND ?2");
        reservedRoomTypeQuery.setParameter(1, checkInDate);
        reservedRoomTypeQuery.setParameter(2, checkOutDate);
        List<RoomType> reservedRoomTypes = reservedRoomTypeQuery.getResultList();
        
        totalRoomTypes.removeAll(reservedRoomTypes);
        
        return totalRoomTypes;
    }
}
