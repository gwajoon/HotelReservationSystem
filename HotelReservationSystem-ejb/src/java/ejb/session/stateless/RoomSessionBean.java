/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seanang
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    @Override
    public Long createNewRoom(Room room, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

        if (constraintViolations.isEmpty()) {

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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

        if (constraintViolations.isEmpty()) {
            if (room != null && room.getId() != null) {
                Room roomToUpdate = em.find(Room.class, room.getId());
                roomToUpdate.setRoomNumber(room.getRoomNumber());
                roomToUpdate.setRoomType(room.getRoomType());
                roomToUpdate.setRoomStatus(room.getRoomStatus());
            } else {
                throw new RoomNotFoundException("Room ID not valid");
            }
        }
        else 
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
