package util.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Indexes an HTML document using the HTML DOM methodology. Contains methods for removing
 * unwanted pieces of html from an html document and then writing this cleaned document to
 * a temporary file.
 * @author Jesse Olds
 */
public class Indexer {
	
	/**
	 * A list of lists of tags. Each list contains a list of a certain type of 
	 * tags for convenience later.
	 */
	private List<List<Tag>> tags;
	
	/**
	 * An arraylist turned stack which houses all tags currently awaiting to be closed by
	 * a closing tag.
	 */
	private List<Tag> tagStack;
	
	
	/**
	 * A string representation of the tagStack for debugging purposes
	 */
	private List<String> strStack;
	
	/**
	 * Flag specifying whether or not a script is currently in progress.
	 * This is important because scripts are ignored by this indexer as 
	 * scripts can be unusually long and in the hopes of optimizing the 
	 * indexing time, we have chosen to ignore them.
	 */
	private boolean scriptInProg;

	/**
	 * Flag specifying whether or not a comment is currently in progress.
	 * This is important because comments are ignored by this indexer as 
	 * comments can be unusually long and in the hopes of optimizing the 
	 * indexing time, we have chosen to ignore them.
	 */
	private boolean commentInProg;
	
	/**
	 * Flag specifying whether or not a style is currently in progress.
	 * This is important because styles tend to have a weird formatting
	 * as well as unusual size.
	 */
	private boolean styleInProg;
	
	/**
	 * Flag specifying whether or not a oneLiner tag is in progress. A
	 * one liner tag is a tag that opens and closes in the same tag 
	 * declaration. For example <img src="http://blahblahblah.jpg" />
	 */
	private boolean oneLinerInProg;
	
	/**
	 * Flag specifying whether or not an html document has been indexed.
	 */
	private boolean indexed;
	
	/**
	 * Constructor that calls the default constructor as well as the
	 * index method with the given input reader.
	 * 
	 * @param in - A buffered input reader from a website.
	 */
	public Indexer(BufferedReader in) {
		initialize();
		index(in);
	}
	
	/**
	 * Default constructor which initializes all of internal variables.
	 */
	public Indexer() {
		initialize();
	}
	
	private void initialize() {
		tags = new ArrayList<List<Tag>>();
		tagStack = new ArrayList<Tag>();
		strStack = new ArrayList<String>();
		scriptInProg = false;
		commentInProg = false;
		styleInProg = false;
		oneLinerInProg = false;
		indexed = false;
	}
	
	/**
	 * A method for finding a list of matching tags within the indexed document.
	 * If no results are found, an empty list is returned. If results are found
	 * they are added in order or appearance in the document. Matches are determined
	 * by checking to see that the tag type matches, as well as a perfect match of
	 * attribute name and attribute value. Attribute name is case-insensitive, while
	 * attribute value is case sensitive. (NaMe == name) (VaLuE != value).
	 * 
	 * @param type - The type of the tag to be found.
	 * @param attributeName - The attribute name of the tag to be found. (Ex. class, href, id)
	 * @param attributeValue - The attribute value to be found.
	 * @return - A list of tags matching the given information. Null if no document has been
	 * indexed.
	 */
	public List<Tag> getTag(String type, String attributeName, String attributeValue) {
		if(indexed) {
			List<Tag> list = new ArrayList<Tag>();
			
			// Search for a list whose type equals the given type
			for(List<Tag> l : tags) {
				if(!l.isEmpty()) {
					if(l.get(0).getType().equals(type.toLowerCase())) {
						// List of correct type found, break
						list = l;
						break;
					}
				}
			}
			
			// Setup return list
			List<Tag> ofTheKing = new ArrayList<Tag>();
			
			// Search for tags with matching attribute name/value
			for(Tag t : list) {
				// Check for a matching attribute name/value in this tag
				for(Attribute a : t.getAttributes()) {
					if(a.name.equals(attributeName.toLowerCase()) && a.value.equals(attributeValue)) {
						// Tag found, add it to return list, break out of the attribute list and 
						// keep looking for matching tags
						ofTheKing.add(t);
						break;
					}
				}
			}
			return ofTheKing;
		} else {
			return null;
		}
	}
	
