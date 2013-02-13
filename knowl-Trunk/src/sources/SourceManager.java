package sources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the sources supported within this product
 * 
 * @author Jesse Olds
 */
public class SourceManager {
	
	/**
	 * The sources managed by this source manager
	 */
	private ArrayList<Source> sources;
	
	/**
	 * Default constructor which initializes the internal list of sources
	 * as well as actually assigning all supported sources to this list
	 */
	public SourceManager() {
		sources = new ArrayList<Source>();
		File dir = new File("bin//sources");
		if(dir.list() != null) {
			for(String filename : dir.list()) 
			{
				createSource(filename);
			}
		}
		
	}
	
	/**
	 * Returns the list of supported sources
	 * @return - The list of sources supported by this product
	 */
	public List<Source> getSources() {
		return sources;
	}
	
	/**
	 * Executes a look-up of all accessible sources for this product
	 * and creates object instances of each source to be added to the internal
	 * source list
	 * @param filename - The filename of the source to be created
	 */
	private void createSource(String filename) {
		if(filename.equals(Source.WIKIPEDIA + ".class"))
		{
			sources.add(new Wikipedia());
		} else if(filename.equals(Source.URBAN_DICTIONARY + ".class"))
		{
			sources.add(new UrbanDictionary());
		} else if(filename.equals(Source.DICTIONARY + ".class"))
		{
			sources.add(new Dictionary());
		//} else if(filename.equals(Source.HOWSTUFFWORKS + ".class")) {
			//sources.add(new HowStuffWorks());
		//} else if(filename.equals(Source.EHOW + ".class")) {
			//sources.add(new EHow());
		} else if(filename.equals(Source.THESAURUS + ".class")) {
			sources.add(new Thesaurus());
		} else if(filename.equals(Source.LYRICS + ".class")) {
			sources.add(new Lyrics());
		}
	}
	
//	/**
//	 * Simple main method for testing purposes
//	 * 
//	 * This method creates an instance of the source manager and checks to make
//	 * sure all of the sources currently supported are correctly added by the 
//	 * manager
//	 */
//	public static void main(String[] args) {
//		SourceManager mgr = new SourceManager();
//		List<Source> sources = mgr.getSources();
//		for(Source src : sources)
//		{
//			System.out.println(src.getName());
//		}
//	}
}
