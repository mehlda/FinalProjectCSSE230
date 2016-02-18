import java.util.LinkedList;

/**
 * LinkedList implementation to store a series of destinations that represent a
 * route a user can travel.
 * 
 * @author derrowap
 *
 */
public class Route extends LinkedList<Destination> {
	/* costs include heuristic values */
	public int timeCost;
	public int distanceCost;
	public int timeHeuristic;
	public int distanceHeuristic;
	public int waypointsReached;

	/**
	 * Constructs a new Route object of null values
	 */
	public Route() {
		this.timeCost = this.distanceCost = this.waypointsReached = this.distanceHeuristic = this.timeHeuristic = 0;
	}

	/**
	 * Constructs a new Route object with a starting destination.
	 * 
	 * @param destination
	 *            - starting destination of this route
	 */
	public Route(Destination destination) {
		this.add(destination);
		this.timeCost = this.distanceCost = this.waypointsReached = this.distanceHeuristic = this.timeHeuristic = 0; 
		// started here, haven't travelled anywhere
	}

	/**
	 * Checks the Route to see if the first element is the starting location and
	 * if the last element is the ending location. If so, it will return true.
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

	/**
	 * Calculates the straight line distance between the last destination in
	 * this Route and the Goal destination. Adds this distance to this distance
	 * cost. Adds this distance (divided by 40) to the time cost of this Route.
	 * Dividing the straight line by 40 calculates the straight line cost of
	 * traveling in timem, assuming average speed is 40 miles per hour.
	 * 
	 * @param end
	 *            - goal destination of this Route
	 */
	public void addHeuristicCost(Destination end) {
		int distance = (int) this.getLast().coordinate
				.straightLineDistance(end.coordinate);
		this.distanceHeuristic += distance;
		// assuming an average of 40 miles per hour.
		this.timeHeuristic += distance / 40;
	}

	/**
	 * Removes the heuristic cost stored within this route's cost fields. Does
	 * the exact opposite of what addHeuristicCost(Destination end) does.
	 * 
	 * @param end
	 *            - goal destination of this Route
	 */
	public void removeHeuristicCost(Destination end) {
		int distance = (int) this.getLast().coordinate
				.straightLineDistance(end.coordinate);
		this.distanceHeuristic -= distance;
		// assuming an average of 40 miles per hour.
		this.timeHeuristic -= distance / 40;
	}

	/**
	 * Compares this Route to the given Route by using their distance cost.
	 * Returns a negative integer, a zero, or a positive integer as the Route's
	 * distance cost is less than, equal to, or greater than the specified
	 * Route's distance cost.
	 * 
	 * @param route
	 *            - Route to compare distance costs with this Route
	 * @return a negative integer, zero, or a positive integer as this Route's
	 *         distance cost is less than, equal to, or greater than the
	 *         specified Route's distance cost.
	 */
	public int compareToDistance(Route route) {
		if (route == null)
			throw new NullPointerException(
					"Route to compare distance with does not exist!");
		if (this.distanceCost + this.distanceHeuristic == route.distanceCost + route.distanceHeuristic)
			return 0;
		if (this.distanceCost + this.distanceHeuristic < route.distanceCost + this.distanceHeuristic)
			return -1;
		return 1;
	}

	/**
	 * Compares this Route to the given Route by using their time cost. Returns
	 * a negative integer, a zero, or a positive integer as the Route's time
	 * cost is less than, equal to, or greater than the specified Route's time
	 * cost.
	 * 
	 * @param route
	 *            - Route to compare time costs with this Route
	 * @return a negative integer, zero, or a positive integer as this Route's
	 *         time cost is less than, equal to, or greater than the specified
	 *         Route's time cost.
	 */
	public int compareToTime(Route route) {
		if (route == null)
			throw new NullPointerException(
					"Route to compare time with does not exist!");
		if (this.timeCost + this.timeHeuristic == route.timeCost + route.timeHeuristic)
			return 0;
		if (this.timeCost + this.timeHeuristic < route.timeCost + route.timeHeuristic)
			return -1;
		return 1;
	}

	/**
	 * Returns the route in html string format
	 * 
	 * @return String of the route
	 */
	public String toString() {
		String retString = "";
		int i = 1;
		for (Destination d : this) {
			System.out.println(d.name);
			retString += "" + i + ". " + d.toString() + "\n";
			i++;
		}
		return retString;
	}
}
