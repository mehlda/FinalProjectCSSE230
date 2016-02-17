import org.junit.Test;
import static org.junit.Assert.*;

public class Testing {

	@Test
	public void testCoordinateDistance() {
		Coordinate[] c1 = { new Coordinate(39.784296, -86.158154),
				new Coordinate(37.649918, -122.462436),
				new Coordinate(47.606663, -122.332648),
				new Coordinate(52.234314, 21.000007) };
		Coordinate[] c2 = { new Coordinate(30.271201, -97.741139),
				new Coordinate(38.125674, -121.825022),
				new Coordinate(35.229555, -80.845007),
				new Coordinate(41.876630, -87.630071) };
		double[] distances = { 926.60, 47.85, 2280.75, 4666.64 };

		for (int i = 0; i < c1.length; i++) {
			assertEquals((int) distances[i],
					(int) c1[i].straightLineDistance(c2[i]));
		}
	}

	@Test
	public void testGraphHashValue() {
		Graph g = new Graph();
		String[] names = { "A", "b", "C", "d", "E", "f", "G", "h", "I", "j",
				"K", "l", "M", "n", "O", "p", "Q", "r", "S", "t", "U", "v",
				"W", "x", "Y", "z" };
		for (int i = 0; i < names.length; i++) {
			assertEquals(i + 1, g.getHashValue(names[i]));
		}
	}

	@Test
	public void testGraphInsert() {
		// TODO: test insert()

	}

	@Test
	public void testGraphRemove() {
		// TODO: test remove()

	}

	@Test
	public void testGraphFind() {
		// TODO: test find()

	}

	@Test
	public void testGraphSuggestions() {
		// TODO: test findSuggestions()

	}

	@Test
	public void testRouteCompare() {
		// TODO: test compareToTime()
		// TODO: test compareToDistance()

	}

	@Test
	public void testRouteComplete() {
		// TODO: test isCompleteRoute()

	}

	@Test
	public void testRouteQueueBuild() {
		// TODO: test buildQueue()
		// TODO: test peek()

	}

	@Test
	public void testRouteQueueAdd() {
		// TODO: test add()

	}

	@Test
	public void testRouteQueuePoll() {
		// TODO: test poll()
		// TODO: test peek()

	}
}
