import java.util.ArrayList;
import java.util.Iterator;

/**
 * Min-Heap Implementation of a Priority Queue to store possible routes.
 * 
 * @author derrowap
 *
 */
public class RouteQueue extends ArrayList<Route> {
	protected Destination start;
	protected Destination end;
	protected Cost costFunction; // if true, use time as cost
	protected int maxDestinations;
	private ArrayList<Route> history;

	protected enum Cost {
		DISTANCE, TIME, INTEREST
	}

	/**
	 * Constructor with no routes on this Priority Queue yet. Used for waypoints
	 * when adding routes manually.
	 * 
	 * @param costFunction
	 *            - the way the user is searching for the best Route
	 * @param start
	 *            - name of starting Destination
	 * @param end
	 *            - name of goal, ending Destination
	 * @param maxDestinations
	 *            - maximum number of destinations to travel in the route
	 */
	protected RouteQueue(Cost costFunction, Destination start, Destination end,
			int maxDestinations) {
		this.maxDestinations = maxDestinations <= 0 ? Integer.MAX_VALUE
				: maxDestinations;
		this.start = start;
		this.end = end;
		this.costFunction = costFunction;
		this.history = new ArrayList<>();
	}

	/**
	 * Constructs a new RouteQueue object with start, end, and waypoint
	 * destinations.
	 * 
	 * @param start
	 *            - name of starting Destination
	 * @param end
	 *            - name of goal, ending Destination
	 * @param waypoints
	 *            - Destination array of all waypoints
	 */
	protected RouteQueue(Destination start, Destination end, Cost costFunction,
			int maxDestinations) {
		this.maxDestinations = maxDestinations <= 0 ? Integer.MAX_VALUE
				: maxDestinations;
		this.start = start;
		this.end = end;
		this.costFunction = costFunction;
		this.history = new ArrayList<>();
		/* Adds the starting destination with heuristic to first waypoint */
		Route firstRoute = new Route(this.start);
		firstRoute.addHeuristicCost(this.end);
		this.add(firstRoute);
		this.buildQueue();
	}

	/**
	 * Takes routes off and on until this Priority Queue has the first element
	 * as a complete route from the starting Destination to every waypoint and
	 * to the final goal Destination in the smallest specified cost.
	 */
	private void buildQueue() {
		while (!this.isEmpty()
				&& !this.get(0).isCompleteRoute(this.start.name, this.end.name)) {
			buildNextWaypoint();
		}
		/* finished, the top element is a complete route. */
	}

	/**
	 * Builds this Priority Queue until the top element is a complete route from
	 * the starting destination to the specified waypoint destination.
	 * 
	 * Takes the first element off of this queue and removes the heuristic cost
	 * that is stored within the cost functions. It then clones an exact object
	 * for as many connections that the last element in the Route has.
	 * 
	 * Each one of these clones adds one of the destinations that it has a
	 * connection with. The cost functions then increase based off the cost
	 * stored within the Connection.
	 * 
	 * The hueristic cost is added back into each individual clone and then
	 * added back into this queue.
	 * 
	 * @param waypoint
	 *            - goal destination to build Routes to
	 */
	private void buildNextWaypoint() {
		Route origin = this.peek();
		Destination last = origin.getLast();
		this.remove(origin);
		for (Connection connection : last.neighbors) {
			Route clone = (Route) origin.clone();
			clone.timeCost = origin.timeCost + connection.pathTime;
			clone.distanceCost = origin.distanceCost + connection.pathDistance;
			clone.interestCost = origin.interestCost;

			if (connection.firstLocation.name.equals(last.name)) {
				if (clone.contains(connection.secondLocation))
					continue;
				clone.add(connection.secondLocation);
				clone.interestCost += connection.secondLocation.rating;
			} else {
				if (clone.contains(connection.firstLocation))
					continue;
				clone.add(connection.firstLocation);
				clone.interestCost += connection.firstLocation.rating;
			}
			clone.addHeuristicCost(this.end);
			if (clone.size() <= this.maxDestinations)
				this.add(clone);
		}
	}

	/**
	 * Adds Route object into PriorityQueue. Route with shortest cost percolates
	 * to top.
	 * 
	 * @param route
	 *            - new route object to add
	 * @return true if added successfully
	 */
	public boolean add(Route route) {
		if (route == null)
			return false;
		super.add(route);
		this.addBalance(super.size() - 1);
		return true;
	}

