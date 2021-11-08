/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerEmailExistException;
import util.exception.PartnerNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    

    public PartnerSessionBean() {
    }

    @Override
    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException 
    {
        try 
        {
           Partner partner = retrievePartnerByEmail(email);
           
           if (partner.getPassword().equals(password)) 
           {
               return partner;
           }
           else 
           {
               throw new InvalidLoginCredentialException("Invalid email or password.");
           }
        }
        catch (PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid email or password.");
        }
    }

    @Override
    public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException 
    {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (Partner)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new PartnerNotFoundException("Partner email " + email + " does not exist!");
        }
    }

    @Override
    public Long createNewPartner(Partner newPartner) throws PartnerEmailExistException, UnknownPersistenceException 
    {
        try {
            em.persist(newPartner);
            em.flush();

            return newPartner.getPartnerId();
        }
        
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new PartnerEmailExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Partner> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p FROM Partner p");
        return query.getResultList();
    }

    
}
