/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RegisteredGuest;
import javax.ejb.Local;
import util.exception.InvalidLoginCredentialException;
import util.exception.RegisteredGuestEmailExistException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Local
public interface RegisteredGuestSessionBeanLocal {


    public Long createNewRegisteredGuest(RegisteredGuest registeredGuest) throws RegisteredGuestEmailExistException, UnknownPersistenceException;

    RegisteredGuest registeredGuestLogin(String registeredGuestEmail, String registeredGuestPassword) throws InvalidLoginCredentialException;

    RegisteredGuest retrieveRegisteredGuestByEmail(String registeredGuestEmail) throws RegisteredGuestNotFoundException;
    
}
