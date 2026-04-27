package game;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GameMenu extends JPanel {
	private JButton leftButton;
	private JButton rightButton;
	private JButton centerButton;
	
	public GameMenu() {
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
		this.leftButton = new JButton("Left");
		this.rightButton = new JButton("Right");
		this.centerButton = new JButton("Center");

		this.add(this.leftButton);
		this.add(this.rightButton);
		this.add(this.centerButton);
		
//		leftButton.addActionListener(event -> {
//			//System.out.println(event.getSource());
//			System.out.println(event.getID());
//			//System.out.println(event);
//			JOptionPane.showMessageDialog(null, "Left Button Clicked");
//		});
//		rightButton.addActionListener(event -> {
//			System.out.println(event.getID());
//			JOptionPane.showMessageDialog(this, "Right Button Clicked");
//		});
//		centerButton.addActionListener(e -> {
//			JOptionPane.showConfirmDialog(null, "Are you sure?");
//		});
		
		
	}
	
	
	public JButton getLeftButton() {
		return this.leftButton;
	}

	public JButton getRightButton() {
		return this.rightButton;
	}

	public JButton getCenterButton() {
		return this.centerButton;
	}
	

}
