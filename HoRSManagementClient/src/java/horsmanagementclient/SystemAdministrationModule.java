/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeType;
import util.exception.EmployeeEmailExistException;
import util.exception.PartnerEmailExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
public class SystemAdministrationModule {
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private Employee currentEmployee;

    public SystemAdministrationModule() {
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
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
                    viewAllEmployees();
                }
                else if(response == 3)
                {
                    doCreateNewPartner();
                }
                else if(response == 4)
                {
                    viewAllPartners();
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
        
        System.out.print("Enter last name > ");
        String lastName = scanner.nextLine().trim();

        
        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        
        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        
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
    
    public void viewAllEmployees()
    {
        List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();
        
        
        for (Employee e : employees)
        {
            System.out.printf("Employee id: %s, first name: %s, last name: %s, email: %s, employee type: %s\n", e.getId(), e.getFirstName(), e.getLastName(), e.getEmail(), e.getEmployeeType());
        }
    }
    
    public void doCreateNewPartner()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Reservation System :: System Administration :: Create New Partner ***\n");
        
        System.out.print("Enter first name > ");
        String firstName = scanner.nextLine().trim();
        
        System.out.print("Enter last name > ");
        String lastName = scanner.nextLine().trim();

        
        System.out.print("Enter email  > ");
        String email = scanner.nextLine().trim();

        
        System.out.print("Enter password > ");
        String password = scanner.nextLine().trim();

        Partner newPartner = new Partner(firstName, lastName, email, password);
        System.out.println(newPartner);
        
        try
        {
            Long newPartnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
            System.out.println("New partner created successfully!: " + newPartnerId + "\n");
        }
        catch (PartnerEmailExistException ex)
        {
            System.out.println("An error has occurred while creating the new partner!: The user name already exist\n");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new partner!: " + ex.getMessage() + "\n");
        }
    }

    public void viewAllPartners()
    {
        List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();
        
        
        for (Partner p : partners)
        {
            System.out.printf("Partner id: %s, first name: %s, last name: %s, email: %s\n", p.getPartnerId(), p.getFirstName(), p.getLastName(), p.getEmail());
        }
    }
}
