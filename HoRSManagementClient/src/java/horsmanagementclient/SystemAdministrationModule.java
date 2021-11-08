/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.EmployeeType;
import util.exception.EmployeeEmailExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class SystemAdministrationModule {
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private Employee currentEmployee;

    public SystemAdministrationModule() {
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }
    
    
    public void systemAdministrationMenu() {
        Scanner scanner = new Scanner(System.in);
        
        int response = 0;
        
        while (true) 
        {
            System.out.println("*** Hotel Reservation System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Log Out\n");
            
            response = 0;

            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewEmployee();
                }
                else if(response == 2)
                {
                }
                else if(response == 3)
                {
                }
                else if(response == 4)
                {
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if (response == 5)
            {
                break;
            }
        }
    }
    
    public void doCreateNewEmployee() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: System Administration :: Create New Employee ***\n");
        
        System.out.print("Enter first name > ");
        String firstName = scanner.nextLine().trim();
        System.out.println(firstName);
        
        System.out.print("Enter last name > ");
        String lastName = scanner.nextLine().trim();
                System.out.println(lastName);

        
        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();
                System.out.println(email);

        
        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();
                System.out.println(password);

        
        int employeeType = 0;
        
        while (true) {
            System.out.println("Select employee type (1. System Administrator 2. Operation Manager 3. Sales Manager 4. Guest Relation Officer");
            
            System.out.print("> ");
            employeeType = scanner.nextInt();
            
            if(employeeType >= 1 && employeeType <= 4)
            {
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        Employee newEmployee = new Employee(firstName, lastName, email, password, EmployeeType.values()[employeeType-1]);
        System.out.println(newEmployee);
        
        try
        {
            Long newEmployeeId = employeeSessionBeanRemote.createNewEmployee(newEmployee);
            System.out.println("New employee created successfully!: " + newEmployeeId + "\n");
        }
        catch (EmployeeEmailExistException ex)
        {
            System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
        }
    }
}
