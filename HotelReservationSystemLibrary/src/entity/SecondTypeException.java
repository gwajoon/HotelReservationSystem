/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author seanang
 */
@Entity
public class SecondTypeException implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long secondTypeExceptionId;
     @Column(nullable = false)
    private String message;

    public SecondTypeException(String message) {
        this.message = message;
    }       

    public Long getSecondTypeExceptionId() {
        return secondTypeExceptionId;
    }

    public void setSecondTypeExceptionId(Long secondTypeExceptionId) {
        this.secondTypeExceptionId = secondTypeExceptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (secondTypeExceptionId != null ? secondTypeExceptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the secondTypeExceptionId fields are not set
        if (!(object instanceof SecondTypeException)) {
            return false;
        }
        SecondTypeException other = (SecondTypeException) object;
        if ((this.secondTypeExceptionId == null && other.secondTypeExceptionId != null) || (this.secondTypeExceptionId != null && !this.secondTypeExceptionId.equals(other.secondTypeExceptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SecondTypeException[ id=" + secondTypeExceptionId + " ]";
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
}
