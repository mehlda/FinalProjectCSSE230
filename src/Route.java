import java.util.LinkedList;

/**
 * LinkedList implementation to store a series of destinations that represent a
 * route a user can travel
 * 
 * @author derrowap
 *
 */
public class Route extends LinkedList<Destination> {
	/* costs include heuristic values */
	public int timeCost;
	public int distanceCost;

	/**
	 * Constructs a new Route object
	 */
	public Route() {
		// TODO: implement this method
		timeCost = distanceCost = 0;
	}

	/**
	 * Constructs a new Route object with a starting destination.
	 * 
	 * @param destination
	 *            - starting destination of this route
	 */
	public Route(Destination destination) {
		this.add(destination);
		timeCost = distanceCost = 0; // started here, haven't travelled anywhere
	}

	/**
	 * Adds a new destination to the end of the Route.
	 * Updates the time and distance cost to appropriate value based off of the
	 * connection value to the next location.
	 * 
	 * Does not add heuristic value to cost!
	 * 
	 * @param destination
	 *            - Destination to add to the end of this Route
	 * @return true if successfully added
	 */
	public boolean addNextLocation(Destination destination) {
		// TODO: implement this method
		return false;
	}

	/**
	 * Checks the Route to see if the first element is the starting location and
	 * if the last element is the ending location.
	 * If so, it will return true.
	 * This will return false if the route is empty or not complete.
	 * 
	 * @param start
	 *            - name of the starting location
	 * @param end
	 *            - name of the ending location
	 * @return true if this is a valid route
	 */
	public boolean isCompleteRoute(String start, String end) {
		if (this.isEmpty())
			return false;
		return this.getFirst().name.equals(start)
				&& this.getLast().name.equals(end);
	}
}
