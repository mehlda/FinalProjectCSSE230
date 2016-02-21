import java.awt.Point;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads the destination file and generates an xml file
 *
 * @author Anderson. Created Feb 21, 2016.
 */
public class DestinationFileReader {
	// need to create a file reader. the fields in this class will need to be.
	private String inputFile = "src/assets/destinations.txt";
	private String line;
	private String address;
	private String name;
	private String location;

	// this one holds for making connections later
	private Object[][] neighbors;
	private String[] neighborNames;
	private int[] neighborTimeCost;
	private int[] neighborDistCost;
	private String[] coord;
	private int rating;
	private Double x;
	private Double y;
	private int mapx;
	private int mapy;
	private Graph newgraph;
	private int index;

	private DestinationFileReader() {
		this.neighbors = new Object[50][3];
		try {
			this.newgraph = new Graph();

			FileReader fileReader = new FileReader(this.inputFile);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// actually read the inforation into arrays
			while ((this.line = bufferedReader.readLine()) != null) {
				String[] lineArray = this.line.split(":");
				this.name = lineArray[0];
				this.location = lineArray[1];
				this.coord = this.location.split(",");
				this.x = Double.parseDouble(this.coord[0]);
				this.y = Double.parseDouble(this.coord[1]);
				this.address = lineArray[2];
				this.rating = Integer.parseInt(lineArray[3]);
				String[] map = lineArray[4].split(",");
				this.mapx = Integer.parseInt(map[0]);
				this.mapy = Integer.parseInt(map[1]);

				// second line of the destination read in
				// holds the names of the neighbors
				this.line = bufferedReader.readLine();
				String[] line2Array = this.line.split(":");
				this.neighborNames = new String[line2Array.length];
				int count = 0;
				for (String names : line2Array) {
					this.neighborNames[count] = names;
					count++;
				}

				// third line of the destination
				// holds the distance cost of each of the neighbors
				this.line = bufferedReader.readLine();
				String[] line3Array = this.line.split(":");
				this.neighborDistCost = new int[line3Array.length];
				count = 0;
				for (String distance : line3Array) {
					this.neighborDistCost[count] = Integer.parseInt(distance);
					count++;
				}

				// fourth line of the destination
				// holds the time cost of each of the neighbors
				this.line = bufferedReader.readLine();
				String[] line4Array = this.line.split(":");
				this.neighborTimeCost = new int[line4Array.length];
				count = 0;
				for (String timecost : line4Array) {
					this.neighborTimeCost[count] = Integer.parseInt(timecost);
					count++;
				}
				Destination dest = this.makeDestination();
				System.out.println(dest.name);
				this.newgraph.insert(dest);

			}
			bufferedReader.close();

		} catch (FileNotFoundException exception) {
			System.out.println("error loading the program, missing files");
			exception.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		this.makeConnections(this.newgraph);
	}

	private void makeConnections(Graph newgraph) {
		for (Object[] temp : this.neighbors) {

			// first element in each is the string
			String thisname = (String) temp[0];
			if (thisname == null)
				return;
			Destination thisdest = newgraph.find(thisname);

			String[] neighNames = (String[]) temp[1];
			int[] neighDist = (int[]) temp[2];
			int[] neighTime = (int[]) temp[3];
			for (int x = 0; x < neighNames.length; x++) {
				Destination a = newgraph.find(neighNames[x]);
				int thistime = neighTime[x];
				int thisdist = neighDist[x];

				Connection con = new Connection(thisdest, a, thisdist, thistime);
				System.out.println("Connection: " + thisdest.name + " -> " + a.name);
				thisdest.addConnection(con);
			}

		}
	}

	private Destination makeDestination() {

		Coordinate thisspot = new Coordinate(this.x, this.y);

		Object[] temp = { this.name, this.neighborNames, this.neighborDistCost, this.neighborTimeCost };

		this.neighbors[this.index] = temp;
		this.index++;

		Point point = new Point(this.mapx, this.mapy);

		return new Destination(thisspot, this.name, this.address, this.rating, null, point);
	}

	/**
	 * Begins generation of xml file
	 *
	 * @param args
	 */
	private static void main(String[] args) {
		DestinationFileReader xxl = new DestinationFileReader();
		Graph thisgraph = xxl.newgraph;

		try {
			xxl.write(thisgraph, "src/assets/graph.xml");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	private static void write(Graph g, String filename) throws Exception {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		encoder.writeObject(g);
		encoder.close();
	}

}
