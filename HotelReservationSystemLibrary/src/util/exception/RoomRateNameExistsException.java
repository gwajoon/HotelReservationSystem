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
public class RoomRateNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeNameExistsException</code>
     * without detail message.
     */
    public RoomRateNameExistsException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeNameExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomRateNameExistsException(String msg) {
        super(msg);
    }
}
