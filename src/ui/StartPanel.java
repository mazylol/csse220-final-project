package ui;

import java.awt.BorderLayout;
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
	public JButton button;

    public StartPanel() {
    	this.setLayout(new BorderLayout());

    	this.title = new JLabel("Game Start");
    	this.title.setHorizontalAlignment(SwingConstants.CENTER);

    	this.button = new JButton("Start");

    	add(this.title, BorderLayout.CENTER);
    	add(this.button, BorderLayout.SOUTH);
    }
}
