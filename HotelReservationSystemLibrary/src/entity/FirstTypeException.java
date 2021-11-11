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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author seanang
 */
@Entity
public class FirstTypeException extends SecondTypeException implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType newRoomType;

    public FirstTypeException() {
    }
    

    public FirstTypeException(RoomType oldRoomType, RoomType newRoomType, Reservation reservation) {
        super(oldRoomType, reservation);
        this.newRoomType = newRoomType;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FirstTypeException)) {
            return false;
        }
        FirstTypeException other = (FirstTypeException) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "1 " + this.oldRoomType.getName() + " has been upgraded to " + this.newRoomType.getName();
    }



    /**
     * @return the oldRoomType
     */
    public RoomType getOldRoomType() {
        return oldRoomType;
    }

    /**
     * @param oldRoomType the oldRoomType to set
     */
    public void setOldRoomType(RoomType oldRoomType) {
        this.oldRoomType = oldRoomType;
    }

    /**
     * @return the newRoomType
     */
    public RoomType getNewRoomType() {
        return newRoomType;
    }

    /**
     * @param newRoomType the newRoomType to set
     */
    public void setNewRoomType(RoomType newRoomType) {
        this.newRoomType = newRoomType;
    }

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
}
