/**
 * Implementation of an x, y coordinate.
 * 
 * @author derrowap
 *
 */
public class Coordinate {
	/**
	 * Latitude and Longitude coordinates
	 */
	public double x, y;

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
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the x value of this Coordinate to given x parameter.
	 * 
	 * @param x
	 *            - new x value of this coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y value of this Coordinate to given y parameter.
	 * 
	 * @param y
	 *            - new y value of this coordinate
	 */
	public void setY(double y) {
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
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculates the straight line distance between the this coordinate point
	 * and the specified coordinate point. Uses the
	 * "Great Circle Navigation Formulae" to have best accuracy since the world
	 * is not flat. Since a straight line distance is not always an int, it is
	 * rounded to be an int. Units = Miles.
	 * 
	 * @param c
	 *            - specified coordinate point to calculate with
	 * @return straight line distance between this coordinate point and the
	 *         specified coordinate point in miles
	 */
	public double straightLineDistance(Coordinate c) {
		return Math.toDegrees(Math.acos(Math.sin(Math.toRadians(this.x))
				* Math.sin(Math.toRadians(c.x))
				+ Math.cos(Math.toRadians(this.x))
				* Math.cos(Math.toRadians(c.x))
				* Math.cos(Math.toRadians(this.y - c.y)))) * 60 * 1.1515;
	}
}
