import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

/**
 * 
 * Constructs the GUI
 *
 * @author David Mehl. Created Feb 10, 2016.
 */
public class MapFrame extends JFrame {
	private MapPanel content;
	private Graph graph;
	private RouteQueue rq;

	/**
	 * 
	 * Creates the GUI frame
	 * 
	 * @throws Exception
	 *
	 */
	public MapFrame() throws Exception {
		this.graph = read("src/assets/graph.xml");
		this.content = new MapPanel();
		super.setJMenuBar(this.content.menuBar);
		this.add(this.content);
		this.validate();
		this.setVisible(true);
		for (LinkedList<Destination> l : this.graph.destinations) {
			if (l == null)
				continue;
			for (Destination d : l) {
				System.out.println(d.name);
				d.picture = ImageIO.read(new File("src/assets/" + d.name + ".jpg"));
			}
		}
	}

	/**
	 * 
	 * Gets the graph from an xml file
	 *
	 * @param filename
	 *            name of the xml file
	 * @return the graph with destinations
	 * @throws Exception
	 *             when file is not able to be decoded for any reason
	 */
	private static Graph read(String filename) throws Exception {
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
		Graph retGraph = (Graph) decoder.readObject();
		decoder.close();
		return retGraph;
	}

	/**
	 * 
	 * Creates the main GUI panel
	 *
	 * @author David Mehl. Created Feb 10, 2016.
	 */
	public class MapPanel extends JPanel {
		private TripPlanner tripPlanner;
		private MapComponent map;
		private InformationComponent info;
		private JMenu menu;
		private JMenuBar menuBar;
		private JSplitPane full;
		private JSplitPane plannerMap;
		private JScrollPane viewer;

		/**
		 * 
		 * Constructs the main panel
		 *
		 */
		public MapPanel() {
			// Define GUI layout

			BorderLayout layout = new BorderLayout(0, 0);
			setLayout(layout);

			// Create Component objects
			this.tripPlanner = new TripPlanner();
			this.map = new MapComponent();
			this.info = new InformationComponent();
			buildMenu();

			// create viewer to use for panning
			this.viewer = new JScrollPane(this.map, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			this.viewer.setPreferredSize(new Dimension(2000, 975));
			JViewport port = this.viewer.getViewport();

			PanListener pl = new PanListener();
			port.addMouseListener(pl);
			port.addMouseMotionListener(pl);

			// Add components to GUI
			this.full = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.viewer, this.info);
			this.plannerMap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.tripPlanner, this.full);
			this.plannerMap.setOneTouchExpandable(true);
			this.plannerMap.setDividerLocation(300);
			this.full.setResizeWeight(1);

			// Provide minimum sizes for the two components in the split pane
			Dimension minimumSize = new Dimension(300, 0);
			this.tripPlanner.setMinimumSize(minimumSize);
			Dimension maximumSize = new Dimension(500, 10000);
			this.tripPlanner.setMaximumSize(maximumSize);

			minimumSize = new Dimension(300, 0);
			this.info.setMinimumSize(minimumSize);
			maximumSize = new Dimension(300, 100000);
			this.info.setMaximumSize(maximumSize);
			// this.info.setS

			this.full.setDividerLocation(1150);
			this.full.setOneTouchExpandable(true);
			this.add(this.plannerMap, BorderLayout.CENTER);
			this.validate();
		}

		/**
		 * 
		 * Controls panning of the map. Source:
		 * http://stackoverflow.com/questions/13341857/how-to-pan-an-image-using
		 * -your-mouse-in-java-swing
		 * 
		 * @author David Mehl. Created Feb 10, 2016.
		 */
		private class PanListener extends MouseAdapter {
			private final Point pp = new Point();

			@Override
			public void mouseDragged(MouseEvent e) {
				JViewport vport = (JViewport) e.getSource();
				JComponent label = (JComponent) vport.getView();
				Point cp = e.getPoint();
				Point vp = vport.getViewPosition();
				vp.translate(this.pp.x - cp.x, this.pp.y - cp.y);
				label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
				this.pp.setLocation(cp);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				this.pp.setLocation(e.getPoint());
			}

		}

