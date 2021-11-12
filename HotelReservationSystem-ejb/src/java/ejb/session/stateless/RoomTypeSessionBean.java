/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author seanang
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public Long createNewRoomType(RoomType newRoomType, Integer nextHigher) throws RoomTypeNameExistsException, UnknownPersistenceException {
        
        List<RoomType> roomTypes = this.viewAllRoomTypes();
        
        for(RoomType roomType: roomTypes){
            int priority = roomType.getPriority();
            if(priority >= nextHigher){
                roomType.setPriority(priority + 1);
            }
        }
        
        try {
            em.persist(newRoomType);
            em.flush();

            return newRoomType.getId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomTypeNameExistsException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    public List<RoomType> viewAllRoomTypes() {

        Query query = em.createQuery("Select r FROM RoomType r");

        return query.getResultList();

    }
    
    public void updateRoomType(RoomType roomType) throws UpdateRoomTypeException, RoomTypeNotFoundException {

        if (roomType != null && roomType.getId() != null) {
            RoomType roomTypeToUpdate = em.find(RoomType.class, roomType.getId());

            if (roomTypeToUpdate.getName().equals(roomType.getName())) {
                roomTypeToUpdate.setName(roomType.getName());
                roomTypeToUpdate.setDescription(roomType.getDescription());
                roomTypeToUpdate.setSize(roomType.getSize());
                roomTypeToUpdate.setBed(roomType.getBed());
                roomTypeToUpdate.setCapacity(roomType.getCapacity());
                roomTypeToUpdate.setAmenities(roomType.getAmenities());

            } else {
                throw new UpdateRoomTypeException("Name of Room Type record to be updated does not match the existing record");
            }
        } else {
            throw new RoomTypeNotFoundException("Room Type ID not provided for Room Type to be updated");
        }
    }

    public void deleteRoomType(Long roomTypeId) throws DeleteRoomTypeException {

        RoomType roomTypeToRemove = em.find(RoomType.class, roomTypeId);

        Query query1 = em.createQuery("Select a FROM Room a ");
        List<Room> rooms = query1.getResultList();
        Query query2 = em.createQuery("Select a FROM RoomRate a ");
        List<RoomRate> roomRates = query2.getResultList();

        for (Room room : rooms) {
            if (room.getRoomType().getId() == roomTypeId) {
                throw new DeleteRoomTypeException("Room Type " + roomTypeId + " is associated with one or more Hotel rooms");
            }
        }
        
        em.remove(roomTypeToRemove);

    }
    
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        
        if (roomType != null) {
            return roomType;
        }
        else {
            throw new RoomTypeNotFoundException("Room Type ID " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByName(String roomTypeName) {
        Query query = em.createQuery("SELECT rt FROM RoomType WHERE rt.name = :inRoomTypeName");
        query.setParameter("inRoomTypeName", roomTypeName);
        return (RoomType) query.getSingleResult();
    }

}
