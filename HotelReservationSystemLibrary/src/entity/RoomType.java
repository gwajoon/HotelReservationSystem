/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author GuoJun
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 32, nullable = false, unique = true)
    private String name;
    @Column(length = 32, nullable = false)
    private String description;
    @Column(length = 32, nullable = false)
    private String size;
    @Column(length = 32, nullable = false)
    private String bed;
    @Column(length = 32, nullable = false)
    private String capacity;
    @Column(length = 32, nullable = false)
    private String amenities;
    @Column(nullable = false)
    private Integer priority;
    
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    public RoomType() {
        this.roomRates = new ArrayList<RoomRate>();
        this.rooms = new ArrayList<Room>();
    }

    public RoomType(String name, String description, String size, String bed, String capacity, String amenities, Integer priority) {
        this();
        this.name = name;
        this.description = description;
        this.size = size;
        this.bed = bed;
        this.capacity = capacity;
        this.amenities = amenities;
        this.priority = priority;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBed() {
        return this.bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return this.amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<RoomRate> getRoomRates() {
        return this.roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRate) {
        this.roomRates = roomRate;
    }

    public List<Room> getRooms() {
        return this.rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
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
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + id + " ]";
    }

    /**
     * @return the order
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the order to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
}
