package util.indexer;

import java.io.BufferedWriter;
import java.io.IOException;

public class DoctypeTag extends Tag {

	private String doctype;
	
	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public DoctypeTag(String doctypeInfo) {
		super("doctype", null, null);
		doctype = "<" + doctypeInfo + ">";
	}
	
	public void linesToFile(BufferedWriter out) {
		try {
			out.write(doctype);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
