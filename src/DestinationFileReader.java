import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;



public class DestinationFileReader {
	// need to create a file reader. the fields in this class will need to be.
	public String inputFile = "src/assets/destinations.txt";
	public String line;
	public String address;
	public String name;
	public String location;
	
	// this one holds for making connections later
	public Object[][] neighbors;
	public String[] neighborNames;
	public int[] neighborTimeCost;
	public int[] neighborDistCost;
	public String[] coord;
	public int rating;
	public Double x;
	public Double y;
	public Graph newgraph;
	public int index;
	
	
	
	
	public DestinationFileReader(){
		neighbors = new Object[50][3];
		try {
			this.newgraph = new Graph();
			
			FileReader fileReader = new FileReader(inputFile);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// actually read the inforation into arrays
			while((line = bufferedReader.readLine()) != null){
				String[] lineArray = line.split(":");
				name = lineArray[0];
				location = lineArray[1];
				coord = location.split(",");
				x = Double.parseDouble(coord[0]);
				y = Double.parseDouble(coord[1]);
				address = lineArray[2];
				rating = Integer.parseInt(lineArray[3]);
				
				// second line of the destination read in
				// holds the names of the neighbors
				line = bufferedReader.readLine();
				String[] line2Array = line.split(":");
				neighborNames = new String[line2Array.length];
				int count = 0;
				for(String names : line2Array){
					neighborNames[count] = names;
					count++;
				}
				
				// third line of the destination
				// holds the distance cost of each of the neighbors
				line = bufferedReader.readLine();
				String[] line3Array = line.split(":");
				neighborDistCost = new int[line3Array.length];
				count = 0;
				for(String distance : line3Array){
					neighborDistCost[count] = Integer.parseInt(distance);
					count++;
				}
				
				
				// fourth line of the destination
				// holds the time cost of each of the neighbors
				line = bufferedReader.readLine();
				String[] line4Array = line.split(":");
				neighborTimeCost = new int[line4Array.length];
				count = 0;
				for(String timecost : line4Array){
					neighborTimeCost[count] = Integer.parseInt(timecost);
					count++;
				}
				Destination dest = this.makeDestination();
				newgraph.insert(dest);
				
				
			}
			bufferedReader.close();
			
		} catch (FileNotFoundException exception) {
			System.out.println("error loading the program, missing files");
			exception.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
		this.makeConnections(newgraph);
	}




	public void makeConnections(Graph newgraph) {
		for(Object[] temp : neighbors){
			
			// first element in each is the string
			String thisname = (String) temp[0];
			if(thisname == null) return;
			Destination thisdest = newgraph.find(thisname);
			
			String[] neighNames = (String[]) temp[1];
			int[] neighDist = (int[]) temp[2];
			int[] neighTime = (int[]) temp[3];
			for(int x = 0 ; x < neighNames.length; x++){
				Destination a = newgraph.find(neighNames[x]);
				int thistime = (int) neighTime[x];
				int thisdist = (int) neighDist[x];
				
				Connection con = new Connection(thisdest, a,thisdist,thistime);
				thisdest.addConnection(con);
			}
			
			
		}
	}




	private Destination makeDestination() {
		
		Coordinate thisspot = new Coordinate(x,y);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("src/assets/" + name + ".jpg"));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		Object[] temp = {this.name, neighborNames, neighborDistCost, neighborTimeCost};
		
		neighbors[index] = temp;
		index ++;
		
		return new Destination(thisspot, name, address, rating, img, null);
	}
	
	public static void main(String[] args){
		DestinationFileReader xxl = new DestinationFileReader();
		Graph thisgraph = xxl.newgraph;
		
		try {
			xxl.write(thisgraph, "src/assets/graph.xml");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
	}
	
	public static void write(Graph g, String filename) throws Exception {
		XMLEncoder encoder = 
				new XMLEncoder(
						new BufferedOutputStream(
								new FileOutputStream(filename)));
		encoder.writeObject(g);
		encoder.close();
	}
	
}
