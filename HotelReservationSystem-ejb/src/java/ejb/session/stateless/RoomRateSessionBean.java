/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomRate;
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
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNameExistsException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomRateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws RoomRateNameExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(roomRate);

        if (constraintViolations.isEmpty()) {
            try {
                RoomType roomType = em.find(RoomType.class, roomTypeId);
                roomRate.setRoomType(roomType);
                em.persist(roomRate);
                roomType.getRoomRates().add(roomRate);
                em.persist(roomType);

                em.flush();

                return roomRate.getId();
            }
            catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RoomRateNameExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(roomRate);

        if (constraintViolations.isEmpty()) {

            if (roomRate != null && roomRate.getId() != null) {
                RoomRate roomRateToUpdate = em.find(RoomRate.class, roomRate.getId());
                    roomRateToUpdate.setName(roomRate.getName());
                    roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                    roomRateToUpdate.setStartDate(roomRate.getStartDate());
                    roomRateToUpdate.setEndDate(roomRate.getEndDate());
                }
            else {
                throw new RoomRateNotFoundException("Room Rate ID not provided for Room Rate to be updated.");
            }
        }
        else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }
    
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException {
        RoomRate roomRateToDelete = retrieveRoomRateByRoomRateId(roomRateId);
        
        Query query = em.createQuery("SELECT r from Reservation r");
        List<Reservation> reservations = query.getResultList();
        
        for (Reservation r : reservations) {
            for (RoomRate rr : r.getRoomRates()) {
                if (rr.getId().equals(roomRateId)) {
                    rr.setEnabled(Boolean.FALSE);
                    break;
                }
            }
        }
        
       
    }
    
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        
        
        if (roomRate != null) {
            return roomRate;
        }
        else {
            throw new RoomRateNotFoundException("RoomRate ID " + roomRateId + " does not exist!");
        }
    }

    
    public List<RoomRate> viewAllRoomRates() {
        Query query = em.createQuery("SELECT r FROM RoomRate r");
        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
