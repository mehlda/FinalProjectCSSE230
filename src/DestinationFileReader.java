import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class DestinationFileReader {
	// need to create a file reader. the fields in this class will need to be.
	public String inputFile = "src/destinations.txt";
	public String line;
	public String address;
	public String image;
	public String name;
	public String location;
	public String[] lineArray;
	public String[] line2Array;
	public String[] line3Array;
	public String[] line4Array;
	public String[] neighborNames;
	public Double[] neighborTimeCost;
	public Double[] neighborDistCost;
	public String[] coord;
	public int rating;
	public int x;
	public int y;
	
	
	
	
	public DestinationFileReader(){
		try {
			Graph newgraph = new Graph();
			
			FileReader fileReader = new FileReader(inputFile);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// actually read the inforation into arrays
			while((line = bufferedReader.readLine()) != null){
				lineArray = line.split(":");
				name = lineArray[0];
				location = lineArray[1];
				coord = location.split(",");
				x = Integer.parseInt(coord[0]);
				y = Integer.parseInt(coord[1]);
				address = lineArray[2];
				rating = Integer.parseInt(lineArray[3]);
				image = lineArray[4];
				
				// second line of the destination read in
				// holds the names of the neighbors
				line = bufferedReader.readLine();
				line2Array = line.split(":");
				int count = 0;
				for(String names : line2Array){
					neighborNames[count] = names;
					count++;
				}
				
				// third line of the destination
				// holds the distance cost of each of the neighbors
				line = bufferedReader.readLine();
				line3Array = line.split(":");
				count = 0;
				for(String distance : line3Array){
					neighborDistCost[count] = Double.parseDouble(distance);
					count++;
				}
				
				
				// fourth line of the destination
				// holds the time cost of each of the neighbors
				line = bufferedReader.readLine();
				line4Array = line.split(":");
				count = 0;
				for(String timecost : line4Array){
					neighborTimeCost[count] = Double.parseDouble(timecost);
					count++;
				}
				
				newgraph.insert(this.makeDestination());
				
			}
			
		} catch (FileNotFoundException exception) {
			// handling the potential read-in error from the filereader
			exception.printStackTrace();
		} catch (IOException ex){
			// handling the potential read-in error from bufferedreader
			ex.printStackTrace();
		}
	}




	private Destination makeDestination() {
		
		Coordinate thisspot = new Coordinate(x,y);
		LinkedList<Connection> x = new LinkedList();
		for(int i = 0; i < this.neighborNames.length; i++){
			Connection conn = new Connection(name,neighborNames[i], neighborDistCost[i], neighborTimeCost[i]);
		}
		
		return new Destination(coord, name, address, rating, image, x);
	}
	
}
