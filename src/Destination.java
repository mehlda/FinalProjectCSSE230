import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * A vertex in the graph that can be traveled to. Stores data about itself and
 * its neighbors.
 * 
 * @author derrowap
 *
 */
public class Destination {
	public Coordinate coordinate;
	public String name;
	public String address;
	public int rating;
	public BufferedImage picture;
	public LinkedList<Connection> neighbors;
	public Point mapPoint;

	/**
	 * Constructs a new Destination object with null values.
	 */
	public Destination() {
		this(null, null, null, -1, null, null);
	}

	/**
	 * Constructs a new Destination object with the given parameter.
	 * 
	 * @param coordinate
	 *            - coordinate of this destination
	 * @param name
	 *            - name of this destination
	 * @param address
	 *            - address of this destination
	 * @param rating
	 *            - rating of this destination
	 * @param picture
	 *            - picture of this destination
	 * @param neighbors
	 *            - LinkedList of all neighbor connections of this destination
	 */
	public Destination(Coordinate coordinate, String name, String address,
			int rating, LinkedList<Connection> neighbors, Point point) {
		this.coordinate = coordinate;
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.neighbors = neighbors;
		this.mapPoint = point;
	}

	/**
	 * Adds a new connection to this Destination's neighbors.
	 * If this connection already exists, return false.
	 * 
	 * @param connection
	 *            - new connection to add
	 * @return true if connection was added
	 */
	public boolean addConnection(Connection connection) {
		if(connection == null) return false;
		/* this check causes it to be O(n) */
		if(this.neighbors == null) {
			this.neighbors = new LinkedList<>();
		}
		if(this.neighbors.contains(connection)) return false;
		this.neighbors.add(connection);
		return true;
	}
	
	/**
	 * Returns a String of this Destination name.
	 * 
	 * @return this destination's name
	 */
	public String toString(){
		return this.name;
	}

}