		/**
		 * 
		 * Printing utility class. Source:
		 * http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://
		 * docs.oracle.com/javase/tutorial/2d/printing/examples/
		 * HelloWorldPrinter.java
		 *
		 * @author David Mehl. Created Feb 11, 2016.
		 */
		private class RoutePrinter implements Printable {

			@Override
			public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
				// We have only one page, and 'page'
				// is zero-based
				if (page > 0) {
					return NO_SUCH_PAGE;
				}

				// User (0,0) is typically outside the
				// imageable area, so we must translate
				// by the X and Y values in the PageFormat
				// to avoid clipping.
				Graphics2D g2d = (Graphics2D) g;
				g2d.translate(pf.getImageableX(), pf.getImageableY());

				// Now we perform our rendering
				String printString = MapPanel.this.tripPlanner.routeWords.getText();
				String printArray[] = printString.split("\n");
				g.drawString("Instructions:", 100, 100);
				int y = 125;
				for (String s : printArray) {
					g.drawString(s, 100, y);
					y += 25;
				}

				// tell the caller that this page is part
				// of the printed document
				return PAGE_EXISTS;

			}

		}

		/**
		 * 
		 * Builds the menu. Java tutorial used as reference
		 *
		 */
		public void buildMenu() {
			// Create the menu bar.
			JMenuItem menuItem;

			this.menuBar = new JMenuBar();

			// Build the first menu.
			this.menu = new JMenu("Admin");
			this.menu.setMnemonic(KeyEvent.VK_A);
			this.menu.getAccessibleContext()
					.setAccessibleDescription("The only menu in this program that has menu items");
			this.menuBar.add(this.menu);

			// a group of JMenuItems
			menuItem = new JMenuItem("Print Route Queue", KeyEvent.VK_I);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						System.out.println(MapFrame.this.rq.printStack());
					} catch (Exception exc) {
						System.out.println("No RouteQueue Present");
					}
				}
			});
			JMenu debug = new JMenu("Debug");
			this.menu.add(debug);
			debug.add(menuItem);
			menuItem = new JMenuItem("Insert Destination", KeyEvent.VK_I);
			menuItem.getAccessibleContext().setAccessibleDescription("Insert a Destination");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String password = "125TREE";
						String entered = "";
						while (!entered.equals(password)) {
							entered = JOptionPane.showInputDialog("Enter Admin Password: ");
						}
					} catch (Exception err) {
						JOptionPane.showMessageDialog(null, "Failure. Exiting");
						return;
					}
					try {
						String name = JOptionPane.showInputDialog("Enter the destination name:");
						String addr = JOptionPane.showInputDialog("Enter the destination address:");
						ArrayList<String> neighbors = new ArrayList<String>();
						String neighbor = "";
						while (!neighbor.equals("DONE")) {
							if (!neighbor.equals(""))
								neighbors.add(neighbor);
							neighbor = JOptionPane.showInputDialog("Enter Neighbors. Type DONE to stop");
						}
						String times = "";
						int[] timesToNeighbors = {};
						while (!times.equals("DONE")) {
							if (!times.equals(""))
								timesToNeighbors[timesToNeighbors.length] = (Integer.parseInt(times));
							times = JOptionPane.showInputDialog("Enter Time To Neighbors. Type DONE to stop");
						}
						String distances = "";
						int[] distanceToNeighbors = {};
						while (!distances.equals("DONE")) {
							if (!distances.equals(""))
								distanceToNeighbors[distanceToNeighbors.length] = (Integer.parseInt(distances));
							distances = JOptionPane.showInputDialog("Enter Distance To Neighbors. Type DONE to stop");
						}
						double lattitude = Double.parseDouble(JOptionPane.showInputDialog("Enter the Lattitude:"));
						double longitude = Double.parseDouble(JOptionPane.showInputDialog("Enter the Longitude:"));
						int interest = Integer.parseInt(JOptionPane.showInputDialog("Enter Interest Rating: "));
						int mapX = Integer.parseInt(JOptionPane.showInputDialog("Enter X coordinate of map point:"));
						int mapY = Integer.parseInt(JOptionPane.showInputDialog("Enter Y coordinate of map point:"));
						Point mapPoint = new Point(mapX, mapY);
						String[] a = {};

						MapFrame.this.graph.insert(name, new Coordinate(lattitude, longitude), addr, interest,
								neighbors.toArray(a), timesToNeighbors, distanceToNeighbors, mapPoint);
					} catch (Exception err) {
						JOptionPane.showMessageDialog(null, "Failure. Cancelling");
						return;
					}

				}
			});
			this.menu.add(menuItem);

			menuItem = new JMenuItem("Delete Destination");
			menuItem.setMnemonic(KeyEvent.VK_D);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String placeToDelete = JOptionPane.showInputDialog("Enter the destination name:");
					MapFrame.this.graph.remove(MapFrame.this.graph.find(placeToDelete));
				}
			});
			this.menu.add(menuItem);

			// TODO work on menus
			// Build second menu in the menu bar.
			this.menu = new JMenu("User");
			this.menu.setMnemonic(KeyEvent.VK_U);
			menuItem = new JMenuItem("Print Instructions");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PrinterJob job = PrinterJob.getPrinterJob();
					job.setPrintable(new RoutePrinter());
					boolean doPrint = job.printDialog();
					if (doPrint) {
						try {
							job.print();
						} catch (PrinterException pe) {
							// The job did not successfully
							// complete
						}
					}

				}
			});
			menuItem.getAccessibleContext().setAccessibleDescription("Print the route information");
			this.menu.add(menuItem);
			menuItem = new JMenuItem("Exit");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);

				}
			});
			// menuItem.setAccelerator();
			this.menu.add(menuItem);
			this.menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
			this.menuBar.add(this.menu);

		}
	}

	/**
	 * 
	 * Creates the trip planner, with map, UI, and info panel
	 *
	 * @author David Mehl. Created Feb 10, 2016.
	 */
	public class TripPlanner extends JComponent {
		private BorderLayout layout = new BorderLayout();
		private JPanel userInputPanel = new JPanel();
		private JPanel routeInfoPanel = new JPanel();
		private JPanel routeInstructionPanel = new JPanel();
		private JPanel interestingDestPanel = new JPanel();
		private JTextArea routeWords;

		@SuppressWarnings("hiding")
		private static final int WIDTH = 300;

		/**
		 * 
		 * Creates the trip planner
		 *
		 */
		public TripPlanner() {
			super();
			this.setLayout(this.layout);
			setupUserInputPanel();
			this.add(this.userInputPanel, BorderLayout.NORTH);
			setupRouteInstructionPanel();
			this.setVisible(true);
		}

		/**
		 * 
		 * Places the routes toString output into the text area
		 *
		 * @param r
		 *            route
		 */
		private void setRouteInstructionPanelText(Route r) {
			JScrollPane textScroll = (JScrollPane) this.routeInstructionPanel.getComponent(0);
			JViewport view = (JViewport) textScroll.getComponent(0);
			JTextArea text = (JTextArea) view.getView();
			text.setText(r.toString());
			System.out.println(this.routeWords.getText());
		}

		/**
		 * 
		 * Button ActionListener that displays a route instruction to the
		 * routeInstruction panel
		 *
		 * @author David Mehl. Created Feb 10, 2016.
		 */
		private class RouteButtonAction implements ActionListener {
			private Route route;

			public RouteButtonAction(Route r) {
				this.route = r;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				TripPlanner.this.remove(TripPlanner.this.routeInfoPanel);
				TripPlanner.this.add(TripPlanner.this.routeInstructionPanel, BorderLayout.CENTER);
				TripPlanner.this.setRouteInstructionPanelText(this.route);
				MapFrame.this.content.map.drawRoute(this.route);
				TripPlanner.this.repaint();
				TripPlanner.this.validate();
				MapFrame.this.validate();
			}
		}

		private class InterestButtonAction implements ActionListener {
			private Destination d;

			public InterestButtonAction(Destination dest) {
				this.d = dest;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				MapFrame.this.content.info.displayDestination(this.d);
				MapFrame.this.validate();
			}
		}

		/**
		 * 
		 * Initializes the routeInstructionPanel
		 *
		 */
		private void setupRouteInstructionPanel() {
			this.routeWords = new JTextArea("testing", 30, 25);
			this.routeWords.setEditable(false);
			JScrollPane scroll = new JScrollPane(this.routeWords, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.routeWords.setLineWrap(true);
			BorderLayout riLayout = new BorderLayout();
			this.routeInstructionPanel.setLayout(riLayout);
			this.routeInstructionPanel.add(scroll, BorderLayout.CENTER);
			JButton back = new JButton("Back");
			back.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TripPlanner.this.remove(TripPlanner.this.routeInstructionPanel);
					TripPlanner.this.add(TripPlanner.this.routeInfoPanel);
					TripPlanner.this.repaint();
					TripPlanner.this.validate();
					MapFrame.this.validate();
				}
			});
			this.routeInstructionPanel.add(back, BorderLayout.SOUTH);
		}

		/**
		 * 
		 * Places the route option buttons using the given RouteQueue
		 *
		 * @param rQ
		 *            routeQueue to get routes from
		 */
		private void getRoutesAndPlaceButtons(RouteQueue rQ) {
			GridLayout rifLayout = new GridLayout(0, 1);
			this.routeInfoPanel.setLayout(rifLayout);
			this.routeInfoPanel.setVisible(true);
			try {
				buildAndAddButton(rQ.poll(), 1);
			} catch (Exception e) {
				JLabel notPossible = new JLabel("<html><h1>No Possible Routes</h1><p>Try Different Parameters</p>");
				notPossible.setEnabled(true);
				this.routeInfoPanel.add(notPossible);
			}
			try {
				buildAndAddButton(rQ.poll(), 2);
				buildAndAddButton(rQ.poll(), 3);
			} catch (Exception e) {
				// do nothing, there's no more routes
			}
		}

		/**
		 * 
		 * Creates an HTML formatted string, places it into a button, and places
		 * that button onto the routeInfoPanel
		 *
		 * @param r
		 *            route
		 * @param routeNumber
		 *            number for labeling the route
		 */
		private void buildAndAddButton(Route r, int routeNumber) {
			String formatting = "<html>" + "<body style= width: " + WIDTH + "'>";
			String routeLabel = "<h1>Route " + routeNumber + "</h1>";
			String routeDistance = "<p>Distance: " + r.distanceCost + "</p>";
			String routeTime = "<p>Time: " + r.timeCost + "</p>";
			String routeInterest = "<p>Interest: " + r.interestCost + "</p>";
			String buttonText = formatting + routeLabel + routeDistance + routeTime + routeInterest;
			JButton button = new JButton(buttonText);
			button.addActionListener(new RouteButtonAction(r));
			this.routeInfoPanel.add(button);
		}

		/**
		 * 
		 * Builds the User Input panel of the TripPlanner
		 *
		 */
		private void setupUserInputPanel() {
			GridLayout bui = new GridLayout(0, 2);
			this.userInputPanel.setLayout(bui);
			JButton startTripButton = new JButton("Start Trip");
			final JTextField start = new JTextField();
			final JTextField waypoints = new JTextField();
			final String defaultWaypoints = "Waypt1:Waypt2:W...";
			waypoints.setText(defaultWaypoints);
			waypoints.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// none

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// none

				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (waypoints.getText().equals(""))
						waypoints.setText(defaultWaypoints);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// none

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (waypoints.getText().equals(defaultWaypoints))
						waypoints.setText("");

				}
			});
			final JRadioButton distance = new JRadioButton("Shortest Distance", true);
			final JRadioButton time = new JRadioButton("Fastest Time");
			final JRadioButton interest = new JRadioButton("Interest");
			final ButtonGroup timeOrDistanceOrInterest = new ButtonGroup();
			timeOrDistanceOrInterest.add(distance);
			timeOrDistanceOrInterest.add(time);
			timeOrDistanceOrInterest.add(interest);

			final JTextField maxWaypoints = new JTextField();
			final JTextField destination = new JTextField();
			startTripButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					TripPlanner.this.routeInfoPanel.removeAll();
					TripPlanner.this.remove(TripPlanner.this.interestingDestPanel);
					RouteQueue.Cost function;
					if (time.isSelected()) {
						function = RouteQueue.Cost.TIME;
					} else if (distance.isSelected()) {
						function = RouteQueue.Cost.DISTANCE;
					} else {
						function = RouteQueue.Cost.INTEREST;
					}
					int maxDest;
					try {
						maxDest = Integer.parseInt(maxWaypoints.getText());
					} catch (Exception expc) {
						maxDest = -1;
					}
					if (!start.getText().equals("") && !destination.getText().equals("")) {
						if (!waypoints.getText().equals(defaultWaypoints)) {
							MapFrame.this.rq = MapFrame.this.graph.getRouteQueue(start.getText(), destination.getText(),
									waypoints.getText().split(":"), function, maxDest);
							TripPlanner.this.getRoutesAndPlaceButtons(MapFrame.this.rq);
						} else {
							MapFrame.this.rq = MapFrame.this.graph.getRouteQueue(start.getText(), destination.getText(),
									null, function, maxDest);
							TripPlanner.this.getRoutesAndPlaceButtons(MapFrame.this.rq);
						}
					}
					MapFrame.this.content.info.displayDestination(MapFrame.this.graph.find(destination.getText()));
					TripPlanner.this.add(TripPlanner.this.routeInfoPanel);
					TripPlanner.this.validate();

				}
			});

			this.userInputPanel.add(new JLabel("Trip Planner"));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JLabel("Start: "));
			this.userInputPanel.add(start);
			this.userInputPanel.add(new JLabel("Waypoints: "));
			this.userInputPanel.add(waypoints);
			this.userInputPanel.add(new JLabel("Destination: "));
			this.userInputPanel.add(destination);
			this.userInputPanel.add(new JLabel("Select Criteria:"));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(distance);
			this.userInputPanel.add(time);
			this.userInputPanel.add(interest);
			maxWaypoints.setText("Max Waypoints");
			maxWaypoints.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// none
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// none
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (maxWaypoints.getText().equals("")) {
						maxWaypoints.setText("Max Waypoints");
					}

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (maxWaypoints.getText().equals("Max Waypoints")) {
						maxWaypoints.setText("");
					}

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// none

				}
			});
			this.userInputPanel.add(maxWaypoints);
			this.userInputPanel.add(startTripButton);
			JButton clear = new JButton("Clear");
			this.userInputPanel.add(clear);
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JLabel(""));
			final JTextField minInterestRating = new JTextField("Minimum Interest");
			clear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					start.setText("");
					waypoints.setText(defaultWaypoints);
					destination.setText("");
					maxWaypoints.setText("Max Waypoints");
				}
			});
			minInterestRating.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// none
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// none
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (minInterestRating.getText().equals(""))
						minInterestRating.setText("Minimum Interest");
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (minInterestRating.getText().equals("Minimum Interest"))
						minInterestRating.setText("");
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// none
				}
			});
			final JButton interestButton = new JButton("Search By Interest");
			interestButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int minInterest = 0;
					try {
						minInterest = Integer.parseInt(minInterestRating.getText());
					} catch (Exception excp) {
						return;
					}
					LinkedList<Destination> dest = MapFrame.this.graph.getAllDestinations();
					for (int k = dest.size() - 1; k > -1; k--) {
						if (dest.get(k).rating < minInterest) {
							dest.remove(k);
						}
					}
					LinkedList<Destination> mostInteresting = new LinkedList<Destination>();
					int finishedSize = dest.size();
					while (mostInteresting.size() < finishedSize) {
						Destination bestSoFar = dest.get(dest.size() - 1);
						int bestRatingSoFar = bestSoFar.rating;
						for (int j = dest.size() - 2; j > -1; j--) {
							Destination underTest = dest.get(j);
							if (underTest.rating > bestRatingSoFar) {
								bestRatingSoFar = underTest.rating;
								bestSoFar = underTest;
							}
						}
						dest.remove(bestSoFar);
						mostInteresting.add(bestSoFar);

					}
					for (int l = 0; l < mostInteresting.size(); l++)
						System.out.println("" + mostInteresting.get(l).toString() + "" + mostInteresting.get(l).rating);
					TripPlanner.this.interestingDestPanel.removeAll();
					TripPlanner.this.interestingDestPanel.setLayout(new GridLayout(0, 1));
					for (int b = 0; b < mostInteresting.size(); b++) {
						JButton button = new JButton(mostInteresting.get(b).name);
						button.addActionListener(new InterestButtonAction(mostInteresting.get(b)));
						TripPlanner.this.interestingDestPanel.add(button);
					}
					TripPlanner.this.remove(TripPlanner.this.routeInfoPanel);
					TripPlanner.this.remove(TripPlanner.this.routeInstructionPanel);
					TripPlanner.this.add(TripPlanner.this.interestingDestPanel);
					TripPlanner.this.validate();
				}
			});
			this.userInputPanel.add(minInterestRating);
			this.userInputPanel.add(interestButton);
			this.userInputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			this.userInputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		}
	}

	/**
	 * 
	 * Map for the trip planner
	 *
	 * @author David Mehl. Created Feb 10, 2016.
	 */
	public class MapComponent extends JPanel {
		private BufferedImage image;
		private Route r = null;

		/**
		 * 
		 * Creates the map component
		 *
		 */
		public MapComponent() {
			try {
				this.image = ImageIO.read(new File("src/assets/capitalsMap.jpg"));
			} catch (IOException exception) {
				// nothing
			}
			this.setSize(5000, 5000);
			this.image = scale(this.image, BufferedImage.TYPE_INT_RGB, this.image.getWidth() * 2,
					this.image.getHeight() * 2, 1.45, 1.45);
			this.setLayout(null);
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub.

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub.

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub.

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub.

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub.
					System.out.println(e.getX());
					System.out.println(e.getY());

				}
			});
			//
			// JLabel olymp = new JLabel("Olympia");
			// this.add(olymp);
			// olymp.setLocation(98,45);
			// olymp.setSize(50,20);
			// olymp.setForeground(Color.RED);
			// olymp.addMouseListener(new MouseListener() {
			//
			// @Override
			// public void mouseReleased(MouseEvent e) {
			// // none
			//
			// }
			//
			// @Override
			// public void mousePressed(MouseEvent e) {
			// // none
			//
			// }
			//
			// @Override
			// public void mouseExited(MouseEvent e) {
			// // none
			//
			// }
			//
			// @Override
			// public void mouseEntered(MouseEvent e) {
			// MapFrame.this.content.info.displayDestination(MapFrame.this.graph.find("Olympia"));
			// MapFrame.this.content.info.validate();
			//
			// }
			//
			// @Override
			// public void mouseClicked(MouseEvent e) {
			// // none
			//
			// }
			// });

			LinkedList<Destination> dest = MapFrame.this.graph.getAllDestinations();
			for (Destination d : dest) {
				JLabel label = new JLabel("<html><span style= 'font-size:12px'>" + d.name + "</span></html>");
				this.add(label);
				label.setForeground(Color.RED);

				label.setLocation(d.mapPoint);
				label.setSize(80, 35);
				//label.setFont(new Font("Arial",Font.PLAIN,5));
				label.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent e) {
						// none

					}

					@Override
					public void mousePressed(MouseEvent e) {
						// none

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// none

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						MapFrame.this.content.info.displayDestination(d);

					}

					@Override
					public void mouseClicked(MouseEvent e) {
						// none

					}
				});
			}

			this.repaint();

			// TODO determine if we have time to implement panning properly
			// this.viewer = new JScrollPane(this.map,
			// ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			// this.secondViewer = new JScrollPane(this.destinationLabels,
			// ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			//
			// this.viewer.setPreferredSize(new Dimension(2000, 975));
			// JViewport port = this.viewer.getViewport();
			// JViewport port2 = this.secondViewer.getViewport();
			//
			// PanListener pl = new PanListener();
			// port.addMouseListener(pl);
			// port2.addMouseListener(pl);
			// port.addMouseMotionListener(pl);
			// port2.addMouseMotionListener(pl);
			this.validate();
		}

		/**
		 * Draws the route on this JPanel
		 *
		 * @param r
		 *            route to be drawn
		 */
		public void drawRoute(Route r) {
			this.r = r;
			this.repaint();
		}

		/**
		 * Scales the image, source:
		 * http://stackoverflow.com/questions/15558202/how-to-resize-image-in-
		 * java
		 * 
		 * @param sbi
		 *            image to scale
		 * @param imageType
		 *            type of image
		 * @param dWidth
		 *            width of destination image
		 * @param dHeight
		 *            height of destination image
		 * @param fWidth
		 *            x-factor for transformation / scaling
		 * @param fHeight
		 *            y-factor for transformation / scaling
		 * @return scaled image
		 */
		private BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth,
				double fHeight) {
			BufferedImage dbi = null;
			if (sbi != null) {
				dbi = new BufferedImage(dWidth, dHeight, imageType);
				Graphics2D g = dbi.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
				g.drawRenderedImage(sbi, at);
			}
			return dbi;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(this.image, 0, 0, null);
			Graphics2D g2 = (Graphics2D) g;
			if (this.r != null) {
				Destination prev = this.r.get(0);
				for (int i = 1; i < this.r.size(); i++) {
					g2.setColor(Color.CYAN);
					Line2D.Double line = new Line2D.Double(prev.mapPoint, this.r.get(i).mapPoint);
					prev = this.r.get(i);
					g2.setStroke(new BasicStroke(10));
					g2.draw(line);
				}
			}
		}
	}

	/**
	 * 
	 * Displays the destination in the info pane
	 *
	 * @author David Mehl. Created Feb 10, 2016.
	 */
	public class InformationComponent extends JPanel {
		private BorderLayout icLayout;
		private WebView browser;
		private JFXPanel jfx;
		private JPanel info;

		/**
		 * 
		 * Creates the info pane
		 *
		 */
		public InformationComponent() {
			super();

			this.icLayout = new BorderLayout();
			this.setLayout(this.icLayout);
			this.setBounds(500, 500, 300, 300);
			try {
				ImageIO.read(new File("src/assets/Detroit.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			this.jfx = new JFXPanel();
			Platform.runLater(() -> {

				this.browser = new WebView();
				this.jfx.setScene(new Scene(this.browser));
				this.browser.getEngine().load("https://en.wikipedia.org/wiki/");
				this.jfx.validate();

			});

			this.validate();
		}

		/**
		 * 
		 * Displays the given destination on this infoPane
		 *
		 * @param d
		 *            destination
		 */
		public void displayDestination(Destination d) {
			this.removeAll();
			String name = "<html>" + "<h1>" + d.name + "</h1>";
			this.info = new JPanel();
			this.info.setLayout(new GridLayout(10, 1));
			this.info.add(new JLabel(name));
			this.info.add(new JLabel(new ImageIcon(d.picture)));
			this.info.add(new JLabel("State: \n" + d.address));
			this.info.add(new JLabel("Interest Rating: " + d.rating));
			this.info.setBounds(0, 0, 300, 300);
			this.add(this.info, BorderLayout.CENTER);
			JButton wiki = new JButton("Wikipedia");
			JButton back = new JButton("Back");
			back.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					InformationComponent.this.remove(InformationComponent.this.jfx);
					InformationComponent.this.remove(back);
					InformationComponent.this.add(InformationComponent.this.info, BorderLayout.CENTER);
					InformationComponent.this.add(wiki, BorderLayout.SOUTH);

					InformationComponent.this.validate();
					InformationComponent.this.repaint();
				}
			});
			wiki.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					InformationComponent.this.remove(InformationComponent.this.info);
					InformationComponent.this.remove(wiki);

					// InformationComponent.this.jfx = new JFXPanel();
					// InformationComponent.this.browser.
					Platform.runLater(() -> {

						// InformationComponent.this.browser = new WebView();
						// InformationComponent.this.jfx.setScene(new
						// Scene(InformationComponent.this.browser));
						InformationComponent.this.browser.getEngine().load("https://en.wikipedia.org/wiki/" + d.name);
						InformationComponent.this.jfx.validate();

					});

					InformationComponent.this.add(InformationComponent.this.jfx, BorderLayout.CENTER);
					InformationComponent.this.add(back, BorderLayout.SOUTH);
					InformationComponent.this.validate();
					InformationComponent.this.repaint();
				}
			});
			this.add(wiki, BorderLayout.SOUTH);
			this.validate();
		}
	}
}
