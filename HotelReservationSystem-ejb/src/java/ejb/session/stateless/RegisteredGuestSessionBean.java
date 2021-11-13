/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RegisteredGuest;
import entity.Reservation;
import java.util.Set;
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
import util.exception.EmployeeEmailExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RegisteredGuestEmailExistException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Stateless
public class RegisteredGuestSessionBean implements RegisteredGuestSessionBeanRemote, RegisteredGuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RegisteredGuestSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewRegisteredGuest(RegisteredGuest registeredGuest) throws RegisteredGuestEmailExistException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<RegisteredGuest>> constraintViolations = validator.validate(registeredGuest);

        if (constraintViolations.isEmpty()) {

            try {
                em.persist(registeredGuest);
                em.flush();

                return registeredGuest.getId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RegisteredGuestEmailExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public RegisteredGuest registeredGuestLogin(String registeredGuestEmail, String registeredGuestPassword) throws InvalidLoginCredentialException {
        try {
            RegisteredGuest registeredGuest = retrieveRegisteredGuestByEmail(registeredGuestEmail);

            if (registeredGuest.getPassword().equals(registeredGuestPassword)) {
                return registeredGuest;
            } else {
                throw new InvalidLoginCredentialException("Invalid email or password.");
            }
        } catch (RegisteredGuestNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid email or password.");
        }
    }

    @Override
    public RegisteredGuest retrieveRegisteredGuestByEmail(String registeredGuestEmail) throws RegisteredGuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", registeredGuestEmail);

        try {
            return (RegisteredGuest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RegisteredGuestNotFoundException("Registered guest email " + registeredGuestEmail + " does not exist!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RegisteredGuest>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
