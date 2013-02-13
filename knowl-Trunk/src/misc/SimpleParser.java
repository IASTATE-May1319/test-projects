package misc;

public class SimpleParser {
	
	public SimpleParser() {}
	
	public static String parse(String toParse, String start, String stop, boolean isInclusive) {
		if(!toParse.contains(start) && !toParse.contains(stop)) {
			return toParse;
		}
		
		int startPos;
		int stopPos;
		
		if(toParse.contains(start)) {
			startPos = isInclusive ? toParse.indexOf(start) : 
				toParse.indexOf(start) + start.length();
		} else {
			startPos = 0;
		}
		if(toParse.contains(stop)) {
			stopPos = isInclusive ? toParse.indexOf(stop) + stop.length() :
				toParse.indexOf(stop);
		} else {
			stopPos = toParse.length();
		}
		
		return toParse.substring(startPos, stopPos);
	}
}