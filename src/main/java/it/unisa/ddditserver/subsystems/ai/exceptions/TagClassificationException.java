package it.unisa.ddditserver.subsystems.ai.exceptions;

/**
 * Custom exception used to handle generic classification model errors.
 *
 * This exception should be used when a version classification model error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-29
 */
public class TagClassificationException extends RuntimeException {
    public TagClassificationException(String message) {
        super(message);
    }
}
