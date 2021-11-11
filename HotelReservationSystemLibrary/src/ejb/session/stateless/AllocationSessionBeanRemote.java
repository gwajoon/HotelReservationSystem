/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.Date;
import javax.ejb.Remote;

/**
 *
 * @author seanang
 */
@Remote
public interface AllocationSessionBeanRemote {

    public void allocateRoomToCurrentDayReservations(Date date);

    public void allocateCurrentDay(Long reservationId, Date date);
    
}