	/**
	 * This is not as useful, but if you need access to an entire list of tags of the same type,
	 * this is the method for you.
	 * 
	 * @param type - Tag type to search for.
	 * @return - A list containing all the tags of the specified type. Empty list if no tags of
	 * that type are found. Null list if no document has been indexed.
	 */
	public List<Tag> getTag(String type) {
		if(indexed) {
			for(List<Tag> l : tags) {
				if(!l.isEmpty()) {
					if(l.get(0).getType().equals(type)) {
						return l;
					}
				}
			}
			return new ArrayList<Tag>(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Finds a text tag in this document that is an exact match of the given value. This is a 
	 * case-sensitive method. Your best bet is to copy and paste the entire text you are look-
	 * ing for from the html document itself.
	 * 
	 * @param value - Text body to look for.
	 * @return - The tag containing the given text. Null if tag is not found.
	 */
	public Tag getTextTag(String value) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals("text")) {
						for(int j = 0; j < tagList.size(); j++) {
							TextTag tag = (TextTag)tagList.get(j);
							String body = tag.getBody();
							if(body.equals(value)) {
								return tag;
							}
						}
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	/**
	 * Writes the entire indexed document to a file in html format.
	 * 
	 * @param out - Buffered writer for writing to the output file.
	 * @return - True if document was written to the file successfully, false otherwise.
	 */
	public boolean linesToFile(BufferedWriter out) {
		if(indexed) {
			System.out.println("Trying to print to file.");
			// Start with the doctype as it has no children, no parent and always comes 
			// first in a document.
			List<Tag> docList = getTag("doctype");
			if(!docList.isEmpty()) {
				DoctypeTag doc = (DoctypeTag)docList.get(0);
				doc.linesToFile(out);
			}
			// Next comes the html tag, which should contain all other tags by HTML DOM
			// methodology.
			List<Tag> htmlList = getTag("html");
			if(!htmlList.isEmpty()) {
				Tag html = htmlList.get(0);
				html.linesToFile(out);
			}
			System.out.println("Printed to file successfully!");
			return true;
		} else {
			// No document has been indexed, return false.
			return false;
		}
	}
	
	public void insertChildAtAndAbsorbChildrenOf(Tag child, int desiredIndex, int childrenToAbsorb, Tag parent) {
		if(indexed) {
			if(parent.getChildren().size() > desiredIndex) {
				List<Tag> newParentChildren = new ArrayList<Tag>();
				List<Tag> parentChildren = parent.getChildren();
				for(int i = 0; i < parentChildren.size(); i++) {
					Tag parentChild = parentChildren.get(i);
					if(i < desiredIndex) {
						newParentChildren.add(parentChild);
					} else if(i == desiredIndex) {
						newParentChildren.add(child);
						child.setParent(parent);
						addToTagsList(child);
						i--;
						desiredIndex = -1;
					} else {
						if(childrenToAbsorb > 0) {
							child.addChild(parentChild);
							parentChild.setParent(child);
							childrenToAbsorb--;
						} else {
							newParentChildren.add(parentChild);
						}
					}
				}
				parent.setChildren(newParentChildren);
			} else if (parent.getChildren().size() == desiredIndex) {
				parent.addChild(child);
				child.setParent(parent);
				addToTagsList(child);
			}
		}
	}
	
	/**
	 * Removes all tags of the specified type and their children from the indexed document
	 * @param type - Tag type to remove from this document
	 */
	public void removeType(String type) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals(type.toLowerCase())) {
						for(int j = 0; j < tagList.size(); j++) {
							Tag tag = tagList.get(j);
							tag.removeIncludeChildren();
						}
						sweep();
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * A very useful method for removing all tags with no children (excluding doctype
	 * and text tags as they are designed to be purely seed/leaf tags respectively in HTML DOM).
	 * This method will greatly reduce the clutter contributed by ads and other unnecessary html.
	 * Always call this method before and after any other cleanup of an indexed file.
	 */
	public void removeEmptyTags() {
		if(indexed) {
			for(int j = 0; j < tags.size(); j++) {
				// Grab a list of tags of the same type
				List<Tag> tagList = tags.get(j);
				if(!tagList.isEmpty()) {
					boolean removed = false;
					for(int i = 0; i < tagList.size(); i++) {
						// Grab a tag from that list
						Tag tag = tagList.get(i);
						// Make sure this tag isn't a text or doctype tag as those are ignored in this method
						// Also make sure this tag is not a oneLiner tag as one liners will never have children
						if(!tag.getType().equals("text") && !tag.getType().equals("doctype") && tag.getClass() != OneLinerTag.class) {
							// Flag specifying whether or not to delete a chain of tags
							// This is in place to remove embedded chains of tags whose innermost tag contains no children
							boolean moveUpTheLadder = true;
							while(moveUpTheLadder) {
								if(tag.getChildren().isEmpty()) {
									// Tag has no children
									
									// Special case for style tags because they have a style body as opposed to children
									// If this is a style tag, check for a body, if no body exists, treat it like a childless tag
									if(tag.getClass() == StyleTag.class) {
										if(!((StyleTag)tag).getBody().isEmpty()) {
											moveUpTheLadder = false;
											break;
										}
									}
									// Get the parent of this childless tag
									Tag parent = tag.getParent();
									// Remove this tag and its children (though it will not have any children at this point)
									tag.removeIncludeChildren();
									// Set the removed tag so we know we need to clean up after this loop
									removed = true;
									// Set the continue tag to false
									moveUpTheLadder = false;
									if(parent != null) {
										// If the parent to this tag exists and is now childless due to the removal of this tag
										// Go through the process again with the parent as the tag subject to removal
										if(parent.getChildren().isEmpty()) {
											tag = parent;
											moveUpTheLadder = true;
										}
									}
								} else {
									// Tag has children, ignore it
									moveUpTheLadder = false;
								}
							}
						}
					}
					// Clean up tags with null parents
					if(removed) {
						sweep();
						removed = false;
					}
				} else {
					// Get rid of empty lists to conserve space
					tags.remove(j);
					j--;
				}
			}
		}
	}

	/**
	 * Removes all tags of the specified type from the indexed document, but connects the children
	 * of the tag removed to the parent of the tag removed
	 * @param type - Tag type to remove from this document
	 */
	public void removeTypeOnly(String type) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals(type.toLowerCase())) {
						for(int j = 0; j < tagList.size(); j++) {
							Tag tag = tagList.get(j);
							tag.removeConnectChildren();
						}
						sweep();
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes all tags that match the given tag description, as well as removing the children of the matching tags
	 * @param type - The tag type to search for and remove
	 * @param attrName - An attribute name to help find a tag match
	 * @param attrValue - An attribute value corresponding to the attribute name to help find a tag match
	 */
	public void removeTagAndChildren(String type, String attrName, String attrValue) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals(type.toLowerCase())) {
						for(int j = 0; j < tagList.size(); j++) {
							Tag tag = tagList.get(j);
							for(Attribute attr : tag.getAttributes()) {
								if(attr.name.equals(attrName.toLowerCase()) && attr.value.equals(attrValue)) {
									tag.removeIncludeChildren();
									break;
								}
							}
						}
						sweep();
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes any text tags whose text body matches the input value
	 * @param value - The text body to search for
	 */
	public void removeTextTag(String value) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals("text")) {
						for(int j = 0; j < tagList.size(); j++) {
							TextTag tag = (TextTag)tagList.get(j);
							String body = tag.getBody();
							if(body.equals(value)) {
								tag.removeIncludeChildren();
							}
						}
						sweep();
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes all tags that match the given tag description, connects any children of the removed tags to the
	 * removed tag's parent.
	 * @param type - The tag type to search for and remove
	 * @param attrName - An attribute name to help find a tag match
	 * @param attrValue - An attribute value corresponding to the attribute name to help find a tag match
	 */
	public void removeTagOnly(String type, String attrName, String attrValue) {
		if(indexed) {
			for(int i = 0; i < tags.size(); i++) {
				List<Tag> tagList = tags.get(i);
				if(!tagList.isEmpty()) {
					String tagType = tagList.get(0).getType();
					if(tagType.equals(type.toLowerCase())) {
						for(int j = 0; j < tagList.size(); j++) {
							Tag tag = tagList.get(j);
							for(Attribute attr : tag.getAttributes()) {
								if(attr.name.equals(attrName.toLowerCase()) && attr.value.equals(attrValue)) {
									tag.removeConnectChildren();
									break;
								}
							}
						}
						sweep();
					}
				} else {
					tags.remove(i);
					i--;
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes the child (and it's children) at the specified index, of the specified type, from the specified tag.
	 * This method is very dangerous as you never know if the html document will be constructed the same way
	 * given different search results. I would recommend using other methods if at all possible.
	 * @param index - Index of the child to be deleted
	 * @param childType - Type of the child to be deleted (just to make sure the right child is removed)
	 * @param parentType - Type of the parent whose child is to be deleted
	 * @param parentAttrName - Attribute name of the parent whose child is to be deleted
	 * @param parentAttrValue - Attribute value of the attribute name of the parent whose child is to be deleted
	 */
	public void removeChildOf(int index, String childType, String parentType, String parentAttrName, String parentAttrValue) {
		if(indexed) {
			List<Tag> matches = getTag(parentType, parentAttrName, parentAttrValue);
			for(int i = 0; i < matches.size(); i++) {
				Tag match = matches.get(i);
				if(match.getChildren().size() >= index + 1) {
					if(match.getChild(index).getType().equals(childType)) {
						Tag child = match.getChild(index);
						child.removeIncludeChildren();
					}
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes the child (leaves the child's children) at the specified index, of the specified type, from the specified tag.
	 * This method is very dangerous as you never know if the html document will be constructed the same way
	 * given different search results. I would recommend using other methods if at all possible.
	 * @param index - Index of the child to be deleted
	 * @param childType - Type of the child to be deleted (just to make sure the right child is removed)
	 * @param parentType - Type of the parent whose child is to be deleted
	 * @param parentAttrName - Attribute name of the parent whose child is to be deleted
	 * @param parentAttrValue - Attribute value of the attribute name of the parent whose child is to be deleted
	 */
	public void removeChildOnlyOf(int index, String childType, String parentType, String parentAttrName, String parentAttrValue) {
		if(indexed) {
			List<Tag> matches = getTag(parentType, parentAttrName, parentAttrValue);
			for(int i = 0; i < matches.size(); i++) {
				Tag match = matches.get(i);
				if(match.getChildren().size() >= index + 1) {
					if(match.getChild(index).getType().equals(childType)) {
						Tag child = match.getChild(index);
						child.removeConnectChildren();
					}
				}
			}
			removeEmptyTags();
		}
	}
	
	/**
	 * Removes any parentless tags from the indexed tags, excluding the html and doctype tags as they will never have a parent.
	 * Also removes any empty lists from the list of lists of tags.
	 */
	public void sweep() {
		if(indexed) {
			for(int j = 0; j < tags.size(); j++) {
				List<Tag> tagList = tags.get(j);
				if(!tagList.isEmpty()) {
					for(int i = 0; i < tagList.size(); i++) {
						Tag tag = tagList.get(i);
						if(!tag.getType().equals("html") && !tag.getType().equals("doctype")) {
							if(tag.getParent() == null) {
								tagList.remove(i);
								i--;
							}
						}
					}
					if(tagList.isEmpty()) {
						tags.remove(j);
						j--;
					}
				} else {
					tags.remove(j);
					j--;
				}
			}
		}
	}
	
	/**
	 * Indexes the html document being read from the specified input reader
	 * @param in - Input reader that is reading from the html document
	 */
	public void index(BufferedReader in) {
		if(in == null) {
			return;
		}
		
		if(indexed == true) {
			initialize();
		}
		
		String inputLine;
		try {
			String fabricatedLine = "";
			while((inputLine = in.readLine()) != null) {
				if(!inputLine.equals("")) {
					String[] lines = null;
					if(fabricatedLine.equals("")) {
						String spliced = spliceComment(inputLine);
						if(spliced != null) {
							spliced = spliceScript(spliced);
							if(spliced != null) {
								spliced = handleStyle(spliced);
								if(spliced != null) {
									lines = makeNiceLine(spliced);
								}
							}
						}
					} else {
						String spliced = spliceComment(inputLine);
						if(spliced != null) {
							spliced = spliceScript(spliced);
							if(spliced != null) {
								spliced = handleStyle(spliced);
								if(spliced != null) {
									lines = makeNiceLine(spliced);
								}
							}
						}
						lines = makeNiceLine(fabricatedLine + spliced);
					}
					if(lines != null) {
						if(lines[0] != null) {
							fabricatedLine = lines[0];
							indexLine(fabricatedLine);
							//System.out.println("Line Indexed: " + fabricatedLine);
							fabricatedLine = "";
						}
						if(lines[1] != null) {
							fabricatedLine += lines[1];
						}
					}
				}
			}
			indexed = true;
			//sweep();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] makeNiceLine(String line) {
		int firstOpener = line.indexOf('<');
		int firstCloser = line.indexOf('>');
		int lastOpener = line.lastIndexOf('<');
		int lastCloser = line.lastIndexOf('>');
		String[] lines = new String[2];
		
		if((firstOpener < firstCloser) && (lastOpener < lastCloser) && (firstOpener != -1) && (firstCloser != -1) && (lastOpener != -1) && (lastCloser != -1)) {
			lines[0] = line;
			lines[1] = null;
		} else if ((lastOpener > lastCloser) && (lastCloser == -1)) {
			lines[0] = line.substring(0, lastOpener);
			lines[1] = line.substring(lastOpener, line.length());
		} else {
			lines[0] = null;
			lines[1] = line;
		}
		
		return lines;
	}

	private void indexLine(String inputLine) {
		// Remove all unnecessary white space
		String modInput = removeExtraWhiteSpace(inputLine);
		
		/* 
		 * Splice the scripts from the line and return what is left.
		 * If a script is in progress and is not closed within this line,
		 * return null (meaning this line is done indexing)
		 */
		modInput = spliceScript(modInput);
		
		// This line is entirely contained within a script, ignore it, this line is finished.
		if(modInput == null) { return; }
		
		/* Splice the comments out of what is left from the script-splicer and
		 * return a String without comments or scripts.
		 * If a comment is in progress, search for an end to the comment in this line.
		 * If no end is found, return null (meaning this line is done indexing)
		 */
		modInput = spliceComment(modInput);
		
		// This line is entirely contained within a comment, ignore it, this line is finished.
		if(modInput == null) { return; }
		
		// Check to see if any tags exist in what is left in this input line
		boolean hasTags = tagFinder(modInput);
		
		if(hasTags) {
			tagHandler(modInput); // Tags have been found, handle them accordingly.
		} else {
			// Tags were not found, check where to add this text element
			
			// If no current tags exist in the tag stack, ignore this block, otherwise add it.
			handleText(modInput);
		}
	}

	private String spliceScript(String inputLine) {
		/*
		 * If a script is in progress, search for an end to that script in this line.
		 * Otherwise, search for and splice out any scripts that begin and end in this line.
		 * If a script is started but not closed in this line, set the script in progress flag
		 * and return any string prior to the script starting, or return null if the script
		 * takes up the whole line.
		 */
		if(scriptInProg) {
			// If a closing script tag is found, handle the line, if not, you're still in a script,
			// ignore this line and return null
			if(inputLine.toLowerCase().contains("</script>") || inputLine.toLowerCase().contains("</noscript>")) {
				// Find </script> and </noscript> in any form and replace with <&script>
				inputLine = cleanAndReplace(inputLine, "</script>", "<&script>");
				inputLine = cleanAndReplace(inputLine, "</noscript>", "<&script>");
				
				// Splice the line based on closing script tags
				Scanner scriptScan = new Scanner(inputLine).useDelimiter("<&script>");
				
				// If the line doesn't start with the closing script tag, skip the first portion as 
				// it was part of the script
				if(!inputLine.toLowerCase().startsWith("<&script>")) {
					scriptScan.next();
				}
				
				// This will help us know whether the script ends on this line or not
				boolean closesAtEnd = false;
				if(inputLine.toLowerCase().endsWith("<&script>")) {
					closesAtEnd = true;
				}
				
				String whatsLeft = "";
				while(scriptScan.hasNext()) {
					/* inBetween will be between closing script tags
					 * For example:
					 * inBetween = "<h2>This is in between two scripts!</h2><script type="text/javascript"> SCRIPTCODE";
					 * or:
					 * inBetween = "<script type="text/javascript"> SCRIPTCODE";
					 * or:
					 * inBetween = "<h2>This is just something that shows up after the script is closed!</h2>";
					 */
					String inBetween = scriptScan.next().trim(); // Remove leading or trailing spaces
					
					if(inBetween.toLowerCase().contains("<script") || inBetween.toLowerCase().contains("<noscript")) {
						inBetween = cleanAndReplace(inBetween, "<script", "&script");
						inBetween = cleanAndReplace(inBetween, "<noscript", "&script");
						
						// inBetween contains another script, keep everything before the script and ignore the script
						if(!(inBetween.toLowerCase().startsWith("&script"))) {
							whatsLeft += inBetween.substring(0, inBetween.toLowerCase().indexOf("&script"));
						}
						
						// If there is more to look at, this script is closed (the delimiter is "</script>"
						// If this is the last line, check if the line ends in a closing tag and set
						// the in progress flag accordingly
						scriptInProg = (scriptScan.hasNext()) ? false : !closesAtEnd;
					} else {
						// No other scripts begin in this area, keep everything and set the in progress flag to false
						whatsLeft += inBetween;
						scriptInProg = false;
					}
				}
				
				// Everything has been checked for scripts and spliced into whatsLeft
				// If whatsLeft is empty, this whole line contained scripts, return null.
				if(whatsLeft.equals("")) {
					if(closesAtEnd) {
						scriptInProg = false;
					}
					return null;
				} else {
					return whatsLeft;
				}
			}
		} else {
			// There is no script in progress, let's check the line for additional scripts
			if(!inputLine.toLowerCase().contains("<script") && !inputLine.toLowerCase().contains("<noscript")) {
				// No scripts start in this line, and since none are in progress, none will end here either
				// Return the entire string
				return inputLine;
			} else {
				// Uh-oh, we found a script, let's get rid of it
				// Find <script in any form and replace with &script
				if(inputLine.contains("<script")) {
					inputLine = cleanAndReplace(inputLine, "<script", "&script");
				}
				
				if(inputLine.contains("<noscript")) {
					inputLine = cleanAndReplace(inputLine, "<noscript", "&script");
				}
				
				// Splice the line based on opening script tags
				Scanner scriptScan = new Scanner(inputLine).useDelimiter("&script");

				// The script-spliced output to be returned
				String whatsLeft = "";
				
				// If the line doesn't start with the opening script tag, add the first portion to 
				// our output as it was not part of the script
				if(!inputLine.toLowerCase().startsWith("&script")) {
					whatsLeft += scriptScan.next();
				}
				
				while(scriptScan.hasNext()) {
					/* inBetween will be between opening script tags
					 * For example:
					 * inBetween = "type="text/javascript">SCRIPTCODE</script><h2>This is in between two scripts!</h2>";
					 * or:
					 * inBetween = "type="text/javascript"></script><h2>This is just after a script</h2>";
					 */
					
					String inBetween = scriptScan.next().trim(); // Remove leading or trailing spaces
					
					if(inBetween.toLowerCase().contains("</script>") || inBetween.toLowerCase().contains("</noscript>")) {
						// inBetween contains a closing script tag, keep whatever comes after that closing tag
						int indexJustAfterScript = inBetween.toLowerCase().contains("</script>") ? inBetween.toLowerCase().indexOf("</script>") + (new String("</script>").length()) : inBetween.toLowerCase().indexOf("</noscript>") + (new String("</noscript>").length());
						whatsLeft += inBetween.substring(indexJustAfterScript, inBetween.length());
						
						// No other scripts begin in this area, move on and set the in progress flag to false
						scriptInProg = false;
					} else {
						// There's no end in sight for this script we found.
						// Set the in progress flag to true and move on.
						scriptInProg = true;
					}
				}
				
				// Everything has been checked for scripts and spliced into whatsLeft
				// If whatsLeft is empty, this whole line contained scripts, return null.
				if(whatsLeft.equals("")) {
					return null;
				} else {
					return whatsLeft;
				}
			}
		}
		return null;
	}

	private String cleanAndReplace(String str, String find, String replace) {
		// Strings to find and replace are the same, let's not waste time!
		if(find.equals(replace))
			return str;
		
		// Replace all that currently match
		str = str.replace(find, replace);
		
		boolean necessary = str.toLowerCase().contains(find);
		while(necessary) {
			// Find first instance of find
			int startIndex = str.toLowerCase().indexOf(find);
			
			// Copy out case sensitive form
			String weirdForm = str.substring(startIndex, startIndex + (new String(find).length()));
			
			// Replace all instances with my intermediate form: replace
			str = str.replace(weirdForm, replace);
			
			// Check for more instances
			necessary = str.toLowerCase().contains(find);
		}
		return str;
	}

	private String spliceComment(String inputLine) {
		/*
		 * If a comment is in progress, search for an end to that comment in this line.
		 * Otherwise, search for and splice out any comments that begin and end in this line.
		 * If a comment is started but not closed in this line, set the comment in progress flag
		 * and return any string prior to the comment starting, or return null if the comment
		 * takes up the whole line.
		 */
		if(commentInProg) {
			// If a closing comment tag is found, handle the line, if not, you're still in a comment,
			// ignore this line and return null
			if(inputLine.contains("-->")) {
				// We found a closing comment tag, let's deal with it
				
				// Find --> in any form and replace with &->
				inputLine = inputLine.replace("-->", "&->");
				
				// Splice the line based on closing comment tags
				Scanner comScan = new Scanner(inputLine).useDelimiter("&->");
				
				// If the line doesn't start with the closing comment tag, skip the first portion as 
				// it was part of the comment
				if(!inputLine.startsWith("&->")) {
					comScan.next();
				}
				
				// This will help us know whether the comment ends on this line or not
				boolean closesAtEnd = false;
				if(inputLine.endsWith("&->")) {
					closesAtEnd = true;
				}
				
				String whatsLeft = "";
				while(comScan.hasNext()) {
					/* inBetween will be between closing comment tags
					 * For example:
					 * inBetween = "<h2>This is in between two comments!</h2><!-- COMMENT CODE";
					 * or:
					 * inBetween = "<!-- COMMENT CODE";
					 * or:
					 * inBetween = "<h2>This is just something that shows up after the comment is closed!</h2>";
					 */
					String inBetween = comScan.next().trim(); // Remove leading or trailing spaces
					
					if(inBetween.contains("<!--")) {
						// inBetween contains another comment, keep everything before the comment and ignore the comment
						if(!(inBetween.startsWith("<!--"))) {
							whatsLeft += inBetween.substring(0, inBetween.indexOf("<!--"));
						}
						
						// If there is more to look at, this comment is closed (the delimiter is "-->"
						// If this is the last line, check if the line ends in a closing tag and set
						// the in progress flag accordingly
						commentInProg = (comScan.hasNext()) ? false : !closesAtEnd;
					} else {
						// No other comments begin in this area, keep everything and set the in progress flag to false
						whatsLeft += inBetween;
						commentInProg = false;
					}
				}
				
				// Everything has been checked for comments and spliced into whatsLeft
				// If whatsLeft is empty, this whole line contained comments, return null.
				if(whatsLeft.equals("")) {
					if(closesAtEnd) {
						commentInProg = false;
					}
					return null;
				} else {
					return whatsLeft;
				}
			}
		} else {
			// There is no comment in progress, let's check the line for additional comments
			if(!inputLine.contains("<!--")) {
				// No comments start in this line, and since none are in progress, none will end here either
				// Return the entire string
				return inputLine;
			} else {
				// Uh-oh, we found a comment, let's get rid of it
				// Find <!-- in any form and replace with <!&-
				inputLine = inputLine.replace("<!--", "<!&-");
				
				// Splice the line based on opening comment tags
				Scanner comScan = new Scanner(inputLine).useDelimiter("<!&-");

				// The comment-spliced output to be returned
				String whatsLeft = "";
				
				// If the line doesn't start with the opening comment tag, add the first portion to 
				// our output as it was not part of the comment
				if(!inputLine.startsWith("<!&-")) {
					whatsLeft += comScan.next();
				}
				
				while(comScan.hasNext()) {
					/* inBetween will be between opening comment tags
					 * For example:
					 * inBetween = "COMMENT CODE --><h2>This is in between two comments!</h2>";
					 * or:
					 * inBetween = "COMMENT CODE --><h2>This is just after a comment</h2>";
					 */
					
					String inBetween = comScan.next().trim(); // Remove leading or trailing spaces
					
					if(inBetween.contains("-->")) {
						// inBetween contains a closing comment tag, keep whatever comes after that closing tag
						int indexJustAfterCom = inBetween.indexOf("-->") + (new String("-->").length());
						whatsLeft += inBetween.substring(indexJustAfterCom, inBetween.length());
						
						// No other comments begin in this area, move on and set the in progress flag to false
						commentInProg = false;
					} else {
						// There's no end in sight for this comment we found.
						// Set the in progress flag to true and move on.
						commentInProg = true;
					}
				}
				
				// Everything has been checked for comments and spliced into whatsLeft
				// If whatsLeft is empty, this whole line contained comments, return null.
				if(whatsLeft.equals("")) {
					return null;
				} else {
					return whatsLeft;
				}
			}
		}
		return null;
	}

	private boolean tagFinder(String inputLine) {
		int startIndex = inputLine.indexOf('<');
		int closeIndex = inputLine.indexOf('>');
		return (startIndex < closeIndex) && (startIndex != -1);
	}

	private void tagHandler(String inputLine) {
		// Tags have been found in this line and we're sure they're not comments or scripts
		// We better handle them like HTML tags
		
		// Let's break this line up with a delimiter of "<"
		// This will separate the line at the very beginning of every tag
		Scanner preTagScan = new Scanner(inputLine).useDelimiter("<");
		
		while(preTagScan.hasNext()) {
			/* At this point we have one of four options:
			 * 1. Some text that comes before a tag
			 * 2. someOpeningTag someAtrribute="someValue">Some text contained in this tag
			 * 3. someOpeningTag someAtrribute="someValue" />Some text after an inline tag 
			 * 3. (continued) (tag was opened and closed in opening tag) Example : <br />
			 * 4. /someClosingTag>Some text after some closing tag
			 */
			String option = preTagScan.next().trim(); // Trim unnecessary spaces
			/* Let's take care of option 1 first
			 * This text is either part of the bottom tag on the tagStack,
			 * or the tagStack is empty and this text should be ignored (no parent)
			 */
			if(!option.contains(">")) {
				if(option.endsWith("=\"")) {
					/* It looks like our original line was something like this:
					 * <input class="linkbox" onclick="this.select()" value="<a href='http://dictionary.reference.com/browse/applesauce'>">
					 * 
					 * Our option now looks like this:
					 * option = "input class="linkbox" onclick="this.select()" value=""
					 * 
					 * Obviously this isn't yet finished, we should append the next section in order to accurately index this tag
					 */
					option += "<" + preTagScan.next().trim();
					
					// Now we have a somewhat normal tag, except for the > contained within, let's replace those with a special character
					// so it doesn't mess up our delimiters
					option = option.replace(">", "?!@**#$");
					
					// We only wanted to replace the middle instances of >, let's put back the last one so we don't break our own code
					int lastIndex = option.lastIndexOf("?!@**#$");
					option = option.substring(0, lastIndex) + ">" + option.substring(lastIndex + new String("?!@**#$").length(), option.length());
					
					// We also want to get rid of any spaces in the enclosed tag, we'll add them back later
					int openIndex = option.indexOf("<");
					int closeIndex = option.lastIndexOf("?!@**#$");
					option = option.substring(0, openIndex) 
					+ option.substring(openIndex, closeIndex).replace(" ", "*&^%$#@!?")
					+ option.substring(closeIndex, option.length());
					
					// We also want to get rid of any other = signs in the enclosed tag, we'll add them back later
					int openIndexEq = option.indexOf("<");
					int closeIndexEq = option.lastIndexOf("?!@**#$");
					option = option.substring(0, openIndexEq) 
					+ option.substring(openIndexEq, closeIndexEq).replace("=", "!#?@%$&^")
					+ option.substring(closeIndexEq, option.length());
				} else {
					handleText(option);
					continue;
				}
			}
			
			/*
			 * Next let's combine options 2, 3, and 4
			 * The reason we are combining is that all three options can be broken up
			 * into a tag (opening or closing) and a block of text. We'll simply
			 * treat the tags differently depending on what they are
			 */
			
			// Now let's break this option up around any > contained within
			Scanner postTagScan = new Scanner(option).useDelimiter(">");
			
			if(postTagScan.hasNext()) {
				/* At this point we have one of three possibilities:
				 * 1. someOpeningTag someAtrribute="someValue"
				 * 2. someOpeningTag someAtrribute="someValue" /
				 * 2. (continued) (Note: This tag both opens and closes within the brackets)
				 * 3. /someClosingTag
				 */
				String possibility = postTagScan.next().trim(); // Trim unnecessary spaces
				
				// Let's take care of possibility 3 first as it is the most unique
				if(possibility.startsWith("/")) {
					handleClosingTag(possibility);
				}
				
				// Now we have only need to deal with opening tags
				
				// Let's send the one that opens and closes in the same tag off to it's own method
				if(possibility.endsWith("/")) {
					handleOneLiner(possibility);
				}
				
				// Finally let's take care of normal opening tags
				if(!possibility.startsWith("/") && !possibility.endsWith("/")) {
					handleOpeningTag(possibility);
				}
			}
			
			// Let's not forget about the possible text that could be sitting in the scanner
			if(postTagScan.hasNext()) {
				handleText(postTagScan.next());
			}
		}
	}
	
	private String handleStyle(String inputLine) {
		if(styleInProg) {
			if(!inputLine.toLowerCase().contains("</style>")) {
				// No end in sight for this style, append to the style body and return null
				StyleTag style = (StyleTag)tagStack.get(tagStack.size() - 1);
				if(!inputLine.equals("")) {
					style.appendBody(inputLine);
				}
				return null;
			}
			
			// We found a closing style tag, let's deal with it
			
			// Find </style> in any form and replace with <&style>
			inputLine = cleanAndReplace(inputLine, "</style>", "<&style>");
			
			// Splice the line based on closing style tags
			Scanner styleScan = new Scanner(inputLine).useDelimiter("<&style>");
			
			// If the line doesn't start with the closing style tag, append the first portion 
			// to the body of the style tag, as it was part of the style
			if(!inputLine.toLowerCase().startsWith("<&style>")) {
				StyleTag style = (StyleTag)(tagStack.get(tagStack.size() - 1));
				style.appendBody(styleScan.next());
			}
			
			// This will help us know whether the style ends on this line or not
			boolean closesAtEnd = false;
			if(inputLine.toLowerCase().endsWith("<&style>")) {
				closesAtEnd = true;
			}
			
			String whatsLeft = "";
			if(!styleScan.hasNext() && closesAtEnd) {
				Tag justClosed = tagStack.remove(tagStack.size() - 1);
				strStack.remove(strStack.size() - 1);
				//System.out.println("\nstrStack : ");
				//for(String str : strStack) {
					//System.out.println(str);
				//}
				
				addToTagsList(justClosed);
			}
			while(styleScan.hasNext()) {
				Tag justClosed = tagStack.remove(tagStack.size() - 1);
				strStack.remove(strStack.size() - 1);
				//System.out.println("\nstrStack : ");
				//for(String str : strStack) {
					//System.out.println(str);
				//}
				
				addToTagsList(justClosed);
				
				/* inBetween will be between closing style tags
				 * For example:
				 * inBetween = "<h2>This is in between two styles!</h2><style type="text/css"> STYLECODE";
				 * or:
				 * inBetween = "<style type="text/css"> STYLECODE";
				 * or:
				 * inBetween = "<h2>This is just something that shows up after the style is closed!</h2>";
				 */
				String inBetween = styleScan.next().trim(); // Remove leading or trailing spaces
				
				if(inBetween.toLowerCase().contains("<style")) {
					inBetween = cleanAndReplace(inBetween, "<style", "&style");
					
					// inBetween contains another style, keep everything before the style and handle the style
					if(!(inBetween.toLowerCase().startsWith("&style"))) {
						whatsLeft += inBetween.substring(0, inBetween.toLowerCase().indexOf("&style"));
						tagHandler(inBetween.substring(0, inBetween.toLowerCase().indexOf("&style")));
					}
					
					// If there is more to look at, this style is closed (the delimiter is "</style>"
					// If this is the last line, check if the line ends in a closing tag and set
					// the in progress flag accordingly
					styleInProg = (styleScan.hasNext()) ? false : !closesAtEnd;
				} else {
					// No other styles begin in this area, keep everything and set the in progress flag to false
					whatsLeft += inBetween;
					styleInProg = false;
				}
			}
			
			// Everything has been checked for styles and spliced into whatsLeft
			// If whatsLeft is empty, this whole line contained styles, return null.
			if(whatsLeft.equals("")) {
				if(closesAtEnd) {
					styleInProg = false;
				}
				return null;
			} else {
				return whatsLeft;
			}
		} else {
			if(!inputLine.toLowerCase().contains("<style")) {
				// No style in progress and no style contained in this line, return
				// the entire string
				return inputLine;
			}
			
			// We found an opening style tag, let's deal with it
			
			// Find <style in any form and replace with <&style
			inputLine = cleanAndReplace(inputLine, "<style", "<&style");
			
			// Splice the line based on opening style tags
			Scanner styleScan = new Scanner(inputLine).useDelimiter("<&style");
			
			String whatsLeft = "";
			
			// If the line doesn't start with the opening style tag, append the first portion 
			// to whatsLeft, as it was not part of the style
			if(!inputLine.toLowerCase().startsWith("<&style")) {
				 whatsLeft += styleScan.next();
			}
			
			int endOfOpenTag = inputLine.toLowerCase().indexOf(">");
			
			String styleTagInfo = inputLine.substring(new String("<&style").length(), endOfOpenTag).trim();
			String leftToHandle = inputLine.substring(endOfOpenTag + 1, inputLine.length()).trim();
			leftToHandle = cleanAndReplace(leftToHandle, "<&style", "<style");
			
			Scanner tagScan = new Scanner(styleTagInfo);

			StyleTag style = new StyleTag();
			if(tagScan.hasNext()) {
				
				List<Attribute> attrs = new ArrayList<Attribute>();
				while(tagScan.hasNext()) {
					String next = "";
					boolean improperAttribute = true;
					while(improperAttribute) {
						if(!next.equals("")) {
							next += " ";
						}
						next += tagScan.next();
						improperAttribute = !(((next.indexOf("\"") < next.length()) && next.endsWith("\"")) || ((next.indexOf("'") < next.length()) && next.endsWith("'")));
					}
					attrs.add(parseAttribute(next));
				}
				
				style.setAttributes(attrs);
			}
			
			// Now that we have a newly formed tag, let's add it to the stack!
			if(!tagStack.isEmpty()) {
				Tag bottomTag = tagStack.get(tagStack.size() - 1);
				bottomTag.addChild(style);
				style.setParent(bottomTag);
			}
			tagStack.add(style);
			strStack.add(style.getType());
			//System.out.println("\nstrStack : ");
			//for(String str : strStack) {
			//	System.out.println(str);
			//}
			
			// Make sure the next call to handleStyle handles the string properly
			styleInProg = true;
			
			// Everything has been checked for styles and spliced into whatsLeft
			// If whatsLeft is empty, this whole line contained styles, return null.
			if(whatsLeft.equals("")) {
				return handleStyle(leftToHandle);
			} else {
				return whatsLeft + handleStyle(leftToHandle);
			}
		}
	}

	private void handleOpeningTag(String tagInfo) {
		if(tagInfo.toLowerCase().startsWith("!doctype")) {
			// The doctype is weird, let's save that out separately
			DoctypeTag doc = new DoctypeTag(tagInfo);
			List<Tag> docList = new ArrayList<Tag>();
			docList.add(doc);
			tags.add(docList);
			return;
		}
		Tag tag = oneLinerInProg ? new OneLinerTag() : null;
		
		if(tagInfo.toLowerCase().startsWith("meta") || tagInfo.toLowerCase().startsWith("link")
				|| tagInfo.toLowerCase().startsWith("input") || tagInfo.toLowerCase().startsWith("img")
				|| tagInfo.toLowerCase().startsWith("hr")) {
			// I've had some trouble with meta, link, input, img, and hr tags not being 
			// closed properly, so if it isn't closed, we're going to force close it and 
			// treat it like a one liner. If oneLiner is in progress, we ignore this
			if(!tagInfo.endsWith("/") && !oneLinerInProg) {
				tagInfo += "/";
				handleOneLiner(tagInfo);
				return;
			}
		}
		
		// At this point we have something along the lines of:
		// someOpeningTag someAtrribute="someValue"
		
		// Let's break it up into a tag type and some attributes
		Scanner tagScan = new Scanner(tagInfo);
		
		if(tagScan.hasNext()) {
			if(tag == null || oneLinerInProg) {
				String type = tagScan.next().toLowerCase();
				if(oneLinerInProg) {
					tag.setType(type);
				} else {
					tag = new Tag(type, null, null);
				}
			}
			
			List<Attribute> attrs = new ArrayList<Attribute>();
			while(tagScan.hasNext()) {
				String next = "";
				boolean improperAttribute = true;
				while(improperAttribute && tagScan.hasNext()) {
					if(!next.equals("")) {
						next += " ";
					}
					next += tagScan.next();
					improperAttribute = !quoteCount(next);
					//improperAttribute = !(((next.indexOf("\"") < next.length()) && next.endsWith("\"")) || ((next.indexOf("'") < next.length()) && next.endsWith("'")));
				}
				attrs.add(parseAttribute(next));
			}
			
			tag.setAttributes(attrs);
			
			// Now that we have a newly formed tag, let's add it to the stack!
			if(!tagStack.isEmpty()) {
				Tag bottomTag = tagStack.get(tagStack.size() - 1);
				bottomTag.addChild(tag);
				tag.setParent(bottomTag);
			}
			tagStack.add(tag);
			strStack.add(tag.getType());
			//System.out.println("\nstrStack : ");
			//for(String str : strStack) {
			//	System.out.println(str);
			//}
		}
	}
	
	/**
	 * Helps determine if a certain " or ' is the closing character for a term.
	 * 
	 * @param line The current line being parsed.
	 * @return True if the tag is complete.
	 */
	private boolean quoteCount(String line) {
		int single = 0; // The number of ' characters in the line.
		int doub = 0; // The number of " characters in the line.
		int helper = -1; // A helper variable.
		int index = -1; // The index of the first ' or " (which ever comes first).
		
		// Get the first instance of ' or ".
		helper = line.indexOf("=") + 1;
		while (helper < line.length() && (line.charAt(helper) != '\'' && line.charAt(helper) != '"'))
			helper++;
		// If the characters don't exist, return false.
		if (helper >= line.length())
			return false;
		index = helper; // Store the index for later use.
		
		// Determine how many " are in the line.
		String myLine = new String(line);
		while (myLine.length() > 0 && (helper = myLine.indexOf("\"")) >= 0) {
			myLine = myLine.substring(helper + 1);
			doub++;
		}
		
		// Determine how many ' are in the line.
		myLine = new String(line);
		while (myLine.length() > 0 && (helper = myLine.indexOf("'")) >= 0) {
			myLine = myLine.substring(helper + 1);
			single++;
		}
		
		// If the line starts with a ', has an even number of ', and
		// ends with a ' return true.
		if (line.charAt(index) == '\'') {
			if (single % 2 != 0)
				return false;
			return line.endsWith("'");
		}
		// Else if the line starts with a ", has an even number of ", and
		// ends with a " return true;
		else if (line.charAt(index) == '"') {
			if (doub % 2 != 0)
				return false;
			return line.endsWith("\"");
		}
		return false;
	}

	private void handleOneLiner(String tagInfo) {
		oneLinerInProg = true;
		handleOpeningTag(tagInfo.substring(0, tagInfo.length() - 1));
		oneLinerInProg = false;
		Tag justClosed = tagStack.remove(tagStack.size() - 1);
		strStack.remove(strStack.size() - 1);
		//System.out.println("\nstrStack : ");
		//for(String str : strStack) {
		//	System.out.println(str);
		//}
		
		addToTagsList(justClosed);
	}

	private void addToTagsList(Tag tag) {
		List<Tag> list = null;
		for(List<Tag> l : tags) {
			if(l.get(0).getType().equals(tag.getType())) {
				list = l;
				break;
			}
		}
		if(list != null) {
			list.add(tag);
		} else {
			list = new ArrayList<Tag>();
			list.add(tag);
			tags.add(list);
		}
		
	}

	private void handleClosingTag(String tagInfo) {
		// get rid of leading slash
		tagInfo = tagInfo.substring(1, tagInfo.length()).trim();
		
		// find the right tag in the tagStack
		for(int i = tagStack.size() - 1; i >= 0; i--) {
			Tag tag = tagStack.get(i);
			if(tag.getType().equals(tagInfo)) {
				tagStack.remove(i);
				strStack.remove(i);
				//System.out.println("\nstrStack : ");
				//for(String str : strStack) {
				//	System.out.println(str);
				//}
				addToTagsList(tag);
				break;
			}
		}
	}

	// If no current tags exist in the tag stack, ignore this block, otherwise add the text
	// in the necessary position.
	private void handleText(String text) {
		if(!tagStack.isEmpty()) {
			if(text.contains("–")) {
				text = text.replace("–", "-");
			}
			if(text.contains("—")) {
				text = text.replace("—", "-");
			}
			if(text.contains("�")) {
				text = text.replace("�", "");
			}
			Tag bottomTag = tagStack.get(tagStack.size() - 1);
			TextTag textTag = new TextTag(text);
			bottomTag.addChild(textTag);
			textTag.setParent(bottomTag);
			addToTagsList(textTag);
		}
	}

	private Attribute parseAttribute(String attrString) {
		if(attrString.contains("?!@**#$")) {
			attrString = attrString.replace("?!@**#$", ">");
		}

		if(attrString.contains("*&^%$#@!?")) {
			attrString = attrString.replace("*&^%$#@!?", " ");
		}
		
		attrString = attrString.replace("=", "!#?@%$&^");
		int firstIndex = attrString.indexOf("!#?@%$&^");
		if (firstIndex >= 0)
			attrString = attrString.substring(0, firstIndex) + "=" + attrString.substring(firstIndex + new String("!#?@%$&^").length());
		
		Scanner scanner = new Scanner(attrString).useDelimiter("=");
		String name = null;
		String value = null;
		if(scanner.hasNext()) {
			name = scanner.next();
		}
		if(scanner.hasNext()) {
			String next = scanner.next();
			if(next.contains("!#?@%$&^")) {
				next = next.replace("!#?@%$&^", "=");
			}
			value = next.substring(1, next.length() - 1);
		}
		if(name == null || value == null) {
			return null;
		}
		return new Attribute(name, value);
	}
	
	private String removeExtraWhiteSpace(String inputStr) {
		String patternStr = "\\s+";
	    String replaceStr = " ";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    return matcher.replaceAll(replaceStr);
	}
	
//	public void tester(String toSpliceForScripts, String toSpliceForComments, String toSpliceForStyles) {
//		scriptInProg = false;
//		System.out.println(spliceScript(toSpliceForScripts));
//		System.out.println("Script in progress? : " + (scriptInProg ? "Yep" : "Nope"));
//		
//		commentInProg = false;
//		System.out.println(spliceComment(toSpliceForComments));
//		System.out.println("Comment in progress? : " + (commentInProg ? "Yep" : "Nope"));
//		
//		styleInProg = false;
//		System.out.println(handleStyle(toSpliceForStyles));
//		System.out.println("Style in progress? : " + (styleInProg ? "Yep" : "Nope"));
//	}
	
//	public static void main(String[] args) {
//		Indexer indexer = new Indexer();
//		indexer.tester("Some Unscripted string", "Some Uncommented string",
//				"<style>This is in a style tag</style>This is in between.<style>This is in a style");
//	}
}
