package util.indexer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StyleTag extends Tag {

private List<String> body;
	
	public List<String> getBody() {
		return body;
	}

	public void setBody(List<String> body) {
		this.body = body;
	}

	public StyleTag() {
		super("style", null, null);
		body = new ArrayList<String>();
	}
	
	public void appendBody(String line) {
		body.add(line);
	}
	
	public void linesToFile(BufferedWriter out) {
		try {
			String line = "";
			line += "<" + getType();
			for(Attribute attr : getAttributes()) {
				line += " " + attr.toString();
			}
			line += ">";
			out.write(line);
			out.newLine();
			for(String bodyLine : body) {
				out.write(bodyLine);
				out.newLine();
			}
			out.write("</" + getType() + ">");
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
