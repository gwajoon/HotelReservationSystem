/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomType;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeType;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeEmailExistException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNameExistsException;
import util.exception.UnknownPersistenceException;
import ejb.session.stateless.RoomSessionBeanLocal;
import entity.Partner;
import entity.RoomRate;
import util.enumeration.RateType;
import util.exception.PartnerEmailExistException;
import util.exception.RoomRateNameExistsException;

/**
 *
 * @author GuoJun
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void postConstruct() {
        try {
            employeeSessionBeanLocal.retrieveEmployeeByUsername("sysadmin");
            employeeSessionBeanLocal.retrieveEmployeeByUsername("opmanager");
        } catch (EmployeeNotFoundException ex) {
            initialiseData();
        }

    }

    public void initialiseData() {
        try {
            partnerSessionBeanLocal.createNewPartner(new Partner("Hotel", "Trivago", "partner", "password"));
            
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "sysadmin", "password", EmployeeType.SYSTEM_ADMIN));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "opmanager", "password", EmployeeType.OPERATION_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "salesmanager", "password", EmployeeType.SALES_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "guestrelo", "password", EmployeeType.GUEST_RELATION));

            RoomType roomType = new RoomType("Deluxe Room", "deluxe room", "Small", "Queen", "2", "drinks", 1);
            em.persist(roomType);
            em.flush();
            Long deluxeId = roomType.getId();
            roomType = new RoomType("Premier Room", "premier room", "Small", "Queen", "2", "drinks", 2);
            em.persist(roomType);
            em.flush();
            Long premierId = roomType.getId();
            roomType = new RoomType("Family Room", "family room", "Small", "Queen", "2", "drinks", 3);
            em.persist(roomType);
            em.flush();
            Long familyId = roomType.getId();
            roomType = new RoomType("Junior Suite", "junior room", "Small", "Queen", "2", "drinks", 4);
            em.persist(roomType);
            em.flush();
            Long juniorId = roomType.getId();
            roomType = new RoomType("Grand Suite", "grand room", "Small", "Queen", "2", "drinks", 5);
            em.persist(roomType);
            em.flush();
            Long grandId = roomType.getId();

            RoomRate deluxeRoomRate1 = new RoomRate("Deluxe Room Published", RateType.PUBLISHED, 100.0);
            RoomRate deluxeRoomRate2 = new RoomRate("Deluxe Room Normal", RateType.NORMAL, 50.0);
            RoomRate premierRoomRate1 = new RoomRate("Premier Room Published", RateType.PUBLISHED, 200.0);
            RoomRate premierRoomRate2 = new RoomRate("Premier Room Normal", RateType.NORMAL, 100.0);
            RoomRate familyRoomRate1 = new RoomRate("Family Room Published", RateType.PUBLISHED, 300.0);
            RoomRate familyRoomRate2 = new RoomRate("Family Room Normal", RateType.NORMAL, 150.0);
            RoomRate juniorSuiteRate1 = new RoomRate("Junior Suite Published", RateType.PUBLISHED, 400.0);
            RoomRate juniorSuiteRate2 = new RoomRate("Junior Suite Normal", RateType.NORMAL, 200.0);
            RoomRate grandSuiteRate1 = new RoomRate("Grand Suite Published", RateType.PUBLISHED, 500.0);
            RoomRate grandSuiteRate2 = new RoomRate("Grand Suite Normal", RateType.NORMAL, 250.0);
            
            try {
                roomRateSessionBeanLocal.createNewRoomRate(deluxeRoomRate1, deluxeId);
                roomRateSessionBeanLocal.createNewRoomRate(deluxeRoomRate2, deluxeId);
                roomRateSessionBeanLocal.createNewRoomRate(premierRoomRate1, premierId);
                roomRateSessionBeanLocal.createNewRoomRate(premierRoomRate2, premierId);
                roomRateSessionBeanLocal.createNewRoomRate(familyRoomRate1, familyId);
                roomRateSessionBeanLocal.createNewRoomRate(familyRoomRate2, familyId);
                roomRateSessionBeanLocal.createNewRoomRate(juniorSuiteRate1, juniorId);
                roomRateSessionBeanLocal.createNewRoomRate(juniorSuiteRate2, juniorId);
                roomRateSessionBeanLocal.createNewRoomRate(grandSuiteRate1, grandId);
                roomRateSessionBeanLocal.createNewRoomRate(grandSuiteRate2, grandId);
                
            } catch (RoomRateNameExistsException ex) {
                System.out.print("Room Rate Name already exists");
            } catch(UnknownPersistenceException ex){
                System.out.print(ex.getMessage());
            }

            Room deluxeRoom1 = new Room("0101");
            Room deluxeRoom2 = new Room("0201");
            Room deluxeRoom3 = new Room("0301");
            Room deluxeRoom4 = new Room("0401");
            Room deluxeRoom5 = new Room("0501");

            Room premierRoom1 = new Room("0102");
            Room premierRoom2 = new Room("0202");
            Room premierRoom3 = new Room("0302");
            Room premierRoom4 = new Room("0402");
            Room premierRoom5 = new Room("0502");

            Room familyRoom1 = new Room("0103");
            Room familyRoom2 = new Room("0203");
            Room familyRoom3 = new Room("0303");
            Room familyRoom4 = new Room("0403");
            Room familyRoom5 = new Room("0503");

            Room juniorSuite1 = new Room("0104");
            Room juniorSuite2 = new Room("0204");
            Room juniorSuite3 = new Room("0304");
            Room juniorSuite4 = new Room("0404");
            Room juniorSuite5 = new Room("0504");

            Room grandSuite1 = new Room("0105");
            Room grandSuite2 = new Room("0205");
            Room grandSuite3 = new Room("0305");
            Room grandSuite4 = new Room("0405");
            Room grandSuite5 = new Room("0505");

            roomSessionBeanLocal.createNewRoom(deluxeRoom1, deluxeId);
            roomSessionBeanLocal.createNewRoom(deluxeRoom2, deluxeId);
            roomSessionBeanLocal.createNewRoom(deluxeRoom3, deluxeId);
            roomSessionBeanLocal.createNewRoom(deluxeRoom4, deluxeId);
            roomSessionBeanLocal.createNewRoom(deluxeRoom5, deluxeId);

            roomSessionBeanLocal.createNewRoom(premierRoom1, premierId);
            roomSessionBeanLocal.createNewRoom(premierRoom2, premierId);
            roomSessionBeanLocal.createNewRoom(premierRoom3, premierId);
            roomSessionBeanLocal.createNewRoom(premierRoom4, premierId);
            roomSessionBeanLocal.createNewRoom(premierRoom5, premierId);

            roomSessionBeanLocal.createNewRoom(familyRoom1, familyId);
            roomSessionBeanLocal.createNewRoom(familyRoom2, familyId);
            roomSessionBeanLocal.createNewRoom(familyRoom3, familyId);
            roomSessionBeanLocal.createNewRoom(familyRoom4, familyId);
            roomSessionBeanLocal.createNewRoom(familyRoom5, familyId);

            roomSessionBeanLocal.createNewRoom(juniorSuite1, juniorId);
            roomSessionBeanLocal.createNewRoom(juniorSuite2, juniorId);
            roomSessionBeanLocal.createNewRoom(juniorSuite3, juniorId);
            roomSessionBeanLocal.createNewRoom(juniorSuite4, juniorId);
            roomSessionBeanLocal.createNewRoom(juniorSuite5, juniorId);

            roomSessionBeanLocal.createNewRoom(grandSuite1, grandId);
            roomSessionBeanLocal.createNewRoom(grandSuite2, grandId);
            roomSessionBeanLocal.createNewRoom(grandSuite3, grandId);
            roomSessionBeanLocal.createNewRoom(grandSuite4, grandId);
            roomSessionBeanLocal.createNewRoom(grandSuite5, grandId);

        } catch (EmployeeEmailExistException | UnknownPersistenceException | RoomNumberExistException | PartnerEmailExistException ex) {
            ex.printStackTrace();
        }

    }

    
}
