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
	public DestinationNode first;
	public DestinationNode last;

	/**
	 * Constructs a new Route object
	 */
	public Route() {
		// TODO: implement this method
		this.first = null;
		this.last = null;
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
		this.first = new DestinationNode(destination);
		timeCost = distanceCost = 0; // started here, haven't travelled anywhere
		this.last = this.first;
	}

	/**
	 * Adds a new destination to the end of the Route. Updates the time and
	 * distance cost to appropriate value based off of the connection value to
	 * the next location.
	 * 
	 * Does not add heuristic value to cost!
	 * 
	 * @param destination
	 *            - Destination to add to the end of this Route
	 * @return true if successfully added
	 */
	public boolean addNextLocation(Destination d) {
		// TODO: This is where I need to be able to find a connection somehow
		// while only having the last element in the route and the destination
		// to be inserted

		int oldx = this.last.destination.coordinate.x;
		int oldy = this.last.destination.coordinate.y;

		this.last.next = new DestinationNode(d);
		this.last = this.last.next;

		int newx = this.last.destination.coordinate.x;
		int newy = this.last.destination.coordinate.y;

		int distx = (oldx - newx) * (oldx - newx);
		int disty = (oldy - newy) * (oldy - newy);
		int totalDist = (int) Math.sqrt(distx + disty);

		distanceCost += totalDist;
		return true;
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

	public class DestinationNode {
		private DestinationNode next;
		private Destination destination;

		public DestinationNode(Destination d) {
			this.next = null;
			this.destination = d;
		}

		public DestinationNode getNext() {
			return this.next;
		}

		public Destination getDestination() {
			return this.destination;
		}
	}
}
