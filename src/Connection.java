/**
 * A field class to store data about edges between destinations.
 * 
 * @author derrowap
 *
 */
public class Connection {
	/**
	 * First location of this connection
	 */
	public Destination firstLocation;
	/**
	 * Final location of this connection
	 */
	public Destination secondLocation;
	/**
	 * Distance of this connection
	 */
	public int pathDistance;
	/**
	 * Time of this connection
	 */
	public int pathTime;
	
	/**
	 * Constructs a new Connection object with null destinations and 0 costs.
	 */
	public Connection() {
		this(null, null, 0, 0);
	}

	/**
	 * Constructs a new Connection object with the given parameters
	 * 
	 * @param first
	 *            - Destination of first location
	 * @param second
	 *            - Destination of second Location
	 * @param distance
	 *            - cost to travel between destinations in distance
	 * @param time
	 *            - cost to travel between destinations in time
	 */
	public Connection(Destination first, Destination second, int distance,
			int time) {
		this.firstLocation = first;
		this.secondLocation = second;
		this.pathDistance = distance;
		this.pathTime = time;
	}

	/**
	 * Sets the first destination location to the given parameter.
	 * 
	 * @param destination
	 *            - destination to set as new first location
	 */
	public void setFirstLocation(Destination destination) {
		this.firstLocation = destination;
	}

	/**
	 * Sets the second destination location to the given parameter.
	 * 
	 * @param destination
	 *            - destination to set as new second location
	 */
	public void setSecondLocation(Destination destination) {
		this.secondLocation = destination;
	}

	/**
	 * Sets the cost distance to travel through this connection.
	 * 
	 * @param distance
	 *            - new distance cost
	 */
	public void setDistance(int distance) {
		this.pathDistance = distance;
	}

	/**
	 * Sets the cost time to travel through this connection.
	 * 
	 * @param time
	 *            - new time cost
	 */
	public void setTime(int time) {
		this.pathTime = time;
	}

	/**
	 * Sets all the fields of this connection to the given parameters.
	 * 
	 * @param first
	 *            - destination to set as new first location
	 * @param second
	 *            - destination to set as new second location
	 * @param distance
	 *            - new distance cost
	 * @param time
	 *            - new time cost
	 */
	public void setConnection(Destination first, Destination second,
			int distance, int time) {
		this.firstLocation = first;
		this.secondLocation = second;
		this.pathDistance = distance;
		this.pathTime = time;
	}

}
