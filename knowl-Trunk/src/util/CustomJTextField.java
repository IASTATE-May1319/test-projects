package util;

import java.awt.Color;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class CustomJTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	private String filler = "Enter Query Here!";
	private boolean hasText = false;

	public CustomJTextField() {
		super();
	}

	public CustomJTextField(int columns) {
		super(columns);
	}

	public CustomJTextField(String text, int columns) {
		super(text, columns);
	}

	public CustomJTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		String temp = super.getText();
		switch (e.getID()) {
		case FocusEvent.FOCUS_GAINED:
			if (!hasText && temp != null && temp.equals(filler) || temp.equals("")) {
				setForeground(Color.BLACK);
				setText(null);
			}
			hasText = true;
			break;
		case FocusEvent.FOCUS_LOST:
			if (temp == null || temp.equals("")) {
				setForeground(Color.GRAY);
				setText(filler);
				hasText = false;
			} else
				hasText = true;
			break;
		}
		super.processFocusEvent(e);
	}

	@Override
	public String getText() {
		if (hasText)
			return super.getText();
		return "";
	}
}
