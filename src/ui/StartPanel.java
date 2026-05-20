package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Simple start screen panel shown before gameplay begins.
 *
 * Fields: title, button.
 * Methods: StartPanel().
 */
public class StartPanel extends JPanel {
	private JLabel title;
	private JLabel tutorial;
	public JButton button;

    public StartPanel() {
    	this.setLayout(new BorderLayout());
    	this.setPreferredSize(new Dimension(GameComponent.WIDTH, GameComponent.HEIGHT));

    	this.title = new JLabel("Game Start");
    	this.title.setHorizontalAlignment(SwingConstants.CENTER);
    	
    	String instructions = "Instructions: Move with WASD/Arrow Keys. Your goal is to collect all of the gems in a level, the key, and then move move to the exit.";
    	int instructionWidth = GameComponent.WIDTH - 80;
    	this.tutorial = new JLabel("<html><div style='text-align: center; width: " + instructionWidth + "px;'>" + instructions + "</div></html>");
    	this.tutorial.setHorizontalAlignment(SwingConstants.CENTER);

	    this.button = new JButton("Start");

	    JPanel centerPanel = new JPanel();
	    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	    this.title.setAlignmentX(CENTER_ALIGNMENT);
	    this.tutorial.setAlignmentX(CENTER_ALIGNMENT);
	    centerPanel.add(Box.createVerticalGlue());
	    centerPanel.add(this.title);
	    centerPanel.add(this.tutorial);
	    centerPanel.add(Box.createVerticalGlue());

	    add(centerPanel, BorderLayout.CENTER);
	    add(this.button, BorderLayout.SOUTH);
    }
}
