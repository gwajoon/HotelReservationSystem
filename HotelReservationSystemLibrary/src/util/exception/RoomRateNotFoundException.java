/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author seanang
 */
public class RoomRateNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeNotFoundException</code> without
     * detail message.
     */
    public RoomRateNotFoundException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomRateNotFoundException(String msg) {
        super(msg);
    }
}
