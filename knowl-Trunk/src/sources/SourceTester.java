package sources;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import misc.MiniBrowser;

public class SourceTester {

	public static void main(String args[]) {
		Dictionary dict = new Dictionary();
		String queryString = (String)JOptionPane.showInputDialog(
                new JFrame(),
                "Type a search term:",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "applesauce");
		String s = dict.executeQuery(queryString);
		String url = s.replace("\\", "//");
		if (url != null) {
			if (!(url.startsWith("http:") || url.startsWith("file:"))) {
				// If it’s not a fully qualified url, assume it’s a file
				if (url.startsWith("/")) {
					// Absolute path, so just prepend "file:"
					url = "file:" + url;
				}
				else {
					try {
						// assume it’s relative to the starting point...
						File f = new File(url);
						url = f.toURI().toURL().toString();
					}
					catch (Exception e) {}
				}
			}
		}
		new MiniBrowser(url).setVisible(true);
		//new MiniBrowser("http://en.wikipedia.org/wiki/Applesauce").setVisible(true);
		//new MiniBrowser("file:///C://Users//Jesse//Documents//My Dropbox//test.html").setVisible(true);
	}
}