	/**
	 * Removes a single instance of the specified Route element from this queue,
	 * if it is present. Priority Queue will be balanced back to properties.
	 * 
	 * @param route
	 *            - Route to be removed from this PriorityQueue, if present
	 * @return true if this PriorityQueue removed the specifed Route
	 */
	public boolean remove(Route route) {
		int index = super.indexOf(route);
		if (index == -1)
			return false;
		int lastIndex = super.size() - 1;
		if (index != lastIndex) {
			super.set(index, super.get(lastIndex));
			super.remove(lastIndex);
			this.removeBalance(index);
			if (this.get(0) == null)
				System.out.println("index 0 is null");
		} else
			super.remove(index);
		this.buildQueue();
		return true;
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
	 * Finds the top element in the PriorityQueue which is at index 0. Does not
	 * remove any objects.
	 * 
	 * @return top Route object in PriorityQueue
	 */
	protected Route peek() {
		if (this.isEmpty())
			return null;
		return this.get(0);
	}

	/**
	 * Take the top element off priority queue. Returns the next best route that
	 * has not been accessed yet.
	 * 
	 * @return Route from top of PriorityQueue
	 */
	protected Route poll() {
		if (this.isEmpty())
			return null;
		Route route = peek();
		while(this.history.contains(route)){
			this.remove(route);
			route = peek();
		}
		if(route.isCompleteRoute(this.start.name, this.end.name)) {
			this.remove(route);
			this.history.add(route);
			return route;
		}
		while(!route.isCompleteRoute(this.start.name, this.end.name)){
			route = this.peek();
			this.remove(route);
		}
		this.remove(route);
		this.history.add(route);
		return route;
	}

	/**
	 * If the Route at the specified index has a smaller cost than the cost of
	 * the parent, then they swap places. Recursively will adjust all the way
	 * through the min-heap PriorityQueue until it no longer needs adjusting.
	 * Considers both cases of using distance or time cost.
	 * 
	 * @param index
	 *            - index of element to check for balancing with parent
	 */
	private void addBalance(int index) {
		Route end = super.get(index);
		Route parent = super.get((index - 1) / 2);
		if (parent.compareBy(this.costFunction, end) == 1) {
			super.set((index - 1) / 2, end);
			super.set(index, parent);
			this.addBalance((index - 1) / 2);
		}
	}

	/**
	 * Checks if the Route at the specified indx is larger than one of its
	 * children Routes, if so then they swap places in the min-heap
	 * PriorityQueue. Recursively goes down the entire tree until no more swaps
	 * are needed. Considers both cases of comparing using time or distance
	 * costs.
	 * 
	 * @param index
	 *            - index of element to check for balancing
	 */
	private void removeBalance(int index) {
		int smallest = this.smallestChild(index);
		if (smallest == -1)
			return; // specified Route is a leaf
		Route parent = super.get(index);
		Route child = super.get(smallest);
		if (child.compareBy(this.costFunction, parent) < 0) {
			super.set(index, child);
			super.set(smallest, parent);
			this.removeBalance(smallest);
		}
	}

	/**
	 * Returns the smallest child's index of the specified Route by the given
	 * index. Returns -1 if there are no children to the parent element.
	 * 
	 * @param index
	 *            - index of the parent Route to compare the smallest children
	 *            with
	 * @return index of the smallest child or -1 if there are no children.
	 */
	private int smallestChild(int index) {
		Route leftChild = (index * 2 + 1) < super.size() ? super
				.get(index * 2 + 1) : null;
		Route rightChild = (index * 2 + 2) < super.size() ? super
				.get(index * 2 + 2) : null;
		if (rightChild == null) {
			if (leftChild != null)
				return index * 2 + 1;
			return -1;
		}
		if (leftChild == null)
			return index * 2 + 2;
		if (leftChild.compareBy(this.costFunction, rightChild) <= 0) {
			return index * 2 + 1;
		}
		return index * 2 + 2;
	}

	/**
	 * Returns an array containing all of the Routes in this PriorityQueue.
	 * 
	 * @return an array containing all of the Routes in this PriorityQueue.
	 */
	public String[] toArray() {
		String[] output = new String[this.size()];
		for (int i = 0; i < this.size(); i++) {
			output[i] = this.get(0).toString();
		}
		return output;
	}

	/**
	 * Used for an admin feature on the GUI.
	 * Constructs a string of all the Routes in this Priority Queue.
	 * 
	 * @return a string of all routes in this Priority Queue.
	 */
	protected String printStack() {
		String output = "";
		for (int i = 0; i < this.size(); i++) {
			output += "i = " + i + " \n";
			output += this.get(i).toString() + " \n";
		}
		return output;
	}
}
