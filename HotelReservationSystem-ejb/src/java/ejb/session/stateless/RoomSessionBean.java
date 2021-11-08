/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author seanang
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long createNewRoom(Room room, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException {
        try {
            RoomType roomType = em.find(RoomType.class, roomTypeId);
            room.setRoomType(roomType);
            em.persist(room);
            roomType.getRooms().add(room);
            em.flush();
            return room.getId();

        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomNumberExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    public void updateRoomType(Room room) throws UpdateRoomException, RoomNotFoundException {

        if (room != null && room.getId() != null) {
            Room roomToUpdate = em.find(Room.class, room.getId());

            if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                roomToUpdate.setRoomType(room.getRoomType());
                roomToUpdate.setRoomStatus(room.getRoomStatus());

            } else {
                throw new UpdateRoomException("Room Number record to be updated does not match the existing record");
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for Room to be updated");
        }
    }
    
    public void deleteRoom(Long roomId) throws DeleteRoomException {

        Room roomToRemove = em.find(Room.class, roomId);
        
        if(roomToRemove.getReservations().isEmpty())
        {
            em.remove(roomToRemove);
        } else {
            throw new DeleteRoomException("Room ID " + roomId + " is associated with existing reservations and cannot be deleted!");
        }

    }
    
    public List<Room> viewAllRooms() {

        Query query = em.createQuery("Select r FROM Room r");

        return query.getResultList();

    }


}
