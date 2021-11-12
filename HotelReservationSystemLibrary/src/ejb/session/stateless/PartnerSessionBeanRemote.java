/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerEmailExistException;
import util.exception.PartnerNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author GuoJun
 */
@Remote
public interface PartnerSessionBeanRemote {

    public Long createNewPartner(Partner newPartner) throws PartnerEmailExistException, UnknownPersistenceException, InputDataValidationException;

    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException;

    public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException;

    public List<Partner> retrieveAllPartners();
}
