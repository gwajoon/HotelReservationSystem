/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNameExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author seanang
 */
@Local
public interface RoomTypeSessionBeanLocal {
    
    public Long createNewRoomType(RoomType newRoomType) throws RoomTypeNameExistsException, UnknownPersistenceException;

    public List<RoomType> viewAllRoomTypes();

    public void updateRoomType(RoomType roomType) throws UpdateRoomTypeException, RoomTypeNotFoundException;

    public void deleteRoomType(Long roomTypeId) throws DeleteRoomTypeException;
    
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException;   
    
    public RoomType retrieveRoomTypeByName(String roomTypeName);
}
