/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author seanang
 */
@Local
public interface RoomSessionBeanLocal {

    public List<Room> viewAllRooms();

    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;

    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException;
    
    public Long createNewRoom(Room room, Long roomTypeId) throws RoomNumberExistException, UnknownPersistenceException, InputDataValidationException;

    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;

}
