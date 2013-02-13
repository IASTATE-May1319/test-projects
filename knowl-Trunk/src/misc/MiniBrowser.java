package misc;

/*
 * MiniBrowser1.java
 * A test bed for the JEditorPane and a custom editor kit.
 * This extremely simple browser has a text field for typing in
 * new urls, a JEditorPane to display the HTML page, and a status
 * bar to display the contents of hyperlinks the mouse passes over.
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 * @author Tony
 *
 */
public class MiniBrowser extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JEditorPane jep;
	/**
	 * 
	 * @param startingUrl URL to render.
	 */
	public MiniBrowser(String startingUrl) {
		// Ok, first just get a screen up and visible, with an appropriate
		// handler in place for the kill window command
		super("MiniBrowser");
		setSize(700,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Now set up our basic screen components, the editor pane, the
		// text field for URLs, and the label for status and link information.
		JPanel urlPanel = new JPanel();
		urlPanel.setLayout(new BorderLayout());
		JTextField urlField = new JTextField(startingUrl);
		urlPanel.add(new JLabel("Site: "), BorderLayout.WEST);
		urlPanel.add(urlField, BorderLayout.CENTER);
		final JLabel statusBar = new JLabel(" ");
		// Here’s the editor pane configuration. It’s important to make
		// the "setEditable(false)" call. Otherwise, our hyperlinks won’t
		// work. (If the text is editable, then clicking on a hyperlink
		// simply means that you want to change the text...not follow the
		// link.)
		jep = new JEditorPane();
		jep.setEditable(false);
		try {
			jep.setPage(startingUrl);
		}
		catch(Exception e) {
			statusBar.setText("Could not open starting page. Using a blank.");
		}
		JScrollPane jsp = new JScrollPane(jep);
		// and get the GUI components onto our content pane
		getContentPane().add(jsp, BorderLayout.CENTER);
		getContentPane().add(urlPanel, BorderLayout.NORTH);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		// and last but not least, hook up our event handlers
		urlField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					jep.setPage(ae.getActionCommand());
				}
				catch(Exception e) {
					statusBar.setText("Error: " + e.getMessage());
				}
			}
		});
		jep.addHyperlinkListener(new SimpleLinkListener1(jep, urlField,
				statusBar));
	}
}
