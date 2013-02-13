package util.indexer;

import java.io.BufferedWriter;
import java.io.IOException;

public class TextTag extends Tag {

	private String body;
	
	public TextTag(String body) {
		super("text", null, null);
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public void append(String text) {
		this.body += text;
	}
	
	public void linesToFile(BufferedWriter out) {
		try {
			out.write(body);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
