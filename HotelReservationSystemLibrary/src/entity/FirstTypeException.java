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
public class FirstTypeException implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long firstTypeExceptionId;
    @Column(nullable = false)
    private String message;

    public FirstTypeException() {
    }

    public FirstTypeException(String message) {
        this.message = message;
    }
    
    

    public Long getFirstTypeExceptionId() {
        return firstTypeExceptionId;
    }

    public void setFirstTypeExceptionId(Long firstTypeExceptionId) {
        this.firstTypeExceptionId = firstTypeExceptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (firstTypeExceptionId != null ? firstTypeExceptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the firstTypeExceptionId fields are not set
        if (!(object instanceof FirstTypeException)) {
            return false;
        }
        FirstTypeException other = (FirstTypeException) object;
        if ((this.firstTypeExceptionId == null && other.firstTypeExceptionId != null) || (this.firstTypeExceptionId != null && !this.firstTypeExceptionId.equals(other.firstTypeExceptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FirstTypeException[ id=" + firstTypeExceptionId + " ]";
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
