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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class MapFrame extends JFrame {
	private static final int FRAMES_PER_SECOND = 30;
	private static final int REPAINT_INTERVAL_MS = 1000 / FRAMES_PER_SECOND;
	private static final int FONT_SIZE = 70;
	private static final Dimension SIZE = new Dimension(1080, 850);

	public MapFrame() {
		MapPanel content = new MapPanel();
		this.add(content);
		this.pack();

		this.setVisible(true);
	}

	public class MapPanel extends JPanel {
		TripPlanner tripPlanner;
		MapComponent map;
		InformationComponent info;

		public MapPanel() {
			// Define GUI layout
			BorderLayout layout = new BorderLayout(0, 0);
			setLayout(layout);

			// Create Componenet objects
			this.tripPlanner = new TripPlanner();
			this.map = new MapComponent();
			this.info = new InformationComponent();

			// Add components to GUI
			this.add(this.tripPlanner, BorderLayout.WEST);
			this.add(this.map, BorderLayout.CENTER);
			this.add(this.info, BorderLayout.EAST);

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

		public void timePassed() {
			// Update graphics here
			System.out.println("time passed");
		}

		/**
		 * Temporary graphics to visualize layout TODO:
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLUE);

			g2.setColor(Color.RED);
			g2.fill(this.map.background);
			g2.setColor(Color.GREEN);
			g2.fill(this.info.background);
		}
	}

	public class TripPlanner extends JComponent {
		private JLabel label;
		protected Shape background = new Rectangle2D.Double(0, 0, SIZE.getWidth() / 4, SIZE.getHeight());
		private BorderLayout layout = new BorderLayout();
		private GridBagConstraints c = new GridBagConstraints();
		private JPanel userInputPanel = new JPanel();
		private JPanel routeInfoPanel = new JPanel();
		private ArrayList<JButton> routeButtons = new ArrayList<JButton>();

		public TripPlanner() {
			super();
			this.setLayout(this.layout);
			setupUserInputPanel();
			this.add(this.userInputPanel, BorderLayout.NORTH);
			buildAndAddButton(50, 50, 200,10);
			setupRouteInfoPanel();
			this.add(routeInfoPanel);

			this.setVisible(true);
		}

		private void buildAndAddButton(int distance, int time, int width, int routeNumber) {
			String content1 = "<html>" + "<body style='background-color: green; width: ";
			String content2 = "'>" + "<h1>Route ";
			String content6 = "</h1>";

			String content3 = "<p>Distance: ";
			String content4 = "<p>Time: ";
			String content5 = "</p>";
			String content = content1 +  width + content2 +routeNumber + content6 + content3 + distance + content5
					+ content4 + time + content5;
			this.routeButtons.add(new JButton(content));
		}

		private void setupRouteInfoPanel() {

			GridLayout rifLayout = new GridLayout(0, 1);
			routeInfoPanel.setLayout(rifLayout);
			routeInfoPanel.add(new JButton("Test Route Info"), BorderLayout.SOUTH);
			routeButtons.add(new JButton("TestButton\nDisance:  50\nTime: 50 minutes"));
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
			this.userInputPanel.add(new JLabel("Trip Planner"));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(new JLabel("Start: "));
			this.userInputPanel.add(new JTextField());
			this.userInputPanel.add(new JLabel("Waypoints: "));
			this.userInputPanel.add(new JTextField());
			this.userInputPanel.add(new JLabel("Destination: "));
			this.userInputPanel.add(new JTextField());
			this.userInputPanel.add(new JLabel("Select Criteria:"));
			this.userInputPanel.add(new JLabel(""));
			this.userInputPanel.add(distance);
			this.userInputPanel.add(time);
			this.userInputPanel.add(startTripButton);
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

	public class InformationComponent extends JComponent {
		private JLabel label;
		protected Shape background = new Rectangle2D.Double(3 * SIZE.getWidth() / 4, 0, SIZE.getWidth() / 4,
				SIZE.getHeight());

		public InformationComponent() {
			this.label = new JLabel();
			this.label.setFont(new Font("Arial", 0, FONT_SIZE));
			this.label.setForeground(Color.RED);
			this.label.setText("Information Component");
			this.add(this.label, BorderLayout.NORTH);
			this.label.setVisible(true);
		}
	}
}
