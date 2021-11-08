/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeEmailExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal 
{

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    

    public EmployeeSessionBean() {
    }

    @Override
    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException 
    {
        try 
        {
           Employee employee = retrieveEmployeeByEmail(email);
           
           if (employee.getPassword().equals(password)) 
           {
               return employee;
           }
           else 
           {
               throw new InvalidLoginCredentialException("Invalid email or password.");
           }
        }
        catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid email or password.");
        }
    }

    @Override
    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException 
    {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (Employee)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new EmployeeNotFoundException("Employee email " + email + " does not exist!");
        }
    }

    @Override
    public Long createNewEmployee(Employee newEmployee) throws EmployeeEmailExistException, UnknownPersistenceException 
    {
        try {
            em.persist(newEmployee);
            em.flush();

            return newEmployee.getId();
        }
        
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new EmployeeEmailExistException();
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
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        return query.getResultList();
    }

    
}
