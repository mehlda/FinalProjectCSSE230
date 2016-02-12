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
			// Route origin = this.pop();
		}
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

	/**
	 * 
	 * @param route
	 * @return
	 */
	public boolean remove(Route route) {

		return false;
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
	public Route poll() {
		if (this.isEmpty())
			return null;
		Route route = this.peek();
		this.remove(route);
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
		if (this.useTime) {
			if (parent.timeCost > end.timeCost) {
				super.set((index - 1) / 2, end);
				super.set(index, parent);
				this.addBalance((index - 1) / 2);
			}
		} else {
			if (parent.distanceCost > end.distanceCost) {
				super.set((index - 1) / 2, end);
				super.set(index, parent);
				this.addBalance((index - 1) / 2);
			}
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
		if (this.useTime) {
			if (child.compareToTime(parent) < 0) {
				super.set(index, child);
				super.set(smallest, parent);
				this.removeBalance(smallest);
			}
		} else {
			if (child.compareToDistance(parent) < 0) {
				super.set(index, child);
				super.set(smallest, parent);
				this.removeBalance(smallest);
			}
		}
	}

	/**
	 * Returns the smallest child's index of the specified Route by the given
	 * index.
	 * Returns -1 if there are no children to the parent element.
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
		if (this.useTime) {
			if (leftChild.compareToTime(rightChild) <= 0)
				return index * 2 + 1;
			return index * 2 + 2;
		}
		if (leftChild.compareToDistance(rightChild) <= 0)
			return index * 2 + 1;
		return index * 2 + 2;
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
