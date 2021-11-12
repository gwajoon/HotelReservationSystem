/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SecondTypeException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author seanang
 */
@Remote
public interface AllocationSessionBeanRemote {

    public void allocateRoomToCurrentDayReservations(Date date);

    public void allocateCurrentDay(Long reservationId, Date date);

    public List<SecondTypeException> viewAllocationExceptionReport();
    
}
