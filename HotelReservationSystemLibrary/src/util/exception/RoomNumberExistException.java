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
public class RoomNumberExistException extends Exception {

    /**
     * Creates a new instance of <code>RoomNumberExistException</code> without
     * detail message.
     */
    public RoomNumberExistException() {
    }

    /**
     * Constructs an instance of <code>RoomNumberExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomNumberExistException(String msg) {
        super(msg);
    }
}
