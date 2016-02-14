/**
 * Implementation of an x, y coordinate.
 * 
 * @author derrowap
 *
 */
public class Coordinate {
	public int x, y;

	/**
	 * Constructs x and y to minimum integer value if no parameters are passed.
	 */
	public Coordinate() {
		this.x = this.y = Integer.MIN_VALUE;
	}

	/**
	 * Constructs coordinated point to given x and y.
	 * 
	 * @param x
	 *            - x value on map of this coordinate
	 * @param y
	 *            - y value on map of this coordinate
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the x value of this Coordinate to given x parameter.
	 * 
	 * @param x
	 *            - new x value of this coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y value of this Coordinate to given y parameter.
	 * 
	 * @param y
	 *            - new y value of this coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the x and y valued of this Coordinate to the given x and y
	 * parameters.
	 * 
	 * @param x
	 *            - new x value of this coordinate
	 * @param y
	 *            - new y value of this coordinate
	 */
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculates the straight line distance between the this coordinate point
	 * and the specified coordinate point. Since a straight line distance is not
	 * always an int, it is rounded to be an int.
	 * 
	 * @param c
	 *            - specified coordinate point to calculate with
	 * @return straight line distance between this coordinate point and the
	 *         specified coordinate point
	 */
	public int straightLineDistance(Coordinate c) {
		return (int) Math
				.sqrt(((this.x - c.x) * (this.x - c.x) + (this.y - c.y)
						* (this.y - c.y)));
	}
}
