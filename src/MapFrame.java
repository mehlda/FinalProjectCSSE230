import java.awt.BorderLayout;
import java.awt.Dimension;
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
	private static final int FRAMES_PER_SECOND = 30;
	private static final int REPAINT_INTERVAL_MS = 1000 / FRAMES_PER_SECOND;
	private MapPanel content;
	private Graph graph;

	/**
	 * 
	 * Creates the GUI frame
	 * @throws Exception 
	 *
	 */
	public MapFrame() throws Exception {
		// TODO have graph read xml file
		this.graph = read("src/assets/graph.xml");
		this.content = new MapPanel();
		super.setJMenuBar(this.content.menuBar);
		this.add(this.content);
		this.validate();
		this.setVisible(true);
	}

	/**
	 * 
	 * Gets the graph from an xml file
	 *
	 * @param filename name of the xml file
	 * @return the graph with destinations
	 * @throws Exception when file is not able to be decoded for any reason
	 */
	@SuppressWarnings("unused")
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

		/**
		 * 
		 * Constructs the main panel
		 *
		 */
		public MapPanel() {
			// Define GUI layout

			BorderLayout layout = new BorderLayout(0, 0);
			setLayout(layout);

			// Create Componenet objects
			this.tripPlanner = new TripPlanner();
			this.map = new MapComponent();
			this.info = new InformationComponent();
			buildMenu();

			// Add components to GUI
			this.full = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.map, this.info);
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

			// This should be only thread in program
			Runnable repainter = new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							Thread.sleep(REPAINT_INTERVAL_MS);
							timePassed();
						}
					} catch (InterruptedException e) {
						// TODO determine if we need handling
					}
				}
			};
			new Thread(repainter).start();
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
				g.drawString(MapPanel.this.tripPlanner.routeWords.getText(), 100, 100);

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
			menuItem = new JMenuItem("Insert Destination", KeyEvent.VK_I);
			menuItem.getAccessibleContext().setAccessibleDescription("Insert a Destination");
			menuItem.addActionListener(new ActionListener() {

				@SuppressWarnings("unused") // TODO remove this when insert
											// method is finalized
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
						double lattitude = Double.parseDouble(JOptionPane.showInputDialog("Enter the Lattitude:"));
						double longitude = Double.parseDouble(JOptionPane.showInputDialog("Enter the Longitude:"));
						String picAddr = JOptionPane.showInputDialog("Enter Picture Address: ");
						int interest = Integer.parseInt(JOptionPane.showInputDialog("Enter Interest Rating: "));
						System.out.println(name + " " + neighbors);
						String[] a;
						// TODO finish this insert method
						// MapFrame.this.graph.insert(name,new
						// Coordinate(lattitude,longitude),addr,interest,ImageIO.read(new
						// File(picAddr)),neighbors.toArray(a),);
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

		/**
		 * 
		 * TODO Determine if we need this
		 *
		 */
		public void timePassed() {
			// Update graphics here
			// System.out.println("time passed");
			// MapFrame.this.validate();

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
				TripPlanner.this.repaint();
				TripPlanner.this.validate();
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
		 * TODO This function will be cut down and removed when other classes
		 * are implemented
		 *
		 */
		private void setupRouteInfoPanel() {
			GridLayout rifLayout = new GridLayout(0, 1);
			this.routeInfoPanel.setLayout(rifLayout);
			this.routeInfoPanel.setVisible(true);
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("src/assets/detPic.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			// TODO need to remove
			Destination d = new Destination(new Coordinate(0, 0), "Detroit", "123 Detroit Ave", 2, image,
					new LinkedList<Connection>());
			Route r = new Route(d);
			Route r2 = new Route();
			Route r3 = new Route();
			buildAndAddButton(r, 1);
			buildAndAddButton(r2, 2);
			buildAndAddButton(r3, 3);
		}

		/**
		 * 
		 * Places the route option buttons using the given RouteQueue
		 *
		 * @param rQ
		 *            routeQueue to get routes from
		 */
		@SuppressWarnings("unused")
		private void getRoutesAndPlaceButtons(RouteQueue rQ) {
			GridLayout rifLayout = new GridLayout(0, 1);
			this.routeInfoPanel.setLayout(rifLayout);
			this.routeInfoPanel.setVisible(true);
			buildAndAddButton(rQ.poll(), 1);
			//buildAndAddButton(rQ.poll(), 2);
			//buildAndAddButton(rQ.poll(), 3);
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
			String buttonText = formatting + routeLabel + routeDistance + routeTime;
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
			final ButtonGroup timeOrDistance = new ButtonGroup();
			timeOrDistance.add(distance);
			timeOrDistance.add(time);
			final JTextField destination = new JTextField();
			startTripButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TripPlanner.this.routeInfoPanel.removeAll();
					//TripPlanner.this.setupRouteInfoPanel(); // Remove this line
															// when other stuff
															// is implemented
					// TODO uncomment below for actual implementation
					if (!start.getText().equals("") && !destination.getText().equals("")) {
						if (!waypoints.getText().equals(defaultWaypoints)) {
							TripPlanner.this.getRoutesAndPlaceButtons(MapFrame.this.graph.getRouteQueue(start.getText(),
									destination.getText(), waypoints.getText().split(":"), time.isSelected()));
						} else {
							//String[] emptyArray = {};
							TripPlanner.this.getRoutesAndPlaceButtons(MapFrame.this.graph.getRouteQueue(start.getText(),
									destination.getText(), null, time.isSelected()));
						}
					}
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
			this.userInputPanel.add(startTripButton);
			JButton clear = new JButton("Clear");
			this.userInputPanel.add(clear);
			clear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					start.setText("");
					waypoints.setText(defaultWaypoints);
					destination.setText("");
				}
			});
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
			this.userInputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		}

		/**
		 * 
		 * TODO Determine if this is needed
		 *
		 */
		public void updateDisplay() {
			// probably not needed
		}
	}

	/**
	 * 
	 * Map for the trip planner
	 *
	 * @author David Mehl. Created Feb 10, 2016.
	 */
	public class MapComponent extends JPanel {
		private JLabel label;
		private JPanel map;
		private JScrollPane viewer;

		/**
		 * 
		 * Creates the map component
		 *
		 */
		public MapComponent() {
			this.label = new JLabel();
			this.map = new JPanel();
			this.map.setSize(new Dimension(5000, 5000));

			// TODO remove this sample image and replace with actual map
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("src/assets/mapPic.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			JLabel imageLabel = new JLabel(new ImageIcon(image));

			this.map.add(imageLabel);

			this.viewer = new JScrollPane(this.map, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			this.viewer.setPreferredSize(new Dimension(2000, 975));
			JViewport port = this.viewer.getViewport();

			this.add(this.viewer);
			this.label.setVisible(true);
			PanListener pl = new PanListener();
			port.addMouseListener(pl);
			port.addMouseMotionListener(pl);
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
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("src/assets/Detroit.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			this.jfx = new JFXPanel();
			Destination d = new Destination(new Coordinate(0, 0), "Detroit", "123 Detroit Ave", 2, image,
					new LinkedList<Connection>());
			Platform.runLater(() -> {

				this.browser = new WebView();
				this.jfx.setScene(new Scene(this.browser));
				this.browser.getEngine().load("https://en.wikipedia.org/wiki/" + d.name);

			});

			// TODO remove this sample destination and its display

			displayDestination(d);
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
			this.info.setLayout(new GridLayout(0, 1));
			this.info.add(new JLabel(name));
			this.info.add(new JLabel(new ImageIcon(d.picture)));
			this.info.add(new JLabel(d.address));
			this.info.add(new JLabel("" + d.rating));
			this.info.setBounds(0, 0, 300, 300);
			this.add(this.info, BorderLayout.CENTER);
			JButton wiki = new JButton("Wikipedia");
			JButton back = new JButton("Back");
			back.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub.
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
					Platform.runLater(() -> {

						InformationComponent.this.browser = new WebView();
						InformationComponent.this.jfx.setScene(new Scene(InformationComponent.this.browser));
						InformationComponent.this.browser.getEngine().load("https://en.wikipedia.org/wiki/" + d.name);

					});
					InformationComponent.this.add(InformationComponent.this.jfx, BorderLayout.CENTER);
					InformationComponent.this.add(back, BorderLayout.SOUTH);
					InformationComponent.this.validate();
					InformationComponent.this.repaint();
				}
			});
			// this.icLayout = new BorderLayout();

			this.add(wiki, BorderLayout.SOUTH);

			// this.add(this.jfx,BorderLayout.CENTER);

			// this.add(back);

			this.validate();

		}
	}
}
