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

import util.indexer.Indexer;
import util.indexer.OneLinerTag;
import util.indexer.Tag;

/**
 * A sub-class of Source that represents the particular website contained in the
 * class name
 * 
 * @author Tony Massarini
 */
public class Lyrics extends Source {

	/**
	 * The JEditorPane displaying this sources URL.
	 */
	private JEditorPane jep;

	/**
	 * Default constructor which creates an instance of the super class Source
	 * and sets the source name to this source's name
	 */
	public Lyrics() {
		super(Source.LYRICS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sources.Source#executeQuery(java.lang.String)
	 */
	@Override
	public String executeQuery(String queryString) {

		// Try to append the queryString to the site url (find results page)
		URL uLyrics = null;
		try {
			if (!queryString.contains("file:/")) { // if they want to do a new search
			uLyrics = new URL("http://search.azlyrics.com/search.php?q="
					+ queryString.replace(" ", "+"));
			} else { // if they went to the next search page
				uLyrics = new URL(queryString.replaceFirst("file:/", "http://search.azlyrics.com/").replaceAll(";", ""));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Try to open the connection with the results page
		BufferedReader in = null;
		if (uLyrics != null) {
			try {
				in = new BufferedReader(new InputStreamReader(uLyrics
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
			//Indexer indexer = new Indexer(in);

			// Clean up all the garbage (ads, unnecessary text, etc.)
			//indexer.removeEmptyTags();
			//indexer.removeTagAndChildren("input", "id", "predictad_result_count");
			

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
			//indexer.linesToFile(out);
			String tempstr = null;
			boolean script = false;
			while(in.ready() ) {
				//out.write(in.readLine());
				tempstr = in.readLine();
				if (tempstr.contains("<script") || tempstr.contains("<iframe") || tempstr.contains("<ul>") || tempstr.contains("<form")) {
					script = true;
				}
				if (!script) {
					out.write(tempstr);
				}
				if (tempstr.contains("script>") || tempstr.contains("iframe>") || tempstr.contains("</ul>") || tempstr.contains("form>")) {
					script = false;
				}
			}

			// Close both the in and out file streams
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the absolute path of the temporary file with a file: type
		// protocol
		// Also remember to replace all '\' with "//", which is shown below
		return "file:/" + temp.getAbsolutePath().replace("\\", "//");
	}

	public void setJEditorPane(JEditorPane jep) {
		this.jep = jep;
		jep.addHyperlinkListener(new LinkListener());
	}
	
	public String executeQueryLyrics(String inURL) {
			
			// Try to append the queryString to the site url (find results page)
			URL uLyric = null;
			try {
				 uLyric = new URL(inURL);
			} catch(MalformedURLException e) {
				e.printStackTrace();
			}
			
			// Try to open the connection with the results page
			BufferedReader in = null;
			if(uLyric != null) {
				try {
					in = new BufferedReader(
								new InputStreamReader(
								uLyric.openStream()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			// Begin creating the temp file and then proceed to cleaning the site input
			File dir = new File("bin//temp//");
			File temp = null;
			try {
				// Index the site
				Indexer indexer = new Indexer(in);
				
				// Clean up all the garbage (ads, unnecessary text, etc.)
				indexer.removeEmptyTags();
				indexer.removeType("style");
				indexer.removeType("img");
				indexer.removeType("form");
				indexer.removeType("input");
				indexer.removeTagAndChildren("img", "class", "InlineImg");
				indexer.removeTagAndChildren("a", "href", "http://www.azlyrics.com/");
				indexer.removeTagAndChildren("a", "href", "http://www.azlyrics.com/add.php");
				indexer.removeTagAndChildren("a", "href", "http://www.stlyrics.com/");
				indexer.removeTagAndChildren("a", "href", "http://www.azvideos.com/");
				indexer.removeTagAndChildren("div", "id", "SkyCraper");
				indexer.removeTagAndChildren("div", "id", "HeaderWhite");
				indexer.removeTagAndChildren("td", "class", "SearchForm");
//				indexer.removeTagAndChildren("div", "class", "spl_unshd");
//				indexer.removeTagAndChildren("span", "class", "pronset");
//				indexer.removeTextTag(" ");
//				indexer.removeTagAndChildren("div", "class", "Lsentnce");
//				indexer.removeTagAndChildren("div", "id", "imgres");
//				indexer.removeTagAndChildren("div", "id", "link_to_div");
//				indexer.removeTagAndChildren("a", "id", "link_to_style");
//				indexer.removeTagAndChildren("div", "id", "webres");
//				indexer.removeTagAndChildren("div", "class", "sep_top");
//				indexer.removeTagAndChildren("a", "rel", "nofollow");
//				indexer.removeTagAndChildren("div", "class", "fC ccF");
//				indexer.removeTypeOnly("a");
//				indexer.removeTagAndChildren("div", "class", "sep_top shd_hdr");
//				indexer.removeTagAndChildren("div", "class", "sr sw");
//				indexer.removeTagAndChildren("div", "class", "bca saw");
//				indexer.removeTagAndChildren("div", "id", "ltm");
//				indexer.removeChildOf(1, "text", "td", "class", "td1");
//				Tag child = new OneLinerTag();
//				child.setType("hr");
//				Tag parent = indexer.getTag("div", "class", "sep_top shd_hdr ").get(0).getParent();
//				int children = parent.getChildren().size();
//				for(int i = 1; i < (children * 2) + 1; i += 2) {
//					indexer.insertChildAtAndAbsorbChildrenOf(child, i, 0, parent);
//				}
//				parent = indexer.getTag("div", "class", "sep_top shd_hdr pb7").get(0).getParent();
//				children = parent.getChildren().size();
//				for(int i = 1; i < (children * 2) + 1; i += 2) {
//					indexer.insertChildAtAndAbsorbChildrenOf(child, i, 0, parent);
//				}
				
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
			
			// Return the absolute path of the temporary file with a file: type protocol
			// Also remember to replace all '\' with "//", which is shown below
			return "file:" + temp.getAbsolutePath().replace("\\", "//");
			// apparently it's automatically adding in the first "/" after "file:" due to the getAbsolutePath().
			// makes sense.
		}

	/**
	 * 
	 * @author Tony Massarini
	 * 
	 */
	class LinkListener implements HyperlinkListener {

		/**
		 * 
		 */
		public LinkListener() {
		}

		public void hyperlinkUpdate(HyperlinkEvent he) {
			if (he.getEventType().equals(EventType.ACTIVATED))
				try {
					String tempURL = he.getURL().toString();
					if (tempURL.contains("search.php")) { // next page
						jep.setPage(executeQuery(tempURL));
					} else { // lyrics page!
						jep.setPage(executeQueryLyrics(tempURL));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	}