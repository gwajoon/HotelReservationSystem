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
            em.persist(roomType);

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

    @Override
    public void updateRoom(Room room) throws RoomNotFoundException {

        if (room != null && room.getId() != null) {
            Room roomToUpdate = em.find(Room.class, room.getId());
            roomToUpdate.setRoomNumber(room.getRoomNumber());
            roomToUpdate.setRoomType(room.getRoomType());
            roomToUpdate.setRoomStatus(room.getRoomStatus());
        } else {
            throw new RoomNotFoundException("Room ID not valid");
        }
    }

    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException {

        Room roomToRemove = em.find(Room.class, roomId);

        if (roomToRemove.getReservations().isEmpty()) {
            em.remove(roomToRemove);
        } else {
            throw new DeleteRoomException("Room ID " + roomId + " is associated with existing reservations and cannot be deleted!");
        }

    }

    @Override
    public List<Room> viewAllRooms() {

        Query query = em.createQuery("Select r FROM Room r");

        return query.getResultList();

    }

    @Override
    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, roomId);

        if (room != null) {
            return room;
        } else {
            throw new RoomNotFoundException("Room ID " + roomId + " does not exist!");
        }
    }

}
