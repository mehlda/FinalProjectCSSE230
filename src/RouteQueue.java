import java.util.ArrayList;

/**
 * MinHeap Implementation of a PriorityQueue to store possible routes.
 * 
 * @author derrowap
 *
 */
public class RouteQueue extends ArrayList<Destination> {
	public String start;
	public String end;
	public Route bestRoute;

	/**
	 * Constructs a new RouteQueue object.
	 * 
	 * @param start
	 *            - name of starting location
	 * @param end
	 *            - name of ending location
	 */
	public RouteQueue(String start, String end) {
		// TODO: implement this method
		this.start = start;
		this.end = end;
	}

	/**
	 * Pops a route off the priorityQueue
	 * if last destination is final destination, alert.
	 * else find next step in route
	 * 
	 * Need to subtract heuristic cost, then get new routes, and for each new
	 * route add in new heuristic values.
	 * 
	 * Algorithm of building:
	 * Store neighbors of the last destination
	 * for each neighbor, make a clone of the route and add neighbor to end of
	 * route, then throw all back in PriorityQueue
	 * 
	 */
	public void buildQueue() {
		// TODO: implement this method
	}

	/**
	 * Take the top element off priority queue
	 * 
	 * @return Route from top of PriorityQueue
	 */
	public Route pop() {
		// TODO: implement this method
		return null;
	}

	/**
	 * Adds Route object into PriorityQueue.
	 * Route with shortest cost percolates to top.
	 * 
	 * @param route
	 *            - new route object to add
	 * @return true if added successfully
	 */
	public boolean push(Route route) {
		// TODO: implement this method
		return false;
	}

	/**
	 * Finds the top element in the PriorityQueue
	 * which is at index 0. Does not remove any objects.
	 * 
	 * @return top Route object in PriorityQueue
	 */
	public Route peek() {
		// TODO: implement this method
		return null;
	}

	/**
	 * Swaps the destinations in the ArrayList at these indices.
	 * If either of the indices are out of range, it will return null.
	 * If the indices are equal, notifies to console.
	 * 
	 * @param index1
	 *            - index of first destination to swap
	 * @param index2
	 *            - index of second destination to swap
	 * @return true if elements were swapped
	 */
	private boolean swap(int index1, int index2) {
		if (index1 >= this.size() || index2 >= this.size())
			return false;
		if (index1 == index2)
			System.out.println("Swapping indexes " + index1 + " and " + index2
					+ " does nothing.");
		Destination temp = this.get(index1);
		this.set(index1, this.get(index2));
		this.set(index2, temp);
		return true;
	}
}
