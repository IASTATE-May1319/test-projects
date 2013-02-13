package util;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import annotations.Censored;

import sources.Source;
import sources.SourceManager;

/**
 * 
 * @author tyler johnson
 * 
 */
public class gui {

	private SourceManager guiSourceManager;
	private int numberOfSources;
	private ListIterator<Source> sourceIter;
	private JCheckBox checkBoxArray[];
	private GUIFrame mainFrame;
	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JPanel searchPanel;
	private CustomJTextField searchQueryString;
	private JButton searchButton;
	private JTabbedPane tabsPane = new JTabbedPane();
	private knowlTab[] tabArray;

	/**
	 * Creates a new Gui
	 */
	public gui() {
		// source manager
		guiSourceManager = new SourceManager();

		// gets the total number of available sources
		numberOfSources = guiSourceManager.getSources().size();

		// set the proper size of the check box array
		checkBoxArray = new JCheckBox[numberOfSources];

		// sets the proper size of the tab array
		tabArray = new knowlTab[numberOfSources];

		// iterator for the list of sources
		sourceIter = guiSourceManager.getSources().listIterator();

		// initializes all the JComponents
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		buttonPanel = new JPanel(new GridLayout(0, 3));
		searchPanel = new JPanel();
		mainFrame = new GUIFrame("Knowl");
		searchButton = new JButton("Search!");
		searchButton.addActionListener(new searchAction());
		tabsPane.setPreferredSize(new Dimension(580, 400));

		// creates all the correct buttons
		int x = 0;
		while (sourceIter.hasNext()) {
			// creates the button with the name
			String sourceName = sourceIter.next().getName();
			checkBoxArray[x] = new JCheckBox(sourceName);
			checkBoxArray[x].addActionListener(new checkAction());
			buttonPanel.add(checkBoxArray[x]);
			tabArray[x] = new knowlTab(sourceName);
			tabArray[x].tieCheckBox(checkBoxArray[x]);
			x++;

		}// while
		
		// create the search panel
		searchQueryString = new CustomJTextField(30);
		searchPanel.add(searchQueryString);
		searchPanel.add(searchButton);
		createKeyListeners();
	}

	/**
	 * Launches the gui. This method must be called to get the gui to show up.
	 */
	public void launch() {
		for (int i = 0; i < numberOfSources; i++) {

			buttonPanel.add(checkBoxArray[i]);
			tabsPane.addTab(tabArray[i].getName(), tabArray[i].getTab());

		}
		mainFrame.setSize(new Dimension(600, 600));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add components to mainPanel
		mainPanel.add(searchPanel);
		mainPanel.add(buttonPanel);
		mainPanel.add(tabsPane);

		// add main panel to the main frame
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);

	}

	/**
	 * method used to enable or disable tabs depending on what checkboxes have
	 * been selected
	 */
	private void updateTabs() {
		for (int x = 0; x < numberOfSources; x++) {
			if (!tabArray[x].isChecked()) {
				tabsPane.setEnabledAt(x, false);
			} else
				tabsPane.setEnabledAt(x, true);

		}

	}

	/**
	 * Search method for the gui
	 */
	private void search() {
		String query = new String();
		query = searchQueryString.getText();

		if (query.isEmpty())
			return;

		else {
			List<Source> searchSources;
			searchSources = guiSourceManager.getSources();
			ListIterator<Source> searchIter = searchSources.listIterator();

			boolean firstFound = false;
			for (int i = 0; i < numberOfSources; i++) {
				if (tabArray[i].isChecked()) {
					if (!firstFound) {
						tabsPane.setSelectedIndex(i);
						firstFound = true;
					}
					Source cur = searchIter.next();
					cur.setJEditorPane(tabArray[i].getJep());
					String path = cur.executeQuery(query);

					doLoadCommand(tabArray[i].getJep(), path);
					tabArray[i].setVisible(true);
					tabArray[i].getTab().validate();
				} else
					searchIter.next();
			}
		}

	}

	public static void doLoadCommand(JEditorPane jep, @Censored String filename) {
		jep.setEditable(false);
		try {
			String os = System.getProperty("os.name");
			if (os.equalsIgnoreCase("linux")) {
				jep.setPage(filename.replaceFirst("//", "/"));
			} else
				jep.setPage(filename);
			// jep.setPage("file:/C://Users//Jesse//Documents//My Dropbox//workspace//knowl-Trunk//bin//temp//test.html");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not open starting page. Using a blank.");
		}
	}

	/**
	 * 
	 * @author johnson The action listener for all the check box buttons
	 */
	private class checkAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			updateTabs();
		}

	}

	private class searchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			search();
		}

	}

	private void createKeyListeners() {
		mainFrame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					System.exit(0);
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});
		searchQueryString.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			public void keyTyped(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					search();
				} else
					mainFrame.publicProcessKeyEvent(e);
			}
		});
		for (JCheckBox box : checkBoxArray)
			box.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}

				@Override
				public void keyReleased(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}

				@Override
				public void keyTyped(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}

			});
		searchButton.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

		});
		tabsPane.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				mainFrame.publicProcessKeyEvent(e);
			}

		});
		
		for (int i = 0; i < tabArray.length; i++) {
			JEditorPane temp = tabArray[i].getJep();
			temp.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}

				@Override
				public void keyReleased(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}

				@Override
				public void keyTyped(KeyEvent e) {
					mainFrame.publicProcessKeyEvent(e);
				}
			});
		}
	}

}
