import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
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
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

public class MapFrame extends JFrame {
	private static final int FRAMES_PER_SECOND = 30;
	private static final int REPAINT_INTERVAL_MS = 1000 / FRAMES_PER_SECOND;
	private static final int FONT_SIZE = 70;
	private static final Dimension SIZE = new Dimension(1080, 850);

	public MapFrame() {
		MapPanel content = new MapPanel();
		super.setJMenuBar(content.menuBar);
		this.add(content);
		this.validate();
		

		this.setVisible(true);
	}

	public class MapPanel extends JPanel {
		TripPlanner tripPlanner;
		MapComponent map;
		InformationComponent info;
		JMenu menu;
		JMenuBar menuBar;

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
			this.add(this.tripPlanner, BorderLayout.WEST);
			this.add(this.map, BorderLayout.CENTER);
			this.add(this.info, BorderLayout.EAST);
			this.validate();

			// This should be only thread in program
			Runnable repainter = new Runnable() {
				public void run() {
					try {
						while (true) {
							Thread.sleep(REPAINT_INTERVAL_MS);
							timePassed();
						}
					} catch (InterruptedException e) {
					}
				}
			};
			new Thread(repainter).start();
		}

		public void buildMenu() {
			// Create the menu bar.
			JMenuItem menuItem;

			menuBar = new JMenuBar();

			// Build the first menu.
			menu = new JMenu("Admin");
			menu.setMnemonic(KeyEvent.VK_A);
			menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
			menuBar.add(menu);

			// a group of JMenuItems
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
						ArrayList<String> neighbors = new ArrayList<String>();
						String neighbor = "";
						while (!neighbor.equals("DONE")) {
							if (!neighbor.equals(""))
								neighbors.add(neighbor);
							neighbor = JOptionPane.showInputDialog("Enter Neighbors. Type DONE to stop");
						}
						String imageLocation = JOptionPane.showInputDialog("Enter Picture Address: ");
						int rating = Integer.parseInt(JOptionPane.showInputDialog("Enter Interest Rating: "));
						int xCoord = Integer.parseInt(JOptionPane.showInputDialog("Enter x Coordinate: "));
						int yCoord = Integer.parseInt(JOptionPane.showInputDialog("Enter y Coordinate: "));
						System.out.println(name + " " + neighbors);
					} catch (Exception err) {
						JOptionPane.showMessageDialog(null, "Failure. Cancelling");
						return;
					}

				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Delete Destination");
			menuItem.setMnemonic(KeyEvent.VK_D);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = JOptionPane.showInputDialog("Enter the destination name:");

				}
			});
			menu.add(menuItem);

			// Build second menu in the menu bar.
			menu = new JMenu("User");
			menu.setMnemonic(KeyEvent.VK_N);
			menuItem = new JMenuItem("Print Instructions");
			menuItem.getAccessibleContext().setAccessibleDescription("Print the route information");
			menu.add(menuItem);
			menuItem = new JMenuItem("Other");
			menu.add(menuItem);
			menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
			menuBar.add(menu);

		}

		public void timePassed() {
			// Update graphics here
			// System.out.println("time passed");
			MapFrame.this.validate();
		}

		/**
		 * Temporary graphics to visualize layout TODO:
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

		}
	}

	public class TripPlanner extends JComponent {
		private JLabel label;
		protected Shape background = new Rectangle2D.Double(0, 0, SIZE.getWidth() / 4, SIZE.getHeight());
		private BorderLayout layout = new BorderLayout();
		private GridBagConstraints c = new GridBagConstraints();
		private JPanel userInputPanel = new JPanel();
		private JPanel routeInfoPanel = new JPanel();
		private JPanel routeInstructionPanel = new JPanel();
		private ArrayList<JButton> routeButtons = new ArrayList<JButton>();
		private JTextArea routeWords;

		private static final int WIDTH = 300;

		public TripPlanner() {
			super();
			this.setLayout(this.layout);
			setupUserInputPanel();
			this.add(this.userInputPanel, BorderLayout.NORTH);

			setupRouteInfoPanel();
			setupRouteInstructionPanel();
			this.add(routeInfoPanel, BorderLayout.CENTER);

			this.setVisible(true);
		}
		
		private void setRouteInstructionPanelText(Route r){
			JScrollPane textScroll = (JScrollPane) this.routeInstructionPanel.getComponent(0);
			JViewport view = (JViewport) textScroll.getComponent(0);
			JTextArea text = (JTextArea) view.getView();
			text.setText(r.toString());
		}

		private class RouteButtonAction implements ActionListener {
			Route route;

			public RouteButtonAction(Route r) {
				this.route = r;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				TripPlanner.this.remove(TripPlanner.this.routeInfoPanel);
				TripPlanner.this.add(TripPlanner.this.routeInstructionPanel, BorderLayout.CENTER);
				TripPlanner.this.setRouteInstructionPanelText(route);
				TripPlanner.this.validate();
			}
		}

		private void setupRouteInstructionPanel() {
			this.routeWords = new JTextArea("testing", 30, 25);
			this.routeWords.setEditable(false);
			JScrollPane scroll = new JScrollPane(this.routeWords, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.routeWords.setLineWrap(true);
			BorderLayout riLayout = new BorderLayout();
			this.routeInstructionPanel.setLayout(riLayout);
			this.routeInstructionPanel.add(scroll,BorderLayout.CENTER);
			JButton back = new JButton("Back");
			back.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					TripPlanner.this.remove(TripPlanner.this.routeInstructionPanel);
					TripPlanner.this.add(TripPlanner.this.routeInfoPanel);
					TripPlanner.this.validate();
					
				}
			});
			this.routeInstructionPanel.add(back,BorderLayout.SOUTH);
		}

		private void buildAndAddButton(Route r, int routeNumber) {
			String formatting = "<html>" + "<body style= width: " + WIDTH + "'>";
			String routeLabel = "<h1>Route " + routeNumber + "</h1>";
			String routeDistance = "<p>Distance: " + r.distanceCost + "</p>";
			String routeTime = "<p>Time: " + r.timeCost + "</p>";
			String buttonText = formatting + routeLabel + routeDistance + routeTime;
			JButton button = new JButton(buttonText);
			button.addActionListener(new RouteButtonAction(r));
			this.routeButtons.add(button);
		}

		private void setupRouteInfoPanel() {

			GridLayout rifLayout = new GridLayout(0, 1);
			routeInfoPanel.setLayout(rifLayout);
			routeInfoPanel.setVisible(true);
			BufferedImage image = null;
			try {
				image = ImageIO
						.read(new File("C:/Users/David Mehl/Documents/GitHub/FinalProjectCSSE230/src/detPic.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			Destination d = new Destination(new Coordinate(0, 0), "Detroit", "123 Detroit Ave", 2, image,
					new LinkedList<Connection>());
			Route r = new Route(d);
			Route r2 = new Route();
			Route r3 = new Route();
			buildAndAddButton(r, 1);
			buildAndAddButton(r2,2);
			buildAndAddButton(r3,3);
			
			// for (int k = 0; k < 5; k++) {
			// buildAndAddButton((k + 1) * 50, (k + 1) * 35, k + 1);
			// }
			for (int i = 0; i < routeButtons.size(); i++)
				routeInfoPanel.add(routeButtons.get(i));
		}

		private void setupUserInputPanel() {
			GridLayout bui = new GridLayout(0, 2);
			this.userInputPanel.setLayout(bui);
			JButton startTripButton = new JButton("Start Trip");
			JRadioButton distance = new JRadioButton("Shortest Distance", true);
			JRadioButton time = new JRadioButton("Fastest Time");
			ButtonGroup timeOrDistance = new ButtonGroup();
			timeOrDistance.add(distance);
			timeOrDistance.add(time);
			
			final JTextField start = new JTextField();
			final JTextField waypoints = new JTextField();
			final JTextField destination = new JTextField();
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
					// TODO Auto-generated method stub.
					start.setText("");
					waypoints.setText("");
					destination.setText("");
					
					
				}
			});
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JSeparator(JSeparator.HORIZONTAL));
			this.userInputPanel.add(new JSeparator(JSeparator.HORIZONTAL));

		}

		public void updateDisplay() {

		}
	}

	public class MapComponent extends JComponent {
		private JLabel label;
		protected Shape background = new Rectangle2D.Double(SIZE.getWidth() / 4, 0, SIZE.getWidth() / 2,
				SIZE.getHeight());

		public MapComponent() {
			this.label = new JLabel();
			this.label.setFont(new Font("Arial", 0, FONT_SIZE));
			this.label.setForeground(Color.BLUE);
			this.label.setText("Map Component");
			this.add(this.label, BorderLayout.NORTH);
			this.label.setVisible(true);
		}
	}

	public class InformationComponent extends JPanel {
		private JLabel label;
		BorderLayout icLayout;

		public InformationComponent() {
			super();
			this.icLayout = new BorderLayout();
			this.setLayout(icLayout);
			this.setBounds(500, 500, 300, 300);
			BufferedImage image = null;
			try {
				image = ImageIO
						.read(new File("C:/Users/David Mehl/Documents/GitHub/FinalProjectCSSE230/src/detPic.jpg"));
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			Destination d = new Destination(new Coordinate(0, 0), "Detroit", "123 Detroit Ave", 2, image,
					new LinkedList<Connection>());

			displayDestination(d);
			this.validate();
		}

		public void displayDestination(Destination d) {
			this.removeAll();
			String name = "<html>" + "<h1>" + d.name + "</h1>";
			JPanel stuff = new JPanel();
			stuff.setLayout(new GridLayout(0, 1));
			stuff.add(new JLabel(name));
			// stuff.add(new JLabel(""));
			stuff.add(new JLabel(new ImageIcon(d.picture)));
			stuff.add(new JLabel(d.address));
			stuff.add(new JLabel("" + d.rating));
			stuff.setBounds(0, 0, 300, 300);
			this.add(stuff, BorderLayout.CENTER);

		}
	}
}
