package de.rosenau.simon.network.exception;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 10.06.2019
 * Time: 19:46
 */

public class SendException extends RuntimeException {

    public SendException(String message) {
        super(message);
    }

}
