import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * TODO Put here a description of what this class does.
 *
 * @author David Mehl.
 *         Created Feb 9, 2016.
 */
public class TripPlanner extends JComponent {

	public TripPlanner(){
		super();
		JButton startTripButton = new JButton("Start Trip");
		this.add(startTripButton,BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public void updateDisplay(){
		
	}
}
