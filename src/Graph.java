import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A graph implementation of all the destinations and connections.
 * 
 * @author derrowap
 *
 */
public class Graph {
	/**
	 * List of destinations
	 */
	public LinkedList<Destination>[] destinations;

	/**
	 * Constructs a new Graph object
	 */
	public Graph() {
		this.destinations = new LinkedList[26];
	}

	/**
	 * Constructs a new Graph object with all of the given destinations
	 * inserted.
	 * 
	 * @param destinations
	 *            - array of Destination objects to initialize the Graph with
	 */
	public Graph(Destination[] destinations) {
		this.destinations = new LinkedList[26];
		for (Destination d : destinations) {
			this.insert(d);
		}
	}

	/**
	 * Creates a new Destination object with the given parameters. Creates new
	 * Connections with given names of Neighbors, times, and distances.
	 * 
	 * Must look up Destination objects by the neighbor's name first to get the
	 * object for the connection.
	 * 
	 * Array of Neighbors, times, and distances are assumed to be indexed at
	 * same number corresponding to values that go together in new Connection
	 * objects.
	 * 
	 * Adds new Connection objects to both Destinations in that connection.
	 * 
	 * Adds Destination objects to the DestinationHashSet map.
	 * 
	 * @param name
	 *            - String name of new Destination to add
	 * @param c
	 *            - Coordinate point of new Destination to add
	 * @param address
	 *            - Sting Address of new Destination to add
	 * @param rating
	 *            - int Rating of new Destination to add
	 * @param pic
	 *            - BufferedImage of new Destination to add
	 * @param neighbors
	 *            - String array of neighbors to add to this new Destination
	 * @param times
	 *            - int array of the time cost to travel from this new
	 *            Destination to the corresponding neighbor in the neighbor
	 *            string array
	 * @param distances
	 *            - int array of the distance cost to travel from this new
	 *            Destination to the corresponding neighbor in the neighbor
	 *            string array
	 * @return - true if insertion was successful
	 * @throws ObjectNotFoundException
	 */
	// Removed BufferedImage pic ###DAM###
	public boolean insert(String name, Coordinate c, String address,
			int rating, String[] neighbors, int[] times, int[] distances,
			Point point) throws ObjectNotFoundException {
		// removed || pic == null ###DAM###
		if (name == null || c == null || address == null)
			return false;
		Destination location1 = new Destination(c, name, address, rating, null,
				point);
		for (int i = 0; i < neighbors.length; i++) {
			Destination location2 = this.find(neighbors[i]);
			if (location2 == null)
				throw new ObjectNotFoundException("Neighbor " + neighbors[i]
						+ " was not found as a Destination object.");
			location1.addConnection(new Connection(location1, location2,
					distances[i], times[i]));
		}
		return this.insert(location1);
	}

	/**
	 * Inserts a new destination object into the graph.
	 * 
	 * @param destination
	 *            - destination to add
	 * @return true if destination is added successfully
	 */
	public boolean insert(Destination destination) {
		if (this.destinations[this.getHashValue(destination.name)] == null) {
			this.destinations[this.getHashValue(destination.name)] = new LinkedList<>();
		}
		System.out.println("insert graph: " + destination.name);
		return this.destinations[this.getHashValue(destination.name)]
				.add(destination);
	}

	/**
	 * Looks up Destination object in DestinationHashSet map, if found it will
	 * be removed, else return false.
	 * 
	 * @param name
	 *            - String name of destination to remove
	 * @return true if destination object was found and removed
	 */
	public boolean remove(Destination destination) {
		return this.destinations[this.getHashValue(destination.name)]
				.remove(destination);
	}

	/**
	 * Finds the Destination object with the same name as the parameter passed.
	 * Uses hash key of name as index in array. If no Destination is found with
	 * the specified name, return null.
	 * 
	 * @param name
	 *            - name of Destination to find
	 * @return Destination object with the given name or null if does not exist
	 */
	public Destination find(String name) {
		if (this.destinations[this.getHashValue(name)] == null)
			return null;
		Iterator<Destination> i = this.destinations[this.getHashValue(name)]
				.iterator();
		while (i.hasNext()) {
			Destination d = i.next();
			if (d.name.equals(name))
				return d;
		}
		return null;
	}

