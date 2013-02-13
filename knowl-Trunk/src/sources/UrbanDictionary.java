package sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import annotations.Uncensored;

import util.indexer.Indexer;
import util.indexer.OneLinerTag;
import util.indexer.Tag;

/**
 * A sub-class of Source that represents the particular website contained in the
 * class name
 * 
 * @author Tony Massarini
 */
@Uncensored public class UrbanDictionary extends Source {

	/**
	 * The JEditorPane displaying this sources URL.
	 */
	private JEditorPane jep;

	/**
	 * Default constructor which creates an instance of the super class Source
	 * and sets the source name to this source's name
	 */
	public UrbanDictionary() {
		super(Source.URBAN_DICTIONARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sources.Source#executeQuery(java.lang.String)
	 */
	@Override
	public @Uncensored String executeQuery(String queryString) {

		// Try to append the queryString to the site url (find results page)
		URL uDict = null;
		try {
			uDict = new URL("http://www.urbandictionary.com/define.php?term="
					+ queryString.replace(" ", "+"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Try to open the connection with the results page
		BufferedReader in = null;
		if (uDict != null) {
			try {
				in = new BufferedReader(new InputStreamReader(uDict
						.openStream()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		// Begin creating the temp file and then proceed to cleaning the site
		// input
		File dir = new File("bin//temp//");
		if (!dir.exists())
			dir.mkdir();
		File temp = null;
		try {
			// Index the site
			Indexer indexer = new Indexer(in);

			// Clean up all the garbage (ads, unnecessary text, etc.)
			indexer.removeEmptyTags();
			indexer.removeTagAndChildren("div", "id", "header_background_left");
			indexer.removeTagAndChildren("div", "id", "form");
			indexer.removeTagAndChildren("div", "id", "topnav");
			indexer.removeTagOnly("div", "id", "header_background");
			indexer.removeTagOnly("div", "id", "header_width");
			indexer.removeTagOnly("div", "id", "header");
			indexer.removeTagOnly("a", "id", "logo_anchor");
			indexer.removeTagAndChildren("div", "id", "email_pane");
			indexer.removeTagAndChildren("div", "class", "zazzle_links");
			indexer.removeTagAndChildren("td", "id", "leftist");
			indexer.removeTagAndChildren("div", "class", "innernav");
			indexer.removeTagAndChildren("div", "class", "greenery");
			indexer.removeTagAndChildren("td", "class", "word");
			indexer.removeChildOnlyOf(0, "a", "td", "class", "index");
			indexer.removeTagAndChildren("div", "id", "copyright");
			indexer.removeTagAndChildren("table", "class", "offsite");
			// indexer.removeChildOnlyOfType("a", "class", "urbantip", "div",
			// "class", "definition");
			indexer.removeTagOnly("a", "class", "urbantip");
			indexer.removeTagOnly("a", "href", "/add.php?word=" + queryString);
			indexer.removeTagAndChildren("td", "id", "image_set");
			
			Tag child = new OneLinerTag();
			child.setType("hr");
			for (Tag p : indexer.getTag("td", "class", "index")) {
				int children = p.getChildren().size();
				for (int i = 1; i < (children * 2) + 1; i += 2) {
					indexer.insertChildAtAndAbsorbChildrenOf(child, i + 1, 1,
							p);
				}
			}

			// Finish creating the temp file in the temp directory
			// Set the file to delete on exit of the virtual machine
			try {
				temp = File.createTempFile("temp", ".html", dir);
				temp.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Setup the output writer for the cleaned temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));

			// Write the clean html to the temp file
			indexer.linesToFile(out);

			// Close both the in and out file streams
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the absolute path of the temporary file with a file: type
		// protocol
		// Also remember to replace all '\' with "//", which is shown below
		@Uncensored String path = "file:/" + temp.getAbsolutePath().replace("\\", "//");
		return path;
	}

	public void setJEditorPane(JEditorPane jep) {
		this.jep = jep;
		jep.addHyperlinkListener(new LinkListener());
	}

	/**
	 * 
	 * @author Tony Massarini
	 * 
	 */
	private class LinkListener implements HyperlinkListener {

		/**
		 * 
		 */
		public LinkListener() {
		}

		public void hyperlinkUpdate(HyperlinkEvent he) {
			if (he.getEventType().equals(EventType.ACTIVATED))
				try {
					jep.setPage(executeQuery(he.getURL().toString().replace(
							"file:/define.php?term=", "")));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

}