/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author seanang
 */
@Local
public interface RoomInventorySessionBeanLocal {
    public List<RoomType> getAvailableRoomTypes(Date checkInDate, Date checkOutDate);
}
