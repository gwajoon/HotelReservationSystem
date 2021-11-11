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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author seanang
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class SecondTypeException implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    protected RoomType oldRoomType; 
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    protected Reservation reservation;

    public SecondTypeException() {
    }
    
    public SecondTypeException(RoomType oldRoomType, Reservation reservation) {
        this.oldRoomType = oldRoomType;
        this.reservation = reservation;
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
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecondTypeException)) {
            return false;
        }
        SecondTypeException other = (SecondTypeException) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
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

    @Override
    public String toString() {
        return "" + getReservation().getId() + ": 1 " +this.getOldRoomType().getName() + " is unavailable for check in and upgrade.";
    }
    
}
