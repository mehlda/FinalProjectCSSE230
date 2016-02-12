/**
 * Custom exception class thrown when an object is not found.
 * 
 * @author derrowap
 *
 */
public class ObjectNotFoundException extends Exception {
	/**
	 * Constructs a throwable java exception with a message
	 * 
	 * @param message
	 *            - error message to pass to super class
	 */
	public ObjectNotFoundException(String message) {
		super(message);
	}
}
