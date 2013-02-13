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


import util.indexer.Indexer;
import util.indexer.OneLinerTag;
import util.indexer.Tag;

/**
 * A sub-class of Source that represents the particular website contained 
 * in the class name
 * @author Jesse Olds
 */
public class Dictionary extends Source {
	
	/**
	 * The JEditorPane displaying this sources URL.
	 */
	private JEditorPane jep;

	/**
	 * Default constructor which creates an instance of the super class
	 * Source and sets the source name to this source's name
	 */
	public Dictionary() {
		super(Source.DICTIONARY);
	}
	
	/* (non-Javadoc)
	 * @see sources.Source#executeQuery(java.lang.String)
	 */
	@Override
	public String executeQuery(String queryString) {
		
		// Try to append the queryString to the site url (find results page)
		URL dictionary = null;
		try {
			 dictionary = new URL("http://dictionary.reference.com/browse/" + queryString);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		
		// Try to open the connection with the results page
		BufferedReader in = null;
		if(dictionary != null) {
			try {
				in = new BufferedReader(
							new InputStreamReader(
							dictionary.openStream()));
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
			indexer.removeTagAndChildren("div", "id", "leftRail");
			indexer.removeTagAndChildren("div", "id", "rightRail");
			indexer.removeTagAndChildren("div", "id", "dict_header_content");
			indexer.removeTagAndChildren("div", "id", "link_to_module");
			indexer.removeTagAndChildren("div", "id", "popup");
			indexer.removeTagAndChildren("div", "id", "macnt");
			indexer.removeTagAndChildren("div", "class", "wrp_rc");
			indexer.removeTagAndChildren("div", "id", "Headserp");
			indexer.removeTagAndChildren("div", "class", "spl_unshd");
			indexer.removeTagAndChildren("span", "class", "pronset");
			indexer.removeTextTag(" ");
			indexer.removeTagAndChildren("div", "class", "Lsentnce");
			indexer.removeTagAndChildren("div", "id", "imgres");
			indexer.removeTagAndChildren("div", "id", "link_to_div");
			indexer.removeTagAndChildren("a", "id", "link_to_style");
			indexer.removeTagAndChildren("div", "id", "webres");
			indexer.removeTagAndChildren("div", "class", "sep_top");
			indexer.removeTagAndChildren("a", "rel", "nofollow");
			indexer.removeTagAndChildren("div", "class", "fC ccF");
			indexer.removeTypeOnly("a");
			indexer.removeTagAndChildren("div", "class", "sep_top shd_hdr");
			indexer.removeTagAndChildren("div", "class", "sr sw");
			indexer.removeTagAndChildren("div", "class", "bca saw");
			indexer.removeTagAndChildren("div", "id", "ltm");
			indexer.removeChildOf(1, "text", "td", "class", "td1");
			Tag child = new OneLinerTag();
			child.setType("hr");
//			Tag parent = indexer.getTag("div", "class", "sep_top shd_hdr ").get(0).getParent();
//			int children = parent.getChildren().size();
//			for(int i = 1; i < (children * 2) + 1; i += 2) {
//				indexer.insertChildAtAndAbsorbChildrenOf(child, i, 0, parent);
//			}
//			parent = indexer.getTag("div", "class", "sep_top shd_hdr pb7").get(0).getParent();
//			children = parent.getChildren().size();
//			for(int i = 1; i < (children * 2) + 1; i += 2) {
//				indexer.insertChildAtAndAbsorbChildrenOf(child, i, 0, parent);
//			}
			
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
		return "file:/" + temp.getAbsolutePath().replace("\\", "//");
	}
	
	public void setJEditorPane(JEditorPane jep) {
		this.jep = jep;
		// Add LinkListener here if needed. //TODO
	}

}