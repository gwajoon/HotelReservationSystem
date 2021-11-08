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
public class RoomTypeNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeNameExistsException</code>
     * without detail message.
     */
    public RoomTypeNameExistsException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeNameExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeNameExistsException(String msg) {
        super(msg);
    }
}
