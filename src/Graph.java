import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * A graph implementation of all the destinations and connections.
 * 
 * @author derrowap
 *
 */
public class Graph {
	public LinkedList<Destination>[] destinations;

	/**
	 * Constructs a new Graph object
	 */
	public Graph() {
		// TODO: implement this method
		this.destinations = new LinkedList[26];
	}

	/**
	 * Creates a new Destination object with the given parameters.
	 * Creates new Connections with given names of Neighbors, times, and
	 * distances.
	 * 
	 * Must look up Destination objects by the neighbor's name first to get the
	 * object for the connection.
	 * 
	 * Array of Neighbors, times, and distances are assumed to be indexed at
	 * same number corresponding to values that go together in new Connection
	 * obejcts.
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
	public boolean insert(String name, Coordinate c, String address,
			int rating, BufferedImage pic, String[] neighbors, int[] times,
			int[] distances) throws ObjectNotFoundException {
		if (name == null || c == null || address == null || pic == null)
			return false;
		Destination location1 = new Destination(c, name, address, rating, pic,
				null);
		for (int i = 0; i < neighbors.length; i++) {
			Destination location2 = this.find(neighbors[i]);
			if(location2 == null) throw new ObjectNotFoundException("Neighbor " + neighbors[i] + " was not found as a Destination object.");
			location1.addConnection(new Connection(location1, location2, distances[i], times[i]));
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
		// TODO: implement this method
		return false;
	}

	/**
	 * Looks up Destination object in DestinationHashSet map,
	 * if found it will be removed, else return false.
	 * 
	 * @param name
	 *            - String name of destination to remove
	 * @return true if destination object was found and removed
	 */
	public boolean remove(String name) {
		// TODO: implement this method
		return false;
	}

	/**
	 * Finds the Destination object with the same name as the parameter passed.
	 * Uses hash key of name as index in array.
	 * 
	 * TODO: If Destination is not found, returns null or throws
	 * ObjectNotFoundException?
	 * 
	 * @param name
	 *            - name of Destination to find
	 * @return Destination object with the given name
	 */
	public Destination find(String name) throws ObjectNotFoundException {
		// TODO: implement this method
		return null;
	}

	/**
	 * If name is not found in find() method, this can be called to find
	 * similar names that may have been what they had been looking for.
	 * Returns LinkedList of all destinations with first letter the same as
	 * the first letter in the name parameter passed.
	 * 
	 * @param name
	 *            - name to return possible suggestions of
	 *            destinations with names similar to
	 * @return LinkedList of suggested possibilities similar to this name
	 */
	public LinkedList<Destination> findSuggestions(String name) {
		// TODO: implement this method
		return null;
	}

	/**
	 * Gets first character of name. Converts it to an int, then modulus
	 * by 26 so that the key is within the array's index size.
	 * 
	 * @param name
	 *            - String to find hash key value of
	 * @return hash key value
	 */
	public int getHashValue(String name) {
		// TODO: implement this method
		return -1;
	}

	/**
	 * TODO: write description
	 * 
	 * @param start
	 * @param end
	 * @param waypoints
	 * @return
	 */
	public RouteQueue getRouteQueue(String start, String end,
			String[] waypoints, boolean useTime) {
		Destination[] midpoints = new Destination[waypoints.length];
		for (int i = 0; i < waypoints.length; i++) {
			try {
				midpoints[i] = this.find(waypoints[i]);
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			return new RouteQueue(this.find(start), this.find(end), midpoints,
					useTime);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
