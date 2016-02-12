import java.util.ArrayList;
import java.util.Iterator;

/**
 * MinHeap Implementation of a PriorityQueue to store possible routes.
 * 
 * @author derrowap
 *
 */
public class RouteQueue extends ArrayList<Route> {
	public Destination start;
	public Destination end;
	public Destination[] waypoints;
	public Route bestRoute;
	public boolean useTime; // if true, use time as cost

	/**
	 * Constructs a new RouteQueue object with start, end, and waypoint
	 * destinations.
	 * 
	 * @param start
	 *            - name of
	 * @param end
	 * @param waypoints
	 */
	public RouteQueue(Destination start, Destination end,
			Destination[] waypoints, boolean useTime) {
		this.start = start;
		this.end = end;
		this.waypoints = waypoints;
		this.useTime = useTime;
		this.add(new Route(this.start));
		this.buildQueue();
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
		while (!this.get(0).isCompleteRoute(this.start.name, this.end.name)) {
//			Route origin = this.pop();
		}
	}

	/**
	 * Removes all of the elements from this priority queue. The queue will be
	 * empty after this call returns.
	 */
	public void clear() {
		super.clear();
	}

	/**
	 * Checks if this Priority Queue contains the specified element
	 * 
	 * @param route
	 *            - object to be checked for containment in this queue
	 * @return true if this Priority Queue contains this Route
	 */
	public boolean contains(Route route) {
		return super.contains(route);
	}

	/**
	 * Builds an iterator over the elements in this queue.
	 * 
	 * @return an iterator over the routes in this Priority Queue
	 */
	public Iterator<Route> iterator() {
		return super.iterator();
	}
	
	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @param route
	 *            - the element to add
	 * 
	 * @return true if specified element was added
	 * 
	 * @throws NullPointerException
	 *             - if the specified element is null
	 */
	public boolean offer(Route route) {
		return this.add(route);
	}
	
	/**
	 * Finds the top element in the PriorityQueue
	 * which is at index 0. Does not remove any objects.
	 * 
	 * @return top Route object in PriorityQueue
	 */
	public Route peek() {
		if(this.isEmpty())
			return null;
		return this.get(0);
	}
	
	/**
	 * Take the top element off priority queue. Returns the next best route that
	 * has not been accessed yet.
	 * 
	 * @return Route from top of PriorityQueue
	 */
	public Route poll() {
		if(this.isEmpty())
			return null;
		Route route = this.peek();
		this.remove(route);
		return route;
	}

	/**
	 * 
	 * @param route
	 * @return
	 */
	public boolean remove(Route route) {
		
		return false;
	}
	
	/**
	 * Adds Route object into PriorityQueue.
	 * Route with shortest cost percolates to top.
	 * 
	 * @param route
	 *            - new route object to add
	 * @return true if added successfully
	 */
	public boolean add(Route route) {
		// TODO: implement this method
		if (route == null)
			throw new NullPointerException();
		super.add(route);
		this.addBalance(super.size() - 1);
		return true;
	}

	

	private void addBalance(int i) {
		// TODO Auto-generated method stub
		
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
		Route temp = this.get(index1);
		this.set(index1, this.get(index2));
		this.set(index2, temp);
		return true;
	}
}
