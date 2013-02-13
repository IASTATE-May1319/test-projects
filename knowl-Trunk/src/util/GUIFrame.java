package util;

import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class GUIFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public GUIFrame() {
		super();
	}
	
	public GUIFrame(String title) {
		super(title);
	}
	
	public GUIFrame(GraphicsConfiguration gc) {
		super(gc);
	}
	
	public GUIFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}

	public void publicProcessKeyEvent(KeyEvent e) {
		processKeyEvent(e);
	}
	
	
}