	/**
	 * If name is not found in find() method, this can be called to find similar
	 * names that may have been what they had been looking for. Returns
	 * LinkedList of all destinations with first letter the same as the first
	 * letter in the name parameter passed.
	 * 
	 * @param name
	 *            - name to return possible suggestions of destinations with
	 *            names similar to
	 * @return LinkedList of suggested possibilities similar to this name
	 */
	public LinkedList<Destination> findSuggestions(String name) {
		return this.destinations[this.getHashValue(name)];
	}

	/**
	 * Gets first character of name. Converts it to an int, then modulus by 26
	 * so that the key is within the array's index size.
	 * 
	 * @param name
	 *            - String to find hash key value of
	 * @return hash key value
	 */
	public int getHashValue(String name) {
		return ((int) name.toLowerCase().charAt(0) - 97) % 26;
	}

	/**
	 * Creates a new RouteQueue object. Will automatically be built so that the
	 * object that is returned already has the best Route at the top of the
	 * Queue from the specified start location and to the specified end location
	 * stopping at each waypoint location in order.
	 * 
	 * @param start
	 *            - name of the starting Destination location
	 * @param end
	 *            - name of the ending Destination location
	 * @param waypoints
	 *            - names of all the waypoint Destinations in order
	 * @return a RouteQueue object already built with the best Route on top
	 */
	public RouteQueue getRouteQueue(String start, String end,
			String[] waypoints, RouteQueue.Cost costFunction,
			int maxDestinations) {
		Destination first = this.find(start);
		Destination last = this.find(end);
		if (waypoints == null) {
			return new RouteQueue(first, last, costFunction, maxDestinations);
		}
		
		RouteQueue[] waypointQueues = new RouteQueue[waypoints.length + 1];
		waypointQueues[0] = new RouteQueue(first, this.find(waypoints[0]), costFunction, maxDestinations);
		for (int i = 1; i < waypoints.length; i++) {
			waypointQueues[i] = new RouteQueue(this.find(waypoints[i-1]), this.find(waypoints[i]), costFunction, maxDestinations);
		}
		waypointQueues[waypoints.length] = new RouteQueue(this.find(waypoints[waypoints.length-1]), this.find(end), costFunction, maxDestinations);

		Route[][] waypointRoutes = new Route[waypointQueues.length][2];
		int index = 0;
		for(RouteQueue queue : waypointQueues) {
			if(queue == null) continue;
			waypointRoutes[index][0] = queue.poll();
			waypointRoutes[index][1] = queue.poll();
			index++;
		}
		Route[] route1 = new Route[2];
		route1[0] = waypointRoutes[0][0];
		route1[1] = waypointRoutes[0][1];
		
		for(int s = 0; s < waypointRoutes.length - 1; s++) {
			Route[] route2 = new Route[route1.length*2];
			index = 0;
			for(int i = 0; i < route1.length; i++) {
				for(int j = 0; j < 2; j++) {
					route2[index] = route1[i].combineRoute(waypointRoutes[s+1][j]);
					index++;
				}
			}
			route1 = route2;
		}
		
		RouteQueue output = new RouteQueue(costFunction, first, last, maxDestinations);
		for(Route r : route1) {
//			if(r.isCompleteRoute(start, end))
				output.add(r);
		}
		return output;
	}

	/**
	 * Gets all of the Destinations in this graph, mostly sorted.
	 * 
	 * @return LinkedList of all the destinations in this graph
	 */
	public LinkedList<Destination> getAllDestinations() {
		LinkedList<Destination> output = new LinkedList<Destination>();
		for (LinkedList<Destination> list : this.destinations) {
			if (list != null) {
				for (Destination d : list) {
					output.add(d);
				}
			}
		}
		return output;
	}
}
