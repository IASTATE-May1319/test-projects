package sources;
import java.util.List;

import javax.swing.JEditorPane;


/**
 * Class to model a source to perform a query against
 * 
 * @author Jesse Olds
 */
public abstract class Source {

	/**
	 * Static references to the names of each source
	 */
	public static final String WIKIPEDIA = "Wikipedia";
	public static final String URBAN_DICTIONARY = "UrbanDictionary";
	public static final String DICTIONARY = "Dictionary";
	public static final String HOWSTUFFWORKS = "HowStuffWorks";
	public static final String EHOW = "EHow";
	public static final String THESAURUS = "Thesaurus";
	public static final String LYRICS = "Lyrics";
	
	// Include a final static String for each source
	
	/**
	 * The string representation of this source's name or title
	 */
	protected String name;
	
	/**
	 * Constructs a source object and assigns the given name to that source
	 * 
	 * @param name - Name of the source
	 */
	public Source(String name) {
		this.name = name;
	}
	
	/**
	 * Gets this source's name
	 * 
	 * @return - The name of this source
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Query method to be implemented by all classes that extend source.
	 * This method is responsible for returning the results of a query based on
	 * the given query string, and the actual web source that this source object
	 * represents
	 * 
	 * @param queryString - The string to be queried by this source
	 * 
	 * @return - The path to the temporary file housing the cleaned search results
	 */
	public abstract String executeQuery(String queryString);
	
	/**
	 * TODO
	 * @param jep A JEditorPane TODO
	 */
	public abstract void setJEditorPane(JEditorPane jep);
}
