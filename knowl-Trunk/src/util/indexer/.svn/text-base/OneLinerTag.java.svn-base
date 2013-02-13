package util.indexer;

import java.io.BufferedWriter;
import java.io.IOException;

public class OneLinerTag extends Tag {
	
	public OneLinerTag() {
		super(null, null, null);
	}
	
	public void linesToFile(BufferedWriter out) {
		try {
			String line = "";
			line += "<" + getType();
			for(Attribute attr : getAttributes()) {
				line += " " + attr.toString();
			}
			line += " />";
			out.write(line);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
