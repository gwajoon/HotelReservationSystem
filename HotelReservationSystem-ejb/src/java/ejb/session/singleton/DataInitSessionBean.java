/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;


import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
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

/**
 *
 * @author GuoJun
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void postConstruct() {
        try {
            employeeSessionBeanLocal.retrieveEmployeeByEmail("admin");
            employeeSessionBeanLocal.retrieveEmployeeByEmail("operation");
        } catch (EmployeeNotFoundException ex) {
            initialiseData();
        }

    }

    public void initialiseData() {
        try {
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "admin", "password", EmployeeType.SYSTEM_ADMIN));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "operation", "password", EmployeeType.OPERATION_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "sales", "password", EmployeeType.SALES_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Default", "Admin", "relation", "password", EmployeeType.GUEST_RELATION));

            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Deluxe Room", "deluxe room", "Small", "Queen", "2", "drinks", 1));
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Premier Room", "premier room", "Small", "Queen", "2", "drinks", 2));
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Family Room", "family room", "Small", "Queen", "2", "drinks", 3));
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Junior Suite", "junior room", "Small", "Queen", "2", "drinks", 4));
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Grand Suite", "grand room", "Small", "Queen", "2", "drinks", 5));

            RoomType rt1 = em.find(RoomType.class, 1l);
            RoomType rt2 = em.find(RoomType.class, 2l);
            RoomType rt3 = em.find(RoomType.class, 3l);
            RoomType rt4 = em.find(RoomType.class, 4l);
            RoomType rt5 = em.find(RoomType.class, 5l);

            Room room1 = new Room(2100l);
            Room room2 = new Room(2101l);
            Room room3 = new Room(2102l);
            Room room4 = new Room(2103l);
            Room room5 = new Room(2104l);

            roomSessionBeanLocal.createNewRoom(room1, 1l);
            roomSessionBeanLocal.createNewRoom(room2, 2l);
            roomSessionBeanLocal.createNewRoom(room3, 3l);
            roomSessionBeanLocal.createNewRoom(room4, 4l);
            roomSessionBeanLocal.createNewRoom(room5, 5l);

        } catch (EmployeeEmailExistException | UnknownPersistenceException | RoomTypeNameExistsException | RoomNumberExistException ex) {
            ex.printStackTrace();
        }

    }

    public void persist(Object object) {
        em.persist(object);
    }
}
