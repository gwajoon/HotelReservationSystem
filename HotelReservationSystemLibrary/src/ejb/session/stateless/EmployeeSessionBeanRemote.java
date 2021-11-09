/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeEmailExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Remote
public interface EmployeeSessionBeanRemote {

    

    Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
    Long createNewEmployee(Employee newEmployee) throws EmployeeEmailExistException, UnknownPersistenceException;

    List<Employee> retrieveAllEmployees();

    Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;
}
