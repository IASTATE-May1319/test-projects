package sources;
import java.util.List;

import javax.swing.JEditorPane;

/**
 * A sub-class of Source that represents the particular website contained 
 * in the class name
 * @author Jesse Olds
 */
public class Wikipedia extends Source {

	/**
	 * The JEditorPane displaying this sources URL.
	 */
	private JEditorPane jep;
	
	/**
	 * Default constructor which creates an instance of the super class
	 * Source and sets the source name to this source's name
	 */
	public Wikipedia() {
		super(Source.WIKIPEDIA);
	}
	
	/* (non-Javadoc)
	 * @see sources.Source#executeQuery(java.lang.String)
	 */
	@Override
	public String executeQuery(String queryString) {
		// TODO Perform the actual query on Wikipedia based on the given queryString
		return null;
	}
	
	public void setJEditorPane(JEditorPane jep) {
		this.jep = jep;
		// Add LinkListener here if needed. //TODO
	}

}
