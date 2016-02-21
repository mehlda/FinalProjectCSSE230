import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JFrame;


/**
 * Launches the application
 *
 * @author David Mehl.
 *         Created Feb 2, 2016.
 */
public class Main {
	private static final Dimension SIZE = new Dimension(1080, 850);
	

	/**
	 * Launches the application
	 *
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new MapFrame();
		frame.setSize(SIZE);
		frame.setTitle("Team Vicious and Delicious Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setResizable(true);
	}
}