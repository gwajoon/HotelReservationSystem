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
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNameExistsException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Local
public interface RoomRateSessionBeanLocal {
   
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException;
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException;
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;
    public List<RoomRate> viewAllRoomRates();
    public Long createNewRoomRate(RoomRate roomRate, Long roomTypeId) throws RoomRateNameExistsException, UnknownPersistenceException, InputDataValidationException;
}
