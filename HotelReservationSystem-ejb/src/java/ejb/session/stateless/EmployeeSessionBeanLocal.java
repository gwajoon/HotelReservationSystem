/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeEmailExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Local
public interface EmployeeSessionBeanLocal {

    Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException;

    Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException;

    Long createNewEmployee(Employee newEmployee) throws EmployeeEmailExistException, UnknownPersistenceException;

    List<Employee> retrieveAllEmployees();
    
}
