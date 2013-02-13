package sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;


import util.indexer.Indexer;
import util.indexer.Tag;


/** 
 * A subclass of Source that turns the source code from Thesaurus web pages
 * into a format that is easier to read and removes ads.
 * 
 * @author Brian Stauffer
 *
 */
public class Thesaurus extends Source {

	/**
	 * The JEditorPane displaying this sources URL.
	 */
	private JEditorPane jep;
	
	/**
	 * Default constructor of Thesaurus.
	 */
	public Thesaurus() {
		super(Source.THESAURUS);
	}

	
	@Override
	public String executeQuery(String queryString) {
		// Create url and BurfferedReader
		URL thesaurus = null;
		BufferedReader in;
		// Begin creating the temp file and then proceed to cleaning the site input
		File dir = new File("bin//temp//");
		File temp = null;
		BufferedWriter out = null;
		
		// Try to create the temporary file
		try {
			temp = File.createTempFile("temp", ".html", dir);
			temp.deleteOnExit();
			// Create the BufferedWriter for writing to the file
			out = new BufferedWriter(new FileWriter(temp));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			// Try to create URL with just queryString(if the actual url was sent
			// in from the link listener).  If it was not an exception will be thrown
			// and then the website will be queried for the search term
			thesaurus = new URL(queryString);
			// Get input from website
			in = new BufferedReader(new InputStreamReader(thesaurus
					.openStream()));
			// Create indexer
			Indexer indexer = new Indexer(in);
			
			// Removes unwanted code
			indexer = removeUnwanted(indexer);
			
			// Write the html code to the temporary file
			indexer.linesToFile(out);
			
			// Close the BufferedReader
			in.close();
			// Close the BufferedWriter
			out.close();
			
		}
		catch (IOException e) {
			try {
				// Base url for searches
				String url = "http://thesaurus.com/browse/";
				// Replace spaces with pluses
				queryString = queryString.replace(' ', '+');
				// Add query string to the url
				url += queryString;
				// Try to create the url
				thesaurus = new URL(url);
				// Try to open the connection with the url
				in = new BufferedReader(new InputStreamReader(thesaurus
						.openStream()));
				// Create the indexer
				Indexer indexer = new Indexer(in);
				// Removes unwanted code
				indexer = removeUnwanted(indexer);
				
				// Write to the file
				indexer.linesToFile(out);
				// Close the BufferedReader
				in.close();
				// Close the BufferedWrite
				out.close();
			}
			catch (IOException e2) {
				// print to file error message
				try {
					out.write("<html> <h3> Unable to find page </h3> </html>");
				}
				catch (IOException e3) {
					
				}
			}
		}
		return "file:/" + temp.getAbsolutePath().replace("\\", "//");
	}
	
	/**
	 * Removes all unwanted code from the web page currently being queried
	 * @param indexer Contains the html source code from the web page
	 * @return An indexer without the unwanted code in it.
	 */
	private Indexer removeUnwanted(Indexer indexer) {
		indexer.removeType("style");
		indexer.removeTagAndChildren("span", "style", "display:none");
		indexer.removeTagAndChildren("div", "class", "dc dw");
		indexer.removeTagAndChildren("div", "id", "ltm");
		indexer.removeTagAndChildren("div", "class", "rh");
		indexer.removeTagAndChildren("div", "class", "ric");
		indexer.removeTagAndChildren("div", "id", "dict_hdr");
		indexer.removeTagAndChildren("div", "id", "top_search");
		indexer.removeTagAndChildren("div", "id", "link_to_module");
		indexer.removeTagAndChildren("div", "id", "leftRail");
		indexer.removeTagAndChildren("div", "id", "v2");
		indexer.removeTagAndChildren("form", "action", "http://ask.reference.com/web");
		indexer.removeTagAndChildren("div", "id", "rightRail");
		indexer.removeTagAndChildren("div", "class", "fC ccF");
		List<Tag> helper = indexer.getTag("div", "class", "result_copyright");
		indexer.removeChildOf(helper.size() - 2, "a", "div", "class", "result_copyright");
		indexer.removeTagAndChildren("div", "id", "macnt");
		indexer.removeTagAndChildren("div", "class", "bca saw");
		return indexer;
	}
	
	@Override
	public void setJEditorPane(JEditorPane jep) {
		// TODO Auto-generated method stub
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
					String url = he.getURL().toString();
					if(url.contains("thesaurus")) {
						jep.setPage(executeQuery(he.getURL().toString()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
}