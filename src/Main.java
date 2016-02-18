import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * TODO Put here a description of what this class does.
 *
 * @author David Mehl.
 *         Created Feb 2, 2016.
 */
public class Main {
	private static final Dimension SIZE = new Dimension(1080, 850);
	

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new MapFrame();
		frame.setSize(SIZE);
		frame.setTitle("Our Map Title");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setResizable(true);
	}
}